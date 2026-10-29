package com.sidephone.calendar.ui.components.popup

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource


private val ENTER_KEYS: Set<Key> = setOf(
	Key.Enter,
	Key.NumPadEnter,
	Key.DirectionCenter, // D-Pad Center
	Key.ButtonStart, // Gamepad Start
	Key.MediaPlayPause, // Sundial Center
)

private val PREV_KEYS = setOf(
	Key.DirectionLeft,
	Key.D, // Compact-QWERTY Left
	Key.MediaPrevious // Sundial Left
)

private val NEXT_KEYS = setOf(
	Key.DirectionRight,
	Key.J, // Compact-QWERTY Right
	Key.MediaNext // Sundial Right
)

@Composable
fun DialogButton(
	modifier: Modifier = Modifier,
	textResId: Int = 0,
	text: String = "",
	textColor: Color,
	backgroundColor: Color = Color.Transparent,
	selectedTextColor: Color,
	selectedBackground: Color,
	onClick: () -> Unit,
) {
	val focusManager = LocalFocusManager.current
	val interactionSource = remember { MutableInteractionSource() }
	val isFocused by interactionSource.collectIsFocusedAsState()

	TextButton(
		onClick = onClick,
		interactionSource = interactionSource,
		colors = ButtonDefaults.textButtonColors(
			containerColor = if (isFocused) selectedBackground else backgroundColor,
			contentColor = if (isFocused) selectedTextColor else textColor,
		),
		modifier = modifier.onKeyEvent { event ->
			if (event.type != KeyEventType.KeyDown || event.nativeKeyEvent.repeatCount != 0) return@onKeyEvent false
			when (event.key) {
				in ENTER_KEYS -> { onClick(); true }
				in NEXT_KEYS -> focusManager.moveFocus(FocusDirection.Next)
				in PREV_KEYS -> focusManager.moveFocus(FocusDirection.Previous)
				else -> false
			}
		},
	) {
		Text(if (textResId == 0) text else stringResource(textResId))
	}
}
