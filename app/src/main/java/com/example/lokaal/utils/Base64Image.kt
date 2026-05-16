package com.example.lokaal.utils

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@Composable
fun Base64Image(
    base64: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    var bitmap by remember(base64) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(base64) {
        withContext(Dispatchers.IO) {
            try {
                val bytes = Base64.decode(base64, Base64.DEFAULT)
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    bitmap?.let {
        Image(
            bitmap = it,
            contentDescription = null,
            contentScale = contentScale,
            modifier = modifier
        )
    }
}