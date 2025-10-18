package com.example.pocket_library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

@Composable
fun BookList(state: UiState) {
    Box(
        Modifier
            .padding(16.dp, 0.dp)
    ) {
        when {
            state.loading -> {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }

            state.error != null -> {
                Text(
                    text = state.error ?: "Error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            state.results.isEmpty() && state.query.isNotEmpty() -> {
                Text("No results", modifier = Modifier.align(Alignment.Center))
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(140.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(state.results) { hit ->
                        Card(Modifier
                            .aspectRatio(2f/3f),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.background
                            )
                        ) {
                            Column(Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = hit.getCoverImage("S"),
                                    contentDescription = "Cover Image",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(4f)
                                )

                                Text(
                                    text = "${hit.title ?: "No title"}",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .align(Alignment.CenterHorizontally)
                                        .size(8.dp)
                                )

                                Text(
                                    text = "${hit.authorName?.firstOrNull() ?: "No title"}",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .align(Alignment.CenterHorizontally)
                                        .size(8.dp)
                                )

                                Text(
                                    text = "${hit.firstPublicYear ?: "No title"}",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .align(Alignment.CenterHorizontally)
                                        .size(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun BookListPreview() {
    val vm: BookViewModel = viewModel()
    val state by vm.state.collectAsState()
    BookList(state)
}

