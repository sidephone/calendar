package com.sidephone.calendar.core.grid

import java.util.Calendar

class DayGrid(today: Calendar) : Grid(today) {
	/**
	 * The day grid contains the events for the selected day. Since they are loaded dynamically, here
	 * we just return an event with ID=0 which is a placeholder for the "Add new" button.
	 * TODO: Figure out how to load them dynamically here
	 */
	override fun generateGrid(today: Calendar): List<List<Int>> = listOf(listOf(0))
	override fun initialX(today: Calendar) = 0
	override fun initialY(today: Calendar) = 0
}
