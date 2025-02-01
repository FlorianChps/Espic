package com.fchps.espic.features.photo

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.fchps.domain.model.Feed
import com.fchps.espic.features.photo.permission.PermissionDeniedUI
import com.fchps.espic.features.photo.permission.PermissionPermanentlyDeniedUI
import com.fchps.espic.features.photo.permission.PermissionState
import com.fchps.espic.navigation.BottomNavItem
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PhotoScreenWithPermission(
    navController: NavHostController,
    viewModel: PhotoViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val permissionState by viewModel.permissionState.collectAsState()
    val imageCapture = remember { mutableStateOf<ImageCapture?>(null) }

    // Launcher to request camera permission
    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                val shouldShowRationale =
                    (context as Activity).shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                viewModel.updatePermissionState(isGranted, shouldShowRationale)
            })

    BackHandler {
        navController.navigateUp()
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {

                is PhotoViewModel.PhotoEvent.NavigateToSettings -> {
                    openAppSettings(context)
                }

                is PhotoViewModel.PhotoEvent.NewFeedAdded -> {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("newFeed", event.feed)
                    navController.popBackStack()
                }
            }
        }
    }

    // Effect to request permission if not granted
    LaunchedEffect(key1 = permissionState) {
        if (permissionState == PermissionState.NotRequested) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

/*    // Observe lifecycle events to re-check permission on resume
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                val isGranted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PermissionChecker.PERMISSION_GRANTED
                if (isGranted && permissionState != PermissionState.Granted) {
                    val shouldShowRationale =
                        (context as? Activity)?.shouldShowRequestPermissionRationale(
                            Manifest.permission.CAMERA
                        ) ?: false
                    viewModel.updatePermissionState(true, shouldShowRationale)
                } else if (!isGranted) {
                    val shouldShowRationale =
                        (context as? Activity)?.shouldShowRequestPermissionRationale(
                            Manifest.permission.CAMERA
                        ) ?: false
                    viewModel.updatePermissionState(false, shouldShowRationale)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }*/

    when (permissionState) {
        is PermissionState.Granted -> {
            PhotoCameraScreen(
                viewModel = viewModel,
                imageCapture = imageCapture,
                onNewPost = { uri, filter ->
                    Log.e("PhotoScreen", "Uri to add: $uri")
                    viewModel.addFeed(uri, filter)
                }
            )
        }

        is PermissionState.Denied -> {
            PermissionDeniedUI(onRequestPermission = {
                viewModel.requestCameraPermission()
            })
        }

        is PermissionState.PermanentlyDenied -> {
            PermissionPermanentlyDeniedUI(
                onOpenSettings = {
                    viewModel.navigateToAppSettings()
                }
            )
        }

        else -> {
            viewModel.requestCameraPermission()

        }
    }
}

/**
 * Opens the app settings screen so the user can manually enable permissions.
 */
fun openAppSettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    ).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}