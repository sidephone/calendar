package com.sidephone.calendar.ui.screens.event

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sidephone.calendar.R
import com.sidephone.calendar.core.Calendar
import com.sidephone.calendar.db.Event
import com.sidephone.calendar.ui.screens.event.form.EventForm
import java.time.LocalDate

@Composable
fun ScreenEvent(
	modifier: Modifier,
	calendar: Calendar,
	eventId: Long,
	onBack: () -> Unit
) {
	BackHandler(enabled = true) { onBack() }

	// New event: a blank draft exists immediately. Edit: starts empty until loaded.
	var draft by remember(eventId) {
		val selectedDate = calendar.date.value.let { (year, month, day) -> LocalDate.of(year, month, day) }
		mutableStateOf(if (eventId == 0L) Event(title = "", startDate = selectedDate) else null)
	}

	// Only runs for edits: fetch the saved event once and use it as the draft.
	if (eventId != 0L) {
		LaunchedEffect(eventId) {
			draft = calendar.getEvent(eventId)
			if (draft == null) onBack()      // should never happen; just leave
		}
	}

	Column(modifier = modifier.fillMaxSize()) {
		Text(
			modifier = Modifier,
			text = stringResource(
				if (eventId == 0L) R.string.event_form_title_add
				else R.string.event_form_title_edit
			)
		)

		draft?.let { event ->
			EventForm(
				modifier = Modifier,
				event = event,
				onChange = { draft = it },
				onSave = { calendar.saveEvent(event); onBack() },
				onCancel = { onBack() },
			)
		}
	}
}
