package com.sidephone.calendar.ui.screens.event.form

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sidephone.calendar.ui.components.Switch
import com.sidephone.calendar.ui.theme.DISABLED_ALPHA
import com.sidephone.calendar.ui.theme.Dimens

@Composable
fun FormSwitch(
	modifier: Modifier = Modifier,
	title: String,
	value: Boolean,
	disabled: Boolean,
	onValueChange: (Boolean) -> Unit
) {
	Switch(
		modifier.fillMaxWidth(),
		title = title,
		value = value,
		disabled = disabled,
		titleColor = MaterialTheme.colorScheme.onBackground,
		summaryColor = MaterialTheme.colorScheme.onSecondaryContainer,
		thumbUncheckedColor = MaterialTheme.colorScheme.onTertiary,
		trackUncheckedColor = MaterialTheme.colorScheme.tertiary,
		trackCheckedColor = MaterialTheme.colorScheme.primary,
		trackCheckedSelectedColor = MaterialTheme.colorScheme.onPrimaryContainer,
		thumbCheckedColor = MaterialTheme.colorScheme.onPrimary,
		thumbCheckedSelectedColor = MaterialTheme.colorScheme.primaryContainer,
		disabledAlpha = DISABLED_ALPHA,
		paddingText = Dimens.FORM_SWITCH_PADDING,
		paddingSwitch = Dimens.FORM_SWITCH_PADDING,
		onValueChange = onValueChange
	)
}
