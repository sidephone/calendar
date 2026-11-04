package com.sidephone.calendar.ui.screens.event.form.validation

import com.sidephone.calendar.db.Event
import com.sidephone.calendar.ui.screens.event.form.reminder.ReminderUnit
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


enum class SaveValidationStatus { EVENT_IN_THE_PAST, REMINDER_IN_THE_PAST, OK }


fun validateEvent(event: Event): Triple<SaveValidationStatus, String, Int> {
	val startTime = event.startTime ?: LocalTime.of(0, 0)

	val today = LocalDate.now()

	if (
		today.isAfter(event.startDate)
		|| (today.equals(event.startDate) && LocalTime.now().isAfter(startTime))
	) {
		val date = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).format(event.startDate)
		val time = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(startTime)
		return Triple(SaveValidationStatus.EVENT_IN_THE_PAST, "$date, $time", 0)
	}

	event.reminders.forEach { minutes ->
		val reminderTime = event.startDate.atTime(startTime).minusMinutes(minutes.toLong())
		if (LocalDate.now().atTime(LocalTime.now()).isAfter(reminderTime)) {
			val (unit, factor) = ReminderUnit.fromPluralized(minutes)
			return Triple(SaveValidationStatus.REMINDER_IN_THE_PAST, "${minutes / factor}", unit)
		}
	}

	return Triple(SaveValidationStatus.OK, "", 0)
}
