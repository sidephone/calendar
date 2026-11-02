package com.sidephone.calendar.db

import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey
import com.sidephone.calendar.Settings
import com.sidephone.calendar.core.events.EventRecurrence
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

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
	val freq: EventRecurrence.Freq = EventRecurrence.Freq.NONE,
	val interval: Int = 1, // every N days/weeks/months/years
	val weekdays: Int = 0, // WEEKLY only: bit 0 = Mon ... bit 6 = Sun; 0 = weekday of startDate
	val untilDate: LocalDate? = null, // last allowed START date of an occurrence
	val maxOccurrences: Int? = null,
	val reminders: List<Int> = emptyList(), // minutes before start; applies to every occurrence
) {
	// Length of the event in days, minus one (0 = single day)
	val spanDays: Long get() = ChronoUnit.DAYS.between(startDate, endDate)

	init {
		require(!endDate.isBefore(startDate)) { "endDate must not be before startDate" }
		require(interval >= 1) { "interval must be >= 1" }
		require(maxOccurrences == null || maxOccurrences >= 1) { "maxOccurrences must be >= 1" }
		if (freq == EventRecurrence.Freq.WEEKLY) {
			require(weekdays in 0..0b1111111) { "weekdays must use bits 0..6" }
		}
		if (startTime != null && endTime != null && endDate == startDate) {
			require(!endTime.isBefore(startTime)) { "endTime must not be before startTime on the same day" }
		}
		require(reminders.all { it in 0..Settings.REMINDER_MAX_MINUTES }) { "reminder out of range" }
	}
}
