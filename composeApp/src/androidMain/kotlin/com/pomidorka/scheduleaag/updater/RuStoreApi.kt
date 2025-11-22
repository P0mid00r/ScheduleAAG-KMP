package com.pomidorka.scheduleaag.updater

import com.pomidorka.scheduleaag.BuildConfig
import com.pomidorka.scheduleaag.utils.createHttpClient
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.json.JSONArray
import org.json.JSONObject

object RuStoreApi {
    private const val URL = "https://public-api.rustore.ru/public/v1/application/com.pomidorka.scheduleaag/version?"
    private const val URL_AUTH = "https://public-api.rustore.ru/public/auth"
    private val client = createHttpClient()

    private suspend fun getAuthToken(): String {
        val requestBody = SignatureGenerator.generateSignature(
            BuildConfig.RUSTORE_KEY_ID,
            BuildConfig.RUSTORE_API_TOKEN
        )

        val response: HttpResponse = client.post(URL_AUTH) {
            header("Content-Type", "application/json")
            setBody(requestBody)
        }

        if (response.status.value !in 200..299) {
            throw Exception("Ошибка получения токена авторизации: ${response.status.value}")
        }

        val json = response.bodyAsText()
        val authToken = JSONObject(json)
            .getJSONObject("body")
            .getString("jwe")

        if (authToken.isEmpty()) {
            throw Exception("Токен авторизации не найден в ответе")
        }

        return authToken
    }

    private suspend fun getVersions(): ArrayList<VersionRuStore> {
        val authToken = getAuthToken()

        val response: HttpResponse = client.get(URL) {
            header("accept", "application/json")
            header("Public-Token", authToken)
        }

        if (response.status.value !in 200..299) {
            throw Exception("Ошибка получения версий: ${response.status.value}")
        }

        val json = response.bodyAsText()
        val versionItems = JSONObject(json)
            .getJSONObject("body")
            .getJSONArray("content")

        return versionItems.parseVersions()
    }

    private fun JSONArray.parseVersions(): ArrayList<VersionRuStore> {
        val versionsList = ArrayList<VersionRuStore>()
        for (i in 0 until this.length() ) {
            val item = this[i] as JSONObject
            versionsList.add(
                VersionRuStore(
                    item.getString("versionId"),
                    item.getString("appName"),
                    item.getString("appType"),
                    item.getString("versionName"),
                    item.getString("versionCode").toInt(),
                    item.getString("versionStatus"),
                    item.getString("publishType"),
                    item.getString("whatsNew")
                )
            )
        }

        return versionsList
    }

    suspend fun getStoreVersion() = getVersions()
        .filter { it.versionStatus == VersionStatus.ACTIVE }
        .maxByOrNull { it.versionCode }
}