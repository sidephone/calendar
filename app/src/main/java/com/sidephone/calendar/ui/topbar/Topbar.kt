package com.sidephone.calendar.ui.topbar

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sidephone.calendar.core.Calendar
import com.sidephone.calendar.core.grid.DayGrid
import com.sidephone.calendar.core.grid.MonthGrid
import com.sidephone.calendar.core.grid.YearGrid
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


@Composable
fun Topbar(calendar: Calendar, noTouch: Boolean, onHelp: () -> Unit, onToggleSidebar: () -> Unit) {
	val grid = calendar.grid.collectAsState().value
	val (year, month, day) = calendar.date.collectAsState().value

	val title = when (grid) {
		is YearGrid -> year.toString()
		is MonthGrid -> LocalDate
			.of(year, month, 1)
			.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
		is DayGrid -> LocalDate
			.of(year, month, day)
			.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
		else -> "Unknown"
	}

	Row(verticalAlignment = Alignment.CenterVertically) {
		ButtonToggleSidebar(onToggleSidebar = onToggleSidebar)
		Text(modifier = Modifier.weight(1f), text = title)
		if (noTouch) {
			ButtonHelp(onClick = onHelp)
		}
	}
}
