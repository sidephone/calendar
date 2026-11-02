package com.sidephone.calendar.ui.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun Switch(
	modifier: Modifier = Modifier,
	titleResId: Int = 0,
	title: String = "",
	summaryOnResId: Int = 0,
	summaryOffResId: Int = 0,
	value: Boolean,
	disabled: Boolean,
	titleColor: Color,
	summaryColor: Color,
	trackUncheckedColor: Color,
	thumbUncheckedColor: Color,
	trackCheckedColor: Color,
	thumbCheckedColor: Color,
	trackCheckedSelectedColor: Color,
	thumbCheckedSelectedColor: Color,
	disabledAlpha: Float,
	paddingText: Dp,
	paddingSwitch: Dp,
	onValueChange: (Boolean) -> Unit
) {
	var isChecked by remember { mutableStateOf(value) }
	val interactionSource = remember { MutableInteractionSource() }
	val isFocused by interactionSource.collectIsFocusedAsState()

	fun toggle(checked: Boolean) {
		isChecked = checked
		if (disabled) return
		onValueChange(checked)
	}

	Column(modifier = modifier) {
		Row(
			modifier = Modifier
				.clip(RoundedCornerShape(8.dp))
				.background(
					color = if (isFocused && !disabled) trackCheckedColor
					else Color.Transparent
				)
				.toggleable(
					value = isChecked,
					enabled = !disabled,
					interactionSource = interactionSource,
					indication = LocalIndication.current,
					onValueChange = { checked -> toggle(checked) }
				)
				.keypadClickable(onClick = { if (!disabled) toggle(!isChecked) }),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			// labels
			Column(
				modifier = Modifier
					.weight(1f)
					.padding(
						start = paddingText,
						top = paddingText,
						bottom = paddingText
					)
			) {
				Text(
					text = if (titleResId != 0) stringResource(titleResId) else title,
					style = typography.titleMedium,
					color = if (disabled) titleColor.copy(alpha = disabledAlpha)
					else if (isFocused) thumbCheckedColor
					else titleColor,
				)

				if (summaryOnResId != 0 && summaryOffResId != 0) {
					Text(
						text = stringResource(
							if (isChecked) summaryOnResId
							else summaryOffResId
						),
						style = typography.bodyMedium,
						color = if (disabled) summaryColor.copy(alpha = disabledAlpha)
						else if (isFocused) thumbCheckedColor
						else summaryColor
					)
				}
			}

			Switch(
				modifier = Modifier.padding(horizontal = paddingSwitch),
				colors = SwitchDefaults.colors(
					checkedThumbColor = if (isFocused) thumbCheckedSelectedColor else thumbCheckedColor,
					checkedTrackColor = if (isFocused) trackCheckedSelectedColor else trackCheckedColor,
					uncheckedThumbColor = thumbUncheckedColor,
					uncheckedTrackColor = trackUncheckedColor
				),
				checked = isChecked,
				enabled = !disabled,
				onCheckedChange = null
			)
		}
	}
}
