package com.sidephone.calendar.db

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate

class EventRepository(private val dao: EventDao) {

	/** Live list of per-day occurrences overlapping [from, to]. Re-emits on any DB change. */
	fun occurrences(from: LocalDate, to: LocalDate): Flow<List<Occurrence>> =
		combine(dao.events(from, to), dao.overrides(from, to)) { events, overrides ->
			Recurrence.expand(events, overrides, from, to)
		}

	suspend fun getEvent(id: Long): Event? = dao.get(id)

	suspend fun search(query: String): List<Event> = dao.search(query)

	/** Insert or update the base event ("all occurrences"). Returns its id. */
	suspend fun saveEvent(event: Event): Long {
		val rowId = dao.save(event)
		return if (event.id != 0L) event.id else rowId
	}

	/** "Delete all occurrences". Overrides are removed by the foreign-key cascade. */
	suspend fun deleteEvent(eventId: Long) = dao.deleteEvent(eventId)

	/** "Delete this occurrence only". */
	suspend fun deleteOccurrence(o: Occurrence) =
		dao.saveOverride(
			EventOverride(
				eventId = o.eventId,
				originalDate = o.originalDate,
				cancelled = true
			)
		)

	/** "Edit this occurrence only". Build the override from the edited values. */
	suspend fun saveOverride(override: EventOverride) = dao.saveOverride(override)

	/** Drops all customisation of one occurrence (also un-cancels it). */
	suspend fun resetOccurrence(eventId: Long, originalDate: LocalDate) =
		dao.deleteOverride(eventId, originalDate)
}
