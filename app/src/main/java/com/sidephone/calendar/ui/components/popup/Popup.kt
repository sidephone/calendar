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
	confirmButtonResId: Int,
	cancelButtonResId: Int = android.R.string.cancel,
	titleColor: Color,
	bodyColor: Color,
	backgroundColor: Color,
	buttonTextColor: Color,
	buttonSelectedTextColor: Color,
	buttonSelectedBackground: Color,
	onConfirm: () -> Unit,
	onDismiss: () -> Unit
) {
	AlertDialog(
		onDismissRequest = onDismiss,
		title = { Text(stringResource(titleResId), color = titleColor) },
		text = { Text(stringResource(bodyResId), color = bodyColor) },
		containerColor = backgroundColor,
		confirmButton = {
			DialogButton(
				textResId = confirmButtonResId,
				textColor = buttonTextColor,
				selectedTextColor = buttonSelectedTextColor,
				selectedBackground = buttonSelectedBackground,
				onClick = {
					onConfirm()
					onDismiss()
				}
			)
		},
		dismissButton = {
			DialogButton(
				textResId = cancelButtonResId,
				textColor = buttonTextColor,
				selectedTextColor = buttonSelectedTextColor,
				selectedBackground = buttonSelectedBackground,
				onClick = onDismiss
			)
		}
	)
}
