package com.sidephone.calendar.core.reminders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


/**
 * Called by Android when the reminder alarm fires, then it does the following:
 * - asks the database for everything due since SharedPreferences.lastNotified
 * - shows a notification for each due reminder
 * - saves SharedPreferences.lastNotified = now
 * - schedules the next alarm (replacing the current)
 */
class AlarmReceiver : BroadcastReceiver() {
	override fun onReceive(context: Context, intent: Intent) {
		Log.d("ALARM", "it works")
	}
}
