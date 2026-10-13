package com.sidephone.calendar.core.grid

import java.util.Calendar

class DayGrid(today: Calendar) : Grid(today) {
	override fun generateGrid(today: Calendar): List<List<Int>> = emptyList()
	override fun initialX(today: Calendar) = 0
	override fun initialY(today: Calendar) = 0
}
