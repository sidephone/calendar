package com.sidephone.calendar.ui.screens.event.form

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun FormTimeField(
	@StringRes titleResId: Int,
	hour: Int,
	minute: Int,
	onTimeChange: (hour: Int, minute: Int) -> Unit,
	modifier: Modifier = Modifier,
	onDone: () -> Unit = {},
) {
	val hoursFocus = remember { FocusRequester() }
	val minutesFocus = remember { FocusRequester() }

	Row(
		modifier = modifier,
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(12.dp),
	) {
		// Title
		Text(
			text = stringResource(titleResId),
			style = MaterialTheme.typography.bodyLarge,
			color = MaterialTheme.colorScheme.onSurface,
		)

		// Keep HH:MM in reading order (and D-pad left/right consistent) in RTL.
		CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(8.dp),
			) {
				DigitField(
					number = hour,
					max = 23,
					maxFirstDigit = 2,
					focusRequester = hoursFocus,
					onCommit = { onTimeChange(it, minute) },
					onPrev = null,
					onNext = { minutesFocus.requestFocus() },
					onConfirm = { minutesFocus.requestFocus() },
				)
				Text(":", fontSize = 32.sp, color = MaterialTheme.colorScheme.onSurface)
				DigitField(
					number = minute,
					max = 59,
					maxFirstDigit = 5,
					focusRequester = minutesFocus,
					onCommit = { onTimeChange(hour, it) },
					onPrev = { hoursFocus.requestFocus() },
					onNext = null,
					onConfirm = onDone,
					onFilled = onDone,
				)
			}
		}
	}
}
