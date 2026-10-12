package com.sidephone.calendar.core.grid

import java.util.Calendar

class DayGrid(today: Calendar) : Grid(today) {
	/**
	 * TODO:
	 * Generates a column of the events for the current day in chronological order. Each cell contains
	 * the event ID. The last cell contains "0" to indicate the "Add Event" option.
	 */
	override fun generateGrid(today: Calendar): List<List<Int>> = emptyList()
	override fun initialX(today: Calendar) = 0
	override fun initialY(today: Calendar) = 0
}
