package com.sidephone.calendar.core

import android.util.Log
import com.sidephone.calendar.core.grid.DayGrid
import com.sidephone.calendar.core.grid.Grid
import com.sidephone.calendar.core.grid.MonthGrid
import com.sidephone.calendar.core.grid.YearGrid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Calendar as jCalendar

class Engine {
	private var grid: Grid
	private var year: Int
	private var month: Int
	private var day: Int
	private val currentDay: Int

	private val _output = MutableStateFlow("")
	val output: StateFlow<String> = _output


	init {
		val today = jCalendar.getInstance()
		grid = MonthGrid(today)
		year = today.get(jCalendar.YEAR)
		month = today.get(jCalendar.MONTH) + 1 // Calendar.MONTH is zero-based
		day = today.get(jCalendar.DAY_OF_MONTH)
		currentDay = day
	}


	fun onAction(action: Action) {
		when (action) {
			Action.UP -> up()
			Action.DOWN -> down()
			Action.LEFT -> left()
			Action.RIGHT -> right()
			Action.ZOOM_IN -> zoomIn()
			Action.ZOOM_OUT -> zoomOut()
			else -> { Log.d("Engine", "Action $action not implemented yet") }
		}

		_output.value = "Grid: ${grid::class.simpleName}, position: ${grid.position()}, $year-$month-$day"
	}


	private fun up() { grid.up(); syncDate() }
	private fun down() { grid.down(); syncDate() }
	private fun left() { grid.left(); syncDate() }
	private fun right() { grid.right(); syncDate() }



	private fun zoomIn() {
		syncDate()

		if (day < 0) day = currentDay
		val jCalendar = jCalendar.getInstance()
		jCalendar.set(year, month - 1, day)

		grid = when (grid) {
			is YearGrid -> MonthGrid(jCalendar)
			is MonthGrid -> DayGrid(jCalendar)
			else -> grid
		}
	}


	private fun zoomOut() {
		if (day < 0) day = currentDay
		val jCalendar = jCalendar.getInstance()
		jCalendar.set(year, month - 1, day)

		grid = when (grid) {
			is DayGrid -> MonthGrid(jCalendar)
			is MonthGrid -> YearGrid(jCalendar)
			else -> grid
		}
	}


	private fun syncDate() {
		when (grid) {
			is YearGrid -> month = grid.current()
			is MonthGrid -> day = grid.current()
			else -> {}
		}
	}
}
