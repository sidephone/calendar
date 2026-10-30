package com.sidephone.calendar

import com.sidephone.calendar.core.events.EventOccurrence
import com.sidephone.calendar.core.events.EventState
import com.sidephone.calendar.core.grid.DayGrid
import com.sidephone.calendar.core.grid.MonthGrid
import com.sidephone.calendar.core.grid.YearGrid
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
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
	reminders = emptyList()
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


class GridCopyTest {

	@Test
	fun `copy has the same position and content`() {
		val original = MonthGrid(date(2026, 10, 13))
		val copy = original.copy()
		assertEquals(original.position(), copy.position())
		assertEquals(original.current(), copy.current())
		assertEquals(original.grid, copy.grid)
	}

	@Test
	fun `copy is a distinct instance of the same type`() {
		val original = MonthGrid(date(2026, 10, 13))
		val copy = original.copy()
		assertNotSame(original, copy)
		assertTrue(copy is MonthGrid)
	}

	@Test
	fun `moving the copy does not move the original`() {
		val original = MonthGrid(date(2026, 10, 13))
		val copy = original.copy()
		copy.right()
		copy.down()
		assertEquals(Pair(2, 2), original.position())
		assertEquals(13, original.current())
		assertEquals(21, copy.current())
	}

	@Test
	fun `moving the original does not move the copy`() {
		val original = MonthGrid(date(2026, 10, 13))
		val copy = original.copy()
		original.up()
		original.left()
		assertEquals(Pair(2, 2), copy.position())
		assertEquals(13, copy.current())
	}

	@Test
	fun `select on the copy does not affect the original`() {
		val original = YearGrid(date(2026, 1, 1))
		val copy = original.copy()
		assertTrue(copy.select(2, 3))
		assertEquals(12, copy.current())
		assertEquals(1, original.current())
	}

	@Test
	fun `copy of a copy stays independent`() {
		val a = MonthGrid(date(2026, 10, 13))
		val b = a.copy()
		b.right()
		val c = b.copy()
		c.right()
		assertEquals(13, a.current())
		assertEquals(14, b.current())
		assertEquals(15, c.current())
	}

	@Test
	fun `day grid copy keeps position and is independent`() {
		val flow = MutableStateFlow(dayState(10, 20, 30))
		val original = DayGrid(date(2026, 10, 13), flow)
		original.down()
		val copy = original.copy()
		assertTrue(copy is DayGrid)
		assertEquals(1, copy.current())

		copy.down()
		assertEquals(1, original.current())
		assertEquals(2, copy.current())
	}

	@Test
	fun `day grid copy shares the same events flow`() {
		val flow = MutableStateFlow(dayState(10, 20, 30))
		val original = DayGrid(date(2026, 10, 13), flow)
		val copy = original.copy() as DayGrid
		original.select(0, 3)
		copy.select(0, 3)

		flow.value = dayState(10)
		assertEquals(0, original.eventId()) // index 3 is now out of range
		assertEquals(0, copy.eventId())
	}
}
