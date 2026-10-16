package com.sidephone.calendar.ui.sidebar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sidephone.calendar.core.Calendar
import com.sidephone.calendar.input.Keypad

@Composable
fun Sidebar(calendar: Calendar) {
	val noKeypadTile = calendar.tile.collectAsState().value == Keypad.Layout.NONE

	Column(
		modifier = Modifier.width(64.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		if (noKeypadTile) {
			ButtonZoomIn(Modifier, calendar)
			ButtonZoomOut(Modifier, calendar)
		}
	}
}
