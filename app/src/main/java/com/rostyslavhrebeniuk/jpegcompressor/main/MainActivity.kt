package com.rostyslavhrebeniuk.jpegcompressor.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rostyslavhrebeniuk.jpegcompressor.ui.image.PhotoPicker
import com.rostyslavhrebeniuk.jpegcompressor.compression.CompressionActivity
import com.rostyslavhrebeniuk.jpegcompressor.ui.theme.JPEGCompressorTheme

class MainActivity : ComponentActivity() {

    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JPEGCompressorTheme {
                val selectedPhoto by mainViewModel.selectedImageUri.collectAsState()

                Scaffold(modifier = Modifier.fillMaxSize()) {
                    SelectPhotoScreen(
                        selectedPhoto = selectedPhoto,
                        onImageSelected = {
                            mainViewModel.setSelectedImageUri(it)
                        },
                        startCompressionActivity = {
                            selectedPhoto?.let { uri ->
                                val intent = Intent(
                                    this@MainActivity,
                                    CompressionActivity::class.java
                                ).also {
                                    it.putExtra("imageUri", uri.toString())
                                }
                                startActivity(intent)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SelectPhotoScreen(
    selectedPhoto: Uri?,
    onImageSelected: (Uri) -> Unit,
    startCompressionActivity: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        PhotoPicker(
            modifier = Modifier.weight(1f),
            selectedPhoto = selectedPhoto,
            onImageSelected = onImageSelected
        )

        Button(
            modifier = Modifier.padding(bottom = 12.dp),
            onClick = {
                startCompressionActivity()
            },
            enabled = selectedPhoto != null,
            shape = RoundedCornerShape(25.dp),
        ) {
            Text(
                text = "Next",
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SelectPhotoScreenPreview() {
    JPEGCompressorTheme {
        SelectPhotoScreen(
            selectedPhoto = null,
            onImageSelected = {},
            startCompressionActivity = {}
        )
    }
}