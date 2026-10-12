package com.sidephone.calendar.core

import android.util.Log
import com.sidephone.calendar.core.grid.DayGrid
import com.sidephone.calendar.core.grid.Grid
import com.sidephone.calendar.core.grid.MonthGrid
import com.sidephone.calendar.core.grid.YearGrid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class Engine {
	private var grid: Grid = MonthGrid()

	private val _output = MutableStateFlow("")
	val output: StateFlow<String> = _output


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

		_output.value = grid.toString()
	}


	private fun up() = grid.up()
	private fun down() = grid.down()
	private fun left() = grid.left()
	private fun right() = grid.right()



	private fun zoomIn() {
		grid = when (grid) {
			is YearGrid -> MonthGrid()
			is MonthGrid -> DayGrid()
			else -> grid
		}
	}


	private fun zoomOut() {
		grid = when (grid) {
			is DayGrid -> MonthGrid()
			is MonthGrid -> YearGrid()
			else -> grid
		}
	}
}
