package com.sidephone.calendar.ui.theme

import androidx.compose.ui.unit.dp

object Dimens {
	const val EVENT_MARKERS_MAX_SLOTS = 4   // 3 squares + "+"; use 5 if the cell is wide enough
	val EVENT_MARKER_SIZE = 6.dp
	val EVENT_MARKER_SPACING = 3.dp

	val EVENT_LIST_BORDER_RADIUS = 12.dp
	val EVENT_LIST_MARGIN = Pair(8.dp, 4.dp)
	val EVENT_LIST_PADDING = Pair(8.dp, 4.dp)

	val FORM_INPUT_WRAPPER = 0.dp
	val FORM_INPUT_PADDING = 12.dp
	val FORM_SWITCH_PADDING = 6.dp
}
