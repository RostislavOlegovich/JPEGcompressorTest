package com.rostyslavhrebeniuk.jpegcompressor.preview

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rostyslavhrebeniuk.jpegcompressor.usecase.GetBitmapFromUriUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PreviewViewModel(application: Application) : AndroidViewModel(application) {

    private val appContext = getApplication<Application>().applicationContext
    private val getBitmapFromUriUseCase by lazy { GetBitmapFromUriUseCase(appContext) }

    private val _originalBitmap = MutableStateFlow<Pair<Bitmap, Int>?>(null)
    val originalBitmap: StateFlow<Pair<Bitmap, Int>?> = _originalBitmap

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
