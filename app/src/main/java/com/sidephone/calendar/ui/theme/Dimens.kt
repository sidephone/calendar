package com.sidephone.calendar.ui.theme

import androidx.compose.ui.unit.dp

object Dimens {

	const val EVENT_MARKERS_MAX_SLOTS = 4   // 3 squares + "+"; use 5 if the cell is wide enough
	val EVENT_MARKER_SIZE = 6.dp
	val EVENT_MARKER_SPACING = 3.dp

	val EVENT_LIST_BORDER_RADIUS = 12.dp
	val EVENT_LIST_MARGIN = Pair(8.dp, 4.dp)
	val EVENT_LIST_PADDING = Pair(8.dp, 4.dp)

	val FORM_CATEGORY_SPACING = 16.dp
	val FORM_INPUT_BORDER_RADIUS = 8.dp
	val FORM_INPUT_SPACING = 8.dp
	val FORM_SWITCH_PADDING = 8.dp
	val FORM_TIME_FIELD_WIDTH = 56.dp
	val FORM_TIME_FIELD_HEIGHT = 32.dp

	val JAKE_TYPE_PADDING = 32.dp

	val MONTH_WEEKDAY_ROW_HEIGHT = 42.dp

	val SWIPE_THRESHOLD = 64.dp
}
