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
        try {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
            val byteArray = stream.toByteArray()
            emit(
                BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size) to byteArray
            )
        } catch (e: Exception) {
            emit(null to null)
        }
    }.flowOn(Dispatchers.IO)
}