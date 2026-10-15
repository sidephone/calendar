package com.sidephone.calendar.db

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

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
