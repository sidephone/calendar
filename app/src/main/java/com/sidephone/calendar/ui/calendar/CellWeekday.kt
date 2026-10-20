package com.sidephone.calendar.ui.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CellWeekday(
	modifier: Modifier,
	textColor: Color,
	year: Int,
	month: Int,
	day: Int,
) {
	val (ny, nm, nd) = normalizeDate(year, month, day)

	Box(
		contentAlignment = Alignment.Center,
		modifier = modifier
	) {
		Text(
			color = textColor,
			text = LocalDate
				.of(ny, nm, nd)
				.format(DateTimeFormatter.ofPattern("eee"))
				.substring(0, 1)
				.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
		)
	}
}

fun normalizeDate(year: Int, month: Int, day: Int): Triple<Int, Int, Int> {
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
