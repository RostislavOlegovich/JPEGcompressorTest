package com.rostyslavhrebeniuk.jpegcompressor.usecase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import com.rostyslavhrebeniuk.jpegcompressor.utils.flipImage
import com.rostyslavhrebeniuk.jpegcompressor.utils.rotateImage
import java.io.InputStream

class GetBitmapFromUriUseCase(private val context: Context) {

    operator fun invoke(uri: Uri): Bitmap? {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            val exifInputStream = context.contentResolver.openInputStream(uri)
            val exif = exifInputStream?.let { ExifInterface(it) }
            val orientation = exif?.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

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

            return rotatedBitmap
        } catch (e: Exception) {
            return null
        }
    }
}