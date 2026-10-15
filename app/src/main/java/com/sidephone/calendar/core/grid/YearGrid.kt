package com.sidephone.calendar.core.grid

import java.util.Calendar

/**
 * A 3x4 grid representing the months of the year. Each cell contains the month number (1-12).
 */
class YearGrid(today: Calendar) : Grid(today) {
	override fun initialX(today: Calendar) = today.get(Calendar.MONTH) % 4
	override fun initialY(today: Calendar) = today.get(Calendar.MONTH) / 4

	override fun generateGrid(today: Calendar): List<List<Int>> {
		return listOf(
			listOf(1, 2, 3, 4),
			listOf(5, 6, 7, 8),
			listOf(9, 10, 11, 12)
		)
	}
}
