package com.sidephone.calendar.ui.screens.calendar.calendar

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp

@Composable
fun Modifier.onHorizontalSwipe(
	enabled: Boolean,
	swipeThreshold: Dp,
	onSwipeLeft: () -> Unit,    // finger moved left  -> usually "next page"
	onSwipeRight: () -> Unit,   // finger moved right -> usually "previous page"
): Modifier {
	val thresholdPx = with(LocalDensity.current) { swipeThreshold.toPx() }
	val left = rememberUpdatedState(onSwipeLeft)
	val right = rememberUpdatedState(onSwipeRight)
	val haptic = LocalHapticFeedback.current

	return pointerInput(enabled, thresholdPx) {
		if (!enabled) return@pointerInput
		var total = 0f
		detectHorizontalDragGestures(
			onDragStart = { total = 0f },
			onDragCancel = { total = 0f },
			onDragEnd = {
				when {
					total <= -thresholdPx -> {
						haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
						left.value()
					}

					total >= thresholdPx -> {
						haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
						right.value()
					}
				}
			},
			onHorizontalDrag = { change, amount ->
				change.consume()
				total += amount
			},
		)
	}
}
