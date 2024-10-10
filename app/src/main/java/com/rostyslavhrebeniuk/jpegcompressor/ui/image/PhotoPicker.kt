package com.rostyslavhrebeniuk.jpegcompressor.ui.image

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PhotoPicker(
    modifier: Modifier = Modifier,
    selectedPhoto: Uri?,
    onImageSelected: (Uri) -> Unit
) {

    var selectedImage by remember(selectedPhoto) { mutableStateOf(selectedPhoto) }

    val singlePhotoPickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri ->
                if (uri == null) return@rememberLauncherForActivityResult
                selectedImage = uri
                onImageSelected(uri)
            })

    fun launchPhotoPicker() {
        singlePhotoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        if (selectedImage == null) {
            Image(
                imageVector = Icons.Rounded.Add,
                colorFilter = ColorFilter.tint(Color.LightGray),
                contentDescription = null,
                modifier = Modifier
                    .matchParentSize()
                    .clickable {
                        launchPhotoPicker()
                    }
                    .padding(24.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .border(2.dp, Color.LightGray, RoundedCornerShape(25.dp))
                    .background(Color.Transparent)
            )
        } else {
            ShimmerImage(
                modifier = Modifier
                    .matchParentSize()
                    .padding(24.dp)
                    .clip(RoundedCornerShape(25.dp)),
                data = selectedImage.toString()
            )
        }

        FilledIconButton(
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.BottomEnd),
            onClick = {
                launchPhotoPicker()
            }
        ) {
            Icon(Icons.Outlined.Edit, contentDescription = "Pick Photo")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PhotoPickerPreview() {
    Column {
        PhotoPicker(selectedPhoto = null) {

        }
    }
}