package com.sidephone.calendar.ui.event

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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
import com.sidephone.calendar.ui.components.FormSwitch
import com.sidephone.calendar.ui.components.FormTextField
import com.sidephone.calendar.ui.components.FormTimeField
import com.sidephone.calendar.ui.modifiers.keypadClickableButton
import java.time.LocalTime

@Composable
fun EventForm(
	modifier: Modifier,
	event: Event,
	onChange: (Event) -> Unit,
	onSave: () -> Unit,
	onCancel: () -> Unit
) {
	var isAllDay by remember { mutableStateOf(event.startTime == null) }
	var startHour by remember { mutableIntStateOf(event.startTime?.hour ?: 9) }
	var startMinute by remember { mutableIntStateOf(event.startTime?.minute ?: 0) }
	var endHour by remember { mutableIntStateOf(event.endTime?.hour ?: 10) }
	var endMinute by remember { mutableIntStateOf(event.endTime?.minute ?: 0) }

	Column(
		modifier = modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
	) {
		FormTextField(
			value = event.title,
			titleResId = R.string.event_form_title,
			onValueChange = { onChange(event.copy(title = it)) },
			singleLine = true,
			modifier = Modifier.fillMaxWidth(),
		)

		FormSwitch(
			titleResId = R.string.event_form_all_day,
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
				modifier = Modifier.fillMaxWidth(),
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
				modifier = Modifier.fillMaxWidth(),
				titleResId = R.string.event_form_end_time,
				hour = endHour,
				minute = endMinute,
				onTimeChange = { hour, minute ->
					var start = LocalTime.of(startHour, startMinute)
					var end = LocalTime.of(hour, minute)

					if (start.hour == 23 && end.isBefore(start)) {
						if (startMinute == 59) {
							start = LocalTime.of(23, 58)
						}
						end = start.plusMinutes(1)
					} else if (end.isBefore(start)) {
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

		FormTextField(
			value = event.description,
			titleResId = R.string.event_form_description,
			onValueChange = { onChange(event.copy(description = it)) },
			singleLine = false,
			modifier = Modifier.fillMaxWidth(),
		)

		FormTextField(
			value = event.address ?: "",
			onValueChange = { onChange(event.copy(address = it)) },
			titleResId = R.string.event_form_address,
			singleLine = true,
			modifier = Modifier.fillMaxWidth(),
		)

		Button(
			modifier = Modifier.keypadClickableButton(onClick = onCancel),
			onClick = onCancel,
		) {
			Text(stringResource(R.string.event_form_cancel))
		}

		Button(
			modifier = Modifier.keypadClickableButton(onClick = { if (event.title.isNotBlank()) onSave() }),
			onClick = onSave,
			enabled = event.title.isNotBlank()
		) {
			Text(stringResource(R.string.event_form_save))
		}
	}
}
