package com.sidephone.calendar.ui.components.sidebar

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sidephone.calendar.R
import com.sidephone.calendar.core.Action
import com.sidephone.calendar.core.Calendar
import com.sidephone.calendar.ui.components.IconButton

@Composable
fun ButtonZoomIn(calendar: Calendar) {
	IconButton(
		accessibilityTitle = stringResource(id = R.string.action_zoom_in),
		imageIconResId = R.drawable.ic_zoom_in,
		color = MaterialTheme.colorScheme.onBackground,
		onClick = { calendar.onAction(Action.ZOOM_IN) }
	)
}
