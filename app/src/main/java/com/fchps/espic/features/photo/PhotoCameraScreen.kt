package com.fchps.espic.features.photo

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import com.fchps.espic.R
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoCameraScreen(
    imageCapture: MutableState<ImageCapture?>,
    onNewPost: (Uri, String) -> Unit
) {
    val uri = remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val showPreview = remember { mutableStateOf(false) }

    LaunchedEffect(uri.value) {
        uri.value?.let {
            showPreview.value = true
        }
    }

    Scaffold(topBar = {
        if (showPreview.value.not()) {
            TopAppBar(
                title = { Text(stringResource(R.string.best_moment_picture)) }
            )
        }
    }, content = { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (showPreview.value && uri.value != null) {
                FinalCameraPreview(
                    modifier = Modifier.padding(paddingValues),
                    uri = uri.value,
                    onClosePreview = {
                        showPreview.value = false
                    },
                    onNewPost = { uri, filter ->
                        onNewPost(uri, filter)
                    })
            } else {
                CameraView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f),
                    imageCapture = imageCapture,
                    onError = {

                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TakePictureButton(
                        onClick = {
                            imageCapture.value?.let {
                                takePhoto(
                                    context,
                                    it,
                                    onImageCaptured = { imageCapture ->
                                        uri.value = imageCapture
                                    },
                                    onImageCapturedError = {

                                    }
                                )
                            }
                        },
                        resId = R.drawable.ic_camera,
                    )
                }
            }
        }
    })
}

@Composable
private fun FinalCameraPreview(
    modifier: Modifier = Modifier,
    uri: Uri?,
    onClosePreview: () -> Unit,
    onNewPost: (Uri, String) -> Unit
) {
    var selectedFilter by remember { mutableStateOf<String?>(null) }

    BackHandler {
        onClosePreview()
    }

    Column {
        // Preview Image + Cross to close the final preview
        Box(
            modifier = modifier
                .clip(shape = RoundedCornerShape(16.dp))
                .fillMaxWidth()
                .fillMaxHeight(0.7f),
        ) {
            Image(
                modifier = Modifier
                    .fillMaxSize(),
                painter = rememberAsyncImagePainter(uri),
                contentDescription = "Captured Image",
                contentScale = ContentScale.Crop
            )

            IconButton(
                onClick = { onClosePreview() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.6f), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        }

        Text(
            modifier = Modifier.padding(8.dp),
            textAlign = TextAlign.Center,
            text = "Sélectionne une catégorie ci-dessous pour publier ta photo :"
        )

        // Row of Filter Chips
        LazyRow(
            modifier = Modifier.padding(4.dp)
        ) {
            val filters = mapOf(
                "Cartes personnalisées Counter Strike 2" to "map",
                "Setup de gaming" to "setup",
                "Meilleur skin voiture Rocket League" to "rl_car"
            )
            items(filters.keys.toList()) { filter ->
                FilterChip(
                    modifier = Modifier.padding(end = 8.dp),
                    selected = selectedFilter == filters[filter],
                    onClick = {
                        selectedFilter = filters[filter]
                    },
                    label = { Text(text = filter) },
                )
            }
        }

        TakePictureButton(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            onClick = {
                if (selectedFilter?.isEmpty()?.not() == true && uri != null) {
                    onNewPost(uri, selectedFilter!!)
                }
            },
            resId = R.drawable.ic_check,
            enable = selectedFilter?.isNotEmpty() == true
        )
    }
}

@Composable
fun TakePictureButton(
    onClick: () -> Unit, modifier: Modifier = Modifier, resId: Int, enable: Boolean = true
) {
    Button(
        enabled = enable,
        onClick = onClick,
        modifier = modifier
            .size(100.dp)
            .padding(16.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(resId),
            contentDescription = "Take Picture",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

/**
 * Function to take photo and have ImageCapture output
 * to load uri in PreviewFinalCamera composable
 */
fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    onImageCaptured: (Uri) -> Unit,
    onImageCapturedError: (ImageCaptureException) -> Unit
) {
    val name =
        SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(System.currentTimeMillis())
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Espic-Images")
        }
    }

    // Create output options object which contains file + metadata
    val outputOptions = ImageCapture.OutputFileOptions.Builder(
        context.contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
    ).build()

    // Set up image capture listener, which is triggered after photo has been taken
    imageCapture.takePicture(outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                onImageCapturedError(exc)
                Log.e("CameraView", "Photo capture failed: ${exc.message}", exc)
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                output.savedUri?.let {
                    onImageCaptured(it)
                }
            }
        })
}