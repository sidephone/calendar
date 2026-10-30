package com.sidephone.calendar.core.events

import java.time.LocalDate
import java.time.LocalTime

/**
 * What the UI sees: one entry per event per DAY it covers.
 * A 3-day conference yields three EventOccurrences (dayIndex 0, 1, 2) with the same eventId/originalDate.
 */
data class EventOccurrence(
	val eventId: Long,
	val originalDate: LocalDate,   // normal START date of this occurrence; with eventId identifies it
	val date: LocalDate,           // the day this entry is shown on
	val startDate: LocalDate,      // actual start of the whole occurrence (after overrides)
	val endDate: LocalDate,        // actual end of the whole occurrence (inclusive)
	val dayIndex: Int,             // 0-based position of [date] within the occurrence
	val dayCount: Int,             // total days covered
	val title: String,
	val description: String,
	val address: String?,
	val startTime: LocalTime?,     // time of the whole occurrence's start; null = all-day
	val endTime: LocalTime?,       // time of the whole occurrence's end on endDate
	val recurring: Boolean,
	val reminders: List<Int>,
) {
	val allDay: Boolean get() = startTime == null
	val isMultiDay: Boolean get() = dayCount > 1
	val isFirstDay: Boolean get() = dayIndex == 0
	val isLastDay: Boolean get() = dayIndex == dayCount - 1
}
