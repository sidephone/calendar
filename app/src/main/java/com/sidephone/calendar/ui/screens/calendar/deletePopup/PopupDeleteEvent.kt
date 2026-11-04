package com.sidephone.calendar.ui.screens.calendar.deletePopup

import androidx.compose.runtime.Composable
import com.sidephone.calendar.R
import com.sidephone.calendar.core.Action
import com.sidephone.calendar.core.Calendar
import com.sidephone.calendar.ui.components.popup.CalendarPopup


@Composable
fun PopupDeleteEvent(calendar: Calendar, onDismiss: () -> Unit) {
	CalendarPopup(
		titleResId = R.string.event_delete_title,
		bodyResId = R.string.event_delete_question,
		confirmButtonResId = R.string.event_delete_delete,
		onConfirm = { calendar.onAction(Action.DELETE) },
		onDismiss = onDismiss
	)
}
