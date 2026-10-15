package com.sidephone.calendar.core.events

import java.time.LocalDate
import java.time.YearMonth

sealed interface EventTimeframe {
	data class Day(val date: LocalDate) : EventTimeframe

	/**
	 * Pass the grid's first/last visible dates as [from]/[to] if the grid shows days of
	 * adjacent months, so those cells get markers too.
	 */
	data class Month(
		val month: YearMonth,
		val from: LocalDate = month.atDay(1),
		val to: LocalDate = month.atEndOfMonth(),
	) : EventTimeframe


	data class Year(val year: Int) : EventTimeframe
}
