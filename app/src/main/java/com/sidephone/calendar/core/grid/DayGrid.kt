package com.sidephone.calendar.core.grid

import com.sidephone.calendar.core.events.EventState
import kotlinx.coroutines.flow.StateFlow
import java.util.Calendar

/**
 * The day grid contains the events for the selected day. It contains only one real true cell.
 * For consistency, here we just do virtual scrolling along the list of events. Item "0" is the
 * "Add new" button, and items 1...N are the events for the day.
 */
class DayGrid(today: Calendar, private val events: StateFlow<EventState>) : Grid(today) {
	override fun current() = currentY
	fun eventId() = if (currentY == 0) 0 else events.value.eventIds.getOrNull(currentY - 1) ?: 0
	override fun generateGrid(today: Calendar): List<List<Int>> = emptyList()
	override fun initialX(today: Calendar) = 0
	override fun initialY(today: Calendar) = 0
	override fun left() {}
	override fun right() {}


	override fun down() {
		if (currentY >= events.value.count) currentY = 0 // +1 for the "Add new" button
		else currentY++
	}

	override fun up() {
		if (currentY <= 0) currentY = events.value.count // +1 for the "Add new" button
		else currentY--
	}

	override fun select(x: Int, y: Int): Boolean {
		if (y < 0 || y > events.value.count) return false // +1 for the "Add new" button

		currentY = y
		return true
	}
}
