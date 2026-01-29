package com.pomidorka.scheduleaag.utils

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import androidx.core.net.toUri
import java.io.ByteArrayInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

class InMemoryImageProvider : ContentProvider() {
    companion object {
        private val imageStore = ConcurrentHashMap<String, ImageData>()
        private const val AUTHORITY_SUFFIX = ".inmemoryprovider"

        fun registerImage(
            context: Context,
            imageData: ByteArray,
            mimeType: String = "image/jpeg",
            fileName: String = "image_${System.currentTimeMillis()}.jpg"
        ): Uri {
            val authority = "${context.packageName}$AUTHORITY_SUFFIX"
            val imageId = "img_${System.currentTimeMillis()}_${(Math.random() * 10000).toInt()}"
            val uri = "content://$authority/images/$imageId".toUri()

            if (imageStore.size >= 10) {
                imageStore.clear()
            }

            imageStore[imageId] = ImageData(imageData, mimeType, fileName)

            return uri
        }
    }

    override fun getType(uri: Uri): String? {
        val imageId = extractImageId(uri) ?: return null
        return imageStore[imageId]?.mimeType
    }

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        if (mode != "r") {
            throw FileNotFoundException("Only read mode is supported")
        }

        val imageId = extractImageId(uri) ?: throw FileNotFoundException("Invalid URI")
        val imageData = imageStore[imageId] ?: throw FileNotFoundException("Image not found")

        // Создаем pipe (канал) для передачи данных
        val pipe = ParcelFileDescriptor.createPipe()

        // Запускаем поток для записи данных в pipe
        Thread {
            try {
                val outputStream = ParcelFileDescriptor.AutoCloseOutputStream(pipe[1])
                val inputStream = ByteArrayInputStream(imageData.data)

                inputStream.copyTo(outputStream)

                outputStream.flush()
                outputStream.close()
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()

        return pipe[0]
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val imageId = extractImageId(uri) ?: return null
        val imageData = imageStore[imageId] ?: return null

        // Создаем курсор с метаданными файла
        val cursor = MatrixCursor(
            projection ?: arrayOf(
                OpenableColumns.DISPLAY_NAME,
                OpenableColumns.SIZE
            )
        )

        cursor.newRow().apply {
            add(OpenableColumns.DISPLAY_NAME, imageData.fileName)
            add(OpenableColumns.SIZE, imageData.data.size.toLong())
        }

        return cursor
    }

    private fun extractImageId(uri: Uri): String? {
        val segments = uri.pathSegments
        return if (segments.size >= 2 && segments[0] == "images") {
            segments[1]
        } else {
            null
        }
    }

    override fun onCreate(): Boolean = true
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int = 0
}

private data class ImageData(
    val data: ByteArray,
    val mimeType: String,
    val fileName: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}