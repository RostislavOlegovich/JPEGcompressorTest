package com.rostyslavhrebeniuk.jpegcompressor.compression

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rostyslavhrebeniuk.jpegcompressor.usecase.CompressImageUseCase
import com.rostyslavhrebeniuk.jpegcompressor.usecase.GetBitmapFromUriUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CompressionViewModel(application: Application) : AndroidViewModel(application) {

    private val appContext = getApplication<Application>().applicationContext
    private val getBitmapFromUriUseCase by lazy { GetBitmapFromUriUseCase(appContext) }
    private val compressImageUseCase by lazy { CompressImageUseCase() }


    //for additional backpressure setup
//    private val _isCompressing = MutableStateFlow(false)

    val originalBitmap = MutableStateFlow<Pair<Bitmap, Int>?>(null)

    private val _compressionLevel = MutableStateFlow(100)
    val compressionLevel: StateFlow<Int> = _compressionLevel

    private val _compressedBitmap: StateFlow<Pair<Bitmap?, ByteArray?>> =
        combine(
            _compressionLevel,
//                .debounce(if (_isCompressing.value) 0L else 10L),
            originalBitmap.filterNotNull(),
            ::Pair
        ).flatMapMerge { (level, bitmap) ->
            compressImageUseCase(bitmap = bitmap.first, quality = level)
//                .onStart {
//                    println("CompressionViewModel: onStart")
//                    _isCompressing.value = true
//                }
//                .onCompletion {
//                    println("CompressionViewModel: onCompletion")
//                    _isCompressing.value = false
//                }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, originalBitmap.value?.first to null)

    val compressedBitmap: StateFlow<Pair<Bitmap?, ByteArray?>> = _compressedBitmap

    fun setOriginalBitmap(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            originalBitmap.value = getBitmapFromUriUseCase(uri)
        }
    }

    fun setCompressionLevel(level: Int) {
        _compressionLevel.value = level
    }
}
