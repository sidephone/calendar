package com.sidephone.calendar.ui.screens.event.form

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction


@Composable
fun FormTextField(
	modifier: Modifier,
	titleResId: Int,
	value: String,
	onValueChange: (String) -> Unit,
	singleLine: Boolean
) {
	OutlinedTextField(
		modifier = modifier.dpadFocusNavigation(),
		keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
		value = value,
		onValueChange = onValueChange,
		label = { Text(stringResource(titleResId)) },
		singleLine = singleLine,
	)
}


@Composable
fun Modifier.dpadFocusNavigation(): Modifier {
	val focus = LocalFocusManager.current

	return onPreviewKeyEvent { e ->
		if (e.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

		when (e.key) {
			Key.DirectionUp -> {
				focus.moveFocus(FocusDirection.Up)
				true
			}

			Key.DirectionDown -> {
				focus.moveFocus(FocusDirection.Down)
				true
			}

			else -> false
		}
	}
}
