package com.example.pocket_library

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun FavouriteScreen(vm: BookViewModel = viewModel()) {
    val favourites by vm.favourites.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .padding(0.dp, 16.dp, 0.dp, 0.dp)
    ) {

        BoxWithConstraints(
            Modifier
                .padding(16.dp, 0.dp)
                .weight(1f)
        ) {
            val cardRatio = if (maxWidth < 360.dp) 1f else 2f/3f
            val fontSize = if (maxWidth < 360.dp) 10.sp else 12.sp
            val iconSize = if (maxWidth < 360.dp) 16.dp else 24.dp
            val boxSize = if (maxHeight < 600.dp) 3f else 4f

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
                            .aspectRatio(cardRatio),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    ) {
                        Column(Modifier.fillMaxSize()) {

                            Box(
                                modifier = Modifier.weight(boxSize)
                            ) {
                                val isFavourite = favourites.contains(book)

                                AsyncImage(
                                    model = book.image,
                                    contentDescription = "Cover Image",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )

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
                                text = "${book.title ?: "No title"}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .align(Alignment.CenterHorizontally),
                                maxLines = 2,
                                fontSize = fontSize
                            )

                            Text(
                                text = "${book.author?.firstOrNull() ?: "No Author"}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .align(Alignment.CenterHorizontally),
                                maxLines = 1,
                                fontSize = fontSize
                            )

                            Text(
                                text = "${book.year ?: "No publish year"}",
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

        NavBar(modifier = Modifier, vm)

    }
}

@Preview(showBackground = true)
@Composable
fun FavouriteScreenPreview() {
    val vm: BookViewModel = viewModel()
    FavouriteScreen(vm)
}