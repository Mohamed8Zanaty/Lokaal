package com.example.lokaal.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lokaal.ui.screens.auth.AuthUiState
import com.example.lokaal.ui.screens.auth.AuthViewModel

@Composable
fun NavRoot(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel()
) {

    val signInState by authViewModel.signInState.collectAsStateWithLifecycle()
    val signUpState by authViewModel.signUpState.collectAsStateWithLifecycle()
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val isAuthenticated = currentUser != null

    LaunchedEffect(signInState, signUpState) {
        if (signInState is AuthUiState.Success || signUpState is AuthUiState.Success) {
            authViewModel.resetSignInState()
            authViewModel.resetSignUpState()
        }
    }
    if (isAuthenticated) {
        MainNavRoot(
            modifier = modifier
        )
    } else {
        AuthNavRoot(
            modifier = modifier
        )
    }
}