package com.sidephone.calendar.ui.screens.main.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class Weekdays {
	companion object {
		fun getFirstLetter(year: Int, month: Int, day: Int): String {
			val (ny, nm, nd) = normalizeDate(year, month, day)

			return LocalDate
				.of(ny, nm, nd)
				.format(DateTimeFormatter.ofPattern("eee"))
				.substring(0, 1)
				.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
		}


		private fun normalizeDate(year: Int, month: Int, day: Int): Triple<Int, Int, Int> {
			var computedYear = year
			var computedMonth = month
			var computedDay = day

			if (computedDay < 0) {
				computedDay = -computedDay
				if (computedMonth == 1) {
					computedYear -= 1
					computedMonth = 12
				} else {
					computedMonth -= 1
				}
			}

			return Triple(computedYear, computedMonth, computedDay)
		}
	}
}
