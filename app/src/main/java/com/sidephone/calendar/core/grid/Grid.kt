package com.sidephone.calendar.core.grid

import java.util.Calendar

abstract class Grid(today: Calendar) {
	protected val grid: List<List<Int>> = generateGrid(today)
	protected var currentX = initialX(today)
	protected var currentY = initialY(today)


	fun current(): Int {
		return if (currentX < 0 || currentY < 0 || currentY >= grid.size || currentX >= grid[currentY].size) {
			-1
		} else {
			grid[currentY][currentX]
		}
	}


	fun position() = Pair(currentX, currentY)


	fun up() {
		if (--currentY < 0) {
			currentY = grid.size - 1
		}
	}


	fun down() {
		if (++currentY >= grid.size) {
			currentY = 0
		}
	}


	fun left() {
		if (grid.isNotEmpty() && --currentX < 0) {
			currentX = grid[currentY].size - 1
		}
	}


	fun right() {
		if (grid.isNotEmpty() && ++currentX >= grid[currentY].size) {
			currentX = 0
		}
	}


	abstract fun generateGrid(today: Calendar): List<List<Int>>
	abstract fun initialX(today: Calendar): Int
	abstract fun initialY(today: Calendar): Int
}
