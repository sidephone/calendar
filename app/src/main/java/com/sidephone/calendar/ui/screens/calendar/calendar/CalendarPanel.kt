package com.sidephone.calendar.ui.screens.calendar.calendar

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
import com.sidephone.calendar.core.Action
import com.sidephone.calendar.core.Calendar
import com.sidephone.calendar.core.events.EventState
import com.sidephone.calendar.core.grid.DayGrid
import com.sidephone.calendar.core.grid.MonthGrid
import com.sidephone.calendar.core.grid.YearGrid
import com.sidephone.calendar.ui.screens.calendar.util.Weekdays
import java.time.LocalDate

@Composable
fun CalendarPanel(modifier: Modifier = Modifier, calendar: Calendar, touchEnabled: Boolean) {
	val grid = calendar.grid.collectAsState().value
	val events: EventState = calendar.events.collectAsState().value
	val (year, month, _) = calendar.date.collectAsState().value
	val selected = grid.position()

	val borderColor = MaterialTheme.colorScheme.outline

	val textColor = MaterialTheme.colorScheme.onBackground
	val cellHighlightBackground = MaterialTheme.colorScheme.primary
	val cellHighlightTextColor = MaterialTheme.colorScheme.onPrimary
	val cellInactiveTextColor = MaterialTheme.colorScheme.onSurfaceVariant

	val eventMarker = MaterialTheme.colorScheme.secondary
	val eventBackground = MaterialTheme.colorScheme.primaryContainer
	val eventText = MaterialTheme.colorScheme.onPrimaryContainer
//	val eventHighlightedBackground = MaterialTheme.colorScheme.secondaryFixed
//	val eventHighlightedText = MaterialTheme.colorScheme.onSecondaryFixed

	val addNewEventText = MaterialTheme.colorScheme.onTertiary
	val addNewEventBackground = MaterialTheme.colorScheme.tertiary


	val weekdays = remember(year, month, grid is MonthGrid) {
		if (grid is MonthGrid) grid.grid[0].map { Weekdays.getFirstLetter(year, month, it) }
		else emptyList()
	}

	val today = LocalDate.now()
	val todayCell: Int? = today.takeIf { it.year == year && it.monthValue == month }?.dayOfMonth


	val boxMod = Modifier
		.border(width = 1.dp, color = borderColor)
		.fillMaxHeight()


	if (grid is DayGrid) {
		ColumnDay(
			modifier = modifier.fillMaxSize(),
			calendar = calendar,
			selected = selected.second,
			touchEnabled = touchEnabled,
			events = if (events is EventState.Day) events.items else emptyList(),
			addNewText = addNewEventText,
			addNewBackground = addNewEventBackground,
			addNewHighlightText = addNewEventBackground,
			addNewHighlightBackground = addNewEventText,
			eventText = eventText,
			eventBackground = eventBackground,
			eventHighlightText = eventBackground,
			eventHighlightBackground = eventText
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
							highlightBackground = cellHighlightBackground,
							highlightTextColor = cellHighlightTextColor,
							isHighlighted = Pair(x, y) == selected,
							month = cell,
							onClick = { touchEnabled && calendar.selectCell(x, y) && calendar.onAction(Action.OK) },
						)

						is MonthGrid -> CellMonth(
							modifier = boxMod.then(Modifier.weight(1f)),
							textColor = textColor,
							eventColor = eventMarker,
							highlightBackground = cellHighlightBackground,
							highlightTextColor = cellHighlightTextColor,
							inactiveTextColor = cellInactiveTextColor,
							isHighlighted = Pair(x, y) == selected,
							isToday = cell == todayCell,
							day = cell,
							eventCount = if (cell > 0 && events is EventState.Month) events.countsByDay[LocalDate.of(year, month, cell)] ?: 0 else 0,
							onClick = { touchEnabled && calendar.selectCell(x, y) && calendar.onAction(Action.OK) },
						)

						else -> {}
					}
				}
			}
		}
	}
}
