package com.sidephone.calendar.ui.screens.calendar.deletePopup

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.sidephone.calendar.R
import com.sidephone.calendar.core.Action
import com.sidephone.calendar.core.Calendar
import com.sidephone.calendar.ui.components.popup.Popup


@Composable
fun PopupDeleteEvent(calendar: Calendar, onDismiss: () -> Unit) {
	Popup(
		titleResId = R.string.event_delete_title,
		bodyResId = R.string.event_delete_question,
		confirmButtonResId = R.string.event_delete_delete,
		titleColor = MaterialTheme.colorScheme.onSurface,
		bodyColor = MaterialTheme.colorScheme.onSecondaryContainer,
		backgroundColor = MaterialTheme.colorScheme.surface,
		buttonTextColor = MaterialTheme.colorScheme.onSurface,
		buttonSelectedTextColor = MaterialTheme.colorScheme.onPrimary,
		buttonSelectedBackground = MaterialTheme.colorScheme.primary,
		onConfirm = {
			calendar.onAction(Action.DELETE)
			onDismiss()
		},
		onDismiss = onDismiss
	)
}
