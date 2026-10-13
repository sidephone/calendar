package com.sidephone.calendar

import com.sidephone.calendar.core.grid.Grid
import java.util.Calendar
import kotlin.test.Test
import kotlin.test.assertEquals


private class Spec(val cells: List<List<Int>>, val startX: Int, val startY: Int)

class GridTest {
	/** Minimal grid used to exercise `current()` bounds handling and empty-grid behavior. */
	private class FakeGrid(
		cells: List<List<Int>>,
		startX: Int = 0,
		startY: Int = 0
	) : Grid(Calendar.getInstance().also { pending = Spec(cells, startX, startY) }) {

		override fun generateGrid(today: Calendar) = pending.cells
		override fun initialX(today: Calendar) = pending.startX
		override fun initialY(today: Calendar) = pending.startY

		private companion object {
			lateinit var pending: Spec
		}
	}

	@Test
	fun `current returns minus one when out of bounds`() {
		assertEquals(-1, FakeGrid(cells = listOf(listOf(1, 2)), startX = 5).current())
		assertEquals(-1, FakeGrid(cells = listOf(listOf(1, 2)), startY = 3).current())
		assertEquals(-1, FakeGrid(cells = listOf(listOf(1, 2)), startX = -1).current())
		assertEquals(-1, FakeGrid(cells = listOf(listOf(1, 2)), startY = -1).current())
	}

	@Test
	fun `navigation on an empty grid is a no-op`() {
		val grid = FakeGrid(emptyList())
		grid.up(); grid.down(); grid.left(); grid.right()
		assertEquals(Pair(0, 0), grid.position())
		assertEquals(-1, grid.current())
	}

	@Test
	fun `negative cells are skipped in every direction`() {
		val grid = FakeGrid(
			listOf(
				listOf(1, -1, 2),
				listOf(-1, -1, -1),
				listOf(3, -1, 4)
			)
		)
		grid.right(); assertEquals(2, grid.current())
		grid.down(); assertEquals(4, grid.current())
		grid.left(); assertEquals(3, grid.current())
		grid.up(); assertEquals(1, grid.current())
	}
}
