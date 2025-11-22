package com.pomidorka.scheduleaag.utils

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
object DateTime {
    private const val NANOS_PER_MILLI = 1_000_000

    fun Month.getMonthName() = when(this) {
        Month.JANUARY -> "Январь"
        Month.FEBRUARY -> "Февраль"
        Month.MARCH -> "Март"
        Month.APRIL -> "Апрель"
        Month.MAY -> "Май"
        Month.JUNE -> "Июнь"
        Month.JULY -> "Июль"
        Month.AUGUST -> "Август"
        Month.SEPTEMBER -> "Сентябрь"
        Month.OCTOBER -> "Октябрь"
        Month.NOVEMBER -> "Ноябрь"
        Month.DECEMBER -> "Декабрь"
    }

    fun DayOfWeek.getDayOfWeekName() = when(this) {
        DayOfWeek.MONDAY -> "Понедельник"
        DayOfWeek.TUESDAY -> "Вторник"
        DayOfWeek.WEDNESDAY -> "Среда"
        DayOfWeek.THURSDAY -> "Четверг"
        DayOfWeek.FRIDAY -> "Пятница"
        DayOfWeek.SATURDAY -> "Суббота"
        DayOfWeek.SUNDAY -> "Воскресенье"
    }

    fun DayOfWeek.getDayOfWeekTrimName() = when(this) {
        DayOfWeek.MONDAY -> "ПН"
        DayOfWeek.TUESDAY -> "ВТ"
        DayOfWeek.WEDNESDAY -> "СР"
        DayOfWeek.THURSDAY -> "ЧТ"
        DayOfWeek.FRIDAY -> "ПТ"
        DayOfWeek.SATURDAY -> "СБ"
        DayOfWeek.SUNDAY -> "ВС"
    }

    @OptIn(ExperimentalTime::class)
    fun getCurrentMillis() = Clock.System.now().toEpochMilliseconds()

    fun Long.getDayNameInRussian(
        tz: TimeZone = TimeZone.currentSystemDefault()
    ) = getLocalDateTime(this, tz).date.dayOfWeek.getDayOfWeekName()

    fun getDayOfWeek(
        millis: Long,
        tz: TimeZone = TimeZone.currentSystemDefault()
    ) = getLocalDateTime(millis, tz).date.dayOfWeek

    fun getLocalDateTime(
        millis: Long,
        tz: TimeZone = TimeZone.currentSystemDefault()
    ) = getInstant(millis).toLocalDateTime(tz)

    fun Long.getLocalDateFromMillis(): LocalDate {
        return getInstant(this)
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

    private fun getInstant(millis: Long) = Instant.fromEpochMilliseconds(millis)

    fun getMillisFromDate(
        day: Int,
        month: Int,
        year: Int,
        tz: TimeZone = TimeZone.currentSystemDefault()
    ) = LocalDate(year, month, day)
        .atTime(0, 0)
        .toInstant(tz)
        .toEpochMilliseconds()

    fun LocalDate.getMillisFromDate(): Long {
        return getMillisFromDate(
            day = day,
            month = this.month.number,
            year = this.year
        )
    }

    fun Long.convertMillisToDate(tz: TimeZone = TimeZone.currentSystemDefault()): String {
        val date = getInstant(this).toLocalDateTime(tz).date
        return date.toString()
    }

    fun Long.convertMillisToDateRu(tz: TimeZone = TimeZone.currentSystemDefault()): String {
        val date = getInstant(this).toLocalDateTime(tz).date
        val day = date.day.toString().padStart(2, '0')
        val month = date.month.number.toString().padStart(2, '0')
        val year = date.year
        return "$day.$month.$year"
    }

    // TODO: Отрефакторить
    fun Long.convertMillisToDateTimeRu(tz: TimeZone = TimeZone.currentSystemDefault()): String {
        val localDate = getInstant(this).toLocalDateTime(tz)
        val date = localDate.date.let {
            val day = it.day.toString().padStart(2, '0')
            val month = it.month.number.toString().padStart(2, '0')
            val year = it.year

            "$day.$month.$year"
        }

        val time = localDate.time.let {
            val hours = it.hour.toString().padStart(2, '0')
            val minutes = it.minute.toString().padStart(2, '0')
            val seconds = it.second.toString().padStart(2, '0')
            val milliseconds = it.nanosecond / NANOS_PER_MILLI

            "$hours:$minutes:$seconds:$milliseconds"
        }

        return "$date $time"
    }
}