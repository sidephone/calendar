package com.sidephone.calendar


import com.sidephone.calendar.core.grid.Grid
import com.sidephone.calendar.core.grid.MonthGrid
import com.sidephone.calendar.core.grid.YearGrid
import java.util.Calendar
import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


private fun date(
	year: Int,
	month: Int,
	day: Int,
	firstDayOfWeek: Int = Calendar.SUNDAY
): Calendar = Calendar.getInstance(Locale.US).apply {
	clear()
	set(year, month - 1, day)
	this.firstDayOfWeek = firstDayOfWeek
}

class GridSelectTest {
	// October 2026, Sunday start. Today = 13 at (x=2, y=2).
	private fun grid() = MonthGrid(date(2026, 10, 13, Calendar.SUNDAY))

	@Test
	fun `select moves to a valid cell and returns true`() {
		val grid = grid()
		assertTrue(grid.select(5, 3))
		assertEquals(Pair(5, 3), grid.position())
		assertEquals(23, grid.current())
	}

	@Test
	fun `select the first and last day of the month`() {
		val grid = grid()
		assertTrue(grid.select(4, 0))
		assertEquals(1, grid.current())
		assertTrue(grid.select(6, 4))
		assertEquals(31, grid.current())
	}

	@Test
	fun `select the current cell succeeds and changes nothing`() {
		val grid = grid()
		assertTrue(grid.select(2, 2))
		assertEquals(Pair(2, 2), grid.position())
	}

	@Test
	fun `select a leading day of the previous month fails and keeps position`() {
		val grid = grid()
		assertFalse(grid.select(0, 0)) // -27
		assertEquals(Pair(2, 2), grid.position())
		assertEquals(13, grid.current())
	}

	@Test
	fun `select a trailing day of the next month fails and keeps position`() {
		val grid = grid()
		assertFalse(grid.select(3, 5)) // -4
		assertEquals(Pair(2, 2), grid.position())
		assertEquals(13, grid.current())
	}

	@Test
	fun `select out of bounds fails and keeps position`() {
		val grid = grid()
		assertFalse(grid.select(-1, 2))
		assertFalse(grid.select(7, 2))
		assertFalse(grid.select(2, -1))
		assertFalse(grid.select(2, 6))
		assertFalse(grid.select(-1, -1))
		assertFalse(grid.select(100, 100))
		assertEquals(Pair(2, 2), grid.position())
		assertEquals(13, grid.current())
	}

	@Test
	fun `navigation works from a selected cell`() {
		val grid = grid()
		assertTrue(grid.select(0, 1)) // 4
		grid.right()
		assertEquals(5, grid.current())
		grid.down()
		assertEquals(12, grid.current())
	}

	@Test
	fun `year grid select`() {
		val grid = YearGrid(date(2026, 10, 13))
		assertTrue(grid.select(1, 2))
		assertEquals(8, grid.current())
		assertFalse(grid.select(5, 0))
		assertFalse(grid.select(0, 5))
		assertEquals(Pair(1, 2), grid.position())
	}

	private class Spec(val cells: List<List<Int>>)

	private class FakeGrid(cells: List<List<Int>>) :
		Grid(Calendar.getInstance().also { pending = Spec(cells) }) {
		override fun generateGrid(today: Calendar) = pending.cells
		override fun initialX(today: Calendar) = 0
		override fun initialY(today: Calendar) = 0

		private companion object {
			lateinit var pending: Spec
		}
	}

	@Test
	fun `select on an empty grid returns false`() {
		val grid = FakeGrid(emptyList())
		assertFalse(grid.select(0, 0))
		assertEquals(Pair(0, 0), grid.position())
	}
}
