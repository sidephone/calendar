package com.sidephone.calendar.ui.screens


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.sidephone.calendar.core.Calendar
import com.sidephone.calendar.input.Keypad
import com.sidephone.calendar.ui.calendar.CalendarPanel
import com.sidephone.calendar.ui.sidebar.Sidebar
import com.sidephone.calendar.ui.topbar.Topbar


@Composable
fun ScreenMain(modifier: Modifier, calendar: Calendar, keypadTile: Keypad.Layout, onHelp: () -> Unit) {
	var isSidebarVisible by remember { mutableStateOf(false) }

	Column(modifier = modifier.fillMaxSize()) {
		Topbar(calendar = calendar, keypadTile = keypadTile, onHelp = onHelp, onToggleSidebar = { isSidebarVisible = !isSidebarVisible })

		Row(modifier = Modifier
			.weight(1f)
			.fillMaxWidth()) {
			if (isSidebarVisible) Sidebar(calendar = calendar)
			CalendarPanel(modifier = Modifier.weight(1f), calendar = calendar)
		}
	}
}
