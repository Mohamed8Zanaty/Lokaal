package com.example.lokaal.navigation

import androidx.navigation3.runtime.NavKey

class Navigator(val state: NavigationState) {
    fun navigate(route: NavKey) {
        if (route in state.backStacks.keys) {
            // Top‑level destination → switch tab
            state.topLevelRoute = route
        } else {
            // Internal screen → push onto current tab’s back stack
            state.backStacks[state.topLevelRoute]?.add(route)
        }
    }

    fun goBack() {
        val currentBackStack = state.backStacks[state.topLevelRoute]
            ?: error("Back Stack for ${state.topLevelRoute} does not exist")
        val currentRoute = currentBackStack.last()
        if (currentRoute == state.topLevelRoute) {
            // At the root of current tab → fallback to start route
            state.topLevelRoute = state.startRoute
        } else {
            currentBackStack.removeLastOrNull()
        }
    }
    fun popToRoot(topLevelRoute: NavKey) {

        state.backStacks[state.topLevelRoute]?.clear()
        state.backStacks[state.topLevelRoute]?.add(state.topLevelRoute)
        state.topLevelRoute = topLevelRoute
    }
}