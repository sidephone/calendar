package com.sidephone.calendar.core.events

import java.time.LocalDate
import java.time.YearMonth

sealed interface EventState {
	data object Loading : EventState
	data object Empty : EventState
	data class Day(val date: LocalDate, val items: List<EventOccurrence>) : EventState

	// Number of entries per day; a multi-day event counts on every day it covers
	data class Month(val month: YearMonth, val countsByDay: Map<LocalDate, Int>) : EventState

	val count: Int
		get() = when (this) {
			is Day -> items.size
			is Month -> countsByDay.values.sum()
			else -> 0
		}

	val eventIds: List<Long>
		get() = if (this is Day) items.map { it.eventId } else emptyList()
}
