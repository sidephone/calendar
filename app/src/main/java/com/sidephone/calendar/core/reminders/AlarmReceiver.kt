package com.sidephone.calendar.core.reminders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.sidephone.calendar.Settings
import com.sidephone.calendar.db.AppDatabase
import com.sidephone.calendar.db.EventStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDateTime

/**
 * Called by Android when the reminder alarm fires.
 */
class AlarmReceiver : BroadcastReceiver() {
	private companion object {
		private val LOG_TAG = AlarmReceiver::class.java.simpleName
	}

	override fun onReceive(context: Context, intent: Intent) {
		val alarmScheduler = AlarmScheduler(context)
		val appContext = context.applicationContext
		val pending = goAsync() // goAsync() lets us do the database work on another thread
		val settings = Settings(context)

		CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
			try {
				val store = EventStore(AppDatabase.get(appContext).eventDao())

				// Android may deliver an alarm a moment early. Looking a few seconds ahead means the
				// reminder is not missed, and using the same instant for the next alarm means it is
				// not scheduled again.
				val upTo = LocalDateTime.now().plusSeconds(Settings.REMINDER_EARLY_TOLERANCE)

				// Order matters: notify, then save lastNotified (inside deliverDue), then reschedule.
				// If the process dies in between, the worst case is a duplicate notification, and the
				// stable notification id makes it replace the original.
				ReminderNotifier(appContext).deliverDue(store, settings, upTo)
				alarmScheduler.scheduleFrom(store, upTo)
			} catch (e: Exception) {
				Log.e(LOG_TAG, "Failed to handle reminder alarm", e)
			} finally {
				pending.finish()
			}
		}
	}
}
