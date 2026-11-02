package com.sidephone.calendar.ui.screens.calendar.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sidephone.calendar.R
import com.sidephone.calendar.core.events.EventOccurrence
import com.sidephone.calendar.ui.screens.event.form.reminder.ReminderUnit
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
	val reminders = event?.reminders ?: emptyList()

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
			Row(verticalAlignment = Alignment.CenterVertically) {
				Icon(
					modifier = Modifier.padding(
						top = Dimens.EVENT_LIST_META_PADDING,
						bottom = Dimens.EVENT_LIST_META_PADDING,
						start = Dimens.EVENT_LIST_META_PADDING,
						end = Dimens.EVENT_LIST_META_PADDING * 2
					),
					painter = painterResource(R.drawable.ic_address),
					tint = metaTextColor,
					contentDescription = null
				)
				Text(
					style = MaterialTheme.typography.bodyMedium,
					text = address,
					color = metaTextColor
				)
			}
		}

		if (description.isNotBlank()) {
			Row(verticalAlignment = Alignment.CenterVertically) {
				Icon(
					modifier = Modifier.padding(
						top = Dimens.EVENT_LIST_META_PADDING,
						bottom = Dimens.EVENT_LIST_META_PADDING,
						start = Dimens.EVENT_LIST_META_PADDING,
						end = Dimens.EVENT_LIST_META_PADDING * 2
					),
					painter = painterResource(R.drawable.ic_description),
					tint = metaTextColor,
					contentDescription = null
				)
				Text(
					style = MaterialTheme.typography.bodyMedium,
					text = description,
					color = metaTextColor
				)
			}
		}

		reminders.forEachIndexed { i, minutes ->
			Row(
				modifier = Modifier.padding(top = if (i == 0) Dimens.EVENT_LIST_META_PADDING else 0.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				Icon(
					modifier = Modifier.padding(
						top = Dimens.EVENT_LIST_META_PADDING,
						bottom = Dimens.EVENT_LIST_META_PADDING,
						start = Dimens.EVENT_LIST_META_PADDING,
						end = Dimens.EVENT_LIST_META_PADDING * 2
					),
					painter = painterResource(R.drawable.ic_reminder),
					tint = metaTextColor,
					contentDescription = null
				)

				val reminderText = ReminderUnit.from(minutes).let { unit ->
					if (unit == ReminderUnit.AT_EVENT) {
						"$minutes ${stringResource(unit.label)}"
					} else {
						val time = minutes / unit.factor
						"$time ${stringResource(unit.label, time)}"
					}
				}

				Text(
					style = MaterialTheme.typography.bodyMedium,
					text = reminderText,
					color = metaTextColor,
				)
			}
		}
	}
}
