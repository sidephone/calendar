package com.sidephone.calendar.db

import com.sidephone.calendar.core.events.EventOccurrence
import com.sidephone.calendar.core.events.EventRecurrence
import com.sidephone.calendar.core.events.EventReminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime

class EventStore(private val dao: EventDao) {
	suspend fun delete(eventId: Long) = dao.deleteEvent(eventId) // delete an event (all occurrences), and any overrides via foreign-key cascade
	suspend fun get(id: Long): Event? = dao.get(id)
	suspend fun search(query: String): List<Event> = dao.search(query)
	suspend fun save(event: Event): Long { // insert or update a base event (all occurrences), then return its id
		val rowId = dao.save(event)
		return if (event.id != 0L) event.id else rowId
	}


	// Live list of per-day occurrences overlapping [from, to]. Re-emits on any DB change.
	fun occurrences(from: LocalDate, to: LocalDate): Flow<List<EventOccurrence>> =
		combine(dao.events(from, to), dao.overrides(from, to)) { events, overrides ->
			EventRecurrence.expand(events, overrides, from, to)
		}

	// Delete this occurrence only
	suspend fun deleteOccurrence(o: EventOccurrence) = dao.saveOverride(
		EventOverride(eventId = o.eventId, originalDate = o.originalDate, cancelled = true)
	)

	// Drops all customisation of one occurrence (also un-cancels it).
	suspend fun resetOccurrence(eventId: Long, originalDate: LocalDate) = dao.deleteOverride(eventId, originalDate)

	// Edit this occurrence only. Build the override from the edited values.
	suspend fun saveOverride(override: EventOverride) = dao.saveOverride(override)

	// First reminder with triggerAt after [after], or null if none within about 3 years.
	suspend fun nextReminder(after: LocalDateTime): EventReminder? {
		val today = after.toLocalDate()
		var best: EventReminder? = null
		for (days in intArrayOf(60, 400, 1100)) {
			val end = today.plusDays(days.toLong())
			val found = EventReminder
				.of(reminderOccurrences(today, end))
				.filter { it.triggerAt.isAfter(after) }
				.minByOrNull { it.triggerAt }
			best = found
			// Stop once no occurrence beyond the window could trigger earlier than the one found
			if (
				found != null
				&& !found.triggerAt.toLocalDate().plusDays(EventReminder.MAX_DAYS.toLong()).isAfter(end)
			) break
		}
		return best
	}

	// Reminders with after < triggerAt <= upTo, oldest first. For firing due ones and catching up.
	suspend fun remindersBetween(after: LocalDateTime, upTo: LocalDateTime): List<EventReminder> =
		EventReminder
			.of(reminderOccurrences(
				after.toLocalDate(),
				upTo.toLocalDate().plusDays(EventReminder.MAX_DAYS.toLong())
			))
			.filter { it.triggerAt.isAfter(after) && !it.triggerAt.isAfter(upTo) }
			.sortedBy { it.triggerAt }


	private suspend fun reminderOccurrences(from: LocalDate, to: LocalDate): List<EventOccurrence> =
		withContext(Dispatchers.Default) {
			EventRecurrence.expand(
				dao.eventsWithReminders(from, to),
				dao.overrides(from, to).first(),
				from,
				to
			)
		}
}
