package com.sidephone.calendar.ui.screens.event.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sidephone.calendar.R
import com.sidephone.calendar.db.Event
import com.sidephone.calendar.ui.components.popup.DialogButton
import com.sidephone.calendar.ui.theme.DISABLED_ALPHA
import com.sidephone.calendar.ui.theme.Dimens
import java.time.LocalTime


@Composable
fun EventForm(
	modifier: Modifier,
	event: Event,
	onChange: (Event) -> Unit,
	onSave: () -> Unit,
	onCancel: () -> Unit
) {
	var showMoreOptions by remember { mutableStateOf(false) }

	var isAllDay by remember { mutableStateOf(event.startTime == null) }
	var startHour by remember { mutableIntStateOf(event.startTime?.hour ?: 9) }
	var startMinute by remember { mutableIntStateOf(event.startTime?.minute ?: 0) }
	var endHour by remember { mutableIntStateOf(event.endTime?.hour ?: 10) }
	var endMinute by remember { mutableIntStateOf(event.endTime?.minute ?: 0) }

	val highlightColor = MaterialTheme.colorScheme.secondary
	val textColor = MaterialTheme.colorScheme.onBackground

	Column(
		modifier = modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState()),
		verticalArrangement = Arrangement.spacedBy(Dimens.FORM_INPUT_SPACING)
	) {
		FormTextField(
			value = event.title,
			titleResId = R.string.event_form_title,
			onValueChange = { onChange(event.copy(title = it)) },
			singleLine = true,
			outlineColor = highlightColor,
			cursorColor = textColor
		)

		FormSwitch(
			title = stringResource(R.string.event_form_all_day, event.startDate.toString()),
			value = isAllDay,
			disabled = false,
			onValueChange = { allDay ->
				isAllDay = allDay
				onChange(
					if (allDay) event.copy(startTime = null, endTime = null)
					else event.copy(
						startTime = LocalTime.of(startHour, startMinute),
						endTime = LocalTime.of(endHour, endMinute),
					)
				)
			}
		)

		if (!isAllDay) {
			FormTimeField(
				titleResId = R.string.event_form_start_time,
				hour = startHour,
				minute = startMinute,
				onTimeChange = { hour, minute ->
					var start = LocalTime.of(hour, minute)
					// Local state is the source of truth (the `event` prop may lag behind).
					var end = LocalTime.of(endHour, endMinute)

					if (event.endDate == event.startDate && hour == 23) {
						if (minute == 59) {
							start = LocalTime.of(23, 58)
							end = LocalTime.of(23, 59)
						} else if (end.isBefore(start)) {
							end = LocalTime.of(23, 59)
						}
					} else if (event.endDate == event.startDate && end.isBefore(start)) {
						end = start.plusHours(1)
					}

					onChange(event.copy(startTime = start, endTime = end))
					startHour = start.hour
					startMinute = start.minute
					endHour = end.hour
					endMinute = end.minute
				},
				onDone = { /* no-op */ }
			)

			FormTimeField(
				titleResId = R.string.event_form_end_time,
				hour = endHour,
				minute = endMinute,
				onTimeChange = { hour, minute ->
					var start = LocalTime.of(startHour, startMinute)
					var end = LocalTime.of(hour, minute)

					if (event.startDate == event.endDate && start.hour == 23 && end.isBefore(start)) {
						if (startMinute == 59) {
							start = LocalTime.of(23, 58)
						}
						end = start.plusMinutes(1)
					} else if (event.startDate == event.endDate && end.isBefore(start)) {
						end = start.plusHours(1)
					}

					onChange(event.copy(startTime = start, endTime = end))
					startHour = start.hour
					startMinute = start.minute
					endHour = end.hour
					endMinute = end.minute
				},
				onDone = { /* no-op */ }
			)
		}

		DialogButton(
			text = stringResource(R.string.event_form_show_more_options) + (if (showMoreOptions) " ▲" else " ▼"),
			textColor = MaterialTheme.colorScheme.onTertiary,
			backgroundColor = MaterialTheme.colorScheme.tertiary,
			selectedTextColor = MaterialTheme.colorScheme.tertiary,
			selectedBackground = MaterialTheme.colorScheme.onTertiary,
			onClick = { showMoreOptions = !showMoreOptions }
		)

		if (showMoreOptions) {
			FormTextField(
				value = event.description,
				titleResId = R.string.event_form_description,
				onValueChange = { onChange(event.copy(description = it)) },
				singleLine = false,
				outlineColor = highlightColor,
				cursorColor = textColor
			)

			FormTextField(
				value = event.address ?: "",
				onValueChange = { onChange(event.copy(address = it)) },
				titleResId = R.string.event_form_address,
				singleLine = true,
				outlineColor = highlightColor,
				cursorColor = textColor
			)
		}

		Row(horizontalArrangement = Arrangement.spacedBy(Dimens.FORM_INPUT_SPACING)) {
			DialogButton(
				modifier = Modifier.weight(1f),
				textResId = R.string.event_form_cancel,
				textColor = MaterialTheme.colorScheme.onTertiary,
				backgroundColor = MaterialTheme.colorScheme.tertiary,
				selectedTextColor = MaterialTheme.colorScheme.tertiary,
				selectedBackground = MaterialTheme.colorScheme.onTertiary,
				onClick = onCancel
			)

			DialogButton(
				modifier = Modifier.weight(1f),
				enabled = event.title.isNotBlank(),
				disabledAlpha = DISABLED_ALPHA,
				textResId = R.string.event_form_save,
				textColor = MaterialTheme.colorScheme.onPrimary,
				backgroundColor = MaterialTheme.colorScheme.primary,
				selectedTextColor = MaterialTheme.colorScheme.primary,
				selectedBackground = MaterialTheme.colorScheme.onPrimary,
				onClick = { if (event.title.isNotBlank()) onSave() }
			)
		}

		// JakeType keyboard covers the bottom of the form, when a text field is focused, so add some
		// padding to avoid it.
		Box(modifier = Modifier.fillMaxWidth().height(Dimens.JAKE_TYPE_PADDING))
	}
}
