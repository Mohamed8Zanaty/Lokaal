package com.example.lokaal.ui.screens.createmoment.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.lokaal.R
import com.example.lokaal.ui.theme.LokaalTheme
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PhotoPreview(
    photoBase64: String,
    locationName: String,
    modifier: Modifier = Modifier
) {
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(photoBase64) {
        if (photoBase64.isBlank()) {
            isLoading = false
            error = "No image data"
            return@LaunchedEffect
        }
        withContext(Dispatchers.IO) {
            try {
                val bytes = android.util.Base64.decode(photoBase64, android.util.Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                withContext(Dispatchers.Main) {
                    imageBitmap = bitmap?.asImageBitmap()
                    isLoading = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    error = "Failed to load"
                    isLoading = false
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(14.dp))
    ) {
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            imageBitmap != null -> {
                Image(
                    bitmap = imageBitmap!!,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            else -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(error ?: "Failed to load image")
                }
            }
        }
        // Location badge
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.45f),
                    shape = RoundedCornerShape(99.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.map_pin),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(10.dp)
            )
            Text(
                text = locationName,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PhotoPreviewPreview() {
    LokaalTheme {
        PhotoPreview(
            photoBase64 = "",
            locationName = "Amsterdam, Netherlands"
        )
    }
}
