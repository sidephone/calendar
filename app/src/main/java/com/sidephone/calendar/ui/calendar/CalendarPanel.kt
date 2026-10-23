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
import com.sidephone.calendar.core.events.EventState
import com.sidephone.calendar.core.grid.DayGrid
import com.sidephone.calendar.core.grid.MonthGrid
import com.sidephone.calendar.core.grid.YearGrid
import com.sidephone.calendar.util.Weekdays
import java.time.LocalDate

@Composable
fun CalendarPanel(modifier: Modifier = Modifier, calendar: Calendar) {
	val grid = calendar.grid.collectAsState().value
	val events: EventState = calendar.events.collectAsState().value
	val (year, month, _) = calendar.date.collectAsState().value
	val selected = grid.position()

	val textColor = MaterialTheme.colorScheme.onBackground
	val highlightBackground = MaterialTheme.colorScheme.primary
	val highlightTextColor = MaterialTheme.colorScheme.onPrimary
	val inactiveCellTextColor = MaterialTheme.colorScheme.onSurfaceVariant

	val eventBackground = MaterialTheme.colorScheme.secondary
	val eventText = MaterialTheme.colorScheme.onSecondary
	val eventHighlightedBackground = MaterialTheme.colorScheme.secondaryFixed
	val eventHighlightedText = MaterialTheme.colorScheme.onSecondaryFixed


	val weekdays = remember(year, month, grid is MonthGrid) {
		if (grid is MonthGrid) grid.grid[0].map { Weekdays.getFirstLetter(year, month, it) }
		else emptyList()
	}

	val today = LocalDate.now()
	val todayCell: Int? = today.takeIf { it.year == year && it.monthValue == month }?.dayOfMonth

	val boxMod = Modifier
		.border(width = 1.dp, color = MaterialTheme.colorScheme.secondary)
		.fillMaxHeight()


	if (grid is DayGrid) {
		ColumnDay(
modifier = modifier.fillMaxSize(),
			selected = selected.second,
			events = if (events is EventState.Day) events.items else emptyList(),
			addNewText = textColor,
			addNewBackground = MaterialTheme.colorScheme.surfaceVariant,
			highlightText = highlightTextColor,
			highlightBackground = highlightBackground,
			eventText = eventText,
			eventBackground = eventBackground,
			eventHighlightText = eventHighlightedText,
			eventHighlightBackground = eventHighlightedBackground,
		)

		return
	}


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

		for ((y, column) in grid.grid.withIndex()) {
			Row(Modifier.weight(1f)) {
				for ((x, cell) in column.withIndex()) {
					when (grid) {
						is YearGrid -> CellYear(
							modifier = boxMod.then(Modifier.weight(1f)),
							textColor = textColor,
							highlightBackground = highlightBackground,
							highlightTextColor = highlightTextColor,
							isHighlighted = Pair(x, y) == selected,
							month = cell
						)

						is MonthGrid -> CellMonth(
							modifier = boxMod.then(Modifier.weight(1f)),
							textColor = textColor,
							highlightBackground = highlightBackground,
							highlightTextColor = highlightTextColor,
							inactiveTextColor = inactiveCellTextColor,
							isHighlighted = Pair(x, y) == selected,
							isToday = cell == todayCell,
							day = cell,
							eventCount = if (cell > 0 && events is EventState.Month) events.countsByDay[LocalDate.of(year, month, cell)] ?: 0 else 0
						)

						else -> {}
					}
				}
			}
		}
	}
}
