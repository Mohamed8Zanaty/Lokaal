package com.example.lokaal.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.lokaal.R
import com.example.lokaal.ui.auth.AuthViewModel

@Composable
fun NavRoot(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val startRoute = if (authViewModel.currentUser != null) {
        Route.Feed
    } else {
        Route.SignIn
    }
    val navigationState = rememberNavigationState(
        startRoute = startRoute,
        topLevelRoutes = TOP_LEVEL_DESTINATIONS.keys
    )
    val navigator = remember { Navigator(navigationState) }
    val showFab = navigationState.topLevelRoute == Route.Feed

    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomNavBar(
                selectedKey = navigationState.topLevelRoute,
                onSelectKey = navigator::navigate
            )
        },
        floatingActionButton = {
            if(showFab) {
                FloatingActionButton(
                    onClick = {
                        navigator.navigate(Route.Camera)
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.camera),
                        contentDescription = "Create Moment"
                    )
                }
            }
        }
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            onBack = navigator::goBack,
            entries = navigationState.toEntries(
                entryProvider {

                }
            )
        )
    }
}