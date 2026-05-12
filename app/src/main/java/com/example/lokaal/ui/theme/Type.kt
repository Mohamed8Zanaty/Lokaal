package com.example.lokaal.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    // Screen titles in top bar
    titleLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    // Section headers
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    // Body text
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    // Captions (date, category label)
    labelSmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp
    ),
    // Big amount number
    displaySmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp
    )
)