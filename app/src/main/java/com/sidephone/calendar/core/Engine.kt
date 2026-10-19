package com.sidephone.calendar.core

import android.app.Application
import com.sidephone.calendar.core.events.CalendarEvents
import com.sidephone.calendar.core.events.EventTimeframe
import com.sidephone.calendar.core.grid.DayGrid
import com.sidephone.calendar.core.grid.Grid
import com.sidephone.calendar.core.grid.MonthGrid
import com.sidephone.calendar.core.grid.YearGrid
import com.sidephone.calendar.db.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.YearMonth
import java.util.Calendar as Jalendar

class Engine(app: Application) {
	// events
	private var events = CalendarEvents(app)

	// date
	private val date = Jalendar.getInstance()
	private val _grid: MutableStateFlow<Grid> = MutableStateFlow(MonthGrid(date))
	private var year: Int = date.get(Jalendar.YEAR)
	private var month: Int = date.get(Jalendar.MONTH) + 1 // Calendar.MONTH is zero-based
	private var day: Int = date.get(Jalendar.DAY_OF_MONTH)

	// output
	private val _dateFlow: MutableStateFlow<Triple<Int, Int, Int>> = MutableStateFlow(Triple(year, month, day))
	val dateFlow: StateFlow<Triple<Int, Int, Int>> = _dateFlow
	val eventsFlow = events.list
	val gridFlow: StateFlow<Grid> = _grid

	// debug
	private val _debugText = MutableStateFlow("")
	val debugText: StateFlow<String> = _debugText


	init {
		events.setTimeframe(EventTimeframe.Month(month = YearMonth.of(year, month)))
		_debugText.value = "Grid: ${_grid.value::class.simpleName}, date: ${year}-${month}-${day} position: ${_grid.value.position()} = ${_grid.value.current()}"
	}


	fun onAction(action: Action) {
		when (action) {
			Action.UP -> up()
			Action.DOWN -> down()
			Action.LEFT -> left()
			Action.RIGHT -> right()
			Action.NEXT_PAGE -> changePage(1)
			Action.PREVIOUS_PAGE -> changePage(-1)
			Action.ZOOM_IN -> zoomIn()
			Action.ZOOM_OUT -> zoomOut()
			Action.OK -> {
				if (_grid.value is DayGrid) {
					if (_grid.value.current() == 0) {
						addEvent()
					} else {
						_debugText.value = "View event on date: ${year}-${month}-${day}, index: ${_grid.value.current()}"
					}
					return
				} else {
					zoomIn()
				}
			}
		}

		_debugText.value = "Grid: ${_grid.value::class.simpleName}, date: ${year}-${month}-${day} position: ${_grid.value.position()} = ${_grid.value.current()}"
	}


	private fun up() {
		_grid.value.up()
		syncDate()
		loadEvents()
	}


	private fun down() {
		_grid.value.down()
		syncDate()
		loadEvents()
	}


	private fun left() {
		_grid.value.left()
		syncDate()
		loadEvents()
	}


	private fun right() {
		_grid.value.right()
		syncDate()
		loadEvents()
	}


	private fun changePage(increment: Int) {
		date.set(year, month - 1, day)

		when (_grid.value) {
			is YearGrid -> {
				date.add(Jalendar.YEAR, increment)
				_grid.value = YearGrid(date)
			}
			is MonthGrid -> {
				date.add(Jalendar.MONTH, increment)
				_grid.value = MonthGrid(date)
			}
			is DayGrid -> {
				date.add(Jalendar.DAY_OF_MONTH, increment)
				_grid.value = DayGrid(date, events.list)
			}
			else -> {}
		}

		year = date.get(Jalendar.YEAR)
		month = date.get(Jalendar.MONTH) + 1
		day = date.get(Jalendar.DAY_OF_MONTH)
		_dateFlow.value = Triple(year, month, day)

		loadEvents()
	}


	private fun addEvent() {
		events.saveEvent(Event(
			title = "Stub Event #" + (Math.random() * 1_000_000).toInt(),
			startDate = LocalDate.of(year, month, day)
		))
	}


	private fun loadEvents() {
		when (_grid.value) {
			is MonthGrid -> events.setTimeframe(EventTimeframe.Month(month = YearMonth.of(year, month)))
			is DayGrid -> events.setTimeframe(EventTimeframe.Day(date = LocalDate.of(year, month, day)))
			else -> events.setTimeframe(EventTimeframe.Year(year))
		}
	}


	private fun syncDate() {
		when (_grid.value) {
			is YearGrid -> month = _grid.value.current()
			is MonthGrid -> day = _grid.value.current()
			else -> {}
		}

		_dateFlow.value = Triple(year, month, day)
	}


	private fun zoomIn() {
		syncDate()

		date.set(year, month - 1, day)

		_grid.value = when (_grid.value) {
			is YearGrid -> MonthGrid(date)
			is MonthGrid -> DayGrid(date, events.list)
			else -> _grid.value
		}

		loadEvents()
	}


	private fun zoomOut() {
		date.set(year, month - 1, day)

		_grid.value = when (_grid.value) {
			is DayGrid -> MonthGrid(date)
			is MonthGrid -> YearGrid(date)
			else -> _grid.value
		}

		loadEvents()
	}
}
