package com.sidephone.calendar.db

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidephone.calendar.db.EventsState.Day
import com.sidephone.calendar.db.EventsState.Month
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

sealed interface Selection {
	data class Day(val date: LocalDate) : Selection

	/**
	 * Pass the grid's first/last visible dates as [from]/[to] if the grid shows days of
	 * adjacent months, so those cells get markers too.
	 */
	data class Month(
		val month: YearMonth,
		val from: LocalDate = month.atDay(1),
		val to: LocalDate = month.atEndOfMonth(),
	) : Selection


	data class Year(val year: Int) : Selection
}

sealed interface EventsState {
	data object Loading : EventsState
	data object Empty : EventsState
	data class Day(val date: LocalDate, val items: List<Occurrence>) : EventsState

	// Number of entries per day; a multi-day event counts on every day it covers
	data class Month(val month: YearMonth, val countsByDay: Map<LocalDate, Int>) : EventsState
}

class CalendarEventsModel(app: Application) : ViewModel() {
	private val repo: EventRepository = EventRepository(AppDatabase.get(app).eventDao())

	// Drive this from the same place that drives your grid StateFlow
	private val selection = MutableStateFlow<Selection>(Selection.Day(LocalDate.now()))

	@OptIn(ExperimentalCoroutinesApi::class)
	val events: StateFlow<EventsState> = selection
		.flatMapLatest { sel ->
			when (sel) {
				is Selection.Day -> repo.occurrences(sel.date, sel.date).map { Day(sel.date, it) }
				is Selection.Month -> repo.occurrences(sel.from, sel.to).map { list ->
					Month(sel.month, list.groupingBy { it.date }.eachCount())
				}
				is Selection.Year -> flowOf(EventsState.Empty)
			}
		}
		.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EventsState.Loading)

	fun select(s: Selection) {
		selection.value = s
	}

	// ── Editing ──
	fun saveEvent(event: Event) = viewModelScope.launch { repo.saveEvent(event) }
	fun deleteEvent(eventId: Long) = viewModelScope.launch { repo.deleteEvent(eventId) }
	fun deleteOccurrence(o: Occurrence) = viewModelScope.launch { repo.deleteOccurrence(o) }
	fun saveOverride(o: EventOverride) = viewModelScope.launch { repo.saveOverride(o) }
	fun resetOccurrence(o: Occurrence) = viewModelScope.launch { repo.resetOccurrence(o.eventId, o.originalDate) }
}
