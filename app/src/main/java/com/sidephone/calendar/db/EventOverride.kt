package com.sidephone.calendar.db

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import com.sidephone.calendar.Settings
import com.sidephone.calendar.core.events.EventReminder
import java.time.LocalDate
import java.time.LocalTime

/**
 * Changes a single generated occurrence of a recurring event (or of a non-recurring one).
 * [originalDate] is the START date the occurrence would normally have; it identifies the occurrence.
 * Null fields mean "keep the base value".
 *
 * To move an occurrence set BOTH [movedStartDate] and [movedEndDate] (the repository/UI must keep
 * the length it wants; copy it from the Occurrence's dayCount).
 */
@Entity(
	primaryKeys = ["eventId", "originalDate"],
	foreignKeys = [
		ForeignKey(
			entity = Event::class,
			parentColumns = ["id"],
			childColumns = ["eventId"],
			onDelete = ForeignKey.CASCADE
		)
	],
	indices = [Index("movedStartDate")]
)
data class EventOverride(
	val eventId: Long,
	val originalDate: LocalDate,
	val cancelled: Boolean = false,
	val title: String? = null,
	val description: String? = null,
	val address: String? = null,
	val allDay: Boolean? = null, // null = keep base; false needs startTime (here or in base)
	val startTime: LocalTime? = null,
	val endTime: LocalTime? = null,
	val movedStartDate: LocalDate? = null,
	val movedEndDate: LocalDate? = null,
	val reminders: List<Int>? = null, // null = inherit from the event; emptyList() = none
) {
	init {
		require((movedStartDate == null) == (movedEndDate == null)) {
			"movedStartDate and movedEndDate must be set together"
		}
		if (movedStartDate != null && movedEndDate != null) {
			require(!movedEndDate.isBefore(movedStartDate)) { "movedEndDate before movedStartDate" }
		}
		reminders?.let { r -> require(r.all { it in 0..Settings.REMINDER_MAX_MINUTES }) { "reminder out of range" } }
	}
}
