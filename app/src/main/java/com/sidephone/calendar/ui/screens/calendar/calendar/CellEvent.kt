package com.sidephone.calendar.ui.screens.calendar.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.sidephone.calendar.R
import com.sidephone.calendar.core.events.EventOccurrence
import com.sidephone.calendar.ui.theme.Dimens

@Composable
fun CellEvent(
	modifier: Modifier,
	event: EventOccurrence? = null,
	altText: String? = null,
	textColor: Color,
	onClick: () -> Unit,
	onLongClick: (() -> Unit)? = null,
	backgroundColor: Color,
	metaTextColor: Color
) {
	val time = if (event == null) {
		""
	} else if (event.startTime == null) {
		stringResource(R.string.event_all_day)
	} else if (event.endTime == null) {
		event.startTime.toString()
	} else {
		"${event.startTime} - ${event.endTime}"
	}

	val altTitle = if (altText.isNullOrBlank()) "" else altText
	val title = event?.title?.ifBlank { stringResource(R.string.event_no_title) } ?: altTitle

	val heading: String = if (event == null) {
		if (altText.isNullOrBlank()) "" else altText
	} else {
		"$time: $title"
	}

	val address = event?.address ?: ""
	val description = event?.description ?: ""

	Column(
		modifier = modifier
			.combinedClickable(
				onClick = onClick,
				onLongClick = if (event == null || onLongClick == null) null else onLongClick,
				onLongClickLabel = if (event == null || onLongClick == null) null else stringResource(R.string.event_delete_title) + ", " + title, // for accessibility
			)
			.padding( // margin, actually
				horizontal = Dimens.EVENT_LIST_MARGIN.first,
				vertical = Dimens.EVENT_LIST_MARGIN.second
			)
			.fillMaxWidth()
			.clip(RoundedCornerShape(Dimens.EVENT_LIST_BORDER_RADIUS))
			.background(backgroundColor)
			.padding( // padding
				horizontal = Dimens.EVENT_LIST_PADDING.first,
				vertical = Dimens.EVENT_LIST_PADDING.second
			)
	) {
		Text(
			style = MaterialTheme.typography.titleMedium,
			color = textColor,
			text = heading,
		)

		if (address.isNotBlank()) {
			Text(
				style = MaterialTheme.typography.bodyMedium,
				text = address,
				color = metaTextColor
			)
		}

		if (description.isNotBlank()) {
			Text(
				style = MaterialTheme.typography.bodyMedium,
				text = description,
				color = metaTextColor
			)
		}
	}
}
