package com.sidephone.calendar.ui.screens.event.form

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
import com.sidephone.calendar.R
import com.sidephone.calendar.Settings
import com.sidephone.calendar.ui.components.timeField.DigitField
import com.sidephone.calendar.ui.theme.Dimens

/**
 * Edits one reminder as "[amount] [unit] before". [minutes] is the stored value (>= 0).
 *
 * D-pad order: amount  <->  unit selector. OK on the amount jumps to the selector, OK on the
 * selector opens the menu, Left on the selector goes back to the amount. When "at event" is chosen,
 * the amount field is removed and the value is 0.
 */
@Composable
fun FormReminderField(
	modifier: Modifier = Modifier,
	minutes: Int,
	onTimeChange: (minutes: Int) -> Unit
) {
	val inputFocus = remember { FocusRequester() }
	val unitFocus = remember { FocusRequester() }

	// The unit is UI state: derive it once from the stored value and then keep it, so typing "60"
	// in minutes does not flip the field to "1 hour" while the user is still editing.
	var unit by remember { mutableStateOf(ReminderUnit.from(minutes)) }

	val max = Settings.REMINDER_MAX_MINUTES / unit.factor
	val amount = (minutes / unit.factor).coerceIn(0, max)

	fun commit(amount: Int, unit: ReminderUnit) {
		val limit = Settings.REMINDER_MAX_MINUTES / unit.factor
		onTimeChange(amount.coerceIn(0, limit) * unit.factor)
	}

	Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
		// Amount (hidden for "at event")
		if (unit != ReminderUnit.AT_EVENT) {
			// key(unit): reset the field's internal state when the unit (and so its max) changes
			key(unit) {
				DigitField(
					number = amount,
					max = max,
					maxFirstDigit = max.toString().first().digitToInt(),
					focusRequester = inputFocus,
					textColor = MaterialTheme.colorScheme.onBackground,
					inputBackgroundColor = MaterialTheme.colorScheme.surface,
					borderColor = MaterialTheme.colorScheme.outline,
					selectedBorderColor = MaterialTheme.colorScheme.primary,
					borderRadius = Dimens.FORM_INPUT_BORDER_RADIUS,
					width = Dimens.FORM_TIME_FIELD_WIDTH,
					height = Dimens.FORM_TIME_FIELD_HEIGHT,
					onCommit = { commit(it, unit) },
					onPrev = null,
					onNext = { unitFocus.requestFocus() },
					onConfirm = { unitFocus.requestFocus() },
				)
			}
			Spacer(Modifier.width(8.dp))
		}

		// Unit dropdown
		UnitSelector(
			unit = unit,
			focusRequester = unitFocus,
			onPrev = { if (unit != ReminderUnit.AT_EVENT) inputFocus.requestFocus() },
			onSelect = { new ->
				unit = new
				if (new == ReminderUnit.AT_EVENT) onTimeChange(0)
				else commit(if (amount == 0) 1 else amount, new)   // never leave "0 minutes before"
			},
		)
	}
}

@Composable
private fun UnitSelector(
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
				.padding(horizontal = 12.dp),
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

private enum class ReminderUnit(val factor: Int, @StringRes val label: Int) {
	AT_EVENT(1, R.string.reminder_at_event),
	MINUTES(1, R.string.reminder_minutes_before),
	HOURS(60, R.string.reminder_hours_before),
	DAYS(24 * 60, R.string.reminder_days_before);

	companion object {
		/** The largest unit that represents [minutes] exactly. */
		fun from(minutes: Int) = when {
			minutes <= 0 -> AT_EVENT
			minutes % DAYS.factor == 0 -> DAYS
			minutes % HOURS.factor == 0 -> HOURS
			else -> MINUTES
		}
	}
}

