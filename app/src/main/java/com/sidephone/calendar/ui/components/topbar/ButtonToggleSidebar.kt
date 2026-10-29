package com.sidephone.calendar.ui.components.topbar

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sidephone.calendar.R
import com.sidephone.calendar.ui.components.IconButton

@Composable
fun ButtonToggleSidebar(onToggleSidebar: () -> Unit) {
	IconButton(
		modifier = Modifier,
		onClick = onToggleSidebar,
		imageIconResId = R.drawable.ic_menu,
		color = MaterialTheme.colorScheme.onBackground,
		accessibilityTitle = stringResource(R.string.action_toggle_sidebar)
	)
}
