package com.example.pocket_library

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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

@Composable
fun FavouriteScreen(vm: BookViewModel = viewModel()) {
    val favourites by vm.favourites.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .padding(0.dp, 16.dp, 0.dp, 0.dp)
    ) {

        Box(
            Modifier
                .padding(16.dp, 0.dp)
                .weight(1f)
        ) {

            LazyVerticalGrid(
                columns = GridCells.Adaptive(140.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(favourites) { book ->
                    Card(
                        Modifier
                            .aspectRatio(2f / 3f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    ) {
                        Column(Modifier.fillMaxSize()) {

                            Box(
                                modifier = Modifier.weight(4f)
                            ) {

                                AsyncImage(
                                    model = book.image,
                                    contentDescription = "Cover Image",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )

                                IconButton(
                                    onClick = {
                                        if (vm.isFavourite(book)) vm.removeFavourite(book) else vm.addFavourite(
                                            book
                                        )
                                    },
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    Icon(
                                        imageVector = if (vm.isFavourite(book)) Icons.Filled.Favorite else Icons.Outlined.Favorite,
                                        contentDescription = "Favourite Button"
                                    )
                                }

                            }

                            Text(
                                text = "${book.title ?: "No title"}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .align(Alignment.CenterHorizontally)
                                    .size(8.dp)
                            )

                            Text(
                                text = "${book.author?.firstOrNull() ?: "No Author"}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .align(Alignment.CenterHorizontally)
                                    .size(8.dp)
                            )

                            Text(
                                text = "${book.year ?: "No publish year"}",
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

        NavBar(modifier = Modifier, vm)

    }
}

@Preview(showBackground = true)
@Composable
fun FavouriteScreenPreview() {
    val vm: BookViewModel = viewModel()
    FavouriteScreen(vm)
}