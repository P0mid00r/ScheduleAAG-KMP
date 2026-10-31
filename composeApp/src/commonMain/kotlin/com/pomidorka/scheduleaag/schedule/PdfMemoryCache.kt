package com.pomidorka.scheduleaag.schedule

import androidx.collection.LruCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PdfMemoryCache(
    maxSize: Int,
    private val scope: CoroutineScope,
) : LruCache<String, ByteArray>(maxSize) {
    private val mutex = Mutex()
    private val inFlight = mutableMapOf<String, Deferred<ByteArray>>()

    suspend fun getOrPut(
        key: String,
        putValue: suspend () -> ByteArray
    ): ByteArray = mutex.withLock {
        get(key)?.let { return it }
        val value = putValue()
        put(key, value)

        return value
    }

    suspend fun getOrPutConcurrent(
        key: String,
        putValue: suspend () -> ByteArray
    ): ByteArray {
        val deferred = mutex.withLock {
            get(key)?.let { return it }

            inFlight[key] ?: scope.async(
                start = kotlinx.coroutines.CoroutineStart.LAZY
            ) {
                putValue()
            }.also { task ->
                inFlight[key] = task

                task.invokeOnCompletion {
                    scope.launch {
                        mutex.withLock {
                            if (inFlight[key] === task) {
                                inFlight.remove(key)
                            }
                        }
                    }
                }

                task.start()
            }
        }

        val value = deferred.await()

        return mutex.withLock {
            get(key) ?: value.also {
                put(key, it)
            }
        }
    }
}