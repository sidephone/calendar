package com.sidephone.calendar.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@Composable
fun CellMonth(
	modifier: Modifier,
	textColor: Color,
	highlightBackground: Color,
	highlightTextColor: Color,
	inactiveTextColor: Color,
	isHighlighted: Boolean,
	day: Int,
	isToday: Boolean = false,
) {
	Box(
		contentAlignment = Alignment.TopCenter,
		modifier =
			if (isHighlighted) modifier.background(highlightBackground)
			else modifier
	) {
		Text(
			fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
			color =
				if (isHighlighted) highlightTextColor
				else if (day < 0) inactiveTextColor
				else textColor,
			text =
				if (day < 0) (-day).toString()
				else day.toString()
		)
	}
}
