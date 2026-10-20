package com.sidephone.calendar.ui.calendar

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sidephone.calendar.core.Calendar
import com.sidephone.calendar.core.grid.MonthGrid
import com.sidephone.calendar.core.grid.YearGrid
import com.sidephone.calendar.util.Weekdays
import java.time.LocalDate

@Composable
fun CalendarPanel(modifier: Modifier = Modifier, calendar: Calendar) {
	val grid = calendar.grid.collectAsState().value
	val (year, month, _) = calendar.date.collectAsState().value
	val current = grid.current()

	val textColor = MaterialTheme.colorScheme.onBackground
	val highlightBackground = MaterialTheme.colorScheme.primary
	val highlightTextColor = MaterialTheme.colorScheme.onPrimary
	val inactiveCellTextColor = MaterialTheme.colorScheme.onSurfaceVariant

	val weekdays = remember {
		grid.grid[0].map { Weekdays.getFirstLetter(year, month, it) }
	}

	val today = LocalDate.now()
	val todayCell = if (today.year == year && today.monthValue == month) today.dayOfMonth else -1

	val boxMod = Modifier
		.border(width = 1.dp, color = MaterialTheme.colorScheme.secondary)
		.fillMaxHeight()

	Column(modifier = modifier.fillMaxSize()) {
		if (grid is MonthGrid) {
			Row(Modifier.height(42.dp)) {
				for (letter in weekdays) {
					CellWeekday(
						modifier = boxMod.then(Modifier.weight(1f)),
						textColor = textColor,
						letter = letter
					)
				}
			}
		}

		for (column in grid.grid) {
			Row(Modifier.weight(1f)) {
				for (cell in column) {
					when (grid) {
						is YearGrid -> CellYear(
							modifier = boxMod.then(Modifier.weight(1f)),
							textColor = textColor,
							highlightBackground = highlightBackground,
							highlightTextColor = highlightTextColor,
							isHighlighted = cell == current,
							month = cell
						)

						is MonthGrid -> CellMonth(
							modifier = boxMod.then(Modifier.weight(1f)),
							textColor = textColor,
							highlightBackground = highlightBackground,
							highlightTextColor = highlightTextColor,
							inactiveTextColor = inactiveCellTextColor,
							isHighlighted = cell == current,
							isToday = cell == todayCell,
							day = cell
						)

						else -> {}
					}
				}
			}
		}
	}
}
