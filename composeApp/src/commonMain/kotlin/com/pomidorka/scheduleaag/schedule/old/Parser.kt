package com.pomidorka.scheduleaag.schedule.old

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.network.parseGetRequest
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.select.Elements
import com.pomidorka.scheduleaag.Strings
import com.pomidorka.scheduleaag.schedule.Result
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlinx.datetime.yearMonth
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

//TODO: Сделать получение ссылки на pdf расписания вместе с датой
@OptIn(ExperimentalTime::class)
sealed class Parser {
    suspend fun parseAllMonthHtml(url: String): Result<String> {
        return try {
            val doc = loadDocument(url)
            Result.Success(doc.getElementsByClass("mtext").toString())
        } catch (_: Exception) {
            Result.Failure(Throwable(Strings.SITE_CONNECTION_ERROR))
        }
    }

    suspend fun parseScheduleCallsHtml(url: String): Result<String> {
        return try {
            val doc = loadDocument(url)
            Result.Success(doc.getElementsByClass("mtext").toString())
        } catch (_: Exception) {
            Result.Failure(Throwable(Strings.SITE_CONNECTION_ERROR))
        }
    }

    suspend fun parseScheduleTodayUrl(url: String): Result<String> {
        try {
            val doc = loadDocument(url)
            val tables = doc.parseTables()

            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val indexTable = if (today.month.number == 1) 0 else 1

            val url = tables[indexTable].getElementsByTag("a").firstOrNull {
                it.text().toInt() == today.day
            }

            return if (url == null) {
                Result.Failure(Throwable(Strings.SCHEDULE_TODAY_ERROR))
            } else {
                Result.Success(url.attr("href"))
            }
        } catch (_: Exception) {
            return Result.Failure(Throwable(Strings.SITE_CONNECTION_ERROR))
        }
    }

    suspend fun parseScheduleNextDayUrl(url: String): Result<String> {
        try {
            val doc = loadDocument(url)
            val tables = doc.parseTables()

            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val indexTable = if (today.month.number == 1) 0 else 1

            val url = tables[indexTable].getElementsByTag("a").firstOrNull {
                it.text().toInt() >= today.day + 1
            }

            return if (url == null) {
                parseScheduleNextMonth(tables)
            } else {
                Result.Success(url.attr("href"))
            }
        } catch (_: Exception) {
            return Result.Failure(Throwable(Strings.SITE_CONNECTION_ERROR))
        }
    }

    private fun parseScheduleNextMonth(tables: Elements): Result<String> {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val date = today.plus(DatePeriod(months = 1))
        val lengthOfMonth = LocalDate(
            year = date.year,
            day = date.day,
            month = date.month.number
        ).yearMonth.numberOfDays

        val indexTable = if (today.month.number == 1) 0 else 1

        val url = tables[indexTable + 1].getElementsByTag("a").firstOrNull {
            it.text().toInt() <= lengthOfMonth
        }

        return if (url == null) {
            Result.Failure(Throwable(Strings.SCHEDULE_NEXT_DAY_ERROR))
        } else {
            Result.Success(url.attr("href"))
        }
    }

    private suspend fun loadDocument(url: String) = Ksoup.parseGetRequest(url)

    private fun Document.parseTables() = this.getElementsByTag("tbody")
}
