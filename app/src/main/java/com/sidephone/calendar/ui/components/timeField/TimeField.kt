package com.sidephone.calendar.ui.components.timeField

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection


@Composable
fun TimeField(
	modifier: Modifier = Modifier,
	@StringRes titleResId: Int,
	hour: Int,
	minute: Int,
	labelTextColor: Color,
	inputTextColor: Color,
	inputBackgroundColor: Color,
	borderColor: Color,
	selectedBorderColor: Color,
	borderRadius: Dp,
	inputSpacing: Dp,
	inputWidth: Dp,
	inputHeight: Dp,
	onTimeChange: (hour: Int, minute: Int) -> Unit,
	onDone: () -> Unit = {},
) {
	val hoursFocus = remember { FocusRequester() }
	val minutesFocus = remember { FocusRequester() }

	Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
		// Title
		Text(
			modifier = Modifier.weight(1f),
			text = stringResource(titleResId),
			style = MaterialTheme.typography.titleMedium,
			color = labelTextColor,
		)

		// Keep HH:MM in reading order (and D-pad left/right consistent) in RTL.
		CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(inputSpacing),
			) {

				// hour
				DigitField(
					number = hour,
					max = 23,
					maxFirstDigit = 2,
					focusRequester = hoursFocus,
					textColor = inputTextColor,
					inputBackgroundColor = inputBackgroundColor,
					borderColor = borderColor,
					selectedBorderColor = selectedBorderColor,
					borderRadius = borderRadius,
					width = inputWidth,
					height = inputHeight,
					onCommit = { onTimeChange(it, minute) },
					onPrev = null,
					onNext = { minutesFocus.requestFocus() },
					onConfirm = { minutesFocus.requestFocus() },
				)

				// separator
				Text(":", color = labelTextColor)

				// minute
				DigitField(
					number = minute,
					max = 59,
					maxFirstDigit = 5,
					focusRequester = minutesFocus,
					textColor = inputTextColor,
					inputBackgroundColor = inputBackgroundColor,
					borderColor = borderColor,
					selectedBorderColor = selectedBorderColor,
					borderRadius = borderRadius,
					width = inputWidth,
					height = inputHeight,
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
