package com.sidephone.calendar

import android.content.Context
import androidx.core.content.edit
import com.sidephone.calendar.input.Keypad

class Settings(context: Context) {
	companion object {
		const val REMINDER_DEFAULT = 15 // minutes before
		const val REMINDER_MAX_MINUTES = 30 * 24 * 60 // 30 days

		private const val PREFS_NAME = "SidephoneCalendarSettings"
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
}
