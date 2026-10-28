package com.sidephone.calendar.ui.components.sidebar

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sidephone.calendar.core.Calendar
import com.sidephone.calendar.input.Keypad
import kotlinx.coroutines.CoroutineScope

@Composable
fun Sidebar(
	calendar: Calendar,
	snackbarProps: Triple<SnackbarHostState, CoroutineScope, Context>,
	keypadOverride: Keypad.Layout,
	isTouchOn: Boolean,
) {
	Column(
		modifier = Modifier.width(64.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		if (isTouchOn) {
			ButtonZoomIn(calendar)
			ButtonZoomOut(calendar)
		}
		ButtonOverrideTile(calendar = calendar, snackbarProps = snackbarProps, layoutOverride = keypadOverride)
	}
}
