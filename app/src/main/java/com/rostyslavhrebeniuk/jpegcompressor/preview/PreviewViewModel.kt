package com.rostyslavhrebeniuk.jpegcompressor.preview

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rostyslavhrebeniuk.jpegcompressor.usecase.GetBitmapFromUriUseCase
import com.rostyslavhrebeniuk.jpegcompressor.utils.flipImage
import com.rostyslavhrebeniuk.jpegcompressor.utils.rotateImage
import com.rostyslavhrebeniuk.jpegcompressor.utils.sizeInKB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.InputStream

class PreviewViewModel(application: Application) : AndroidViewModel(application) {

    private val appContext = getApplication<Application>().applicationContext
    private val getBitmapFromUriUseCase by lazy { GetBitmapFromUriUseCase(appContext) }

    private val _originalBitmap = MutableStateFlow<Bitmap?>(null)
    val originalBitmap: StateFlow<Bitmap?> = _originalBitmap

    private val _originalBitmapSize: StateFlow<Int> = _originalBitmap.filterNotNull().map {
        it.byteCount.sizeInKB()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val originalBitmapSize: StateFlow<Int> = _originalBitmapSize

    private val _compressedBitmap = MutableStateFlow<Bitmap?>(null)
    val compressedBitmap: StateFlow<Bitmap?> = _compressedBitmap

    fun setOriginalBitmap(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            _originalBitmap.value = getBitmapFromUriUseCase(uri)
        }
    }

    fun setCompressedBitmap(byteArray: ByteArray?) {
        byteArray?.let {
            viewModelScope.launch(Dispatchers.IO) {
                _compressedBitmap.value =
                    BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            }
        }
    }
}
