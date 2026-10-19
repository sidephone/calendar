package com.sidephone.calendar.ui.sidebar

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sidephone.calendar.R
import com.sidephone.calendar.core.Calendar
import com.sidephone.calendar.ui.components.Button

@Composable
fun ButtonZoomIn(calendar: Calendar) {
	Button(
		accessibilityTitle = stringResource(id = R.string.action_zoom_in),
		imageIconResId = R.drawable.ic_zoom_in,
		onClick = { calendar.onAction(com.sidephone.calendar.core.Action.ZOOM_IN) }
	)
}
