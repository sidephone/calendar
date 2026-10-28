package com.sidephone.calendar.ui.screens.event.form

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


private fun Int.toFieldValue() = TextFieldValue("%02d".format(this), TextRange(2))

@Composable
fun DigitField(
	number: Int,
	max: Int,
	maxFirstDigit: Int,
	focusRequester: FocusRequester,
	onCommit: (Int) -> Unit,
	onPrev: (() -> Unit)?,
	onNext: (() -> Unit)?,
	onConfirm: () -> Unit,
	onFilled: (() -> Unit)? = onNext,
) {
	var value by remember { mutableStateOf(number.toFieldValue()) }
	var focused by remember { mutableStateOf(false) }
	var dirty by remember { mutableStateOf(false) }
	// Bumped after every commit so we re-check against the parent's final value,
	// even when that value didn't change (e.g. user typed 59, parent kept 58).
	var syncTick by remember { mutableIntStateOf(0) }

	val focus = LocalFocusManager.current

	LaunchedEffect(number, syncTick) {
		if (value.text.toIntOrNull() != number) value = number.toFieldValue()
	}

	fun commit() {
		if (!dirty) return
		dirty = false
		val n = (value.text.toIntOrNull() ?: number).coerceIn(0, max)
		value = n.toFieldValue()
		onCommit(n)
		syncTick++
	}

	val colors = MaterialTheme.colorScheme
	val borderColor = if (focused) colors.primary else colors.outline

	BasicTextField(
		value = value,
		onValueChange = { new ->
			val old = value.text
			val raw = new.text

			// Ignore anything that isn't a digit.
			if (raw.any { !it.isDigit() }) return@BasicTextField

			var digits: String
			var cursor: Int
			var inserted = raw.length > old.length

			if (raw.length > 2) {
				// Field was full: overwrite at the cursor, or start over if at the end.
				val p = (new.selection.start - 1).coerceIn(0, raw.length - 1)
				val typed = raw[p]
				if (old.length == 2 && p < 2) {
					digits = old.replaceRange(p, p + 1, typed.toString())
					cursor = p + 1
				} else {
					digits = typed.toString()
					cursor = 1
				}
				inserted = true
			} else {
				digits = raw
				cursor = new.selection.end.coerceIn(0, raw.length)
			}

			// T9 convenience: "5" can only mean 05 for hours (or 6-9 for minutes).
			if (inserted && digits.length == 1 && digits[0].digitToInt() > maxFirstDigit) {
				digits = "0$digits"
				cursor = 2
			}

			if (digits.length == 2 && digits.toInt() > max) {
				digits = max.toString()
				cursor = 2
			}

			value = TextFieldValue(digits, TextRange(cursor))
			dirty = true

			if (inserted && digits.length == 2 && cursor == 2) {
				commit()
				onFilled?.invoke()
			}
		},
		singleLine = true,
		textStyle = MaterialTheme.typography.displayMedium.copy(
			textAlign = TextAlign.Center,
			color = colors.onSurface,
		),
		cursorBrush = SolidColor(colors.primary),
		keyboardOptions = KeyboardOptions(
			keyboardType = KeyboardType.Number,
			imeAction = ImeAction.Next,
		),
		keyboardActions = KeyboardActions(
			onNext = { commit(); onConfirm() },
			onDone = { commit(); onConfirm() },
		),
		modifier = Modifier
			.width(88.dp)
			.focusRequester(focusRequester)
			.onFocusChanged {
				focused = it.isFocused
				if (!it.isFocused) commit()
			}
			.onPreviewKeyEvent { event ->
				if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
				val sel = value.selection
				when (event.key) {
					Key.DirectionUp -> {
						focus.moveFocus(FocusDirection.Up)
						true
					}

					Key.DirectionDown -> {
						focus.moveFocus(FocusDirection.Down)
						true
					}

					// Only jump fields when the cursor is collapsed at the edge;
					// otherwise let the text field move the cursor as usual.
					Key.DirectionLeft ->
						if (onPrev != null && sel.collapsed && sel.start == 0) {
							onPrev(); true
						} else false

					Key.DirectionRight ->
						if (onNext != null && sel.collapsed && sel.end == value.text.length) {
							onNext(); true
						} else false

					Key.Backspace, Key.Delete ->
						if (onPrev != null && value.text.isEmpty()) {
							onPrev(); true
						} else false

					// --- Your existing Enter / D-pad Center handling goes here ---
					Key.Enter, Key.NumPadEnter, Key.DirectionCenter -> {
						commit(); onConfirm(); true
					}

					else -> false
				}
			}
			.background(colors.surfaceVariant, RoundedCornerShape(8.dp))
			.border(BorderStroke(if (focused) 3.dp else 1.dp, borderColor), RoundedCornerShape(8.dp)),
		decorationBox = { inner -> Box(contentAlignment = Alignment.Center) { inner() } },
	)
}
