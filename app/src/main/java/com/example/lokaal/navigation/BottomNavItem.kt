package com.example.lokaal.navigation

import com.example.lokaal.R

data class BottomNavItem(val icon: Int, val label: String)

val TOP_LEVEL_DESTINATIONS = mapOf(
    Route.Feed to BottomNavItem(R.drawable.home, "Feed"),
    Route.Map to BottomNavItem(R.drawable.map, "Map"),
    Route.Profile to BottomNavItem(R.drawable.profile, "Profile")
)