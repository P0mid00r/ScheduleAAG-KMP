package com.pomidorka.scheduleaag.updater

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.http.headers

class GitHubApi(val httpClient: HttpClient) {
    val urlReleases = "$API_URL/repos/$USER_NAME/$REPO_NAME/releases/latest"

    suspend fun getReleases(): ReleasesData? {
        val response = httpClient.get(urlReleases) {
            headers {
                append("content-type", "application/json; charset=utf-8")
            }
        }

        return if (response.status.isOk()) {
            response.body<ReleasesData>()
        } else null
    }

    private fun HttpStatusCode.isOk() = this == HttpStatusCode.OK

    private companion object {
        const val API_URL = "https://api.github.com/"
        const val REPO_NAME = "ScheduleAAG-KMP"
        const val USER_NAME = "P0mid00r"
    }
}