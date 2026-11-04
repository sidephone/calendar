package com.sidephone.calendar.ui.screens.event.form.validation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sidephone.calendar.R
import com.sidephone.calendar.ui.components.popup.CalendarPopup

@Composable
fun PopupInvalidForm(
	validationStatus: Triple<SaveValidationStatus, String, Int>,
	onDismiss: (Triple<SaveValidationStatus, String, Int>) -> Unit
) {

	when (validationStatus.first) {
		SaveValidationStatus.EVENT_IN_THE_PAST -> {
			CalendarPopup(
				titleResId = R.string.error,
				body = stringResource(R.string.event_form_error_event_in_the_past, validationStatus.second),
				cancelButtonResId = android.R.string.ok,
				onDismiss = { onDismiss(Triple(SaveValidationStatus.OK, "", 0)) }
			)
		}

		SaveValidationStatus.REMINDER_IN_THE_PAST -> {
			CalendarPopup(
				titleResId = R.string.error,
				body = stringResource(
					R.string.event_form_error_reminder_in_the_past,
					validationStatus.second + " " + stringResource(validationStatus.third)
				),
				cancelButtonResId = android.R.string.ok,
				onDismiss = { onDismiss(Triple(SaveValidationStatus.OK, "", 0)) }
			)
		}

		else -> {}
	}
}
