package com.sidephone.calendar.ui.screens.main.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.sidephone.calendar.R
import com.sidephone.calendar.core.Action
import com.sidephone.calendar.core.Calendar
import com.sidephone.calendar.core.events.EventOccurrence

@Composable
fun ColumnDay(
	modifier: Modifier,
	calendar: Calendar,
	selected: Int,
	touchEnabled: Boolean,
	events: List<EventOccurrence>,
	addNewText: Color,
	addNewBackground: Color,
	highlightText: Color,
	highlightBackground: Color,
	eventText: Color,
	eventBackground: Color,
	eventHighlightText: Color,
	eventHighlightBackground: Color
) {
	val listState: LazyListState = rememberLazyListState()

	// Keep the selected row fully visible. Index 0 is "add new", events start at 1.
	LaunchedEffect(selected) {
		val info = listState.layoutInfo
		val row = info.visibleItemsInfo.firstOrNull { it.index == selected }
		if (row == null) { // not on screen at all
			listState.animateScrollToItem(selected)
		} else {
			val delta = when {
				// clipped at top
				row.offset < info.viewportStartOffset -> row.offset - info.viewportStartOffset
				// clipped at bottom
				row.offset + row.size > info.viewportEndOffset -> row.offset + row.size - info.viewportEndOffset
				else -> 0
			}
			if (delta != 0) listState.animateScrollBy(delta.toFloat())
		}
	}


	LazyColumn(modifier = modifier, state = listState, userScrollEnabled = touchEnabled) {
		item(key = "add") {
			Text(
				text = stringResource(id = R.string.event_add),
				color = if (selected == 0) highlightText else addNewText,
				modifier = Modifier
					.background(if (selected == 0) highlightBackground else addNewBackground)
					.fillMaxWidth()
					.focusProperties { canFocus = false }
					.clickable {
						calendar.selectCell(0, 0)
						calendar.onAction(Action.OK)
					}
			)
		}

		itemsIndexed(
			items = events,
			key = { _, e -> "${e.eventId}:${e.originalDate.toEpochDay()}" }
		) { i, event ->
			CellEvent(
				modifier = Modifier
					.focusProperties { canFocus = false }
					.background(if (selected == i + 1) eventHighlightBackground else eventBackground)
					.fillMaxWidth(),
				event = event,
				highlighted = selected == i + 1,
				textColor = eventText,
				highlightTextColor = eventHighlightText,
				onClick = {
					calendar.selectCell(0, i + 1)
					calendar.onAction(Action.OK)
				},
				onLongClick = {
					if (touchEnabled) {
						calendar.selectCell(0, i + 1)
						calendar.onAction(Action.CONFIRM_DELETE)
					}
				}
			)
		}
	}
}
