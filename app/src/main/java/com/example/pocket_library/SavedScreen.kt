package com.example.pocket_library

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun SavedScreen(vm: BookViewModel = viewModel()) {
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
                items(saved) { book ->
                    Card(
                        Modifier
                            .aspectRatio(2f / 3f),
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
                                    .weight(1f)
                                    .align(Alignment.CenterHorizontally),
                                fontSize = 12.sp
                            )

                            Text(
                                text = "${book.author?.firstOrNull() ?: "No Author"}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .align(Alignment.CenterHorizontally),
                                fontSize = 12.sp
                            )

                            Text(
                                text = "${book.year ?: "No publish year"}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .align(Alignment.CenterHorizontally),
                                fontSize = 12.sp
                            )
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
    val db = Firebase.firestore

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
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


@Preview
@Composable
fun SavedScreenPreview() {
    val vm: BookViewModel = viewModel()
    SavedScreen(vm)
}

@Preview
@Composable
fun dialogScreenPreview() {
    var showDialog = true
    dialogScreen(onDismissRequest = { showDialog = false}, vm = viewModel())
}