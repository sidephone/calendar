package com.sidephone.calendar.ui.screens.event.form.reminder

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sidephone.calendar.R
import com.sidephone.calendar.Settings
import com.sidephone.calendar.ui.components.IconButton
import com.sidephone.calendar.ui.components.keypadClickable
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
	onTimeChange: (minutes: Int) -> Unit,
	onDeleteEntry: () -> Unit,
) {
	val inputFocus = remember { FocusRequester() }
	val unitFocus = remember { FocusRequester() }
	val deleteFocus = remember { FocusRequester() }

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
			onNext = { deleteFocus.requestFocus() },
			onSelect = { new ->
				unit = new
				if (new == ReminderUnit.AT_EVENT) onTimeChange(0)
				else commit(if (amount == 0) 1 else amount, new)   // never leave "0 minutes before"
			},
		)

		IconButton(
			modifier = Modifier.keypadClickable(onClick = { onDeleteEntry() }),
			focusRequester = deleteFocus,
			imageIconResId = R.drawable.ic_delete,
			accessibilityTitle = stringResource(R.string.reminder_delete),
			color = MaterialTheme.colorScheme.onTertiary,
			highlightColor = MaterialTheme.colorScheme.onTertiary,
			highlightBackground = MaterialTheme.colorScheme.tertiary,
			onPrev = { unitFocus.requestFocus() },
			onClick = { onDeleteEntry() }
		)
	}
}
