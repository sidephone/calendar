package com.sidephone.calendar.core.grid

import java.util.Calendar

/**
 * A 4x3 grid representing the months of the year. Each cell contains the month number (1-12).
 */
class YearGrid(today: Calendar) : Grid(today) {
	override fun initialX(today: Calendar) = today.get(Calendar.MONTH) % 3
	override fun initialY(today: Calendar) = today.get(Calendar.MONTH) / 3

	override fun generateGrid(today: Calendar): List<List<Int>> {
		return listOf(
			listOf(1, 2, 3),
			listOf(4, 5, 6),
			listOf(7, 8, 9),
			listOf(10, 11, 12)
		)
	}
}
