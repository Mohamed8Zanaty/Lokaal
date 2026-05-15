package com.example.lokaal.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lokaal.ui.auth.AuthUiState
import com.example.lokaal.ui.auth.AuthViewModel

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