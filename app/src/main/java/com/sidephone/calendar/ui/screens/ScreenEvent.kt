package com.sidephone.calendar.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.sidephone.calendar.core.Calendar
import com.sidephone.calendar.db.Event
import com.sidephone.calendar.ui.event.EventForm

@Composable
fun ScreenEvent(modifier: Modifier, calendar: Calendar, eventId: Long, onBack: () -> Unit) {
	BackHandler(enabled = true) { onBack() }

	// New event: a blank draft exists immediately. Edit: starts empty until loaded.
	var draft by remember(eventId) {
		val today = java.time.LocalDate.now()
		mutableStateOf(if (eventId == 0L) Event(title = "", startDate = today) else null)
	}

	// Only runs for edits: fetch the saved event once and use it as the draft.
	if (eventId != 0L) {
		LaunchedEffect(eventId) {
			draft = calendar.getEvent(eventId)
			if (draft == null) onBack()      // should never happen; just leave
		}
	}

	draft?.let { event ->
		EventForm(
			modifier = modifier,
			event = event,
			onChange = { draft = it },
			onSave = { calendar.saveEvent(event); onBack() },
		)
	}
}
