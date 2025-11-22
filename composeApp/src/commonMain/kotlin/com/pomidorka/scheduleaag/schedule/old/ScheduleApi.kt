package com.pomidorka.scheduleaag.schedule.old

import com.pomidorka.scheduleaag.utils.currentPlatform
import com.pomidorka.scheduleaag.utils.addProxyInUrl
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
object ScheduleApi : Parser() {
    fun CollegeBuilding.toUrl(): String {
        return "https://altag.ru/student/schedule/rescheduling-${this.id}".let {
            if (currentPlatform().type.isWeb) it.addProxyInUrl()
            else it
        }
    }

    suspend fun getAllMonthHtml(collegeBuilding: CollegeBuilding) = parseAllMonthHtml(collegeBuilding.toUrl())

    suspend fun getScheduleCallsHtml() = parseScheduleCallsHtml(
        "https://altag.ru/student/schedule/call_schedule".let {
            if (currentPlatform().type.isWeb) it.addProxyInUrl()
            else it
        }
    )

    fun getDayOfWeek(): String {
        val dayOfWeek = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).dayOfWeek

        return when(dayOfWeek) {
            DayOfWeek.MONDAY -> "понедельник"
            DayOfWeek.TUESDAY -> "вторник"
            DayOfWeek.WEDNESDAY -> "среду"
            DayOfWeek.THURSDAY -> "четверг"
            DayOfWeek.FRIDAY -> "пятницу"
            DayOfWeek.SATURDAY -> "субботу"
            DayOfWeek.SUNDAY -> "воскресенье"
        }
    }

    fun getScheduleCalls(dayOfWeek: DayOfWeek) = when(dayOfWeek) {
        DayOfWeek.MONDAY -> listOf(
            ScheduleCall(lessonTime = "08:00 - 08:45", interval = "5 минут"),
            ScheduleCall(lessonTime = "08:50 - 10:20", interval = "10 минут"),
            ScheduleCall(lessonTime = "10:30 - 12:00", interval = "20 минут"),
            ScheduleCall(lessonTime = "12:20 - 13:50", interval = "5 минут"),

            ScheduleCall(lessonTime = "13:55-14:40", interval = "10 минут"),
            ScheduleCall(lessonTime = "14:50-16:20", interval = "20 минут"),
            ScheduleCall(lessonTime = "16:40-18:10", interval = "5 минут"),
            ScheduleCall(lessonTime = "18:15-19:45", interval = " "),
        )
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY -> listOf(
            ScheduleCall(lessonTime = "08:00 - 09:30", interval = "10 минут"),
            ScheduleCall(lessonTime = "09:40 - 11:10", interval = "20 минут"),
            ScheduleCall(lessonTime = "11:30 - 13:00", interval = "10 минут"),

            ScheduleCall(lessonTime = "13:10 - 14:40", interval = "10 минут"),
            ScheduleCall(lessonTime = "14:50 - 16:20", interval = "20 минут"),
            ScheduleCall(lessonTime = "16:40 - 18:10", interval = "5 минут"),
            ScheduleCall(lessonTime = "18:15 - 19:45", interval = " "),
        )
        DayOfWeek.SATURDAY -> listOf(
            ScheduleCall(lessonTime = "08:00 - 09:30", interval = "10 минут"),
            ScheduleCall(lessonTime = "09:40 - 11:10", interval = "20 минут"),
            ScheduleCall(lessonTime = "11:30 - 13:00", interval = "5 минут"),

            ScheduleCall(lessonTime = "13:05 - 14:35", interval = "20 минут"),
            ScheduleCall(lessonTime = "14:55 - 16:25", interval = "5 минут"),
            ScheduleCall(lessonTime = "16:30 - 18:00", interval = " "),
        )
        else -> emptyList()
    }
}