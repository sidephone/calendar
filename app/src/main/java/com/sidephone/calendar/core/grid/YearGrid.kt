package com.sidephone.calendar.core.grid

import java.util.Calendar

class YearGrid : Grid() {
	override fun generateGrid(today: Calendar): List<List<Int>> {
		return listOf(
			listOf(1, 2, 3, 4),
			listOf(5, 6, 7, 8),
			listOf(9, 10, 11, 12)
		)
	}

	override fun initialX(today: Calendar): Int {
		return today.get(Calendar.MONTH) % 4
	}

	override fun initialY(today: Calendar): Int {
		return (today.get(Calendar.MONTH)) / 4
	}
}
