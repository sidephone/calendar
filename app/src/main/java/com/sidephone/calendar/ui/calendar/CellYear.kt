package com.sidephone.calendar.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun CellYear(
	modifier: Modifier,
	textColor: Color,
	highlightBackground: Color,
	highlightTextColor: Color,
	isHighlighted: Boolean,
	month: Int
) {
	Box(
		contentAlignment = Alignment.Center,
		modifier =
			if (isHighlighted) modifier.background(highlightBackground)
			else modifier
	) {
		Text(
			color = if (isHighlighted) highlightTextColor else textColor,
			text = LocalDate
				.of(2000, month, 1)
				.format(DateTimeFormatter.ofPattern("MMM"))
		)
	}
}

