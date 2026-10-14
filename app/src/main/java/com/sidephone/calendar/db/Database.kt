package com.sidephone.calendar.db

import android.content.Context
import androidx.room3.ColumnTypeConverter
import androidx.room3.ColumnTypeConverters
import androidx.room3.Dao
import androidx.room3.Database
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import androidx.room3.Query
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.room3.Upsert
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

// ───────────────────────────── Entities ─────────────────────────────

enum class Freq { NONE, DAILY, WEEKLY, MONTHLY, YEARLY }

/**
 * A base event. For recurring events this describes the FIRST occurrence;
 * every other occurrence has the same length (endDate - startDate days).
 *
 * - startTime == null  -> all-day event. endDate is INCLUSIVE (Mon..Fri = start Mon, end Fri).
 *                         endTime is ignored.
 * - startTime != null  -> timed event. It ends on endDate at endTime (endTime may be null = unspecified).
 *                         Overnight: startDate = D, endDate = D+1, endTime < startTime is fine.
 */
@Entity(indices = [Index("startDate"), Index("freq")])
data class Event(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val title: String,
	val description: String = "",
	val address: String? = null,
	val startDate: LocalDate,
	val startTime: LocalTime? = null,
	val endDate: LocalDate = startDate,
	val endTime: LocalTime? = null,
	val freq: Freq = Freq.NONE,
	val interval: Int = 1,                 // every N days/weeks/months/years
	val weekdays: Int = 0,                 // WEEKLY only: bit 0 = Mon ... bit 6 = Sun; 0 = weekday of startDate
	val untilDate: LocalDate? = null,      // last allowed START date of an occurrence
	val maxOccurrences: Int? = null,
) {
	init {
		require(!endDate.isBefore(startDate)) { "endDate must not be before startDate" }
		require(interval >= 1) { "interval must be >= 1" }
		if (startTime != null && endTime != null && endDate == startDate) {
			require(!endTime.isBefore(startTime)) { "endTime must not be before startTime on the same day" }
		}
	}
}

/** Length of the event in days, minus one (0 = single day). */
val Event.spanDays: Long get() = ChronoUnit.DAYS.between(startDate, endDate)

/**
 * Changes a single generated occurrence of a recurring event (or of a non-recurring one).
 * [originalDate] is the START date the occurrence would normally have; it identifies the occurrence.
 * Null fields mean "keep the base value".
 *
 * To move an occurrence set BOTH [movedStartDate] and [movedEndDate] (the repository/UI must keep
 * the length it wants; copy it from the Occurrence's dayCount).
 */
@Entity(
	primaryKeys = ["eventId", "originalDate"],
	foreignKeys = [
		ForeignKey(
			entity = Event::class,
			parentColumns = ["id"],
			childColumns = ["eventId"],
			onDelete = ForeignKey.CASCADE
		)
	],
	indices = [Index("movedStartDate")]
)
data class EventOverride(
	val eventId: Long,
	val originalDate: LocalDate,
	val cancelled: Boolean = false,
	val title: String? = null,
	val description: String? = null,
	val address: String? = null,
	val allDay: Boolean? = null,           // null = keep base; false needs startTime (here or in base)
	val startTime: LocalTime? = null,
	val endTime: LocalTime? = null,
	val movedStartDate: LocalDate? = null,
	val movedEndDate: LocalDate? = null,
) {
	init {
		require((movedStartDate == null) == (movedEndDate == null)) {
			"movedStartDate and movedEndDate must be set together"
		}
		if (movedStartDate != null && movedEndDate != null) {
			require(!movedEndDate.isBefore(movedStartDate)) { "movedEndDate before movedStartDate" }
		}
	}
}

// ───────────────────────────── Converters ─────────────────────────────

class Converters {
	@ColumnTypeConverter
	fun fromDate(d: LocalDate?): Long? = d?.toEpochDay()

	@ColumnTypeConverter
	fun toDate(v: Long?): LocalDate? = v?.let(LocalDate::ofEpochDay)

	@ColumnTypeConverter
	fun fromTime(t: LocalTime?): Int? = t?.toSecondOfDay()

	@ColumnTypeConverter
	fun toTime(v: Int?): LocalTime? = v?.let { LocalTime.ofSecondOfDay(it.toLong()) }
}

// ───────────────────────────── DAO ─────────────────────────────

@Dao
interface EventDao {

	/** Base events that may have an occurrence overlapping [from, to]. */
	@Query(
		"""
        SELECT * FROM Event WHERE
            (freq = 'NONE' AND startDate <= :to AND endDate >= :from)
         OR (freq != 'NONE' AND startDate <= :to
             AND (untilDate IS NULL OR untilDate + (endDate - startDate) >= :from))
         OR id IN (SELECT eventId FROM EventOverride
                   WHERE movedStartDate IS NOT NULL AND movedStartDate <= :to AND movedEndDate >= :from)
        """
	)
	fun events(from: LocalDate, to: LocalDate): Flow<List<Event>>

	/** Overrides whose original OR moved position overlaps [from, to]. */
	@Query(
		"""
        SELECT o.* FROM EventOverride AS o
        JOIN Event AS e ON e.id = o.eventId
        WHERE (o.originalDate <= :to AND o.originalDate + (e.endDate - e.startDate) >= :from)
           OR (o.movedStartDate IS NOT NULL AND o.movedStartDate <= :to AND o.movedEndDate >= :from)
        """
	)
	fun overrides(from: LocalDate, to: LocalDate): Flow<List<EventOverride>>

	@Query("SELECT * FROM Event WHERE id = :id")
	suspend fun get(id: Long): Event?

	/** Returns the new row id on insert, but -1 on update; use the repository wrapper. */
	@Upsert
	suspend fun save(event: Event): Long

	@Upsert
	suspend fun saveOverride(o: EventOverride)

	@Query("DELETE FROM Event WHERE id = :id")
	suspend fun deleteEvent(id: Long)

	@Query("DELETE FROM EventOverride WHERE eventId = :eventId AND originalDate = :originalDate")
	suspend fun deleteOverride(eventId: Long, originalDate: LocalDate)

	@Query(
		"""
        SELECT * FROM Event
        WHERE title LIKE '%' || :q || '%'
           OR description LIKE '%' || :q || '%'
           OR address LIKE '%' || :q || '%'
        ORDER BY startDate
        """
	)
	suspend fun search(q: String): List<Event>
}

// ───────────────────────────── Database ─────────────────────────────

@Database(entities = [Event::class, EventOverride::class], version = 1, exportSchema = true)
@ColumnTypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
	abstract fun eventDao(): EventDao

	companion object {
		@Volatile
		private var instance: AppDatabase? = null

		fun get(context: Context): AppDatabase =
			instance ?: synchronized(this) {
				instance ?: Room.databaseBuilder<AppDatabase>(
					context.applicationContext, "calendar.db"
				)
					.setDriver(BundledSQLiteDriver())   // Room 3 is driver-based; a driver is required
					.build()
					.also { instance = it }
			}
	}
}
