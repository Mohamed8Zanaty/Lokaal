package com.example.lokaal.navigation

import androidx.compose.foundation.layout.WindowInsets
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
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.lokaal.R
import com.example.lokaal.ui.auth.AuthViewModel
import com.example.lokaal.ui.screens.camera.CameraScreen
import com.example.lokaal.ui.screens.createmoment.CreateMomentScreen
import com.example.lokaal.ui.screens.feed.FeedScreen
import com.example.lokaal.ui.screens.MapScreen
import com.example.lokaal.ui.screens.ProfileScreen
import com.example.lokaal.ui.screens.feed.FeedViewModel

@Composable
fun MainNavRoot(
    authViewModel: AuthViewModel,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navigationState = rememberNavigationState(
        startRoute = Route.Feed,                      
        topLevelRoutes = TOP_LEVEL_DESTINATIONS.keys
    )
    val navigator = remember { Navigator(navigationState) }

    val isTopLevel = navigationState.currentRoute !is Route.Camera &&
            navigationState.currentRoute !is Route.CreateMoment
    val showFab = navigationState.currentRoute is Route.Feed

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            if (isTopLevel) {
                BottomNavBar(
                    selectedKey = navigationState.topLevelRoute,
                    onSelectKey = navigator::navigate
                )
            }
        },
        floatingActionButton = {
            if (showFab) {
                FloatingActionButton(
                    onClick = { navigator.navigate(Route.Camera) },
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
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            onBack = navigator::goBack,
            entries = navigationState.toEntries(
                entryProvider {
                    entry<Route.Feed> {
                        val viewModel = hiltViewModel<FeedViewModel>()
                        val moments = viewModel.moments.collectAsLazyPagingItems()
                        FeedScreen(
                            moments = moments
                        )
                    }
                    entry<Route.Map> {
                        MapScreen()
                    }
                    entry<Route.Profile> {
                        ProfileScreen()
                    }
                    entry<Route.Camera> {
                        CameraScreen(
                            onPhotoCaptured = { photoBase64 ->
                                navigator.navigate(Route.CreateMoment(photoBase64))
                            }
                        )
                    }
                    entry<Route.CreateMoment> { (photoBase64) ->
                        CreateMomentScreen(
                            photoBase64 = photoBase64,
                            onPostSuccess = { navigator.popToRoot(Route.Feed) }
                        )
                    }
                }
            )
        )
    }
}