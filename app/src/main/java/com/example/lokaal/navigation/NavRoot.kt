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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.lokaal.ui.screens.MapScreen
import com.example.lokaal.ui.screens.ProfileScreen
import com.example.lokaal.ui.screens.camera.CameraScreen
import com.example.lokaal.ui.screens.createmoment.CreateMomentScreen
import com.example.lokaal.ui.screens.feed.FeedScreen

@Composable
fun NavRoot(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var isAuthenticated by remember {
        mutableStateOf(authViewModel.currentUser != null)
    }
    val signInState by authViewModel.signInState.collectAsStateWithLifecycle()
    val signUpState by authViewModel.signUpState.collectAsStateWithLifecycle()
    LaunchedEffect(signInState, signUpState) {
        if (signInState is AuthUiState.Success || signUpState is AuthUiState.Success) {
            isAuthenticated = true
            authViewModel.resetSignInState()
            authViewModel.resetSignUpState()
        }
    }
    if (isAuthenticated) {
        MainNavRoot(
            authViewModel = authViewModel,
            onSignOut = { isAuthenticated = false }
        )
    } else {
        AuthNavRoot(
            authViewModel = authViewModel,
            signInState = signInState,
            signUpState = signUpState
        )
    }
}