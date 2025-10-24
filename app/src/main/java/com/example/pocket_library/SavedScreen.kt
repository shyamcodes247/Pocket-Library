package com.example.pocket_library

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun SavedScreen(vm: BookViewModel = viewModel()) {
    // Gets all saved books from view model
    val saved by vm.saved.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .padding(0.dp, 16.dp, 0.dp, 0.dp)
    ) {
        var showDialog by remember { mutableStateOf(false) }

        IconButton(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Book Button"
            )
        }

        if (showDialog) {
            dialogScreen(onDismissRequest = { showDialog = false }, vm)
        }

        BoxWithConstraints(
            Modifier
                .padding(16.dp, 0.dp)
                .weight(1f)
        ) {
            val cardRatio = if (maxWidth < 360.dp) 1f else 2f / 3f
            val fontSize = if (maxWidth < 360.dp) 10.sp else 12.sp
            val iconSize = if (maxWidth < 360.dp) 16.dp else 24.dp
            val isTablet = if (maxWidth >= 600.dp) true else false

            if (isTablet) {
                tabletSavedScreen(vm, cardRatio, fontSize, iconSize)
            } else {

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(140.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(saved) { book ->
                        Card(
                            Modifier
                                .aspectRatio(cardRatio),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.background
                            )
                        ) {
                            Column(Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = book.image,
                                    contentDescription = "Cover Image",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.weight(4f)
                                )

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
        }

        NavBar(modifier = Modifier, vm)
    }
}

@Composable
fun dialogScreen(onDismissRequest: () -> Unit, vm: BookViewModel) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
        ) {
            var title by remember { mutableStateOf("") }
            var author by remember { mutableStateOf("") }
            var year by remember { mutableStateOf("0") }

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                IconButton(
                    onClick = { onDismissRequest() },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Close dialog button"
                    )
                }

                TextField(
                    value = title,
                    onValueChange = { newTitle -> title = newTitle },
                    label = { Text("Enter the name of the book: ") }
                )

                TextField(
                    value = author,
                    onValueChange = { newAuthor -> author = newAuthor },
                    label = { Text("Enter the author of the book: ") }
                )

                TextField(
                    value = year,
                    onValueChange = { newYear -> year = newYear },
                    label = { Text("Enter the publication year of the book: ") }
                )

                IconButton(
                    onClick = {
                        val book: Book = Book(" ", author, title, year.toInt(), null)
                        vm.addSavedBook(book)
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.bookmark_border_icon),
                        contentDescription = "Add Book Button"
                    )
                }
            }
        }
    }
}

@Composable
fun tabletSavedScreen(vm: BookViewModel, cardRatio: Float, fontSize: TextUnit, iconSize: Dp) {
    // Gets list of saved from view model
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
                items(saved) { hit ->
                    val book: Book = Book(
                        hit.id.toString(),
                        hit.author,
                        hit.title,
                        hit.year,
                        hit.image
                    )


                    Card(
                        onClick = { vm.selectSavedBook(book)},
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
                                    model = hit.image,
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
                                        text = "${hit.author?.firstOrNull() ?: "No title"}",
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
            val hit by vm.savedSelectBook.collectAsState()
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
fun SavedScreenPreview() {
    val vm: BookViewModel = viewModel()
    SavedScreen(vm)
}

@Previews
@Composable
fun dialogScreenPreview() {
    var showDialog = true
    dialogScreen(onDismissRequest = { showDialog = false}, vm = viewModel())
}