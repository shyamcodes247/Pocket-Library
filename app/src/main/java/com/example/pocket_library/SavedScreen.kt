package com.example.pocket_library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Close
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import android.graphics.Bitmap
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.graphics.asImageBitmap
import java.io.ByteArrayOutputStream
import androidx.compose.foundation.Image
import androidx.compose.material3.Button
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.lazy.items

@Composable
fun SavedScreen(vm: BookViewModel = viewModel()) {
    val localBooks by vm.localBooks.collectAsState()
    var query by remember { mutableStateOf("") }
    var editDialogBook by remember { mutableStateOf<Book?>(null) }
    val context = LocalContext.current
    val gridState = rememberLazyGridState()

    LaunchedEffect(localBooks){
        if (localBooks.isNotEmpty()) {
            delay(100)
            getScrollPosition(context).collect { (index, offset) ->
                gridState.scrollToItem(index, offset)
            }
        }
    }

    LaunchedEffect(
        gridState.firstVisibleItemIndex,
        gridState.firstVisibleItemScrollOffset
    ) {
        if (localBooks.isNotEmpty()){
            saveScrollPosition(
                context,
                gridState.firstVisibleItemIndex,
                gridState.firstVisibleItemScrollOffset
            )
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(0.dp, 16.dp, 0.dp, 0.dp)
    ) {
        var showDialog by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                vm.searchLocal(query)
            },
            label = { Text("Search saved books...") },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        )

        IconButton(
            onClick = { showDialog = true }
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Book Button"
            )
        }
    }

        if (showDialog) {
            dialogScreen(onDismissRequest = { showDialog = false }, vm)
        }

        if (editDialogBook != null)
            EditBookDialog(
                book = editDialogBook!!,
                onDismiss = { editDialogBook = null },
                onSave = { updatedBook ->
                    vm.updateBookLocal(updatedBook)
                    editDialogBook = null
                }
            )

        BoxWithConstraints(
            Modifier
                .padding(16.dp, 0.dp)
                .weight(1f)
        ) {
            val cardRatio = if (maxWidth < 360.dp) 1f else 2f / 3f
            val fontSize = if (maxWidth < 360.dp) 10.sp else 12.sp
            val iconSize = if (maxWidth < 360.dp) 16.dp else 24.dp
            val boxSize = if (maxHeight < 600.dp) 3f else 4f

            val configuration = LocalConfiguration.current
            val isPortrait =
                configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT

            val listState = rememberLazyListState()
            val gridState = rememberLazyGridState()

            if (isPortrait) {
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(items = localBooks) { book ->
                        Card(
                            Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.background
                            )
                        ) {
                            Column(Modifier.fillMaxWidth()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(2f / 3f)
                                        .padding(top = 24.dp, bottom = 24.dp)
                                ) {
                                    val imageBitmap = remember(book.image) {
                                        try {
                                            val decodedBytes =
                                                Base64.decode(book.image, Base64.DEFAULT)
                                            BitmapFactory.decodeByteArray(
                                                decodedBytes,
                                                0,
                                                decodedBytes.size
                                            )
                                        } catch (e: Exception) {
                                            null
                                        }
                                    }

                                    if (imageBitmap != null) {
                                        Image(
                                            bitmap = imageBitmap.asImageBitmap(),
                                            contentDescription = "Cover Image",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        AsyncImage(
                                            model = book.image,
                                            contentDescription = "Cover Image",
                                            contentScale = ContentScale.Fit,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }

                                    IconButton(
                                        onClick = { editDialogBook = book },
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .padding(4.dp)
                                            .size(iconSize)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Edit,
                                            contentDescription = "Edit"
                                        )
                                    }

                                    IconButton(
                                        onClick = { vm.deleteBookLocal(book) },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(4.dp)
                                            .size(iconSize)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = "Delete"
                                        )
                                    }

                                    val contactPickerLauncher = rememberLauncherForActivityResult(
                                        contract = ActivityResultContracts.PickContact()
                                    ) { contactUri: Uri? ->
                                        if (contactUri != null) {
                                            val cursor = context.contentResolver.query(
                                                contactUri,
                                                arrayOf(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY),
                                                null, null, null
                                            )
                                            var contactName = "your friend"
                                            cursor?.use {
                                                if (it.moveToFirst()) {
                                                    contactName = it.getString(
                                                        it.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
                                                    )
                                                }
                                            }

                                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(
                                                    Intent.EXTRA_TEXT,
                                                    "Hey $contactName, check out this book: ${book.author} by ${book.title}."
                                                )
                                            }
                                            context.startActivity(
                                                Intent.createChooser(
                                                    shareIntent,
                                                    "Share book via..."
                                                )
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { contactPickerLauncher.launch(null) },
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

                                Text(book.title ?: "No title", fontSize = 20.sp)
                                Text(book.author ?: "No Author", fontSize = 16.sp)
                                Text(book.year?.toString() ?: "No year", fontSize = fontSize)
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
                    items(localBooks) { book ->
                        Card(
                            Modifier
                                .aspectRatio(1f / 1.5f)
                                .wrapContentHeight(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.background
                            )
                        ) {
                            Column(Modifier.fillMaxSize()) {
                                Box(modifier = Modifier.weight(4f)) {
                                    val imageBitmap = remember(book.image) {
                                        try {
                                            val decodeBytes =
                                                Base64.decode(book.image, Base64.DEFAULT)
                                            BitmapFactory.decodeByteArray(
                                                decodeBytes,
                                                0,
                                                decodeBytes.size
                                            )
                                        } catch (e: Exception) {
                                            null
                                        }
                                    }

                                    if (imageBitmap != null) {
                                        Image(
                                            bitmap = imageBitmap.asImageBitmap(),
                                            contentDescription = "Cover Image",
                                            contentScale = ContentScale.Fit,
                                            modifier = Modifier
                                                .padding(8.dp)
                                                .height(100.dp)
                                                .fillMaxWidth()
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .padding(8.dp)
                                                .height(100.dp)
                                                .fillMaxWidth()
                                        )
                                        AsyncImage(
                                            model = book.image,
                                            contentDescription = "Cover Image",
                                            contentScale = ContentScale.Fit,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }

                                    IconButton(
                                        onClick = { editDialogBook = book },
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .padding(4.dp)
                                            .size(iconSize)
                                    ) {
                                        Icon(Icons.Filled.Edit, contentDescription = "Edit")
                                    }

                                    IconButton(
                                        onClick = { vm.deleteBookLocal(book) },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(4.dp)
                                            .size(iconSize)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = "Delete"
                                        )
                                    }

                                    val contactPickerLauncher = rememberLauncherForActivityResult(
                                        contract = ActivityResultContracts.PickContact()
                                    ) { contactUri: Uri? ->
                                        if (contactUri != null) {
                                            val cursor = context.contentResolver.query(
                                                contactUri,
                                                arrayOf(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY),
                                                null, null, null
                                            )
                                            var contactName = "your friend"
                                            cursor?.use {
                                                if (it.moveToFirst()) {
                                                    contactName = it.getString(
                                                        it.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
                                                    )
                                                }
                                            }

                                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(
                                                    Intent.EXTRA_TEXT,
                                                    "Hey $contactName, check out this book: ${book.author} by ${book.title}."
                                                )
                                            }
                                            context.startActivity(
                                                Intent.createChooser(
                                                    shareIntent,
                                                    "Share book via..."
                                                )
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { contactPickerLauncher.launch(null) },
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

                                Text(book.title ?: "No title", fontSize = 20.sp)
                                Text(book.author ?: "No Author", fontSize = 16.sp)
                                Text(book.year?.toString() ?: "No year", fontSize = fontSize)
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
fun EditBookDialog(book: Book, onDismiss: () -> Unit, onSave: (Book) -> Unit){
    var title by remember { mutableStateOf(book.title ?: "") }
    var author by remember { mutableStateOf(book.author ?: "") }
    var year by remember { mutableStateOf(book.year?.toString() ?: "") }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        imageBitmap = bitmap
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch()
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column (Modifier.padding(16.dp)){
                Text("Edit book", style = MaterialTheme.typography.titleMedium)
                TextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                TextField(value = author, onValueChange = { author = it }, label = { Text("Author") })
                TextField(value = year, onValueChange = { year = it }, label = { Text("Year") })

                Spacer(modifier = Modifier.height(8.dp))

                imageBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Captured Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { permissionLauncher.launch(android.Manifest.permission.CAMERA)}
                ) {
                    Text("Capture New Image")
                }

                Row(Modifier.align(Alignment.End)) {
                    IconButton(onClick = { onDismiss() }) {
                        Icon(Icons.Outlined.Close, contentDescription = "Cancel")
                    }
                    IconButton(onClick = {
                        onSave(
                            book.copy(
                                title = title,
                                author = author,
                                year = year.toIntOrNull(),
                                image = imageBitmap?.let { bitmap ->
                                    val output = ByteArrayOutputStream()
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, output)
                                    Base64.encodeToString(output.toByteArray(), Base64.DEFAULT)
                                } ?: book.image
                            )
                        )
                    }
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = "Save")
                    }
                }
            }
        }
    }
}

@Composable
fun dialogScreen(onDismissRequest: () -> Unit, vm: BookViewModel) {
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        imageBitmap = bitmap
    }

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

                Spacer(modifier = Modifier.height(8.dp))

                imageBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Captured Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { cameraLauncher.launch() },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Capture Cover Image")
                }

                Spacer(modifier = Modifier.height(8.dp))

                IconButton(
                    onClick = {
                        val yearInt = year.toIntOrNull()

                        val imageString = imageBitmap?.let { bitmap ->
                            val output = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, output)
                            Base64.encodeToString(output.toByteArray(), Base64.DEFAULT)
                        }

                        val book = Book(id = null, author = author, title = title, year = yearInt, image = imageString)

                        vm.saveBookLocal(book)

                        onDismissRequest()
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


