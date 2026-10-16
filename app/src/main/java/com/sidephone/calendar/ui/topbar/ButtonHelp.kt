package com.sidephone.calendar.ui.topbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sidephone.calendar.R
import com.sidephone.calendar.ui.components.Button

@Composable
fun ButtonHelp(onClick: () -> Unit) {
	Button(
		modifier = Modifier,
		onClick = onClick,
		imageIconResId = R.drawable.ic_help,
		accessibilityTitle = "Help"
	)
}
