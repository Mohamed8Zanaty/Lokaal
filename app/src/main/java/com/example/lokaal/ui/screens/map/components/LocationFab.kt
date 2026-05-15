package com.example.lokaal.ui.screens.map.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.lokaal.R

@Composable
fun LocationFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(40.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(12.dp),
        elevation = FloatingActionButtonDefaults.elevation(0.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.current_location),
            contentDescription = "My location",
            modifier = Modifier.size(18.dp)
        )
    }
}