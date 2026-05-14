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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.lokaal.R
import com.example.lokaal.ui.auth.AuthUiState
import com.example.lokaal.ui.auth.AuthViewModel
import com.example.lokaal.ui.auth.SignInScreen
import com.example.lokaal.ui.auth.SignUpScreen
import com.example.lokaal.ui.screens.camera.CameraScreen

@Composable
fun NavRoot(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
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
    val showFab = navigationState.currentRoute is Route.Feed
    val isTopLevel = navigationState.currentRoute in TOP_LEVEL_DESTINATIONS.keys
    val signInState by authViewModel.signInState.collectAsStateWithLifecycle()
    val signUpState by authViewModel.signUpState.collectAsStateWithLifecycle()

    LaunchedEffect(signInState) {
        if (signInState is AuthUiState.Success) {
            navigator.navigate(Route.Feed)
            authViewModel.resetSignInState()
        }
    }

    LaunchedEffect(signUpState) {
        if (signUpState is AuthUiState.Success) {
            navigator.navigate(Route.Feed)
            authViewModel.resetSignUpState()
        }
    }
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
        },
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            onBack = navigator::goBack,
            entries = navigationState.toEntries(
                entryProvider {
                    entry<Route.SignIn> {
                        SignInScreen(
                            state = signInState,
                            onSignIn = authViewModel::signIn,
                            onNavigateToSignUp = { navigator.navigate(Route.SignUp) }
                        )
                    }
                    entry<Route.SignUp> {
                        SignUpScreen(
                            state = signUpState,
                            onSignUp = authViewModel::signUp,
                            onNavigateToSignIn = { navigator.goBack() }
                        )
                    }
                    entry<Route.Camera> {
                        CameraScreen(
                            onPhotoCaptured = { photoUri ->
                                navigator.navigate(Route.CreateMoment(photoUri.toString()))
                            }
                        )
                    }
                }
            )
        )
    }
}