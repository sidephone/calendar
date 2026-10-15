package com.sidephone.calendar.db

import androidx.room3.ColumnTypeConverter
import java.time.LocalDate
import java.time.LocalTime

class Converters {
	@ColumnTypeConverter
	fun fromDate(d: LocalDate?): Long? = d?.toEpochDay()

	@ColumnTypeConverter
	fun toDate(v: Long?): LocalDate? = v?.let(LocalDate::ofEpochDay)

	@ColumnTypeConverter
	fun fromTime(t: LocalTime?): Int? = t?.toSecondOfDay()

	@ColumnTypeConverter
	fun toTime(v: Int?): LocalTime? = v?.let { LocalTime.ofSecondOfDay(it.toLong()) }
}
