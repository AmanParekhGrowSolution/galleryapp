package com.example.galleryapp.data.local

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.galleryapp.domain.model.Album
import com.example.galleryapp.domain.model.AlbumType
import com.example.galleryapp.domain.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaStoreDataSource(private val context: Context) {

    suspend fun queryPhotosAndVideos(): List<Photo> = withContext(Dispatchers.IO) {
        val photos = mutableListOf<Photo>()
        photos += queryImages()
        photos += queryVideos()
        photos.sortedByDescending { it.dateTaken }
    }

    private fun queryImages(): List<Photo> {
        val results = mutableListOf<Photo>()
        val collection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATE_TAKEN,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT
        )

        context.contentResolver.query(
            collection, projection, null, null,
            "${MediaStore.MediaColumns.DATE_TAKEN} DESC"
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val nameCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
            val dateCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_TAKEN)
            val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
            val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
            val widthCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH)
            val heightCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.HEIGHT)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val uri = Uri.withAppendedPath(collection, id.toString()).toString()
                results += Photo(
                    id = id,
                    displayName = cursor.getString(nameCol) ?: "",
                    dateTaken = cursor.getLong(dateCol),
                    size = cursor.getLong(sizeCol),
                    mimeType = cursor.getString(mimeCol) ?: "image/jpeg",
                    placeholderColor = 0xFF1E3A5FL,
                    width = cursor.getInt(widthCol),
                    height = cursor.getInt(heightCol),
                    uri = uri
                )
            }
        }
        return results
    }

    private fun queryVideos(): List<Photo> {
        val results = mutableListOf<Photo>()
        val collection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATE_TAKEN,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
            MediaStore.Video.Media.DURATION
        )

        context.contentResolver.query(
            collection, projection, null, null,
            "${MediaStore.MediaColumns.DATE_TAKEN} DESC"
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val nameCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
            val dateCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_TAKEN)
            val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
            val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
            val widthCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH)
            val heightCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.HEIGHT)
            val durationCol = cursor.getColumnIndex(MediaStore.Video.Media.DURATION)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val uri = Uri.withAppendedPath(collection, id.toString()).toString()
                results += Photo(
                    id = id,
                    displayName = cursor.getString(nameCol) ?: "",
                    dateTaken = cursor.getLong(dateCol),
                    size = cursor.getLong(sizeCol),
                    mimeType = cursor.getString(mimeCol) ?: "video/mp4",
                    placeholderColor = 0xFF1A3A5CL,
                    width = cursor.getInt(widthCol),
                    height = cursor.getInt(heightCol),
                    duration = if (durationCol >= 0) cursor.getLong(durationCol) else 0L,
                    uri = uri
                )
            }
        }
        return results
    }

    suspend fun getPhotoById(id: Long): Photo? = withContext(Dispatchers.IO) {
        val imageCollection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        val imageProjection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATE_TAKEN,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT
        )
        val selection = "${MediaStore.MediaColumns._ID} = ?"
        val selectionArgs = arrayOf(id.toString())

        context.contentResolver.query(imageCollection, imageProjection, selection, selectionArgs, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                    val nameCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                    val dateCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_TAKEN)
                    val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
                    val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
                    val widthCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH)
                    val heightCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.HEIGHT)
                    return@withContext Photo(
                        id = cursor.getLong(idCol),
                        displayName = cursor.getString(nameCol) ?: "",
                        dateTaken = cursor.getLong(dateCol),
                        size = cursor.getLong(sizeCol),
                        mimeType = cursor.getString(mimeCol) ?: "image/jpeg",
                        placeholderColor = 0xFF1E3A5FL,
                        width = cursor.getInt(widthCol),
                        height = cursor.getInt(heightCol),
                        uri = Uri.withAppendedPath(imageCollection, cursor.getLong(idCol).toString()).toString()
                    )
                }
            }

        val videoCollection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }
        val videoProjection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATE_TAKEN,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
            MediaStore.Video.Media.DURATION
        )

        context.contentResolver.query(videoCollection, videoProjection, selection, selectionArgs, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                    val nameCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                    val dateCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_TAKEN)
                    val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
                    val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
                    val widthCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH)
                    val heightCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.HEIGHT)
                    val durationCol = cursor.getColumnIndex(MediaStore.Video.Media.DURATION)
                    return@withContext Photo(
                        id = cursor.getLong(idCol),
                        displayName = cursor.getString(nameCol) ?: "",
                        dateTaken = cursor.getLong(dateCol),
                        size = cursor.getLong(sizeCol),
                        mimeType = cursor.getString(mimeCol) ?: "video/mp4",
                        placeholderColor = 0xFF1A3A5CL,
                        width = cursor.getInt(widthCol),
                        height = cursor.getInt(heightCol),
                        duration = if (durationCol >= 0) cursor.getLong(durationCol) else 0L,
                        uri = Uri.withAppendedPath(videoCollection, cursor.getLong(idCol).toString()).toString()
                    )
                }
            }

        null
    }

    suspend fun queryAlbums(): List<Album> = withContext(Dispatchers.IO) {
        val buckets = mutableMapOf<Long, Pair<String, Int>>()

        val collection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )

        context.contentResolver.query(
            collection, projection, null, null,
            "${MediaStore.MediaColumns.DATE_TAKEN} DESC"
        )?.use { cursor ->
            val bucketIdCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val bucketNameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val bucketId = cursor.getLong(bucketIdCol)
                val bucketName = cursor.getString(bucketNameCol) ?: "Unknown"
                val existing = buckets[bucketId]
                buckets[bucketId] = if (existing == null) {
                    Pair(bucketName, 1)
                } else {
                    Pair(existing.first, existing.second + 1)
                }
            }
        }

        val placeholderColors = listOf(
            0xFF1E3A5FL, 0xFF2D5A27L, 0xFF5C1F1FL, 0xFF3D1F5CL,
            0xFF1F4D5CL, 0xFF5C3D1FL, 0xFF1F5C3DL, 0xFF5C1F4DL
        )

        buckets.entries.mapIndexed { index, (bucketId, pair) ->
            Album(
                id = bucketId,
                name = pair.first,
                count = pair.second,
                coverColor = placeholderColors[index % placeholderColors.size],
                type = AlbumType.Featured
            )
        }.sortedByDescending { it.count }
    }
}
