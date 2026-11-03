package com.sidephone.calendar.ui.components.popup

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable


@Composable
fun CalendarPopup(
	titleResId: Int,
	bodyResId: Int = 0,
	body: String = "",
	confirmButtonResId: Int = 0,
	cancelButtonResId: Int = android.R.string.cancel,
	onConfirm: (() -> Unit)? = null,
	onDismiss: () -> Unit
) {
	Popup(
		titleResId = titleResId,
		bodyResId = bodyResId,
		body = body,
		confirmButtonResId = confirmButtonResId,
		cancelButtonResId = cancelButtonResId,
		titleColor = MaterialTheme.colorScheme.onSurface,
		bodyColor = MaterialTheme.colorScheme.onSecondaryContainer,
		backgroundColor = MaterialTheme.colorScheme.surface,
		buttonTextColor = MaterialTheme.colorScheme.onSurface,
		buttonSelectedTextColor = MaterialTheme.colorScheme.onPrimary,
		buttonSelectedBackground = MaterialTheme.colorScheme.primary,
		onConfirm = onConfirm,
		onDismiss = onDismiss
	)
}
