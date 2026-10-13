package com.sidephone.calendar.core.grid

import java.util.Calendar

class MonthGrid(today: Calendar) : Grid(today) {
	/**
	 * Returns a 7x6 grid representing the days of the month, where each cell contains the day number
	 * (1-31) or negative numbers for the days of the previous or next month that fill the grid. The
	 * days are aligned according to the first day of the week, based on the current locale.
	 */
	override fun generateGrid(today: Calendar): List<List<Int>> {
		val calendar = today.clone() as Calendar
		calendar.set(Calendar.DAY_OF_MONTH, 1)
		val firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK)
		val firstDayOfWeek = calendar.firstDayOfWeek
		val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
		val previousMonth = (calendar.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
		val previousMonthDays = previousMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
		val leadingDays = (firstDayOfMonth - firstDayOfWeek + 7) % 7

		val grid = mutableListOf<MutableList<Int>>()
		var dayCounter = 1
		var nextMonthDayCounter = 1
		for (week in 0 until 6) {
			val weekRow = mutableListOf<Int>()
			for (day in 0 until 7) {
				if (week == 0 && day < leadingDays) {
					// Fill with previous month's days
					weekRow.add(-(previousMonthDays - leadingDays + day + 1))
				} else if (dayCounter <= daysInMonth) {
					// Fill with current month's days
					weekRow.add(dayCounter++)
				} else {
					// Fill with next month's days
					weekRow.add(-1 * nextMonthDayCounter++)
				}
			}
			grid.add(weekRow)
		}
		return grid
	}

	override fun initialX(today: Calendar): Int {
		for (week in grid.indices) {
			for (day in grid[week].indices) {
				if (grid[week][day] == today.get(Calendar.DAY_OF_MONTH)) {
					return day
				}
			}
		}
		return 0
	}

	override fun initialY(today: Calendar): Int {
		for (week in grid.indices) {
			if (grid[week].contains(today.get(Calendar.DAY_OF_MONTH))) {
				return week
			}
		}
		return 0
	}
}
