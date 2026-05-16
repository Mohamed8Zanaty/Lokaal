package com.example.lokaal.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.lokaal.ui.screens.auth.SignInScreen
import com.example.lokaal.ui.screens.auth.SignUpScreen

@Composable
fun AuthNavRoot(
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(
        configuration = serializersConfig,
        Route.SignIn
    )
    NavDisplay(
        modifier = modifier.fillMaxSize(),
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Route.SignIn> {
                SignInScreen(
                    onNavigateToSignUp = { backStack.add(Route.SignUp) }
                )
            }
            entry<Route.SignUp> {
                SignUpScreen(
                    onNavigateToSignIn = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}