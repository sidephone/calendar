package com.sidephone.calendar.ui.components


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun IconButton(modifier: Modifier = Modifier, accessibilityTitle: String, title: String = "", textIcon: String = "", imageIconResId: Int = 0, color: Color, onClick: () -> Unit) {
	Row(
		modifier = modifier,
		verticalAlignment = Alignment.CenterVertically
	) {
		IconButton(
			modifier = Modifier
				.focusProperties { canFocus = false }
				.padding(horizontal = 0.dp)
				.semantics { contentDescription = accessibilityTitle },
			onClick = { onClick() },
			colors = IconButtonDefaults.iconButtonColors(contentColor = color)
		) {
			if (imageIconResId != 0) {
				Icon(painterResource(id = imageIconResId), contentDescription = null)
			} else if (textIcon.isNotEmpty()) {
				Text(textIcon, color = color, style = MaterialTheme.typography.titleMedium)
			}
		}
		if (title.isNotEmpty()) Text(title, color = color, style = MaterialTheme.typography.labelLarge)
	}
}
