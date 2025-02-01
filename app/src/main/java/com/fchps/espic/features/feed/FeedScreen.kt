package com.fchps.espic.features.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.fchps.domain.model.Feed

@Composable
fun FeedScreen(
    navController: NavHostController,
    viewModel: FeedViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val newFeed by savedStateHandle?.getStateFlow<Feed?>("newFeed", null)?.collectAsState()
        ?: remember { mutableStateOf(null) }

    LaunchedEffect(newFeed) {
        if (newFeed != null) {
            viewModel.addNewFeed(newFeed = newFeed!!)
            savedStateHandle?.remove<Feed>("newFeed")
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (val state = uiState) {
            FeedUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is FeedUiState.Success -> {
                LazyColumn(
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(state.feeds) { feed ->
                        FeedItem(feed)
                    }
                }
            }

            is FeedUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Une erreur est survenue.", color = Color.Red)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.getFeed() }) {
                        Text("Réessayer")
                    }
                }
            }
        }

    }
}

@Composable
fun FeedItem(feed: Feed) {
    Card(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxSize()
            .height(320.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Box {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = feed.uri,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(0.7f),
                    text = feed.author,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.fillMaxWidth(0.3f))

                Text(
                    text = feed.likes.toString(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.width(8.dp))

                Image(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Filled.Favorite,
                    colorFilter = ColorFilter.tint(Color.Red),
                    contentDescription = "j'aime"
                )
            }
        }
    }
}