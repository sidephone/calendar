package com.sidephone.calendar.core.reminders

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.util.Log
import com.sidephone.calendar.R
import com.sidephone.calendar.Settings
import com.sidephone.calendar.core.events.EventReminder
import com.sidephone.calendar.db.EventStore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Objects

class ReminderNotifier(context: Context) {
	companion object {
		private val LOG_TAG = ReminderNotifier::class.java.simpleName
		private const val CHANNEL_ID = "reminders"
	}

	private val context: Context = context.applicationContext
	private val notificationManager: NotificationManager = this.context.getSystemService(NotificationManager::class.java)


	/**
	 * Shows a notification for every reminder that became due since the last time this ran
	 * (but never looks back further than catch up limit), then records [upTo] as the new
	 * `lastNotified`. Safe to call from the alarm receiver and from boot/update catch-up.
	 */
	suspend fun deliverDue(store: EventStore, settings: Settings, upTo: LocalDateTime) {
		val floor = upTo.minus(Settings.REMINDER_CATCH_UP_LIMIT)
		val last = settings.lastNotified
		val after = if (last.isAfter(floor)) last else floor

		val due = store.remindersBetween(after, upTo)
		Log.d(LOG_TAG, "${due.size} reminder(s) due in ($after, $upTo]")
		due.forEach { show(it) }

		if (upTo.isAfter(last)) settings.lastNotified = upTo
	}


	private fun show(r: EventReminder) {
		if (!notificationManager.areNotificationsEnabled()) {
			Log.w(LOG_TAG, "Notifications are disabled; dropping reminder for '${r.title}'")
			return
		}
		createChannel()

		// Tapping opens the app.
		// Optionally, in the future, pass the event's date as an extra to open that day.
		val contentIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.let {
			PendingIntent.getActivity(
				context,
				0,
				it,
				PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
			)
		}

		val notification = Notification.Builder(context, CHANNEL_ID)
			.setSmallIcon(R.drawable.ic_event)
			.setContentTitle(r.title)
			.setContentText(title(r))
			.setStyle(Notification.BigTextStyle().bigText(details(r)))
			.setCategory(Notification.CATEGORY_REMINDER)
			.setAutoCancel(true)
			.setContentIntent(contentIntent)
			.build()

		// Stable id per (event, occurrence, offset): re-showing the same reminder replaces it
		// instead of stacking duplicates, e.g. after a catch-up.
		val id = Objects.hash(r.eventId, r.originalDate, r.minutesBefore)
		notificationManager.notify(id, notification)
	}


	private fun createChannel() {
		// Creating an existing channel is a no-op
		notificationManager.createNotificationChannel(
			NotificationChannel(
				CHANNEL_ID,
				context.getString(R.string.reminder_notification_channel_name),
				NotificationManager.IMPORTANCE_HIGH
			)
		)
	}


	private fun details(r: EventReminder): String {
		return listOfNotNull(title(r), r.address?.takeIf { it.isNotBlank() }).joinToString("\n")
	}


	private fun title(r: EventReminder): String {
		val today = LocalDateTime.now()
		val date = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).format(r.start)
		val time = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(r.start)

		return if (r.start.dayOfYear == today.dayOfYear) {
			if (r.minutesBefore == 0) context.getString(R.string.notification_event_now)
			else if (r.allDay) context.getString(R.string.notification_event_today_all_day)
			else context.getString(R.string.notification_event_today, time)
		} else if (r.start.dayOfYear == today.plusDays(1).dayOfYear) {
			if (r.allDay) context.getString(R.string.notification_event_tomorrow_all_day)
			else context.getString(R.string.notification_event_tomorrow, time)
		} else {
			if (r.allDay) date
			else context.getString(R.string.notification_event, date, time)
		}
	}
}
