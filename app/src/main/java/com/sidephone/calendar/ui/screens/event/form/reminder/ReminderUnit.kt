package com.sidephone.calendar.ui.screens.event.form.reminder

import androidx.annotation.StringRes
import com.sidephone.calendar.R
import com.sidephone.calendar.ui.components.DropDownEntry


internal enum class ReminderUnit(val factor: Int, @StringRes override val label: Int) : DropDownEntry {
	AT_EVENT(1, R.string.reminder_option_at_event),
	MINUTES(1, R.string.reminder_option_minutes_before),
	HOURS(60, R.string.reminder_option_hours_before),
	DAYS(24 * 60, R.string.reminder_option_days_before);

	companion object {
		/** The largest unit that represents minutes exactly. */
		fun from(minutes: Int) = when {
			minutes <= 0 -> AT_EVENT
			minutes % DAYS.factor == 0 -> DAYS
			minutes % HOURS.factor == 0 -> HOURS
			else -> MINUTES
		}

		fun fromPluralized(minutes: Int): Pair<Int, Int> = when {
			minutes <= 0 -> Pair(AT_EVENT.label, 1)
			minutes == 1 -> Pair(R.string.reminder_option_minute_before, 1)
			minutes == 60 -> Pair(R.string.reminder_option_hour_before, 60)
			minutes == 1440 -> Pair(R.string.reminder_option_day_before, 1440)
			minutes % DAYS.factor == 0 -> Pair(DAYS.label, DAYS.factor)
			minutes % HOURS.factor == 0 -> Pair(HOURS.label, HOURS.factor)
			else -> Pair(R.string.reminder_option_minutes_before, 1)
		}
	}
}
