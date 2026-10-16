package com.sidephone.calendar.ui.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sidephone.calendar.R
import com.sidephone.calendar.core.Calendar

@Composable
fun CalendarPanel(modifier: Modifier = Modifier, calendar: Calendar) {
	val txt = stringResource(R.string.app_name) +
		"\nactive tile: ${calendar.tile.collectAsState().value}" +
		"\noverride tile: ${calendar.tileOverride.collectAsState().value}" +
		"\nstate: ${calendar.debugText.collectAsState().value}" +
		"\nevents: ${calendar.events.collectAsState().value}"

	Column(modifier = modifier.fillMaxSize()) {
		Text(text = txt)
	}
}
