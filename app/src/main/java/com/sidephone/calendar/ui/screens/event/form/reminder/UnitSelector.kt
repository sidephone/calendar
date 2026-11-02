package com.sidephone.calendar.ui.screens.event.form.reminder

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import com.sidephone.calendar.ui.components.DropDown
import com.sidephone.calendar.ui.theme.Dimens


@Composable
internal fun UnitSelector(
	modifier: Modifier = Modifier,
	unit: ReminderUnit,
	focusRequester: FocusRequester,
	onPrev: () -> Unit,
	onNext: () -> Unit,
	onSelect: (ReminderUnit) -> Unit,
) {
	DropDown(
		modifier = modifier,
		current = unit,
		entries = ReminderUnit.entries,
		textColor = MaterialTheme.colorScheme.onSurface,
		backgroundColor = MaterialTheme.colorScheme.surface,
		borderColor = MaterialTheme.colorScheme.outline,
		highlightedBorderColor = MaterialTheme.colorScheme.primary,
		labelSpacing = Dimens.FORM_INPUT_SPACING,
		focusRequester = focusRequester,
		onPrev = onPrev,
		onNext = onNext,
		onSelect = onSelect
	)
}
