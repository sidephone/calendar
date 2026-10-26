package com.sidephone.calendar.core.grid

import java.util.Calendar

abstract class Grid(today: Calendar) : Cloneable {
	val grid: List<List<Int>> = generateGrid(today)
	protected var currentX = initialX(today)
	protected var currentY = initialY(today)


	fun copy(): Grid {
		return super.clone() as Grid
	}


	open fun current(): Int {
		return if (currentX < 0 || currentY < 0 || currentY >= grid.size || currentX >= grid[currentY].size) {
			-1
		} else {
			grid[currentY][currentX]
		}
	}


	fun position() = Pair(currentX, currentY)


	open fun select(x: Int, y: Int): Boolean {
		val prevX = currentX
		val prevY = currentY

		if (y < 0 || y >= grid.size || x < 0 || x >= grid[y].size) return false

		currentX = x
		currentY = y

		if (current() < 0) {
			currentX = prevX
			currentY = prevY
			return false
		}

		return true
	}


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
