package com.rostyslavhrebeniuk.jpegcompressor.compression

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rostyslavhrebeniuk.jpegcompressor.preview.PreviewActivity
import com.rostyslavhrebeniuk.jpegcompressor.ui.theme.JPEGCompressorTheme
import com.rostyslavhrebeniuk.jpegcompressor.utils.sizeInKB

class CompressionActivity : ComponentActivity() {

    private val compressionViewModel by viewModels<CompressionViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imageUri = Uri.parse(intent.getStringExtra("imageUri"))

        compressionViewModel.setOriginalBitmap(imageUri)

        setContent {
            JPEGCompressorTheme {
                val originalBitmap by compressionViewModel.originalBitmap.collectAsState()
                val compressedBitmap by compressionViewModel.compressedBitmap.collectAsState()
                val compressionLevel by compressionViewModel.compressionLevel.collectAsState()

                Scaffold(modifier = Modifier.fillMaxSize()) {
                    CompressionScreen(
                        originalBitmapSize = originalBitmap?.second ?: 0,
                        compressedBitmap = compressedBitmap.first,
                        compressedSize = compressedBitmap.second?.size?.sizeInKB() ?: 0,
                        compressionLevel = compressionLevel,
                        onLevelChanged = {
                            compressionViewModel.setCompressionLevel(it)
                        },
                        startPreviewActivity = {
                            val intent = Intent(
                                this@CompressionActivity,
                                PreviewActivity::class.java
                            ).apply {
                                putExtra("compressedBitmap", compressedBitmap.second)
                                putExtra("imageUri", imageUri.toString())
                            }
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CompressionScreen(
    originalBitmapSize: Int,
    compressedSize: Int,
    compressedBitmap: Bitmap?,
    compressionLevel: Int,
    onLevelChanged: (Int) -> Unit,
    startPreviewActivity: () -> Unit
) {

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        compressedBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Preview Image",
                modifier = Modifier
                    .padding(24.dp)
                    .weight(1f)
                    .fillMaxSize()
            )
        }

        Text("Оригинальный размер: $originalBitmapSize KB")
        Text("Сжатый размер: $compressedSize KB")

        Slider(
            modifier = Modifier.padding(bottom = 24.dp),
            value = compressionLevel.toFloat(),
            onValueChange = {
                onLevelChanged(it.toInt())
            },
            valueRange = 1f..100f
        )

        Button(
            modifier = Modifier.padding(bottom = 24.dp),
            onClick = {
                startPreviewActivity()
            }) {
            Text("Далее")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SelectPhotoScreenPreview() {
    JPEGCompressorTheme {
        CompressionScreen(
            originalBitmapSize = 100,
            compressedSize = 100,
            compressedBitmap = null,
            compressionLevel = 100,
            onLevelChanged = {},
            startPreviewActivity = {}
        )
    }
}