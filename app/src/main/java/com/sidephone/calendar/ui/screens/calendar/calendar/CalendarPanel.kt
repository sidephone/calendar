package com.sidephone.calendar.ui.screens.calendar.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.sidephone.calendar.core.Action
import com.sidephone.calendar.core.Calendar
import com.sidephone.calendar.core.events.EventState
import com.sidephone.calendar.core.grid.DayGrid
import com.sidephone.calendar.core.grid.MonthGrid
import com.sidephone.calendar.core.grid.YearGrid
import com.sidephone.calendar.ui.screens.calendar.util.Weekdays
import com.sidephone.calendar.ui.theme.Dimens
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
	val eventMetaText = MaterialTheme.colorScheme.onPrimaryFixed

	val addNewEventText = MaterialTheme.colorScheme.onTertiary
	val addNewEventBackground = MaterialTheme.colorScheme.tertiary


	if (grid is DayGrid) {
		ColumnDay(
			modifier = modifier
				.fillMaxSize()
				.onHorizontalSwipe(
					enabled = touchEnabled,
					swipeThreshold = Dimens.SWIPE_THRESHOLD,
					onSwipeLeft = { calendar.onAction(Action.NEXT_PAGE) },
					onSwipeRight = { calendar.onAction(Action.PREVIOUS_PAGE) },
				),
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
			eventHighlightBackground = eventText,
			eventMetaText = eventMetaText
		)

		return
	}


	val weekdays = remember(year, month, grid is MonthGrid) {
		if (grid is MonthGrid) grid.grid[0].map { Weekdays.getFirstLetter(year, month, it) }
		else emptyList()
	}

	val today = LocalDate.now()
	val todayCell: Int? = today.takeIf { it.year == year && it.monthValue == month }?.dayOfMonth

	val cellWithBorder = Modifier
		.drawBehind {
			val w = 1.dp.toPx()
			// right edge
			drawRect(borderColor, topLeft = Offset(size.width - w, 0f), size = Size(w, size.height))
			// bottom edge
			drawRect(borderColor, topLeft = Offset(0f, size.height - w), size = Size(size.width, w))
		}
		.fillMaxHeight()


	Column(
		modifier = modifier
			.fillMaxSize()
			.onHorizontalSwipe(
				enabled = touchEnabled,
				swipeThreshold = Dimens.SWIPE_THRESHOLD,
				onSwipeLeft = { calendar.onAction(Action.NEXT_PAGE) },
				onSwipeRight = { calendar.onAction(Action.PREVIOUS_PAGE) },
			)
	) {
		// top border
		Box(Modifier
			.fillMaxWidth()
			.height(1.dp)
			.background(borderColor)
		)

		// weekday row (only for month grid)
		if (grid is MonthGrid) {
			Row(Modifier.height(Dimens.MONTH_WEEKDAY_ROW_HEIGHT)) {
				for (letter in weekdays) {
					CellWeekday(
						modifier = cellWithBorder.then(Modifier.weight(1f)),
						textColor = textColor,
						letter = letter
					)
				}
			}
		}

		// grid
		for ((y, column) in grid.grid.withIndex()) {
			Row(Modifier.weight(1f)) {
				for ((x, cell) in column.withIndex()) {
					when (grid) {
						is YearGrid -> CellYear(
							modifier = cellWithBorder.then(Modifier.weight(1f)),
							textColor = textColor,
							highlightBackground = cellHighlightBackground,
							highlightTextColor = cellHighlightTextColor,
							isHighlighted = Pair(x, y) == selected,
							month = cell,
							onClick = { touchEnabled && calendar.selectCell(x, y) && calendar.onAction(Action.OK) },
						)

						is MonthGrid -> CellMonth(
							modifier = cellWithBorder.then(Modifier.weight(1f)),
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
