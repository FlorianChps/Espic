package com.fchps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.fchps.espic.navigation.EspicNavHost

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val pseudo by viewModel.uiState.collectAsState()
    val navController = rememberNavController()

    EspicNavHost(navController = navController, startDestination = pseudo.orEmpty())
}