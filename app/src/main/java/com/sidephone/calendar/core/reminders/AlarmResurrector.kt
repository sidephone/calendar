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

/**
 * Re-creates the reminder alarm when Android has cleared it or its meaning changed:
 * BOOT_COMPLETED (reboot, OS upgrade), MY_PACKAGE_REPLACED (app update),
 * TIME_SET and TIMEZONE_CHANGED (events are floating local times).
 */
class AlarmResurrector : BroadcastReceiver() {
	private companion object {
		private val LOG_TAG = AlarmResurrector::class.java.simpleName
	}

	override fun onReceive(context: Context, intent: Intent) {
		// A receiver gets ~10 seconds; goAsync() lets us finish the database work on another thread.
		val pending = goAsync()

		CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
			try {
				Log.d(LOG_TAG, "Rescheduling an alarm after ${intent.action}")
				val appContext = context.applicationContext
				AlarmScheduler(appContext).scheduleFrom(
					EventStore(AppDatabase.get(appContext).eventDao()),
					Settings(appContext).lastNotified
				)
			} catch (e: Exception) {
				Log.e(LOG_TAG, "Reschedule failed after ${intent.action}", e)
			} finally {
				pending.finish()
			}
		}
	}
}

