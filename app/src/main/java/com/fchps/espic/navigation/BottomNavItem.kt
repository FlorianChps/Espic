package com.fchps.espic.navigation

import com.fchps.espic.R

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: Int
) {

    data object Feed : BottomNavItem("feed", "Accueil", R.drawable.ic_feed)

    data object Photo :
        BottomNavItem("photo", "Photo", R.drawable.ic_camera)

    data object Profile : BottomNavItem("profile", "Profil", R.drawable.ic_profile)

    companion object {
        val items = listOf(Feed, Photo, Profile)
    }
}