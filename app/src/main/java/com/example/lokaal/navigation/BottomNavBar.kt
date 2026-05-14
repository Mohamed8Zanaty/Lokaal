package com.example.lokaal.navigation

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation3.runtime.NavKey

@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier,
    selectedKey: NavKey,
    onSelectKey: (NavKey) -> Unit
) {
    BottomAppBar(modifier = modifier) {
        TOP_LEVEL_DESTINATIONS.forEach { (route, data) ->
            NavigationBarItem(
                selected = route == selectedKey,
                onClick = { onSelectKey(route) },
                icon = { Icon(painterResource(data.icon), data.label) },
                label = { Text(data.label) }
            )
        }
    }
}