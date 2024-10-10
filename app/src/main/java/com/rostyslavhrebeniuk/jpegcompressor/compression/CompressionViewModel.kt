package com.rostyslavhrebeniuk.jpegcompressor.compression

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rostyslavhrebeniuk.jpegcompressor.usecase.CompressImageUseCase
import com.rostyslavhrebeniuk.jpegcompressor.usecase.GetBitmapFromUriUseCase
import com.rostyslavhrebeniuk.jpegcompressor.utils.sizeInKB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CompressionViewModel(application: Application) : AndroidViewModel(application) {

    private val appContext = getApplication<Application>().applicationContext
    private val getBitmapFromUriUseCase by lazy { GetBitmapFromUriUseCase(appContext) }
    private val compressImageUseCase by lazy { CompressImageUseCase() }

    private val _originalBitmap = MutableStateFlow<Bitmap?>(null)
    private val _originalBitmapSize: StateFlow<Int> = _originalBitmap.filterNotNull().map {
        it.byteCount.sizeInKB()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val originalBitmapSize: StateFlow<Int> = _originalBitmapSize

    private val _compressionLevel = MutableStateFlow(100)
    val compressionLevel: StateFlow<Int> = _compressionLevel

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _compressedBitmap: StateFlow<Pair<Bitmap?, ByteArray?>> =
        combine(
            _compressionLevel,
            _originalBitmap.filterNotNull(),
            ::Pair
        ).flatMapLatest { (level, bitmap) ->
            compressImageUseCase(bitmap = bitmap, quality = level)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, _originalBitmap.value to null)

    val compressedBitmap: StateFlow<Pair<Bitmap?, ByteArray?>> = _compressedBitmap

    fun setOriginalBitmap(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            _originalBitmap.value = getBitmapFromUriUseCase(uri)
        }
    }

    fun setCompressionLevel(level: Int) {
        _compressionLevel.value = level
    }

//    private fun compressImage(bitmap: Bitmap, quality: Int): Flow<Pair<Bitmap?, ByteArray?>> =
//        flow {
//            try {
//                val stream = ByteArrayOutputStream()
//                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
//                val byteArray = stream.toByteArray()
//                emit(
//                    BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size) to byteArray
//                )
//            } catch (e: Exception) {
//                emit(null to null)
//            }
//        }.flowOn(Dispatchers.IO)
}
