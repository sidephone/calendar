package com.sidephone.calendar.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import android.view.KeyEvent as AndroidKeyEvent

fun Modifier.keypadClickableModifier(onClick: () -> Unit): Modifier =
	this.onKeyEvent { event: KeyEvent ->
		if (
			event.type == KeyEventType.KeyDown
			&& (
				event.nativeKeyEvent.keyCode == AndroidKeyEvent.KEYCODE_BUTTON_START
				|| event.nativeKeyEvent.keyCode == AndroidKeyEvent.KEYCODE_DPAD_CENTER
				|| event.nativeKeyEvent.keyCode == AndroidKeyEvent.KEYCODE_ENTER
				|| event.nativeKeyEvent.keyCode == AndroidKeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
			)
		) {
			onClick()
			true // consume the event
		} else {
			false
		}
	}
