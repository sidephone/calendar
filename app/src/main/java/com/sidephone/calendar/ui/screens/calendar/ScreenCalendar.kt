package com.sidephone.calendar.ui.screens.calendar


import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.sidephone.calendar.core.Calendar
import com.sidephone.calendar.input.Keypad
import com.sidephone.calendar.ui.components.sidebar.Sidebar
import com.sidephone.calendar.ui.components.topbar.Topbar
import com.sidephone.calendar.ui.screens.calendar.calendar.CalendarPanel
import com.sidephone.calendar.ui.screens.calendar.deletePopup.PopupDeleteEvent
import kotlinx.coroutines.CoroutineScope


@Composable
fun ScreenCalendar(
	modifier: Modifier,
	snackbarProps: Triple<SnackbarHostState, CoroutineScope, Context>,
	calendar: Calendar,
	isTouchOn: Boolean,
	keypadOverride: Keypad.Layout,
	onHelp: () -> Unit,
	isDeleteEventShown: Boolean,
	onDeleteEventDismissed: () -> Unit,
) {
	var isSidebarVisible by remember { mutableStateOf(false) }

	Column(modifier = modifier.fillMaxSize()) {
		Topbar(calendar = calendar, noTouch = !isTouchOn, onHelp = onHelp, onToggleSidebar = { isSidebarVisible = !isSidebarVisible })

		Row(modifier = Modifier
			.weight(1f)
			.fillMaxWidth()) {
			if (isSidebarVisible) {
				Sidebar(
					calendar = calendar,
					snackbarProps = snackbarProps,
					keypadOverride = keypadOverride,
					isTouchOn = isTouchOn
				)
			}
			CalendarPanel(modifier = Modifier.weight(1f), calendar = calendar, touchEnabled = isTouchOn)
		}
	}

	if (isDeleteEventShown) {
		PopupDeleteEvent(
			calendar = calendar,
			onDismiss = onDeleteEventDismissed
		)
	}
}
