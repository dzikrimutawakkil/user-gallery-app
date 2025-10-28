package com.techtestuserapp.data.repository

import android.content.Context
import android.net.Uri
import com.techtestuserapp.data.local.dao.MediaDao
import com.techtestuserapp.data.local.entity.MediaEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class MediaRepository @Inject constructor(
    private val mediaDao: MediaDao,
    @ApplicationContext private val context: Context
) {

    fun getMediaForUser(userId: Int): Flow<List<MediaEntity>> {
        return mediaDao.getMediaForUser(userId)
    }

    suspend fun saveMedia(userId: Int, uri: Uri, type: String): MediaEntity? {
        // Buat file tujuan di internal storage
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val extension = if (type == "IMAGE") ".jpg" else ".mp4"
        val fileName = "${type}_${timeStamp}$extension"
        val destinationFile = File(context.filesDir, fileName)

        try {
            // Salin file dari (cache/gallery) ke internal storage
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destinationFile).use { output ->
                    input.copyTo(output)
                }
            }

            val newMediaEntity = MediaEntity(
                userId = userId,
                uri = destinationFile.absolutePath,
                type = type
            )
            mediaDao.insertMedia(newMediaEntity)
            return newMediaEntity

        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
}