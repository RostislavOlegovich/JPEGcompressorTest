package com.rostyslavhrebeniuk.jpegcompressor.preview

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rostyslavhrebeniuk.jpegcompressor.ui.theme.JPEGCompressorTheme
import com.rostyslavhrebeniuk.jpegcompressor.utils.sizeInKB

class PreviewActivity : ComponentActivity() {

    private val previewViewModel by viewModels<PreviewViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imageUri = Uri.parse(intent.getStringExtra("imageUri"))
        val byteArray = intent.getByteArrayExtra("compressedBitmap")

        with(previewViewModel) {
            setOriginalBitmap(imageUri)
            setCompressedBitmap(byteArray)
        }

        setContent {
            JPEGCompressorTheme {
                val originalBitmap by previewViewModel.originalBitmap.collectAsState()
                val compressedBitmap by previewViewModel.compressedBitmap.collectAsState()
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    PreviewScreen(
                        originalBitmap = originalBitmap?.first,
                        compressedBitmap = compressedBitmap,
                        originalBitmapSize = originalBitmap?.second ?: 0,
                        compressedBitmapSize = byteArray?.size?.sizeInKB() ?: 0
                    )
                }
            }
        }
    }
}

@Composable
fun PreviewScreen(
    originalBitmap: Bitmap?,
    compressedBitmap: Bitmap?,
    originalBitmapSize: Int,
    compressedBitmapSize: Int
) {

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(12.dp)
                .weight(1f)
                .fillMaxSize()
        ) {
            compressedBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Preview Image",
                )
            }

            Text("Сжатый размер: $compressedBitmapSize KB")
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(12.dp)
                .weight(1f)
                .fillMaxSize()
        ) {
            originalBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Preview Image",
                )
            }

            Text("Оригинальный размер: $originalBitmapSize KB")
        }

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewScreenPreview() {
    JPEGCompressorTheme {
        PreviewScreen(
            originalBitmap = null,
            compressedBitmap = null,
            originalBitmapSize = 100,
            compressedBitmapSize = 100
        )
    }
}