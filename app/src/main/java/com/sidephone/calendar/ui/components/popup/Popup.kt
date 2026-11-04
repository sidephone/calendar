package com.sidephone.calendar.ui.components.popup

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource

@Composable
fun Popup(
	titleResId: Int,
	bodyResId: Int,
	body: String = "",
	confirmButtonResId: Int = 0,
	cancelButtonResId: Int = android.R.string.cancel,
	titleColor: Color,
	bodyColor: Color,
	backgroundColor: Color,
	buttonTextColor: Color,
	buttonSelectedTextColor: Color,
	buttonSelectedBackground: Color,
	onConfirm: (() -> Unit)? = null,
	onDismiss: () -> Unit,
) {
	AlertDialog(
		onDismissRequest = onDismiss,
		title = { Text(
			stringResource(titleResId),
			color = titleColor
		)},
		text = { Text(
			text = body.ifBlank { stringResource(bodyResId) },
			color = bodyColor
		)},
		containerColor = backgroundColor,
		confirmButton = {
			if (onConfirm != null) {
				PopupButton(
					textResId = confirmButtonResId,
					textColor = buttonTextColor,
					selectedTextColor = buttonSelectedTextColor,
					selectedBackground = buttonSelectedBackground,
					onClick = {
						onConfirm()
						onDismiss()
					}
				)
			}
		},
		dismissButton = {
			PopupButton(
				textResId = cancelButtonResId,
				textColor = buttonTextColor,
				selectedTextColor = buttonSelectedTextColor,
				selectedBackground = buttonSelectedBackground,
				onClick = onDismiss
			)
		}
	)
}
