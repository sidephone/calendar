package com.sidephone.calendar.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import android.view.KeyEvent as AndroidKeyEvent

private val ACTION_KEYS = listOf(
	AndroidKeyEvent.KEYCODE_BUTTON_START,
	AndroidKeyEvent.KEYCODE_DPAD_CENTER,
	AndroidKeyEvent.KEYCODE_ENTER,
	AndroidKeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
)

fun Modifier.keypadClickable(onClick: () -> Unit): Modifier =
	this.onKeyEvent { event: KeyEvent ->
		if (ACTION_KEYS.contains(event.nativeKeyEvent.keyCode)) {
			if (event.type == KeyEventType.KeyDown) onClick()
			true // consume the event
		} else {
			false
		}
	}
