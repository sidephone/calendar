package com.sidephone.calendar

import com.sidephone.calendar.core.events.EventOccurrence
import com.sidephone.calendar.core.events.EventState
import com.sidephone.calendar.core.grid.DayGrid
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate
import java.time.YearMonth
import java.util.Calendar
import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private val TEST_DAY = LocalDate.of(2026, 10, 13)

private fun occurrence(id: Long) = EventOccurrence(
	eventId = id,
	originalDate = LocalDate.of(2026, 10, 13),
	date = LocalDate.of(2026, 10, 13),
	startDate = LocalDate.of(2026, 10, 13),
	endDate = LocalDate.of(2026, 10, 13),
	dayIndex = 0,
	dayCount = 1,
	title = "Test Event",
	description = "This is a test event",
	address = null,
	startTime = null,
	endTime = null,
	recurring = false,
)

private fun dayState(vararg ids: Long): EventState =
	EventState.Day(TEST_DAY, ids.map(::occurrence))

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

class DayGridTest {

	private fun dayGrid(vararg ids: Long): Pair<DayGrid, MutableStateFlow<EventState>> {
		val flow = MutableStateFlow(dayState(*ids))
		return DayGrid(date(2026, 10, 13), flow) to flow
	}

	private fun gridWith(state: EventState): Pair<DayGrid, MutableStateFlow<EventState>> {
		val flow = MutableStateFlow(state)
		return DayGrid(date(2026, 10, 13), flow) to flow
	}

	// ---- Initial state ----

	@Test
	fun `starts on the add new button`() {
		val (grid, _) = dayGrid(10, 20, 30)
		assertEquals(0, grid.current())
		assertEquals(Pair(0, 0), grid.position())
		assertEquals(0L, grid.eventId())
	}

	@Test
	fun `underlying grid is empty`() {
		val (grid, _) = dayGrid(10, 20, 30)
		assertTrue(grid.grid.isEmpty())
	}

	// ---- down ----

	@Test
	fun `down walks through the events in order`() {
		val (grid, _) = dayGrid(10, 20, 30)
		grid.down(); assertEquals(1, grid.current()); assertEquals(10L, grid.eventId())
		grid.down(); assertEquals(2, grid.current()); assertEquals(20L, grid.eventId())
		grid.down(); assertEquals(3, grid.current()); assertEquals(30L, grid.eventId())
	}

	@Test
	fun `down from the last event wraps to add new`() {
		val (grid, _) = dayGrid(10, 20, 30)
		repeat(3) { grid.down() }
		grid.down()
		assertEquals(0, grid.current())
		assertEquals(0L, grid.eventId())
	}

	@Test
	fun `down full lap returns to start`() {
		val (grid, _) = dayGrid(10, 20, 30)
		repeat(4) { grid.down() } // count + 1 items
		assertEquals(0, grid.current())
	}

	// ---- up ----

	@Test
	fun `up from add new wraps to the last event`() {
		val (grid, _) = dayGrid(10, 20, 30)
		grid.up()
		assertEquals(3, grid.current())
		assertEquals(30L, grid.eventId())
	}

	@Test
	fun `up walks back through the events`() {
		val (grid, _) = dayGrid(10, 20, 30)
		grid.up() // 3
		grid.up(); assertEquals(2, grid.current()); assertEquals(20L, grid.eventId())
		grid.up(); assertEquals(1, grid.current()); assertEquals(10L, grid.eventId())
		grid.up(); assertEquals(0, grid.current()); assertEquals(0L, grid.eventId())
	}

	@Test
	fun `up full lap returns to start`() {
		val (grid, _) = dayGrid(10, 20, 30)
		repeat(4) { grid.up() }
		assertEquals(0, grid.current())
	}

	// ---- left / right ----

	@Test
	fun `left and right are no-ops`() {
		val (grid, _) = dayGrid(10, 20, 30)
		grid.down()
		grid.left()
		grid.right()
		assertEquals(1, grid.current())
		assertEquals(Pair(0, 1), grid.position())
	}

	// ---- select ----

	@Test
	fun `select jumps to a valid index`() {
		val (grid, _) = dayGrid(10, 20, 30)
		assertTrue(grid.select(0, 2))
		assertEquals(2, grid.current())
		assertEquals(20L, grid.eventId())
	}

	@Test
	fun `select accepts both ends of the range`() {
		val (grid, _) = dayGrid(10, 20, 30)
		assertTrue(grid.select(0, 0))
		assertTrue(grid.select(0, 3))
		assertEquals(30L, grid.eventId())
	}

	@Test
	fun `select rejects out of range indices and keeps position`() {
		val (grid, _) = dayGrid(10, 20, 30)
		grid.select(0, 2)
		assertFalse(grid.select(0, 4))
		assertFalse(grid.select(0, -1))
		assertEquals(2, grid.current())
	}

	@Test
	fun `select ignores x`() {
		val (grid, _) = dayGrid(10, 20, 30)
		assertTrue(grid.select(5, 1))
		assertEquals(Pair(0, 1), grid.position())
		assertEquals(10L, grid.eventId())
	}

	// ---- No events (Day with an empty list) ----

	@Test
	fun `with an empty day navigation stays on add new`() {
		val (grid, _) = dayGrid()
		grid.down(); assertEquals(0, grid.current())
		grid.up(); assertEquals(0, grid.current())
		assertEquals(0L, grid.eventId())
	}

	@Test
	fun `with an empty day only index zero is selectable`() {
		val (grid, _) = dayGrid()
		assertTrue(grid.select(0, 0))
		assertFalse(grid.select(0, 1))
	}

	// ---- Non-Day states ----

	@Test
	fun `loading and empty states behave like no events`() {
		listOf(EventState.Loading, EventState.Empty).forEach { state ->
			val (grid, _) = gridWith(state)
			grid.down(); assertEquals(0, grid.current(), "$state down")
			grid.up(); assertEquals(0, grid.current(), "$state up")
			assertEquals(0L, grid.eventId(), "$state eventId")
			assertFalse(grid.select(0, 1), "$state select")
		}
	}

	@Test
	fun `month state never yields an event id`() {
		val month = EventState.Month(
			YearMonth.of(2026, 10),
			mapOf(TEST_DAY to 2, TEST_DAY.plusDays(1) to 1)
		)
		val (grid, _) = gridWith(month)
		assertEquals(0L, grid.eventId())
		grid.select(0, 1)
		assertEquals(0L, grid.eventId())
	}

	@Test
	fun `month state count still bounds navigation`() {
		val month = EventState.Month(YearMonth.of(2026, 10), mapOf(TEST_DAY to 3))
		val (grid, _) = gridWith(month)
		repeat(3) { grid.down() }
		assertEquals(3, grid.current())
		assertEquals(0L, grid.eventId())
	}

	// ---- Reacting to changes in the flow ----

	@Test
	fun `events become reachable after loading finishes`() {
		val (grid, flow) = gridWith(EventState.Loading)
		grid.down()
		assertEquals(0, grid.current())

		flow.value = dayState(10, 20)
		grid.down(); grid.down()
		assertEquals(2, grid.current())
		assertEquals(20L, grid.eventId())
	}

	@Test
	fun `new events become reachable`() {
		val (grid, flow) = dayGrid(10)
		flow.value = dayState(10, 20)
		grid.down(); grid.down()
		assertEquals(2, grid.current())
		assertEquals(20L, grid.eventId())
	}

	@Test
	fun `eventId is zero when the selected event was removed`() {
		val (grid, flow) = dayGrid(10, 20, 30)
		grid.select(0, 3)
		flow.value = dayState(10)
		assertEquals(0L, grid.eventId())
	}

	@Test
	fun `eventId is zero when the state becomes empty`() {
		val (grid, flow) = dayGrid(10, 20)
		grid.select(0, 1)
		flow.value = EventState.Empty
		assertEquals(0L, grid.eventId())
	}

	@Test
	fun `down after shrinking wraps to add new`() {
		val (grid, flow) = dayGrid(10, 20, 30)
		grid.select(0, 3)
		flow.value = dayState(10)
		grid.down()
		assertEquals(0, grid.current())
	}

	@Test
	fun `up after shrinking from add new goes to the new last event`() {
		val (grid, flow) = dayGrid(10, 20, 30)
		flow.value = dayState(10)
		grid.up()
		assertEquals(1, grid.current())
		assertEquals(10L, grid.eventId())
	}

	@Test
	fun `select uses the latest event count`() {
		val (grid, flow) = dayGrid(10)
		assertFalse(grid.select(0, 3))
		flow.value = dayState(10, 20, 30)
		assertTrue(grid.select(0, 3))
		assertEquals(30L, grid.eventId())
	}
}
