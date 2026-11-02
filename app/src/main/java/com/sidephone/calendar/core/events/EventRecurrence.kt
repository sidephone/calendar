package com.sidephone.calendar.core.events

import com.sidephone.calendar.db.Event
import com.sidephone.calendar.db.EventOverride
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.MonthDay
import java.time.YearMonth
import java.time.temporal.ChronoUnit

object EventRecurrence {
	enum class Freq { NONE, DAILY, WEEKLY, MONTHLY, YEARLY }

	/** Lazy, ascending sequence of the START dates of all occurrences of [e], honoring until/count. */
	fun dates(e: Event): Sequence<LocalDate> {
		val step = maxOf(1, e.interval).toLong()
		val start = e.startDate
		val all: Sequence<LocalDate> = when (e.freq) {
			Freq.NONE -> sequenceOf(start)
			Freq.DAILY -> generateSequence(start) { it.plusDays(step) }
			Freq.WEEKLY -> weekly(e, step)
			Freq.MONTHLY -> generateSequence(0L) { it + 1 }.mapNotNull { k ->
				val ym = YearMonth.from(start).plusMonths(k * step)
				// Months without that day (e.g. the 31st) are skipped, as in iCalendar
				if (start.dayOfMonth <= ym.lengthOfMonth()) ym.atDay(start.dayOfMonth) else null
			}

			Freq.YEARLY -> generateSequence(0L) { it + 1 }.mapNotNull { k ->
				val year = (start.year + k * step).toInt()
				val md = MonthDay.of(start.month, start.dayOfMonth)
				// Feb 29 is skipped in non-leap years
				if (md.isValidYear(year)) md.atYear(year) else null
			}
		}
		val bounded = e.untilDate?.let { u -> all.takeWhile { !it.isAfter(u) } } ?: all
		return e.maxOccurrences?.let { bounded.take(it) } ?: bounded
	}

	private fun weekly(e: Event, step: Long): Sequence<LocalDate> {
		val mask = if (e.weekdays == 0) 1 shl (e.startDate.dayOfWeek.value - 1) else e.weekdays
		val firstMonday = e.startDate.with(DayOfWeek.MONDAY)   // weeks start on Monday
		return generateSequence(0L) { it + 1 }
			.flatMap { k ->
				val monday = firstMonday.plusWeeks(k * step)
				(0..6).asSequence()
					.filter { mask and (1 shl it) != 0 }
					.map { monday.plusDays(it.toLong()) }
			}
			.filter { !it.isBefore(e.startDate) }
	}

	/**
	 * All occurrences (one entry per covered day) that fall within [from, to], sorted.
	 * [events] and [overrides] are what the DAO returned for the same range.
	 */
	fun expand(
		events: List<Event>,
		overrides: List<EventOverride>,
		from: LocalDate,
		to: LocalDate,
	): List<EventOccurrence> {
		val byKey = overrides.associateBy { it.eventId to it.originalDate }
		val eventsById = events.associateBy { it.id }
		val out = ArrayList<EventOccurrence>()

		// 1. Generated occurrences that overlap the range (they may START before it)
		for (e in events) {
			val span = e.spanDays
			val starts = dates(e)
				.dropWhile { it.plusDays(span).isBefore(from) }
				.takeWhile { !it.isAfter(to) }
			for (start in starts) {
				val o = byKey[e.id to start]
				if (o != null && (o.cancelled || o.movedStartDate != null)) continue // moved ones: step 2
				emit(out, e, o, start, start.plusDays(span), from, to, originalDate = start)
			}
		}

		// 2. Occurrences moved so that they overlap the range (their original date may be anywhere)
		for (o in overrides) {
			val s = o.movedStartDate ?: continue
			val en = o.movedEndDate ?: continue
			if (o.cancelled || s.isAfter(to) || en.isBefore(from)) continue
			val e = eventsById[o.eventId] ?: continue
			emit(out, e, o, s, en, from, to, originalDate = o.originalDate)
		}

		return out.sortedWith(
			compareBy(
				{ it.date },
				{ !(it.allDay || it.dayIndex > 0) },   // all-day and continuing events first
				{ it.startTime },
				{ it.title },
			)
		)
	}

	/** Adds one Occurrence per day of [start, end] that lies inside [from, to]. */
	private fun emit(
		out: MutableList<EventOccurrence>,
		e: Event,
		o: EventOverride?,
		start: LocalDate,
		end: LocalDate,
		from: LocalDate,
		to: LocalDate,
		originalDate: LocalDate,
	) {
		val allDay = o?.allDay ?: (e.startTime == null)
		val startTime = if (allDay) null else (o?.startTime ?: e.startTime)
		val endTime = if (startTime == null) null else (o?.endTime ?: e.endTime)
		val count = (ChronoUnit.DAYS.between(start, end) + 1).toInt()

		var d = if (start.isAfter(from)) start else from
		val last = if (end.isBefore(to)) end else to
		while (!d.isAfter(last)) {
			out += EventOccurrence(
				eventId = e.id,
				originalDate = originalDate,
				date = d,
				startDate = start,
				endDate = end,
				dayIndex = ChronoUnit.DAYS.between(start, d).toInt(),
				dayCount = count,
				title = o?.title ?: e.title,
				description = o?.description ?: e.description,
				address = o?.address ?: e.address,
				startTime = startTime,
				endTime = endTime,
				recurring = e.freq != Freq.NONE,
				reminders = o?.reminders ?: e.reminders,
			)
			d = d.plusDays(1)
		}
	}
}
