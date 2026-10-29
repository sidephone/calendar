package com.sidephone.calendar.ui.screens.calendar.calendar

import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontStyle
import com.sidephone.calendar.R
import com.sidephone.calendar.core.Action
import com.sidephone.calendar.core.Calendar
import com.sidephone.calendar.core.events.EventOccurrence
import com.sidephone.calendar.input.Keypad
import com.sidephone.calendar.ui.theme.Dimens

@Composable
fun ColumnDay(
	modifier: Modifier,
	calendar: Calendar,
	tile: Keypad.Layout,
	selected: Int,
	touchEnabled: Boolean,
	events: List<EventOccurrence>,
	addNewText: Color,
	addNewBackground: Color,
	addNewHighlightText: Color,
	addNewHighlightBackground: Color,
	eventText: Color,
	eventBackground: Color,
	eventHighlightText: Color,
	eventHighlightBackground: Color,
	eventMetaText: Color,
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
		if (
			events.isNotEmpty() &&
			(tile == Keypad.Layout.SUNDIAL || tile == Keypad.Layout.GAMEPAD || tile == Keypad.Layout.T9_NO_DPAD)
		) {
			item(key = "help") {
				Text(
					modifier = Modifier.padding(Dimens.FORM_INPUT_SPACING),
					fontStyle = FontStyle.Italic,
					text = stringResource(id = R.string.help_press_left_to_delete),
					color = eventMetaText,
				)
			}
		}

		item(key = "add") {
			CellEvent(
				modifier = Modifier.focusProperties { canFocus = false },
				altText = stringResource(id = R.string.event_add),
				textColor = if (selected == 0) addNewHighlightText else addNewText,
				metaTextColor = if (selected == 0) addNewHighlightText else addNewText,
				backgroundColor = if (selected == 0) addNewHighlightBackground else addNewBackground,
				onClick = {
					touchEnabled && calendar.selectCell(0, 0) && calendar.onAction(Action.OK)
				},
				onLongClick = {},
			)
		}

		itemsIndexed(
			items = events,
			key = { _, e -> "${e.eventId}:${e.originalDate.toEpochDay()}" }
		) { i, event ->
			CellEvent(
				modifier = Modifier.focusProperties { canFocus = false },
				event = event,
				textColor = if (selected == i + 1) eventHighlightText else eventText,
				metaTextColor = if (selected == i + 1) eventHighlightText else eventMetaText,
				backgroundColor = if (selected == i + 1) eventHighlightBackground else eventBackground,
				onClick = {
					touchEnabled && calendar.selectCell(0, i + 1) && calendar.onAction(Action.OK)
				},
				onLongClick = {
					touchEnabled && calendar.selectCell(0, i + 1) && calendar.onAction(Action.CONFIRM_DELETE)
				}
			)
		}
	}
}
