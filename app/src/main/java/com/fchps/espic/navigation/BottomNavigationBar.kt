package com.fchps.espic.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(
    navController: NavController,
    onPhotoClick: () -> Unit
) {
    val items = BottomNavItem.items
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = Modifier
            .clip(
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .background(MaterialTheme.colorScheme.background),
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        items.forEach { bottomItem ->
            val isPhoto = bottomItem.route == BottomNavItem.Photo.route

            NavigationBarItem(
                icon = {
                    if (isPhoto) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = bottomItem.icon),
                                contentDescription = bottomItem.title,
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    } else {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = bottomItem.icon),
                            contentDescription = bottomItem.title,
                            tint = LocalContentColor.current
                        )
                    }
                },
                selected = currentDestination == bottomItem.route,
                onClick = {
                    if (isPhoto) {
                        onPhotoClick()
                    } else {
                        navController.navigate(bottomItem.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                label = if (!isPhoto) {
                    { Text(fontSize = 10.sp, text = bottomItem.title) }
                } else null
            )
        }
    }
}