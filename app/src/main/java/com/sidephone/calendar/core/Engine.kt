package com.sidephone.calendar.core

import com.sidephone.calendar.core.grid.DayGrid
import com.sidephone.calendar.core.grid.Grid
import com.sidephone.calendar.core.grid.MonthGrid
import com.sidephone.calendar.core.grid.YearGrid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Calendar as Jalendar

class Engine {
	private val date = Jalendar.getInstance()

	private var grid: Grid = MonthGrid(date)
	private var year: Int = date.get(Jalendar.YEAR)
	private var month: Int = date.get(Jalendar.MONTH) + 1 // Calendar.MONTH is zero-based
	private var day: Int = date.get(Jalendar.DAY_OF_MONTH)
	private val currentDay: Int = day

	private val _output = MutableStateFlow("")
	val output: StateFlow<String> = _output


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
				if (grid is DayGrid) {
					_output.value = "OK action on date: $year-$month-$day"
					return
				} else {
					zoomIn()
				}
			}
		}

		_output.value = "Grid: ${grid::class.simpleName}, position: ${grid.position()}, $year-$month-$day"
	}


	private fun up() { grid.up(); syncDate() }
	private fun down() { grid.down(); syncDate() }
	private fun left() { grid.left(); syncDate() }
	private fun right() { grid.right(); syncDate() }


	private fun changePage(increment: Int) {
		normalizeDay()
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
				grid = DayGrid(date)
			}
			else -> {}
		}

		year = date.get(Jalendar.YEAR)
		month = date.get(Jalendar.MONTH) + 1
		day = date.get(Jalendar.DAY_OF_MONTH)
	}


	private fun normalizeDay() {
		day = if (day < 0) currentDay else day
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

		normalizeDay()
		date.set(year, month - 1, day)

		grid = when (grid) {
			is YearGrid -> MonthGrid(date)
			is MonthGrid -> DayGrid(date)
			else -> grid
		}
	}


	private fun zoomOut() {
		normalizeDay()
		date.set(year, month - 1, day)

		grid = when (grid) {
			is DayGrid -> MonthGrid(date)
			is MonthGrid -> YearGrid(date)
			else -> grid
		}
	}
}
