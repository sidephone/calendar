package com.sidephone.calendar

import android.content.Context
import androidx.core.content.edit
import com.sidephone.calendar.input.Keypad
import java.time.Duration

class Settings(context: Context) {
	companion object {
		val REMINDER_CATCH_UP_LIMIT: Duration = Duration.ofHours(12) // reminders older than this are not shown late
		const val REMINDER_DEFAULT = 15 // minutes before
		const val REMINDER_EARLY_TOLERANCE = 5L // seconds
		const val REMINDER_MAX_MINUTES = 30 * 24 * 60 // 30 days

		private const val PREFS_NAME = "SidephoneCalendarSettings"
		private const val LAST_NOTIFIED_KEY = "last_notified"
		private const val SCREEN_LAYOUT_KEY = "screen_layout"
	}

	private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)


	fun getTile(): Keypad.Layout {
		return sharedPreferences.getInt(SCREEN_LAYOUT_KEY, Keypad.Layout.UNKNOWN.ordinal).let { ordinal ->
			Keypad.Layout.entries.getOrElse(ordinal) { Keypad.Layout.UNKNOWN }
		}
	}


	fun setTile(layout: Keypad.Layout) {
		sharedPreferences.edit { putInt(SCREEN_LAYOUT_KEY, layout.ordinal) }
	}


	var lastNotified = sharedPreferences.getLong(LAST_NOTIFIED_KEY, 0L)
		set(value) {
			field = value
			sharedPreferences.edit { putLong(LAST_NOTIFIED_KEY, value) }
		}
}
