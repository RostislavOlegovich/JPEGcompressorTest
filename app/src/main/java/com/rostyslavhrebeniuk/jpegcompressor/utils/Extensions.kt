package com.rostyslavhrebeniuk.jpegcompressor.utils

import android.graphics.Bitmap
import android.graphics.Matrix

fun Int.sizeInKB(): Int {
    return this / 1024
}

fun Bitmap.rotateImage(degree: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degree) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

fun Bitmap.flipImage(horizontal: Boolean, vertical: Boolean): Bitmap {
    val matrix = Matrix().apply {
        preScale(
            if (horizontal) -1f else 1f,
            if (vertical) -1f else 1f
        )
    }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}