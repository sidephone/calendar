package com.sidephone.calendar.ui.sidebar

import android.content.Context
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sidephone.calendar.R
import com.sidephone.calendar.core.Calendar
import com.sidephone.calendar.input.Keypad
import com.sidephone.calendar.ui.components.Button
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ButtonOverrideTile(
	calendar: Calendar,
	snackbarProps: Triple<SnackbarHostState, CoroutineScope, Context>,
	layoutOverride: Keypad.Layout,
) {
	var title: Int
	var iconResId: Int

	when (layoutOverride) {
		Keypad.Layout.SUNDIAL -> {
			iconResId = R.drawable.ic_input_sundial
			title = R.string.layout_sundial
		}
		Keypad.Layout.COMPACT_QWERTY,
		Keypad.Layout.GAMEPAD,
		Keypad.Layout.T9,
		Keypad.Layout.T9_NO_DPAD -> {
			iconResId = R.drawable.ic_input_keyboard
			title = R.string.layout_default
		}
		Keypad.Layout.NONE -> {
			iconResId = R.drawable.ic_input_touch
			title = R.string.layout_none
		}
		else -> {
			iconResId = R.drawable.ic_input_auto
			title = R.string.layout_auto
		}
	}

	Button(
		accessibilityTitle = stringResource(id = title),
		imageIconResId = iconResId,
		onClick = {
			snackbarProps.second.launch {
				snackbarProps.first.currentSnackbarData?.dismiss()
				val message = snackbarProps.third.getString(overrideMessageRes(calendar.nextTile()))
				snackbarProps.first.showSnackbar(message = message, duration = SnackbarDuration.Short)
			}
		}
	)
}

fun overrideMessageRes(override: Keypad.Layout): Int {
	return when (override) {
		Keypad.Layout.SUNDIAL -> R.string.layout_sundial
		Keypad.Layout.COMPACT_QWERTY,
		Keypad.Layout.GAMEPAD,
		Keypad.Layout.T9,
		Keypad.Layout.T9_NO_DPAD -> R.string.layout_default
		Keypad.Layout.NONE -> R.string.layout_none
		else -> R.string.layout_auto
	}
}
