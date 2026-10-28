package com.sidephone.calendar.ui.screens.event.form

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Switch
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
import androidx.compose.ui.unit.dp
import com.sidephone.calendar.ui.screens.event.modifiers.keypadClickableButton
import com.sidephone.calendar.ui.theme.Dimens
import com.sidephone.calendar.ui.theme.DisabledAlpha


@Composable
fun FormSwitch(
	titleResId: Int,
	summaryOnResId: Int = 0,
	summaryOffResId: Int = 0,
	value: Boolean,
	disabled: Boolean,
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

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(Dimens.FORM_INPUT_WRAPPER)
	) {
		Row(
			modifier = Modifier
				.clip(RoundedCornerShape(8.dp))
				.background(
					color = if (isFocused && !disabled) colorScheme.secondary
					else Color.Transparent
				)
				.toggleable(
					value = isChecked,
					enabled = !disabled,
					interactionSource = interactionSource,
					indication = LocalIndication.current,
					onValueChange = { checked -> toggle(checked) }
				)
				.keypadClickableButton(onClick = { if (!disabled) toggle(!isChecked) }),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			// labels
			Column(
				modifier = Modifier
					.weight(1f)
					.padding(
						start = Dimens.FORM_INPUT_PADDING,
						top = Dimens.FORM_INPUT_PADDING,
						bottom = Dimens.FORM_INPUT_PADDING
					)
			) {
				Text(
					text = stringResource(titleResId),
					style = typography.titleLarge,
					color = if (disabled) colorScheme.onBackground.copy(alpha = DisabledAlpha)
					else if (isFocused) colorScheme.onSecondary
					else colorScheme.onBackground,
				)

				if (summaryOnResId != 0 && summaryOffResId != 0) {
					Text(
						text = stringResource(
							if (isChecked) summaryOnResId
							else summaryOffResId
						),
						style = typography.titleMedium,
						color = if (disabled) colorScheme.onSurfaceVariant.copy(alpha = DisabledAlpha)
						else if (isFocused) colorScheme.onSecondary
						else colorScheme.onSurfaceVariant
					)
				}
			}

			Switch(
				modifier = Modifier.padding(horizontal = Dimens.FORM_SWITCH_PADDING),
				checked = isChecked,
				enabled = !disabled,
				onCheckedChange = null
			)
		}
	}
}
