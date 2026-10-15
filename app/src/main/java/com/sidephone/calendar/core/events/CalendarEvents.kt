package com.sidephone.calendar.core.events

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidephone.calendar.db.AppDatabase
import com.sidephone.calendar.db.Event
import com.sidephone.calendar.db.EventOverride
import com.sidephone.calendar.db.EventStore
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

class CalendarEvents(app: Application) : ViewModel() {
	private val db: EventStore = EventStore(AppDatabase.get(app).eventDao())
	private val timeframe = MutableStateFlow<EventTimeframe>(EventTimeframe.Day(LocalDate.now()))

	val list = getListForTimeframe()


	/*** Get ***/

	@OptIn(ExperimentalCoroutinesApi::class)
	private fun getListForTimeframe(): StateFlow<EventState> = timeframe
		.flatMapLatest { timeframe ->
			when (timeframe) {
				is EventTimeframe.Day -> db.occurrences(timeframe.date, timeframe.date).map {
					EventState.Day(
						timeframe.date,
						it
					)
				}
				is EventTimeframe.Month -> db.occurrences(timeframe.from, timeframe.to).map { list ->
					EventState.Month(timeframe.month, list.groupingBy { it.date }.eachCount())
				}
				is EventTimeframe.Year -> flowOf(EventState.Empty)
			}
		}
		.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EventState.Loading)


	/**
	 * Change the current timeframe for which events are being displayed. This will trigger a reload,
	 * and update `list` and `count` accordingly.
	 */
	fun setTimeframe(t: EventTimeframe) { if (timeframe.value != t) timeframe.value = t }


	/*** Edit ***/

	fun saveEvent(event: Event) = viewModelScope.launch { db.save(event) }
	fun deleteEvent(eventId: Long) = viewModelScope.launch { db.delete(eventId) }
	fun deleteOccurrence(o: EventOccurrence) = viewModelScope.launch { db.deleteOccurrence(o) }
	fun saveOverride(o: EventOverride) = viewModelScope.launch { db.saveOverride(o) }
	fun resetOccurrence(o: EventOccurrence) = viewModelScope.launch { db.resetOccurrence(o.eventId, o.originalDate) }
}
