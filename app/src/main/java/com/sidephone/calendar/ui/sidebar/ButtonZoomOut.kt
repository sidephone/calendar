package com.sidephone.calendar.ui.sidebar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sidephone.calendar.R
import com.sidephone.calendar.core.Action
import com.sidephone.calendar.core.Calendar
import com.sidephone.calendar.ui.components.Button

@Composable
fun ButtonZoomOut(modifier: Modifier = Modifier, calendar: Calendar) {
	Button(
		modifier = modifier,
		accessibilityTitle = stringResource(id = R.string.action_zoom_out),
		imageIconResId = R.drawable.ic_zoom_out,
		onClick = { calendar.onAction(Action.ZOOM_OUT) }
	)
}
