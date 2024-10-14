package com.rostyslavhrebeniuk.jpegcompressor.usecase

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.ByteArrayOutputStream

class CompressImageUseCase {
    operator fun invoke(bitmap: Bitmap, quality: Int): Flow<Pair<Bitmap?, ByteArray?>> = flow {
        val byteArray = ByteArrayOutputStream().use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, it)
            it.toByteArray()
        }
        emit(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size) to byteArray)
    }.flowOn(Dispatchers.IO)
}