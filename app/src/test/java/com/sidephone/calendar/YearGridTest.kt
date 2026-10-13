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
	fun `grid is 3 rows by 4 columns of months 1 to 12`() {
		val expected = listOf(
			listOf(1, 2, 3, 4),
			listOf(5, 6, 7, 8),
			listOf(9, 10, 11, 12)
		)
		assertEquals(expected, YearGrid(date(2026, 10, 13)).cells())
	}

	@Test
	fun `initial position matches today's month for every month`() {
		for (month in 1..12) {
			val grid = YearGrid(date(2026, month, 1))
			assertEquals(month, grid.current(), "month $month")
			assertEquals(Pair((month - 1) % 4, (month - 1) / 4), grid.position(), "month $month")
		}
	}

	@Test
	fun `initial position for october`() {
		val grid = YearGrid(date(2026, 10, 13))
		assertEquals(Pair(1, 2), grid.position())
		assertEquals(10, grid.current())
	}

	@Test
	fun `right moves forward and wraps within the row`() {
		val grid = YearGrid(date(2026, 1, 1))
		grid.right()
		assertEquals(2, grid.current())
		repeat(3) { grid.right() }
		assertEquals(1, grid.current())
	}

	@Test
	fun `left moves backward and wraps within the row`() {
		val grid = YearGrid(date(2026, 1, 1))
		grid.left()
		assertEquals(4, grid.current())
		assertEquals(Pair(3, 0), grid.position())
	}

	@Test
	fun `down moves forward and wraps to the top`() {
		val grid = YearGrid(date(2026, 2, 1))
		grid.down()
		assertEquals(6, grid.current())
		grid.down()
		assertEquals(10, grid.current())
		grid.down()
		assertEquals(2, grid.current())
	}

	@Test
	fun `up moves backward and wraps to the bottom`() {
		val grid = YearGrid(date(2026, 2, 1))
		grid.up()
		assertEquals(10, grid.current())
		assertEquals(Pair(1, 2), grid.position())
	}

	@Test
	fun `every cell is reachable and consistent with position`() {
		val grid = YearGrid(date(2026, 1, 1))
		val seen = mutableSetOf<Int>()
		repeat(3) {
			repeat(4) {
				seen.add(grid.current())
				grid.right()
			}
			grid.down()
		}
		assertEquals((1..12).toSet(), seen)
	}
}
