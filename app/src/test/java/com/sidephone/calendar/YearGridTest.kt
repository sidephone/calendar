package com.sidephone.calendar

import com.sidephone.calendar.core.grid.Grid
import com.sidephone.calendar.core.grid.YearGrid
import java.util.Calendar
import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals

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

class YearGridTest {

	@Test
	fun `grid is 4 rows by 3 columns of months 1 to 12`() {
		val expected = listOf(
			listOf(1, 2, 3),
			listOf(4, 5, 6),
			listOf(7, 8, 9),
			listOf(10, 11, 12)
		)
		assertEquals(expected, YearGrid(date(2026, 10, 13)).cells())
	}

	@Test
	fun `initial position matches today's month for every month`() {
		for (month in 1..12) {
			val grid = YearGrid(date(2026, month, 1))
			assertEquals(month, grid.current(), "month $month")
			assertEquals(Pair((month - 1) % 3, (month - 1) / 3), grid.position(), "month $month")
		}
	}

	@Test
	fun `initial position for october`() {
		val grid = YearGrid(date(2026, 10, 13))
		assertEquals(Pair(0, 3), grid.position())
		assertEquals(10, grid.current())
	}

	@Test
	fun `right moves forward and wraps within the row`() {
		val grid = YearGrid(date(2026, 1, 1))
		grid.right()
		assertEquals(2, grid.current())
		repeat(2) { grid.right() }
		assertEquals(1, grid.current())
	}

	@Test
	fun `left moves backward and wraps within the row`() {
		val grid = YearGrid(date(2026, 1, 1))
		grid.left()
		assertEquals(3, grid.current())
		assertEquals(Pair(2, 0), grid.position())
	}

	@Test
	fun `down moves forward and wraps to the top`() {
		val grid = YearGrid(date(2026, 2, 1))
		grid.down()
		assertEquals(5, grid.current())
		assertEquals(Pair(1, 1), grid.position())
		grid.down()
		assertEquals(8, grid.current())
		assertEquals(Pair(1, 2), grid.position())
		grid.down()
		assertEquals(11, grid.current())
		assertEquals(Pair(1, 3), grid.position())
		grid.down()
		assertEquals(2, grid.current())
		assertEquals(Pair(1, 0), grid.position())
	}

	@Test
	fun `up moves backward and wraps to the bottom`() {
		val grid = YearGrid(date(2026, 3, 1))
		grid.up()
		assertEquals(12, grid.current())
		assertEquals(Pair(2, 3), grid.position())
		grid.up()
		assertEquals(9, grid.current())
		assertEquals(Pair(2, 2), grid.position())
		grid.up()
		assertEquals(6, grid.current())
		assertEquals(Pair(2, 1), grid.position())
		grid.up()
		assertEquals(3, grid.current())
		assertEquals(Pair(2, 0), grid.position())
	}

	@Test
	fun `every cell is reachable and consistent with position`() {
		val grid = YearGrid(date(2026, 1, 1))
		val seen = mutableSetOf<Int>()
		repeat(4) {
			repeat(3) {
				seen.add(grid.current())
				grid.right()
			}
			grid.down()
		}
		assertEquals((1..12).toSet(), seen)
	}
}
