package com.sidephone.calendar.ui.topbar

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sidephone.calendar.R
import com.sidephone.calendar.core.Calendar
import com.sidephone.calendar.core.grid.DayGrid
import com.sidephone.calendar.core.grid.MonthGrid
import com.sidephone.calendar.core.grid.YearGrid
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


@Composable
fun Topbar(calendar: Calendar, noTouch: Boolean, onHelp: () -> Unit, onToggleSidebar: () -> Unit) {
	val months: List<Int> = listOf(
		R.string.month_1,
		R.string.month_2,
		R.string.month_3,
		R.string.month_4,
		R.string.month_5,
		R.string.month_6,
		R.string.month_7,
		R.string.month_8,
		R.string.month_9,
		R.string.month_10,
		R.string.month_11,
		R.string.month_12
	)

	val grid = calendar.grid.collectAsState().value
	val year = calendar.date.first.collectAsState().value
	val month = calendar.date.second.collectAsState().value
	val day = calendar.date.third.collectAsState().value

	val title = when (grid) {
		is YearGrid -> year.toString()
		is MonthGrid -> "${stringResource(months[month - 1])} $year"
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
