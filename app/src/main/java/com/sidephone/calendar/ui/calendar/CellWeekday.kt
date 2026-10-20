package com.sidephone.calendar.ui.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun CellWeekday(
	modifier: Modifier,
	textColor: Color,
	letter: String,
) {
	Box(
		contentAlignment = Alignment.Center,
		modifier = modifier
	) {
		Text(color = textColor, text = letter)
	}
}
