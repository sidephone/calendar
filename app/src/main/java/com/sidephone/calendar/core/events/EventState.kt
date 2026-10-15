package com.sidephone.calendar.core.events

import java.time.LocalDate
import java.time.YearMonth

sealed interface EventState {
	data object Loading : EventState
	data object Empty : EventState
	data class Day(val date: LocalDate, val items: List<EventOccurrence>) : EventState

	// Number of entries per day; a multi-day event counts on every day it covers
	data class Month(val month: YearMonth, val countsByDay: Map<LocalDate, Int>) : EventState
}
