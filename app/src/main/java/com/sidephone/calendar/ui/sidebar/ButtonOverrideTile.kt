package com.sidephone.calendar.ui.sidebar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.sidephone.calendar.R
import com.sidephone.calendar.input.Keypad

@Composable
fun ButtonOverrideTile(modifier: Modifier = Modifier, layoutOverride: Keypad.Layout, onChange: () -> Unit) {
	val title = when (layoutOverride) {
		Keypad.Layout.SUNDIAL -> stringResource(id = R.string.layout_sundial)
		Keypad.Layout.COMPACT_QWERTY,
		Keypad.Layout.GAMEPAD,
		Keypad.Layout.T9,
		Keypad.Layout.T9_NO_DPAD -> stringResource(id = R.string.layout_default)
		Keypad.Layout.NONE -> stringResource(id = R.string.layout_none)
		else -> stringResource(id = R.string.layout_auto)
	}

	val accessibilityTitle = "TODO"

	Row(
		modifier = modifier,
		verticalAlignment = Alignment.CenterVertically
	) {
		IconButton(
			modifier = Modifier
				.focusProperties { canFocus = false }
				.padding(horizontal = 0.dp)
				.semantics { contentDescription = accessibilityTitle },
			onClick = { onChange() },
		) {
			Text("\u2328", style = MaterialTheme.typography.titleMedium)
		}
		Text(title, style = MaterialTheme.typography.labelLarge)
	}
}
