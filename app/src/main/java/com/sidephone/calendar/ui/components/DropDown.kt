package com.sidephone.calendar.ui.components

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
import androidx.compose.material3.MenuItemColors
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sidephone.calendar.ui.theme.Dimens


interface DropDownEntry {
	val label: Int
}


@Composable
fun <T : DropDownEntry> DropDown(
	modifier: Modifier = Modifier,
	entries: List<T>,
	current: T,
	focusRequester: FocusRequester,
	textColor: Color,
	backgroundColor: Color,
	borderColor: Color,
	highlightedBorderColor: Color,
	labelSpacing: Dp,
	onPrev: () -> Unit,
	onNext: () -> Unit,
	onSelect: (T) -> Unit,
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
			modifier = modifier
				.height(Dimens.FORM_TIME_FIELD_HEIGHT)
				.background(backgroundColor, shape)
				.border(
					width = 1.dp,
					color = if (focused) highlightedBorderColor else borderColor,
					shape = shape
				)
				.focusRequester(focusRequester)
				.onFocusChanged { focused = it.isFocused }
				.onPreviewKeyEvent { e ->
					if (e.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

					when (e.key) {
						Key.DirectionLeft, Key.MediaPrevious -> { onPrev(); true }
						Key.DirectionRight, Key.MediaNext -> { onNext(); true }
						else -> false
					}
				}
				.clickable { expanded = true }
				.keypadClickable(onClick = { expanded = true })
				.padding(horizontal = labelSpacing),
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(text = stringResource(current.label), color = textColor)
			Text(text = " ▾", color = textColor)
		}

		DropdownMenu(
			expanded = expanded,
			onDismissRequest = { expanded = false },
			containerColor = backgroundColor,
		) {
			for (u in entries) {
				DropdownMenuItem(
					modifier =
						if (u == current) Modifier
							.focusRequester(selectedItem)
							.keypadClickable(onClick = { if (expanded) onSelect(u); expanded = false })
						else Modifier
							.keypadClickable(onClick = { if (expanded) onSelect(u); expanded = false }),
					colors = MenuItemColors(
						textColor = textColor,
						leadingIconColor = textColor,
						trailingIconColor = textColor,
						disabledTextColor = borderColor,
						disabledLeadingIconColor = borderColor,
						disabledTrailingIconColor = borderColor,
					),
					text = { Text(stringResource(u.label)) },
					onClick = {
						if (expanded) onSelect(u)
						expanded = false
					},
				)
			}
		}
	}
}
