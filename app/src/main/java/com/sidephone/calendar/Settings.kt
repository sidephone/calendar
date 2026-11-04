package com.sidephone.calendar

import android.content.Context
import androidx.core.content.edit
import com.sidephone.calendar.input.Keypad
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class Settings(context: Context) {
	companion object {
		val REMINDER_CATCH_UP_LIMIT: Duration = Duration.ofHours(12) // reminders older than this are not shown late
		const val REMINDER_DEFAULT = 15 // minutes before
		const val REMINDER_EARLY_TOLERANCE = 5L // seconds
		const val REMINDER_MAX_DAYS = 30
		const val REMINDER_MAX_MINUTES = REMINDER_MAX_DAYS * 24 * 60

		private const val PREFS_NAME = "SidephoneCalendarSettings"
		private const val LAST_NOTIFIED_KEY = "last_notified"
		private const val SCREEN_LAYOUT_KEY = "screen_layout"
	}

	private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)


		var lastNotified: LocalDateTime =
		LocalDateTime
			.ofInstant(
				Instant.ofEpochMilli(sharedPreferences.getLong(LAST_NOTIFIED_KEY, 0L)),
				ZoneId.systemDefault()
			)
		set(value) {
			field = value
			sharedPreferences.edit {putLong(
				LAST_NOTIFIED_KEY,
				value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
			)}
		}


	var tile =
		sharedPreferences
			.getInt(SCREEN_LAYOUT_KEY, Keypad.Layout.UNKNOWN.ordinal)
			.let { ordinal -> Keypad.Layout.entries.getOrElse(ordinal) { Keypad.Layout.UNKNOWN } }
		set(value) {
			field = value
			sharedPreferences.edit { putInt(SCREEN_LAYOUT_KEY, value.ordinal) }
		}
}
