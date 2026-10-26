package com.sidephone.calendar.ui.event

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sidephone.calendar.R
import com.sidephone.calendar.db.Event
import com.sidephone.calendar.ui.components.TextField
import java.time.LocalTime

@Composable
fun EventForm(
	modifier: Modifier,
	event: Event,
	onChange: (Event) -> Unit,
	onSave: () -> Unit,
) {
	Column(modifier = modifier.fillMaxSize()) {
		TextField(
			value = event.title,
			titleResId = R.string.event_form_title,
			onValueChange = { onChange(event.copy(title = it)) },
			singleLine = true,
			modifier = Modifier.fillMaxWidth(),
		)

		TextField(
			value = event.description,
			titleResId = R.string.event_form_description,
			onValueChange = { onChange(event.copy(description = it)) },
			singleLine = false,
			modifier = Modifier.fillMaxWidth(),
		)

		TextField(
			value = event.address ?: "",
			onValueChange = { onChange(event.copy(address = it)) },
			titleResId = R.string.event_form_address,
			singleLine = true,
			modifier = Modifier.fillMaxWidth(),
		)

		Row(verticalAlignment = Alignment.CenterVertically) {
			Switch(
				checked = event.startTime == null,
				onCheckedChange = { allDay ->
					onChange(
						if (allDay) event.copy(startTime = null, endTime = null)
						else event.copy(startTime = LocalTime.of(9, 0))
					)
				}
			)
			Text(stringResource(R.string.event_form_all_day))
		}

		/* val startTime: LocalTime? = null,
    val endDate: LocalDate = startDate,
    val endTime: LocalTime? = null,
    val freq: EventRecurrence.Freq = EventRecurrence.Freq.NONE,
    val interval: Int = 1,                 // every N days/weeks/months/years
    val weekdays: Int = 0,                 // WEEKLY only: bit 0 = Mon ... bit 6 = Sun; 0 = weekday of startDate
    val untilDate: LocalDate? = null,      // last allowed START date of an occurrence
    val maxOccurrences: Int? = null,

		 */

		Button(onClick = onSave, enabled = event.title.isNotBlank()) {
			Text(stringResource(R.string.event_form_save))
		}
	}
}
