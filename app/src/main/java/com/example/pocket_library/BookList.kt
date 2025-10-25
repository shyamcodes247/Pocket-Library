package com.example.pocket_library

import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.magnifier
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
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
import androidx.compose.material3.IconButton
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.material3.CardDefaults
import android.content.Intent
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun BookList(state: UiState, vm: BookViewModel) {
    val saved by vm.saved.collectAsState()
    val context = LocalContext.current

    BoxWithConstraints(
        Modifier
            .padding(16.dp, 0.dp)
    ) {
        val cardRatio = if (maxWidth < 360.dp) 1f else 2f/3f
        val fontSize = if (maxWidth < 360.dp) 10.sp else 12.sp
        val iconSize = if (maxWidth < 360.dp) 16.dp else 24.dp
        val boxSize = if (maxHeight < 600.dp) 3f else 4f
        val isTablet = if (maxWidth >= 600.dp) true else false

        val configuration = LocalConfiguration.current
        val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT

        val listState = rememberLazyListState()
        val gridState = rememberLazyGridState()


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
                if (isPortrait) {
                    if (isTablet) {
                        tabletBookList(state, vm, cardRatio, fontSize, iconSize)
                    } else {
                        LazyColumn(
                            state = listState,
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(state.results) { hit ->

                                Card(
                                    Modifier
                                        .aspectRatio(cardRatio)
                                        .wrapContentHeight(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.background
                                    )
                                ) {
                                    Column(Modifier.fillMaxSize()) {

                                        Box(
                                            modifier = Modifier
                                                .weight(boxSize)
                                                .padding(top = 24.dp, bottom = 24.dp)
                                        ) {

                                            val book: Book = Book(
                                                hit.coverId.toString(),
                                                hit.title,
                                                hit.authorName?.firstOrNull(),
                                                hit.firstPublicYear,
                                                hit.getCoverImage("L"),
                                                null
                                            )

                                            AsyncImage(
                                                model = hit.getCoverImage("L"),
                                                contentDescription = "Cover Image",
                                                contentScale = ContentScale.Fit,
                                                modifier = Modifier.fillMaxSize()
                                            )

                                            IconButton(
                                                onClick = {
                                                    if (vm.isBookSaved(book)) {
                                                        vm.removeSavedBook(book)
                                                    } else {
                                                        vm.addSavedBook(book)
                                                    }
                                                },
                                                modifier = Modifier.align(Alignment.TopStart)
                                                    .size(iconSize)
                                            ) {
                                                Icon(
                                                    painter = if (vm.isBookSaved(book)) painterResource(R.drawable.bookmark_icon) else painterResource(
                                                        R.drawable.bookmark_border_icon
                                                    ),
                                                    contentDescription = "Favourite Button"
                                                )
                                            }

                                            IconButton(
                                                onClick = {
                                                    val shareIntent =
                                                        Intent(Intent.ACTION_SEND).apply {
                                                            type = "text/plain"
                                                            putExtra(
                                                                Intent.EXTRA_TEXT,
                                                                "Check out this book: ${book.title ?: "Unknown Title"} by ${book.author ?: "Unknown Author"}."
                                                            )
                                                        }
                                                    context.startActivity(
                                                        Intent.createChooser(
                                                            shareIntent,
                                                            "Share book via..."
                                                        )
                                                    )
                                                },
                                                modifier = Modifier
                                                    .align(Alignment.BottomEnd)
                                                    .padding(4.dp)
                                                    .size(iconSize)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Share,
                                                    contentDescription = "Share Book"
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
                } else {
                    LazyVerticalGrid(
                        state = gridState,
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(state.results) { hit ->
                            val book = Book(
                                id = hit.coverId.toString(),
                                author = hit.authorName?.firstOrNull(),
                                title = hit.title,
                                year = hit.firstPublicYear,
                                image = hit.getCoverImage("L"),
                                firebaseId = null
                            )

                            val isSaved = saved.contains(book)

                            Card(
                                modifier = Modifier.wrapContentHeight(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.TopStart
                                    ) {
                                        AsyncImage(
                                            model = hit.getCoverImage("L"),
                                            contentDescription = "Cover Image",
                                            contentScale = ContentScale.Fit,
                                            modifier = Modifier.fillMaxWidth()
                                                .wrapContentHeight()
                                        )
                                        IconButton(
                                            onClick = {
                                                if (isSaved) vm.removeSavedBook(book) else vm.addSavedBook(
                                                    book
                                                )
                                            },
                                            modifier = Modifier.padding(4.dp).size(iconSize)
                                                .align(Alignment.TopStart)
                                        ) {
                                            Icon(
                                                painter = if (isSaved) painterResource(R.drawable.bookmark_icon) else painterResource(R.drawable.bookmark_border_icon),
                                                contentDescription = "Save"
                                            )
                                        }
                                    }

                                    Text(text = book.title ?: "No title",  fontSize = fontSize, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                    Text(text = book.author ?: "No author", fontSize = fontSize, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(text = book.year?.toString() ?: "No year", fontSize = fontSize, maxLines = 1)
                                }
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

// Compose function for view on tablets
@Composable
fun tabletBookList(state: UiState, vm: BookViewModel, cardRatio: Float, fontSize: TextUnit, iconSize: Dp) {
    // Gets list of favourites and saved from view model
    val saved by vm.saved.collectAsState()

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.results) { hit ->
                    val book: Book = Book(
                        hit.coverId.toString(),
                        hit.authorName?.firstOrNull(),
                        hit.title,
                        hit.firstPublicYear,
                        hit.getCoverImage("L"),
                        null
                    )


                    Card(
                        onClick = { vm.selectBook(book)},
                        Modifier
                            .aspectRatio(cardRatio)
                            .wrapContentHeight(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    ) {
                        Row(Modifier.fillMaxSize()) {

                            Box(
                                modifier = Modifier.weight(1f)
                            ) {

                                val isSaved = saved.contains(book)

                                AsyncImage(
                                    model = hit.getCoverImage("L"),
                                    contentDescription = "Cover Image",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )

                                IconButton(
                                    onClick = {
                                        if (isSaved) vm.removeSavedBook(book) else vm.addSavedBook(book)
                                    },
                                    modifier = Modifier.align(Alignment.TopStart).size(iconSize)
                                ) {
                                    Icon(
                                        painter = if (isSaved) painterResource(R.drawable.bookmark_icon) else painterResource(
                                            R.drawable.bookmark_border_icon
                                        ),
                                        contentDescription = "Favourite Button"
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier.weight(3f)
                            ) {
                                Column(Modifier.fillMaxSize()) {
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
                                }
                            }

                        }
                    }
                }
            }

        }

        Box(
            modifier = Modifier.weight(1f)
        ) {
            val hit by vm.selectedBook.collectAsState()
            Text(
                text = "${hit?.title ?: "No title"}",
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                maxLines = 2,
                fontSize = fontSize
            )

            Text(
                text = "${hit?.author ?: "No title"}",
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                maxLines = 1,
                fontSize = fontSize
            )

            Text(
                text = "${hit?.year ?: "No title"}",
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                maxLines = 1,
                fontSize = fontSize
            )
        }
    }
}

@Preview
@Composable
fun tabletBookListPreview() {
    val vm: BookViewModel = viewModel()
    val state by vm.state.collectAsState()
    tabletBookList(state, vm, 2f/3f, 12.sp, 24.dp)
}
