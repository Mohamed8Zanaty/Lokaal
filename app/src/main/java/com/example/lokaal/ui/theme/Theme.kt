package com.example.lokaal.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Composable
fun LokaalTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        primary             = Purple700,
        onPrimary           = Purple50,
        primaryContainer    = Purple50,
        onPrimaryContainer  = Purple900,

        secondary           = Coral400,
        onSecondary         = Color.White,
        secondaryContainer  = Coral50,
        onSecondaryContainer = Coral600,

        background          = Gray50,
        onBackground        = Gray900,
        surface             = Color.White,
        onSurface           = Gray900,
        surfaceVariant      = Gray100,
        onSurfaceVariant    = Gray600,
        outline             = Gray200,
        outlineVariant      = Gray100,
        error               = Coral400,
        onError             = Color.White
    )
    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}