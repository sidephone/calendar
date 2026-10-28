package com.sidephone.calendar.core

import com.sidephone.calendar.core.events.CalendarEvents
import com.sidephone.calendar.core.events.EventTimeframe
import com.sidephone.calendar.core.grid.DayGrid
import com.sidephone.calendar.core.grid.Grid
import com.sidephone.calendar.core.grid.MonthGrid
import com.sidephone.calendar.core.grid.YearGrid
import com.sidephone.calendar.input.Keypad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.YearMonth
import java.util.Calendar as Jalendar

class Engine(private val events: CalendarEvents) {
	// date
	private val date = Jalendar.getInstance()
	private var grid: Grid = MonthGrid(date)
	private var year: Int = date.get(Jalendar.YEAR)
	private var month: Int = date.get(Jalendar.MONTH) + 1 // Calendar.MONTH is zero-based
	private var day: Int = date.get(Jalendar.DAY_OF_MONTH)

	// input
	internal var onEditEvent: (Long) -> Unit = {}
	internal var onDeleteEvent: () -> Unit = {}
	internal var keypad: StateFlow<Keypad.Layout> = MutableStateFlow(Keypad.Layout.UNKNOWN)

	// output
	private val _dateFlow: MutableStateFlow<Triple<Int, Int, Int>> = MutableStateFlow(Triple(year, month, day))
	val dateFlow: StateFlow<Triple<Int, Int, Int>> = _dateFlow
	val eventsFlow = events.list
	private val _gridFlow: MutableStateFlow<Grid> = MutableStateFlow(grid)
	val gridFlow: StateFlow<Grid> = _gridFlow


	init {
		events.setTimeframe(EventTimeframe.Month(month = YearMonth.of(year, month)))
	}


	fun onAction(action: Action): Boolean {
		when (action) {
			Action.UP -> up()
			Action.DOWN -> down()
			Action.LEFT -> {
				if (grid is DayGrid) {
					if (
						keypad.value == Keypad.Layout.SUNDIAL
						|| keypad.value == Keypad.Layout.GAMEPAD
						|| keypad.value == Keypad.Layout.T9_NO_DPAD
					) onDeleteEvent()
				} else {
					left()
				}
			}
			Action.RIGHT -> {
				if (grid !is DayGrid) {
					right()
				}
			}
			Action.CONFIRM_DELETE -> if (grid is DayGrid && (grid as DayGrid).eventId() != 0L) onDeleteEvent()
			Action.DELETE -> deleteCurrent()
			Action.NEXT_PAGE -> changePage(1)
			Action.PREVIOUS_PAGE -> changePage(-1)
			Action.ZOOM_IN -> zoomIn()
			Action.ZOOM_OUT -> zoomOut()
			Action.BACK -> when (grid) {
				is DayGrid -> zoomOut()
				is YearGrid -> zoomIn()
				else -> return false
			}
			Action.OK -> {
				if (grid is DayGrid) {
					onEditEvent((grid as DayGrid).eventId())
				} else {
					zoomIn()
				}
			}
		}

		_dateFlow.value = Triple(year, month, day)
		_gridFlow.value = grid.copy()

		return true
	}


	fun select(x: Int, y: Int): Boolean {
		if (!grid.select(x, y)) return false

		syncDate()
		loadEvents()
		_dateFlow.value = Triple(year, month, day)
		_gridFlow.value = grid.copy()
		return true
	}


	private fun up() {
		grid.up()
		syncDate()
		loadEvents()
	}


	private fun down() {
		grid.down()
		syncDate()
		loadEvents()
	}


	private fun left() {
		if (grid is DayGrid) {
			changePage(-1)
		} else {
			grid.left()
			syncDate()
			loadEvents()
		}
	}


	private fun right() {
		if (grid is DayGrid) {
			changePage(1)
		} else {
			grid.right()
			syncDate()
			loadEvents()
		}
	}


	private fun changePage(increment: Int) {
		date.set(year, month - 1, day)

		when (grid) {
			is YearGrid -> {
				date.add(Jalendar.YEAR, increment)
				grid = YearGrid(date)
			}
			is MonthGrid -> {
				date.add(Jalendar.MONTH, increment)
				grid = MonthGrid(date)
			}
			is DayGrid -> {
				date.add(Jalendar.DAY_OF_MONTH, increment)
				grid = DayGrid(date, events.list)
			}
			else -> {}
		}

		year = date.get(Jalendar.YEAR)
		month = date.get(Jalendar.MONTH) + 1
		day = date.get(Jalendar.DAY_OF_MONTH)

		loadEvents()
	}


	internal fun deleteCurrent() {
		if (grid is DayGrid) {
			val eventId = (grid as DayGrid).eventId()
			events.deleteEvent(eventId)
			loadEvents()
		}
	}


	private fun loadEvents() {
		when (grid) {
			is MonthGrid -> events.setTimeframe(EventTimeframe.Month(month = YearMonth.of(year, month)))
			is DayGrid -> events.setTimeframe(EventTimeframe.Day(date = LocalDate.of(year, month, day)))
			else -> events.setTimeframe(EventTimeframe.Year(year))
		}
	}


	private fun syncDate() {
		when (grid) {
			is YearGrid -> month = grid.current()
			is MonthGrid -> day = grid.current()
			else -> {}
		}
	}


	private fun zoomIn() {
		syncDate()

		if (day > YearMonth.of(year, month).lengthOfMonth()) {
			day = YearMonth.of(year, month).lengthOfMonth()
		}
		date.set(year, month - 1, day)

		grid = when (grid) {
			is YearGrid -> MonthGrid(date)
			is MonthGrid -> DayGrid(date, events.list)
			else -> grid
		}

		loadEvents()
	}


	private fun zoomOut() {
		date.set(year, month - 1, day)

		grid = when (grid) {
			is DayGrid -> MonthGrid(date)
			is MonthGrid -> YearGrid(date)
			else -> grid
		}

		loadEvents()
	}
}
