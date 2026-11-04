package com.sidephone.calendar.core.events

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidephone.calendar.core.reminders.AlarmScheduler
import com.sidephone.calendar.db.AppDatabase
import com.sidephone.calendar.db.Event
import com.sidephone.calendar.db.EventOverride
import com.sidephone.calendar.db.EventStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class CalendarEvents(app: Application, private val alarmScheduler: AlarmScheduler) : ViewModel() {
	private val db: EventStore = EventStore(AppDatabase.get(app).eventDao())
	private val timeframe = MutableStateFlow<EventTimeframe>(EventTimeframe.Day(LocalDate.now()))

	val list = getListForTimeframe()

	init {
		viewModelScope.launch { alarmScheduler.scheduleFrom(db) } // schedule lost alarms on app start
	}

	/*** Get One ***/
	suspend fun getEvent(id: Long): Event? = db.get(id)


	/*** Get Many ***/

	@OptIn(ExperimentalCoroutinesApi::class)
	private fun getListForTimeframe(): StateFlow<EventState> = timeframe
		.flatMapLatest { stateFor(it).onStart { emit(EventState.Loading) } }
		.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EventState.Loading)

	private fun stateFor(t: EventTimeframe): Flow<EventState> = when (t) {
		is EventTimeframe.Day -> db.occurrences(t.date, t.date).map { EventState.Day(t.date, it) }
		is EventTimeframe.Month -> db.occurrences(t.from, t.to).map { EventState.Month(t.month, it.groupingBy { o -> o.date }.eachCount()) }
		is EventTimeframe.Year -> flowOf(EventState.Empty)
	}


	/**
	 * Change the current timeframe for which events are being displayed. This will trigger a reload,
	 * and update `list` and `count` accordingly.
	 */
	fun setTimeframe(t: EventTimeframe) { if (timeframe.value != t) timeframe.value = t }


	/*** Edit ***/

	fun saveEvent(event: Event) = viewModelScope.launch { db.save(event); alarmScheduler.scheduleFrom(db) }
	fun deleteEvent(eventId: Long) = viewModelScope.launch { db.delete(eventId); alarmScheduler.scheduleFrom(db) }
	fun deleteOccurrence(o: EventOccurrence) = viewModelScope.launch { db.deleteOccurrence(o); alarmScheduler.scheduleFrom(db) }
	fun saveOverride(o: EventOverride) = viewModelScope.launch { db.saveOverride(o); alarmScheduler.scheduleFrom(db) }
	fun resetOccurrence(o: EventOccurrence) = viewModelScope.launch { db.resetOccurrence(o.eventId, o.originalDate); alarmScheduler.scheduleFrom(db) }
}
