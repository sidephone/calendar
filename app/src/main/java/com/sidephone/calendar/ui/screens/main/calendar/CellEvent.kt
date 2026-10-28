package com.sidephone.calendar.ui.screens.main.calendar

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.sidephone.calendar.R
import com.sidephone.calendar.core.events.EventOccurrence

@Composable
fun CellEvent(
	modifier: Modifier,
	event: EventOccurrence,
	highlighted: Boolean,
	textColor: Color,
	highlightTextColor: Color,
	onClick: () -> Unit,
	onLongClick: () -> Unit
) {
	val time = if (event.startTime == null) {
		stringResource(R.string.event_all_day)
	} else if (event.endTime == null) {
		event.startTime.toString()
	} else {
		"${event.startTime} - ${event.endTime}"
	}

	val title = event.title.ifBlank { stringResource(R.string.event_no_title) }

	Box(
		modifier = modifier.combinedClickable(
			onClick = onClick,
			onLongClick = onLongClick,
			onLongClickLabel = stringResource(R.string.event_delete_title) + ", " + title, // for accessibility
		)
	) {
		Text(
			text = "${time}: ${title}",
			color = if (highlighted) highlightTextColor else textColor
		)
	}
}
