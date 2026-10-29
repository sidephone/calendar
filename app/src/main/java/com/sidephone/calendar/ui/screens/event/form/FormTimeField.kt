package com.sidephone.calendar.ui.screens.event.form

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sidephone.calendar.ui.components.timeField.TimeField
import com.sidephone.calendar.ui.theme.Dimens

@Composable
fun FormTimeField(
	@StringRes titleResId: Int,
	hour: Int,
	minute: Int,
	onTimeChange: (hour: Int, minute: Int) -> Unit,
	onDone: () -> Unit = {},
) {
	TimeField(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = Dimens.FORM_INPUT_SPACING),
		titleResId = titleResId,
		hour = hour,
		minute = minute,
		labelTextColor = MaterialTheme.colorScheme.onBackground,
		inputTextColor = MaterialTheme.colorScheme.onSurface,
		inputBackgroundColor = MaterialTheme.colorScheme.surface,
		borderColor = MaterialTheme.colorScheme.outline,
		selectedBorderColor = MaterialTheme.colorScheme.primary,
		borderRadius = Dimens.FORM_INPUT_BORDER_RADIUS,
		inputSpacing = Dimens.FORM_INPUT_SPACING,
		inputWidth = Dimens.FORM_TIME_FIELD_WIDTH,
		inputHeight = Dimens.FORM_TIME_FIELD_HEIGHT,
		onTimeChange = onTimeChange,
		onDone = onDone
	)
}
