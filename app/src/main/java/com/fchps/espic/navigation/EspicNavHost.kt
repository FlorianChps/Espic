package com.fchps.espic.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.fchps.espic.features.feed.FeedScreen
import com.fchps.espic.features.login.LoginScreen
import com.fchps.espic.features.photo.PhotoScreenWithPermission
import com.fchps.espic.features.profile.ProfileScreen

@Composable
fun EspicNavHost(
    navController: NavHostController,
    startDestination: String,
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val isPhotoScreen = currentRoute == "photo_create" || currentRoute == "login"

    Scaffold(
        bottomBar = {
            if (!isPhotoScreen) {
                BottomNavigationBar(
                    navController = navController,
                    onPhotoClick = { navController.navigate("photo_create") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = if (startDestination.isEmpty()) "login" else BottomNavItem.Feed.route
            ) {
                composable(route = "login") {
                    LoginScreen(navController)
                }

                composable(route = BottomNavItem.Feed.route) {
                    FeedScreen(navController = navController)
                }

                composable(route = BottomNavItem.Profile.route) {
                    ProfileScreen(navController)
                }

                composable(route = "photo_create") {
                    PhotoScreenWithPermission(
                        navController = navController
                    )
                }
            }
        }
    }
}