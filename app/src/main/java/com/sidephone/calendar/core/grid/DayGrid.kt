package com.sidephone.calendar.core.grid

import kotlinx.coroutines.flow.StateFlow
import java.util.Calendar

/**
 * The day grid contains the events for the selected day. It contains only one real true cell.
 * For consistency, here we just do virtual scrolling along the list of events. Item "0" is the
 * "Add new" button, and items 1...N are the events for the day.
 */
class DayGrid(today: Calendar, private val eventCount: StateFlow<Int>) : Grid(today) {
	private var current = 0

	override fun current() = current
	override fun generateGrid(today: Calendar): List<List<Int>> = emptyList()
	override fun initialX(today: Calendar) = 0
	override fun initialY(today: Calendar) = current
	override fun left() {}
	override fun right() {}

	override fun up() {
		if (current >= eventCount.value) current = 0 // +1 for the "Add new" button
		else current++
	}

	override fun down() {
		if (current < 0) current = eventCount.value // +1 for the "Add new" button
		else current--
	}
}
