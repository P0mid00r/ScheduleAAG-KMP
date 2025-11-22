package com.pomidorka.scheduleaag.utils

import androidx.compose.runtime.Immutable
import com.pomidorka.scheduleaag.utils.DateTime.convertMillisToDateTimeRu
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object Log {
    private val currentPlatform = currentPlatform()
    private val scope = CoroutineScope(Dispatchers.Default)
    private val messagesChannel = Channel<LogMessage>()
    private val messages = messagesChannel.receiveAsFlow()
    private val messagesList = mutableListOf<LogMessage>()

    init {
        scope.launch {
            messages.collect {
                messagesList.add(it)
                println(it)
            }
        }
    }

    suspend fun sendReport() {
        val json = Json.encodeToString(messagesList)
        println(json)
    }

    fun print(message: () -> String) {
        scope.launch {
            messagesChannel.send(LogMessage(
                message = message(),
                type = null,
                tag = null,
                timestamp = timestamp(),
            ))
        }
    }

    fun info(tag: String, message: () -> String) {
        scope.launch {
            messagesChannel.send(LogMessage(
                message = message(),
                type = LogType.INFO,
                tag = tag,
                timestamp = timestamp()
            ))
        }
    }

    fun error(tag: String, message: () -> String) {
        scope.launch {
            messagesChannel.send(LogMessage(
                message = message(),
                type = LogType.ERROR,
                tag = tag,
                timestamp = timestamp()
            ))
        }
    }

    fun error(tag: String, throwable: Throwable) {
        error(tag) {
            throwable.let { it.message ?: it.cause.toString() }
        }
    }

    fun clear() = messagesList.clear()

    private fun timestamp(): String {
        val date = DateTime
            .getCurrentMillis()
            .convertMillisToDateTimeRu()

        return date
    }
}

enum class LogType {
    INFO, ERROR
}

@Serializable
@Immutable
data class LogMessage(
    val message: String,
    val type: LogType?,
    val tag: String?,
    val timestamp: String
) {
    override fun toString(): String {
        return "[$timestamp]" +
                (tag?.let { "[$it]" } ?: "") +
                (type?.let{ "[${it.name}]" } ?: "") +
                ": $message"
    }
}

