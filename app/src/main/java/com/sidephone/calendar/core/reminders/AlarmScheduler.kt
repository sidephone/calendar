package com.sidephone.calendar.core.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.sidephone.calendar.db.EventStore
import java.time.LocalDateTime
import java.time.ZoneId


/**
 * Owns the one and only reminder alarm of the app: "wake the app at the next reminder".
 *
 * The alarm is identified by a fixed PendingIntent (same request code + same action), so setting
 * it again REPLACES the previous one and cancel() removes it. No per-event bookkeeping is needed:
 * after any change, recompute the next reminder from the database and call [scheduleAt]/[scheduleFrom].
 */
class AlarmScheduler(context: Context) {
	companion object {
		private val LOG_TAG = AlarmScheduler::class.java.simpleName
		private const val REQUEST_CODE = 1
		const val ACTION = "com.sidephone.calendar.REMINDER"
	}

	private val context: Context = context.applicationContext
	private val alarmManager: AlarmManager = this.context.getSystemService(AlarmManager::class.java)


	/**
	 * Sets the alarm for [triggerAt] (local time), replacing any existing one. null cancels it. Safe
	 * to call multiple times. It won't schedule duplicate alarms.
	 */
	fun scheduleAt(triggerAt: LocalDateTime?) {
		if (triggerAt == null) {
			cancel()
			return
		}

		val millis = triggerAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

		if (alarmManager.canScheduleExactAlarms()) {
			alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millis, pendingIntent())
			Log.d(LOG_TAG, "Exact alarm set for $triggerAt")
		} else {
			// Permission missing or revoked (possible on Android 12 via system settings).
			// setExact* would throw SecurityException, so fall back to an inexact alarm.
			alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millis, pendingIntent())
			Log.w(LOG_TAG, "Exact alarms not permitted; inexact alarm set for $triggerAt")
		}
	}


	/**
	 * Recomputes the next reminder from the database and (re)sets or cancels the alarm. It's safe to
	 * call multiple times. It won't schedule duplicate alarms.
	 */
	suspend fun scheduleFrom(store: EventStore, after: LocalDateTime = LocalDateTime.now()) {
		scheduleAt(store.nextReminder(after)?.triggerAt)
	}


	fun cancel() {
		alarmManager.cancel(pendingIntent())
		Log.d(LOG_TAG, "Alarm cancelled")
	}


	private fun pendingIntent(): PendingIntent = PendingIntent.getBroadcast(
		context,
		REQUEST_CODE,
		Intent(context, AlarmReceiver::class.java).setAction(ACTION),
		PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
	)
}
