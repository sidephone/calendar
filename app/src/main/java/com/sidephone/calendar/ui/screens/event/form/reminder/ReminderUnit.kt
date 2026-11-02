package com.sidephone.calendar.ui.screens.event.form.reminder

import androidx.annotation.StringRes
import com.sidephone.calendar.R


internal enum class ReminderUnit(val factor: Int, @StringRes val label: Int) {
	AT_EVENT(1, R.string.reminder_option_at_event),
	MINUTES(1, R.string.reminder_option_minutes_before),
	HOURS(60, R.string.reminder_option_hours_before),
	DAYS(24 * 60, R.string.reminder_option_days_before);

	companion object {
		/** The largest unit that represents [minutes] exactly. */
		fun from(minutes: Int) = when {
			minutes <= 0 -> AT_EVENT
			minutes % DAYS.factor == 0 -> DAYS
			minutes % HOURS.factor == 0 -> HOURS
			else -> MINUTES
		}
	}
}
