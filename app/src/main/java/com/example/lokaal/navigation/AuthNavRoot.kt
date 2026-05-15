package com.example.lokaal.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.lokaal.ui.auth.AuthUiState
import com.example.lokaal.ui.auth.AuthViewModel
import com.example.lokaal.ui.auth.SignInScreen
import com.example.lokaal.ui.auth.SignUpScreen

@Composable
fun AuthNavRoot(
    authViewModel: AuthViewModel,
    signInState: AuthUiState,
    signUpState: AuthUiState,
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(
        configuration = serializersConfig,
        Route.SignIn
    )

    NavDisplay(
        modifier = modifier.fillMaxSize(),
        backStack = backStack,
        onBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Route.SignIn> {
                SignInScreen(
                    state = signInState,
                    onSignIn = authViewModel::signIn,
                    onNavigateToSignUp = { backStack.add(Route.SignUp) }
                )
            }
            entry<Route.SignUp> {
                SignUpScreen(
                    state = signUpState,
                    onSignUp = authViewModel::signUp,
                    onNavigateToSignIn = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}