package com.sidephone.calendar.ui.screens.calendar.calendar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.sidephone.calendar.ui.theme.Dimens


@Composable
fun CellMonth(
	modifier: Modifier,
	onClick: () -> Unit,
	textColor: Color,
	highlightBackground: Color,
	highlightTextColor: Color,
	todayBackground: Color,
	todayTextColor: Color,
	inactiveTextColor: Color,
	isHighlighted: Boolean,
	day: Int,
	isToday: Boolean = false,
	eventCount: Int,
	eventColor: Color,
) {
	val eventBoxes = minOf(eventCount, Dimens.EVENT_MARKERS_MAX_SLOTS)
	val eventOverflow = eventCount > Dimens.EVENT_MARKERS_MAX_SLOTS

	val clickable = modifier
		.focusProperties { canFocus = false }
		.clickable { onClick() }

	Column(
		modifier =
			if (isHighlighted) clickable.background(highlightBackground)
			else if (isToday) clickable.background(todayBackground)
			else clickable
	) {
		// day number
		Text(
			modifier = Modifier.fillMaxWidth(),
			textAlign = TextAlign.Center,
			fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
			color =
				if (isHighlighted) highlightTextColor
				else if (isToday) todayTextColor
				else if (day < 0) inactiveTextColor
				else textColor,
			text =
				if (day < 0) (-day).toString()
				else day.toString()
		)

		if (eventCount <= 0) return@Column

		// event markers
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(Dimens.EVENT_MARKER_SPACING, Alignment.CenterHorizontally),
			verticalAlignment = Alignment.CenterVertically
		) {
			val color =
				if (isHighlighted) highlightTextColor
				else if (isToday) todayTextColor
				else eventColor

			repeat(eventBoxes) { i ->
				if (eventOverflow && i == eventBoxes - 1) PlusMarker(Modifier.size(Dimens.EVENT_MARKER_SIZE), color)
				else Box(Modifier.size(Dimens.EVENT_MARKER_SIZE).background(color))
			}
		}
	}
}

@Composable
fun PlusMarker(modifier: Modifier, color: Color) {
	Canvas(modifier) {
		val t = size.width / 3f
		drawRect(color, topLeft = Offset(0f, t), size = Size(size.width, t))   // horizontal bar
		drawRect(color, topLeft = Offset(t, 0f), size = Size(t, size.height))  // vertical bar
	}
}
