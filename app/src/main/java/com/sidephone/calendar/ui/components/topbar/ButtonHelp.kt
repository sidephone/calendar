package com.sidephone.calendar.ui.components.topbar

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sidephone.calendar.R
import com.sidephone.calendar.ui.components.IconButton

@Composable
fun ButtonHelp(onClick: () -> Unit) {
	IconButton(
		modifier = Modifier,
		onClick = onClick,
		imageIconResId = R.drawable.ic_help,
		color = MaterialTheme.colorScheme.onBackground,
		accessibilityTitle = stringResource(R.string.action_show_help)
	)
}
