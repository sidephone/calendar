package com.sidephone.calendar.core.grid

import java.util.Calendar

abstract class Grid(today: Calendar) {
	protected val grid: List<List<Int>> = generateGrid(today)
	protected var currentX = initialX(today)
	protected var currentY = initialY(today)


	open fun current(): Int {
		return if (currentX < 0 || currentY < 0 || currentY >= grid.size || currentX >= grid[currentY].size) {
			-1
		} else {
			grid[currentY][currentX]
		}
	}


	fun position() = Pair(currentX, currentY)


	open fun up() {
		if (grid.isEmpty()) return
		do { if (--currentY < 0) currentY = grid.size - 1 }
		while (current() < 0)
	}


	open fun down() {
		if (grid.isEmpty()) return
		do { if (++currentY >= grid.size) currentY = 0 }
		while (current() < 0)
	}


	open fun left() {
		if (grid.isEmpty()) return
		do { if (--currentX < 0) currentX = grid[currentY].size - 1 }
		while (current() < 0)
	}


	open fun right() {
		if (grid.isEmpty()) return
		do { if (++currentX >= grid[currentY].size) currentX = 0 }
		while (current() < 0)
	}


	abstract fun generateGrid(today: Calendar): List<List<Int>>
	abstract fun initialX(today: Calendar): Int
	abstract fun initialY(today: Calendar): Int
}
