package com.sidephone.calendar.ui.screens.main.deletePopup

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sidephone.calendar.R
import com.sidephone.calendar.core.Calendar


@Composable
fun PopupDeleteEvent(calendar: Calendar, onDismiss: () -> Unit) {
	val buttonTextColor = MaterialTheme.colorScheme.primary
	val buttonSelectedTextColor = MaterialTheme.colorScheme.onPrimary
	val buttonSelectedBackground = MaterialTheme.colorScheme.primary

	AlertDialog(
		onDismissRequest = onDismiss,
		title = { Text(stringResource(R.string.event_delete_title)) },
		text = { Text(stringResource(R.string.event_delete_question)) },
		confirmButton = {
			DialogButton(
				textResId = R.string.event_delete_delete,
				textColor = buttonTextColor,
				selectedTextColor = buttonSelectedTextColor,
				selectedBackground = buttonSelectedBackground,
				onClick = {
					calendar.deleteEvent()
					onDismiss()
				}
			)
		},
		dismissButton = {
			DialogButton(
				textResId = android.R.string.cancel,
				textColor = buttonTextColor,
				selectedTextColor = buttonSelectedTextColor,
				selectedBackground = buttonSelectedBackground,
				onClick = onDismiss
			)
		}
	)
}
