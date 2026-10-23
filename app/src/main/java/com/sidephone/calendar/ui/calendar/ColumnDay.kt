package com.sidephone.calendar.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.sidephone.calendar.R
import com.sidephone.calendar.core.events.EventOccurrence

@Composable
fun ColumnDay(
	modifier: Modifier,
	selected: Int,
	events: List<EventOccurrence>,
	addNewText: Color,
	addNewBackground: Color,
	highlightText: Color,
	highlightBackground: Color,
	eventText: Color,
	eventBackground: Color,
	eventHighlightText: Color,
	eventHighlightBackground: Color,
) {
	Column(modifier = modifier) {
		Text(
			text = stringResource(id = R.string.event_add),
			color = if (selected == 0) highlightText else addNewText,
			modifier = Modifier
				.background(if (selected == 0) highlightBackground else addNewBackground)
				.fillMaxWidth(),
		)

		for ((i, event) in events.withIndex()) {
			CellEvent(
				modifier = Modifier
					.background(if (selected == i + 1) eventHighlightBackground else eventBackground)
					.fillMaxWidth(),
				event = event,
				highlighted = selected == i + 1,
				textColor = eventText,
				highlightTextColor = eventHighlightText
			)
		}
	}
}
