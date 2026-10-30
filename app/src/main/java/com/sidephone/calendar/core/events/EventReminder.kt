package com.sidephone.calendar.core.events

import com.sidephone.calendar.Settings
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * One reminder of one occurrence: "[minutesBefore] minutes before [start]".
 * The value is signed: negative means AFTER [start]. For all-day events [start] is midnight
 * (00:00) of the first day, so "09:00 on the day" is -540 and "20:00 the evening before" is 240.
 */
data class EventReminder(
	val eventId: Long,
	val originalDate: LocalDate,   // together with eventId, identifies the occurrence
	val title: String,
	val address: String?,
	val start: LocalDateTime,      // start of the occurrence (all-day: its start date at 00:00)
	val minutesBefore: Int,
) {
	val triggerAt: LocalDateTime get() = start.minusMinutes(minutesBefore.toLong())

	companion object {
		/** Largest "before" offset (30 days). Keeps the look-ahead window of the scheduler bounded. */
		const val MAX_DAYS = Settings.REMINDER_MAX_MINUTES / (24 * 60)

		/** Largest "after" offset: just under one day after the anchor (23:59 on the start day for all-day). */
		const val MIN_MINUTES = -(24 * 60 - 1)

		/**
		 * One EventReminder per (occurrence, reminder). Only entries with dayIndex == 0 are used,
		 * so a multi-day occurrence counts once, at its start.
		 */
		fun of(occurrences: List<EventOccurrence>): List<EventReminder> =
			occurrences
				.filter { it.dayIndex == 0 }
				.flatMap { o ->
					val start = o.startDate.atTime(o.startTime ?: LocalTime.MIDNIGHT)
					o.reminders.distinct().map { m ->
						EventReminder(o.eventId, o.originalDate, o.title, o.address, start, m)
					}
				}
	}
}
