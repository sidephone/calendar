package com.sidephone.calendar.ui.calendar

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
import androidx.compose.ui.unit.dp


private const val MAX_EVENT_MARKER_SLOTS = 4   // 3 squares + "+"; use 5 if the cell is wide enough

@Composable
fun CellMonth(
	modifier: Modifier,
	onClick: () -> Unit,
	textColor: Color,
	highlightBackground: Color,
	highlightTextColor: Color,
	inactiveTextColor: Color,
	isHighlighted: Boolean,
	day: Int,
	isToday: Boolean = false,
	eventCount: Int,
) {
	val eventBoxes = minOf(eventCount, MAX_EVENT_MARKER_SLOTS)
	val eventOverflow = eventCount > MAX_EVENT_MARKER_SLOTS

	Column(
		modifier =
			if (isHighlighted) modifier
				.background(highlightBackground)
				.focusProperties { canFocus = false }
				.clickable { onClick() }
			else modifier
				.focusProperties { canFocus = false }
				.clickable { onClick() }
	) {
		// day number
		Text(
			modifier = Modifier.fillMaxWidth(),
			textAlign = TextAlign.Center,
			fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
			color =
				if (isHighlighted) highlightTextColor
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
			horizontalArrangement = Arrangement.spacedBy(3.dp, Alignment.CenterHorizontally),
			verticalAlignment = Alignment.CenterVertically
		) {
			val color =
				if (isHighlighted) highlightTextColor
				else textColor

			repeat(eventBoxes) { i ->
				if (eventOverflow && i == eventBoxes - 1) PlusMarker(Modifier.size(6.dp), color)
				else Box(Modifier.size(6.dp).background(color))
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
