package com.example.pocket_library

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.IconButton
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun BookList(state: UiState, vm: BookViewModel) {
    val saved by vm.saved.collectAsState()
    val favourites by vm.favourites.collectAsState()

    BoxWithConstraints(
        Modifier
            .padding(16.dp, 0.dp)
    ) {
        val cardRatio = if (maxWidth < 360.dp) 1f else 2f/3f
        val fontSize = if (maxWidth < 360.dp) 10.sp else 12.sp
        val iconSize = if (maxWidth < 360.dp) 16.dp else 24.dp
        val boxSize = if (maxHeight < 600.dp) 3f else 4f

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
                            .aspectRatio(cardRatio)
                            .wrapContentHeight(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.background
                            )
                        ) {
                            Column(Modifier.fillMaxSize()) {

                                Box(
                                    modifier = Modifier.weight(boxSize)
                                ) {

                                    val book: Book = Book(hit.coverId.toString(), hit.authorName?.firstOrNull(), hit.title, hit.firstPublicYear,hit.getCoverImage("S"))

                                    val isSaved = saved.contains(book)
                                    val isFavourite = favourites.contains(book)

                                    AsyncImage(
                                        model = hit.getCoverImage("S"),
                                        contentDescription = "Cover Image",
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier.fillMaxSize()
                                    )

                                    IconButton(
                                        onClick = { if (isSaved) vm.removeSavedBook(book) else vm.addSavedBook(book) },
                                        modifier = Modifier.align(Alignment.TopStart).size(iconSize)
                                    ) {
                                        Icon(
                                            painter = if (isSaved) painterResource(R.drawable.bookmark_icon) else painterResource(R.drawable.bookmark_border_icon),
                                            contentDescription = "Favourite Button"
                                        )
                                    }

                                    IconButton(
                                        onClick = { if (isFavourite) vm.removeFavourite(book) else vm.addFavourite(book) },
                                        modifier = Modifier.align(Alignment.TopEnd).size(iconSize)
                                    ) {
                                        Icon(
                                            painter = if (isFavourite) painterResource(R.drawable.favourite_icon) else painterResource(R.drawable.favourite_outline_icon),
                                            contentDescription = "Favourite Button"
                                        )
                                    }

                                }

                                Text(
                                    text = "${hit.title ?: "No title"}",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .align(Alignment.CenterHorizontally),
                                    maxLines = 2,
                                    fontSize = fontSize
                                )

                                Text(
                                    text = "${hit.authorName?.firstOrNull() ?: "No title"}",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .align(Alignment.CenterHorizontally),
                                    maxLines = 1,
                                    fontSize = fontSize
                                )

                                Text(
                                    text = "${hit.firstPublicYear ?: "No title"}",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .align(Alignment.CenterHorizontally),
                                    maxLines = 1,
                                    fontSize = fontSize
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
    BookList(state, vm)
}

