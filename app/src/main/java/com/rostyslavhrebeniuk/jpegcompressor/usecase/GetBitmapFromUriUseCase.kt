package com.rostyslavhrebeniuk.jpegcompressor.usecase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.provider.OpenableColumns
import com.rostyslavhrebeniuk.jpegcompressor.utils.flipImage
import com.rostyslavhrebeniuk.jpegcompressor.utils.rotateImage
import com.rostyslavhrebeniuk.jpegcompressor.utils.sizeInKB

class GetBitmapFromUriUseCase(private val context: Context) {

    operator fun invoke(uri: Uri): Pair<Bitmap, Int>? {
        try {
            val bitmap = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            } ?: return null

            val orientation = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                ExifInterface(inputStream).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            }

            val rotatedBitmap = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> bitmap.rotateImage(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> bitmap.rotateImage(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> bitmap.rotateImage(270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> bitmap.flipImage(
                    horizontal = true,
                    vertical = false
                )

                ExifInterface.ORIENTATION_FLIP_VERTICAL -> bitmap.flipImage(
                    horizontal = false,
                    vertical = true
                )

                else -> bitmap
            }

            return rotatedBitmap to getSizeFromUri(uri)
        } catch (e: Exception) {
            return null
        }
    }

    private fun getSizeFromUri(uri: Uri): Int {
        return context.contentResolver.query(uri, null, null, null, null).use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (sizeIndex != -1) {
                    cursor.getLong(sizeIndex).toInt().sizeInKB()
                } else {
                    0
                }
            } else {
                0
            }
        }
    }
}