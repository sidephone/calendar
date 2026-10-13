package com.sidephone.calendar

import com.sidephone.calendar.core.grid.Grid
import com.sidephone.calendar.core.grid.MonthGrid
import java.util.Calendar
import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private fun date(
	year: Int,
	month: Int, // 1-based for readability
	day: Int,
	firstDayOfWeek: Int = Calendar.SUNDAY
): Calendar = Calendar.getInstance(Locale.US).apply {
	clear()
	set(year, month - 1, day)
	this.firstDayOfWeek = firstDayOfWeek
}

/** Exposes the protected grid for assertions. */
private fun Grid.cells(): List<List<Int>> {
	val field = Grid::class.java.getDeclaredField("grid").apply { isAccessible = true }
	@Suppress("UNCHECKED_CAST")
	return field.get(this) as List<List<Int>>
}

class MonthGridTest {

	// October 2026: Oct 1 is a Thursday, 31 days, September has 30 days.

	@Test
	fun `grid is always 6 rows of 7 columns`() {
		listOf(
			date(2026, 10, 13),
			date(2026, 2, 1),
			date(2024, 2, 29),
			date(2026, 12, 31)
		).forEach { today ->
			val cells = MonthGrid(today).cells()
			assertEquals(6, cells.size)
			cells.forEach { assertEquals(7, it.size) }
		}
	}

	@Test
	fun `october 2026 with sunday as first day of week`() {
		val expected = listOf(
			listOf(-27, -28, -29, -30, 1, 2, 3),
			listOf(4, 5, 6, 7, 8, 9, 10),
			listOf(11, 12, 13, 14, 15, 16, 17),
			listOf(18, 19, 20, 21, 22, 23, 24),
			listOf(25, 26, 27, 28, 29, 30, 31),
			listOf(-1, -2, -3, -4, -5, -6, -7)
		)
		assertEquals(expected, MonthGrid(date(2026, 10, 13, Calendar.SUNDAY)).cells())
	}

	@Test
	fun `october 2026 with monday as first day of week`() {
		val expected = listOf(
			listOf(-28, -29, -30, 1, 2, 3, 4),
			listOf(5, 6, 7, 8, 9, 10, 11),
			listOf(12, 13, 14, 15, 16, 17, 18),
			listOf(19, 20, 21, 22, 23, 24, 25),
			listOf(26, 27, 28, 29, 30, 31, -1),
			listOf(-2, -3, -4, -5, -6, -7, -8)
		)
		assertEquals(expected, MonthGrid(date(2026, 10, 13, Calendar.MONDAY)).cells())
	}

	@Test
	fun `month starting on first day of week has no leading days`() {
		// Feb 1 2026 is a Sunday, 28 days
		val cells = MonthGrid(date(2026, 2, 10, Calendar.SUNDAY)).cells()
		assertEquals(listOf(1, 2, 3, 4, 5, 6, 7), cells[0])
		assertEquals(listOf(22, 23, 24, 25, 26, 27, 28), cells[3])
		assertEquals(listOf(-1, -2, -3, -4, -5, -6, -7), cells[4])
		assertEquals(listOf(-8, -9, -10, -11, -12, -13, -14), cells[5])
	}

	@Test
	fun `leap year february has 29 days`() {
		val positives = MonthGrid(date(2024, 2, 15)).cells().flatten().filter { it > 0 }
		assertEquals((1..29).toList(), positives)
	}

	@Test
	fun `non leap year february has 28 days`() {
		val positives = MonthGrid(date(2026, 2, 15)).cells().flatten().filter { it > 0 }
		assertEquals((1..28).toList(), positives)
	}

	@Test
	fun `january uses december of previous year for leading days`() {
		// Jan 1 2026 is a Thursday -> 4 leading days from December (31 days)
		val first = MonthGrid(date(2026, 1, 5, Calendar.SUNDAY)).cells()[0]
		assertEquals(listOf(-28, -29, -30, -31, 1, 2, 3), first)
	}

	@Test
	fun `positive days appear exactly once and in order`() {
		val positives = MonthGrid(date(2026, 10, 13)).cells().flatten().filter { it > 0 }
		assertEquals((1..31).toList(), positives)
	}

	@Test
	fun `generateGrid does not mutate the calendar passed in`() {
		val today = date(2026, 10, 13)
		val before = today.timeInMillis
		MonthGrid(today)
		assertEquals(before, today.timeInMillis)
	}

	// ---- Initial position ----

	@Test
	fun `initial position points at today`() {
		val grid = MonthGrid(date(2026, 10, 13, Calendar.SUNDAY))
		assertEquals(Pair(2, 2), grid.position())
		assertEquals(13, grid.current())
	}

	@Test
	fun `initial position respects first day of week`() {
		val grid = MonthGrid(date(2026, 10, 13, Calendar.MONDAY))
		assertEquals(Pair(1, 2), grid.position())
		assertEquals(13, grid.current())
	}

	@Test
	fun `initial position works for first and last day of month`() {
		val first = MonthGrid(date(2026, 10, 1, Calendar.SUNDAY))
		assertEquals(Pair(4, 0), first.position())
		assertEquals(1, first.current())

		val last = MonthGrid(date(2026, 10, 31, Calendar.SUNDAY))
		assertEquals(Pair(6, 4), last.position())
		assertEquals(31, last.current())
	}

	// ---- Navigation ----

	@Test
	fun `down moves to the next week`() {
		val grid = MonthGrid(date(2026, 10, 13))
		grid.down()
		assertEquals(20, grid.current())
	}

	@Test
	fun `up moves to the previous week`() {
		val grid = MonthGrid(date(2026, 10, 13))
		grid.up()
		assertEquals(6, grid.current())
	}

	@Test
	fun `right moves to the next day`() {
		val grid = MonthGrid(date(2026, 10, 13))
		grid.right()
		assertEquals(14, grid.current())
	}

	@Test
	fun `left moves to the previous day`() {
		val grid = MonthGrid(date(2026, 10, 13))
		grid.left()
		assertEquals(12, grid.current())
	}

	@Test
	fun `up skips leading days of the previous month and wraps to the bottom`() {
		val grid = MonthGrid(date(2026, 10, 13)) // column 2
		grid.up() // 6
		grid.up() // row 0 is -29 (skip), row 5 is next month (skip), lands on row 4
		assertEquals(27, grid.current())
		assertEquals(Pair(2, 4), grid.position())
	}

	@Test
	fun `down skips trailing days of the next month and wraps to the top`() {
		val grid = MonthGrid(date(2026, 10, 27)) // row 4, column 2
		grid.down() // row 5 negative, row 0 negative, lands on row 1
		assertEquals(6, grid.current())
		assertEquals(Pair(2, 1), grid.position())
	}

	@Test
	fun `right wraps around within a week and skips non month cells`() {
		val grid = MonthGrid(date(2026, 10, 3)) // last column of row 0
		grid.right() // wraps to column 0 (negative), skips to column 4
		assertEquals(1, grid.current())
		assertEquals(Pair(4, 0), grid.position())
	}

	@Test
	fun `left wraps around within a week and skips non month cells`() {
		val grid = MonthGrid(date(2026, 10, 1)) // first positive cell of row 0
		grid.left() // columns 3..0 negative, wraps to column 6
		assertEquals(3, grid.current())
		assertEquals(Pair(6, 0), grid.position())
	}

	@Test
	fun `right on a row with trailing days skips them`() {
		val grid = MonthGrid(date(2026, 10, 31, Calendar.MONDAY)) // row 4, column 5, then -1
		grid.right() // column 6 is -1 (skip), wraps to column 0
		assertEquals(26, grid.current())
	}

	@Test
	fun `current never returns a non positive value after any navigation`() {
		val grid = MonthGrid(date(2026, 10, 13))
		val moves = listOf(grid::up, grid::down, grid::left, grid::right)
		repeat(200) { i ->
			moves[i % 4]()
			moves[(i * 7 + 1) % 4]()
			assertTrue(grid.current() > 0, "Landed on ${grid.current()} at ${grid.position()}")
		}
	}

	@Test
	fun `full lap in every direction returns to start`() {
		var grid = MonthGrid(date(2026, 10, 15))
		var start = grid.position()

		repeat(7) { grid.right() }
		assertEquals(start, grid.position())

		repeat(7) { grid.left() }
		assertEquals(start, grid.position())

		// Column 4 (Thursday) has 5 valid rows in Oct 2026
		grid = MonthGrid(date(2026, 10, 1))
		start = grid.position()

		repeat(5) { grid.down() }
		assertEquals(start, grid.position())

		repeat(5) { grid.up() }
		assertEquals(start, grid.position())

		// Column 3 (Wednesday) has 4 valid rows in Oct 2026
		grid = MonthGrid(date(2026, 10, 14))
		start = grid.position()

		repeat(4) { grid.down() }
		assertEquals(start, grid.position())

		repeat(4) { grid.up() }
		assertEquals(start, grid.position())
	}
}
