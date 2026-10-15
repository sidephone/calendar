package com.sidephone.calendar.db

import com.sidephone.calendar.core.events.EventOccurrence
import com.sidephone.calendar.core.events.EventRecurrence
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate

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
}
