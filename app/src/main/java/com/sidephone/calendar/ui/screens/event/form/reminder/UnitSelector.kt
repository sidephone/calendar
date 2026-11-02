package com.sidephone.calendar.ui.screens.event.form.reminder

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sidephone.calendar.ui.theme.Dimens


@Composable
internal fun UnitSelector(
	unit: ReminderUnit,
	focusRequester: FocusRequester,
	onPrev: () -> Unit,
	onSelect: (ReminderUnit) -> Unit,
) {
	var expanded by remember { mutableStateOf(false) }
	var focused by remember { mutableStateOf(false) }
	val selectedItem = remember { FocusRequester() }
	val shape = RoundedCornerShape(Dimens.FORM_INPUT_BORDER_RADIUS)

	// Put the D-pad cursor on the current unit when the menu opens
	LaunchedEffect(expanded) {
		if (expanded) {
			withFrameNanos { }
			runCatching { selectedItem.requestFocus() }
		}
	}

	Box {
		Row(
			modifier = Modifier
				.height(Dimens.FORM_TIME_FIELD_HEIGHT)
				.background(MaterialTheme.colorScheme.surface, shape)
				.border(
					width = 1.dp,
					color = if (focused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
					shape = shape
				)
				.focusRequester(focusRequester)
				.onFocusChanged { focused = it.isFocused }
				.onPreviewKeyEvent { e ->
					if (e.type == KeyEventType.KeyDown && e.key == Key.DirectionLeft) { onPrev(); true }
					else false
				}
				.clickable { expanded = true }   // also handles OK / Enter / DPAD_CENTER
				.padding(horizontal = Dimens.FORM_INPUT_SPACING),
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(
				text = stringResource(unit.label),
				color = MaterialTheme.colorScheme.onBackground,
			)
			Text(text = " ▾", color = MaterialTheme.colorScheme.onBackground)
		}

		DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
			for (u in ReminderUnit.entries) {
				DropdownMenuItem(
					modifier = if (u == unit) Modifier.focusRequester(selectedItem) else Modifier,
					text = { Text(stringResource(u.label)) },
					onClick = {
						expanded = false
						onSelect(u)
					},
				)
			}
		}
	}
}
