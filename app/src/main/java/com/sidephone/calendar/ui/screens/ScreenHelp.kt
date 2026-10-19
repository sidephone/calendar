package com.sidephone.calendar.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.sidephone.calendar.R
import com.sidephone.calendar.input.Keypad

@Composable
fun ScreenHelp(modifier: Modifier, keypadTile: Keypad.Layout, onBack: () -> Unit) {
	BackHandler(enabled = true) { onBack() }

	var content: String
	var title: String

	when (keypadTile) {
		Keypad.Layout.COMPACT_QWERTY -> {
			content = stringResource(id = R.string.help_content_compact_qwerty)
			title = stringResource(id = R.string.help_title_compact_qwerty)
		}
		Keypad.Layout.GAMEPAD -> {
			content = stringResource(id = R.string.help_content_gamepad)
			title = stringResource(id = R.string.help_title_gamepad)
		}
		Keypad.Layout.SUNDIAL -> {
			content = stringResource(id = R.string.help_content_sundial)
			title = stringResource(id = R.string.help_title_sundial)
		}
		Keypad.Layout.T9,
		Keypad.Layout.T9_NO_DPAD -> {
			content = stringResource(id = R.string.help_content_t9)
			title = stringResource(id = R.string.help_title_t9)
		}
		else -> {
			content = ""
			title = ""
		}
	}


	Column(modifier = modifier.fillMaxWidth()) {
		Text(
			modifier = Modifier.fillMaxWidth(),
			text = title,
			style = MaterialTheme.typography.headlineLarge,
			fontWeight = FontWeight.Bold,
			textAlign = TextAlign.Center,
			color = MaterialTheme.colorScheme.onBackground
		)
		Text(
			text = content,
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onBackground
		)
	}
}
