package com.pomidorka.scheduleaag.schedule.interactive

import com.fleeksoft.ksoup.Ksoup
import com.pomidorka.scheduleaag.utils.createHttpClient
import com.pomidorka.scheduleaag.utils.currentPlatform
import com.pomidorka.scheduleaag.schedule.Result
import com.pomidorka.scheduleaag.utils.addProxyInUrl
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Parameters
import io.ktor.http.headers
import io.ktor.http.parameters

object ScheduleInteractiveApi {
//    private val BASE_URL: String = (
//        if (getPlatform().type.isWeb) Strings.PROXY else ""
//    ).plus("http://schedule.altag.ru:89/")

    private val BASE_URL = "https://schedule.altag.ru/".let {
        if (currentPlatform().type.isWeb) it.addProxyInUrl()
        else it
    }

    private val client = createHttpClient()

    private fun getFormBodyForFilter() = parameters {
        append("dostup", "true")
    }

    private fun getFormBodyForSchedule(
        nameFilter: String,
        hash: String,
        calendar: String,
        ras: String
    ) = parameters {
        append("dostup", "true")
        append(nameFilter, hash)
        append("calendar", calendar)
        append("ras", ras)
    }

    private suspend fun executeRequest(
        path: String,
        formBody: Parameters
    ): String {
        return client.post("$BASE_URL$path") {
            headers {
//                TODO: Сервер должен вернуть * чтобы был доступ с веб версии
//                append("Access-Control-Allow-Origin", "*")
                append("Connection", "keep-alive")
                append("Content-Type", "application/x-www-form-urlencoded")
                append("X-Requested-With", "XMLHttpRequest")
            }

            setBody(FormDataContent(formBody))
        }.bodyAsText()
    }

    suspend inline fun FilterType.getFilters() = getFiltersAtType(this)

    suspend fun getFiltersAtType(filterType: FilterType) = when(filterType) {
        FilterType.Group -> getFilterGroups()
        FilterType.Aud -> getFilterAud()
        FilterType.Prep -> getFilterPrep()
    }

    suspend fun getFilterGroups(): Result<List<FilterData>> {
        return try {
            val response = executeRequest(
                path = "filter_grup.php",
                formBody = getFormBodyForFilter()
            )

            Result.Success(parseFilter(response))
        } catch (ex: Exception) {
            Result.Failure(ex)
        }
    }

    suspend fun getFilterPrep(): Result<List<FilterData>> {
        return try {
            val response = executeRequest(
                path = "filter_prep.php",
                formBody = getFormBodyForFilter()
            )

            Result.Success(parseFilter(response))
        } catch (ex: Exception) {
            Result.Failure(ex)
        }
    }

    suspend fun getFilterAud(): Result<List<FilterData>> {
        return try {
            val response = executeRequest(
                path = "filter_aud.php",
                formBody = getFormBodyForFilter()
            )

            Result.Success(parseFilter(response))
        } catch (ex: Exception) {
            Result.Failure(ex)
        }
    }

    suspend fun FilterType.getSchedule(
        filterData: FilterData,
        calendar: String,
    ) = getScheduleAtFilterType(
        filterType = this,
        filterData = filterData,
        calendar = calendar
    )


    suspend fun getScheduleAtFilterType(
        filterType: FilterType,
        filterData: FilterData,
        calendar: String,
    ) = when(filterType) {
        FilterType.Group -> getScheduleForGroup(filterData, calendar)
        FilterType.Aud -> getScheduleForAud(filterData, calendar)
        FilterType.Prep -> getScheduleForPrep(filterData, calendar)
    }


    suspend fun getScheduleForGroup(
        filterData: FilterData,
        calendar: String,
    ): Result<List<Schedule>> {
        return try {
            val response = executeRequest(
                path = "ras.php",
                formBody = getFormBodyForSchedule(
                    nameFilter = "gruppa",
                    hash = filterData.hash,
                    calendar = calendar,
                    ras = "GRUP"
                )
            )

            Result.Success(parseSchedule(response))
        } catch (ex: Exception) {
            Result.Failure(ex)
        }
    }

    suspend fun getScheduleForPrep(
        filterData: FilterData,
        calendar: String,
    ): Result<List<Schedule>> {
        return try {
            val response = executeRequest(
                path = "ras.php",
                formBody = getFormBodyForSchedule(
                    nameFilter = "prepod",
                    hash = filterData.hash,
                    calendar = calendar,
                    ras = "PREP"
                )
            )

            Result.Success(parseSchedule(response))
        } catch (ex: Exception) {
            Result.Failure(ex)
        }
    }

    suspend fun getScheduleForAud(
        filterData: FilterData,
        calendar: String,
    ): Result<List<Schedule>> {
        return try {
            val response = executeRequest(
                path = "ras.php",
                formBody = getFormBodyForSchedule(
                    nameFilter = "auditoria",
                    hash = filterData.hash,
                    calendar = calendar,
                    ras = "AUD"
                )
            )

            Result.Success(parseSchedule(response))
        } catch (ex: Exception) {
            Result.Failure(ex)
        }
    }

    private fun parseFilter(html: String): List<FilterData> {
        val doc = Ksoup.parse(html)
        val elements = doc.select("option")

        val list = mutableListOf<FilterData>()
        for (element in elements) {
            list.add(
                FilterData(
                    data = element.text(),
                    hash = element.attr("value")
                )
            )
        }

        return list
    }

    private fun parseSchedule(html: String): List<Schedule> {
        val doc = Ksoup.parse(html)
        val elements = doc.select("div.table > div.table-body > div.table-body_item")

        // Группируем по номеру пары и дисциплине (группы могут быть разными)
        val scheduleMap = mutableMapOf<Pair<Int, String>, MutableList<Schedule>>()

        for (element in elements) {
            element.select("div > span.title").remove()

            val numberLesson = element.select("div.time").text().toInt()
            val group = element.select("div.group").text()
            val lesson = element.select("div.lesson").text()
            val teacher = trimNameTeacher(element.select("div.teacher").text())
            val territory = element.select("div.territory").text()
            val classRoom = element.select("div.classroom").text()

            val key = numberLesson to lesson // Ключ: номер пары + дисциплина

            scheduleMap.getOrPut(key) { mutableListOf() }.add(
                Schedule(
                    numberLesson = numberLesson,
                    group = group,
                    lesson = lesson,
                    teacher = teacher,
                    territory = territory,
                    classRoom = classRoom
                )
            )
        }

        // Объединяем записи с одинаковыми номерами пар и дисциплинами
        return scheduleMap.flatMap { (key, scheduleItems) ->
            if (scheduleItems.size == 1) {
                scheduleItems // Оставляем как есть, если нет дублей
            } else {
                // Объединяем группы, преподавателей и аудитории
                listOf(
                    Schedule(
                        numberLesson = key.first,
                        group = scheduleItems.joinToString(", ") { it.group }, // Все группы через запятую
                        lesson = key.second,
//                        teacher = scheduleItems.joinToString(", ") { it.teacher },
                        teacher = if (scheduleItems.first().teacher == scheduleItems.last().teacher) {
                            scheduleItems.first().teacher
                        } else {
                            scheduleItems.joinToString(", ") { it.teacher }
                        },
                        territory = scheduleItems.first().territory, // Берём первый корпус (если они одинаковые)
                        classRoom = scheduleItems.joinToString(", ") { it.classRoom }
                    )
                )
            }
        }
    }

    fun trimNameTeacher(fullName: String): String {
        val items = fullName.trim().split(" ")

        return when {
            items.size == 3 -> "${items[0]} ${items[1][0]}.${items[2][0]}."
            items.size in 1..2 -> fullName
            items.size > 3 -> "${items[0]} ${items[1][0]}.${items[2][0]}."
            else -> ""
        }
    }
}