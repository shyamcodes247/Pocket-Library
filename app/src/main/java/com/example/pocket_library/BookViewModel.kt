package com.example.pocket_library

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UiState(
    val query: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val results: List<Hit> = emptyList()
)

data class Book(
    var id: String? = " ",
    val author: String? = null,
    val title: String? = null,
    val year: Int? = null,
    val image: String? = null
)

class BookViewModel : ViewModel() {
    // Initialising firestore
    private val db = Firebase.firestore
    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    private val _favourites = MutableStateFlow<List<Book>>(emptyList())

    val favourites: StateFlow<List<Book>> = _favourites

    private val _saved = MutableStateFlow<List<Book>>(emptyList())

    val saved: StateFlow<List<Book>> = _saved

    private val _selectedBook = MutableStateFlow<Book?>(null)

    val selectedBook: StateFlow<Book?> = _selectedBook

    private val _favouriteSelectBook = MutableStateFlow<Book?>(null)

    val favouriteSelectBook: StateFlow<Book?> = _favouriteSelectBook

    private val _savedSelectBook = MutableStateFlow<Book?>(null)

    val savedSelectBook: StateFlow<Book?> = _savedSelectBook


    init {
        getFavourites()
        getSavedBooks()
    }

    var screen by mutableStateOf(0)
    private var searchJob: Job? = null

    // Searching functions for implementing searching for books
    fun updateQuery(q: String) {
        _state.value = _state.value.copy(query = q)
        // Simple debounce:
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            search()
        }
    }

    fun search() {
        val q = _state.value.query.trim()
        if (q.isEmpty()) {
            _state.value = _state.value.copy(results = emptyList(),
                error = null, loading = false)
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                val resp = Network.api.searchBooks(
                    query = q
                )
                _state.value = _state.value.copy(results = resp.hits, loading = false)
            } catch (t: Throwable) {
                _state.value = _state.value.copy(
                    loading = false,
                    error = t.message ?: "Something went wrong"
                )
            }
        }
    }

    // Functions for favouriting books
    fun addFavourite(book: Book) {
        db.collection("favourites")
            .add(book)
            .addOnSuccessListener { bookRef ->
                Log.d("BookViewModel", "Book added with id: ${bookRef.id}")
                book.id = bookRef.id
            }
            .addOnFailureListener { e ->
                Log.w("BookViewModel", "Error adding book ", e)
            }

        _favourites.value = _favourites.value + book
    }

    // Function for removing favourited book from firestore
    fun removeFavourite(book: Book) {
        db.collection("favourites")
            .document(book.id.toString())
            .delete()
            .addOnSuccessListener {
                Log.d("BookViewModel", "Book successfully deleted!")
            }
            .addOnFailureListener { e ->
                Log.w("BookViewModel", "Error deleting book", e)
            }

        _favourites.value = _favourites.value.filter {it.id != book.id}
    }

    // Function for saving a book (as saved) to firestore
    fun addSavedBook(book: Book) {
        db.collection("saved")
            .add(book)
            .addOnSuccessListener { bookRef ->
                Log.d(TAG, "Book added with id: ${bookRef.id}")
                book.id = bookRef.id
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding book ", e)
            }

        _saved.value = _saved.value + book
    }

    // Removing saved book from firestore
    fun removeSavedBook(book: Book) {
        db.collection("saved")
            .document(book.id.toString())  // bookId is the Firestore document ID
            .delete()
            .addOnSuccessListener {
                Log.d("BookViewModel", "Saved book successfully deleted!")
            }
            .addOnFailureListener { e ->
                Log.w("BookViewModel", "Error deleting book", e)
            }

        _saved.value = _saved.value.filter {it.id != book.id}
    }

    // Function for getting favourited books to firestore
    fun getFavourites() {
        db.collection("favourites")
            .get()
            .addOnSuccessListener { result ->
                val list = result.documents.mapNotNull { it.toObject(Book::class.java) }
                _favourites.value = list
            }
            .addOnFailureListener { e ->
                Log.e("BookViewModel", "Error fetching books", e)
            }
    }

    // Function for getting saved books to firestore
    fun getSavedBooks() {
        db.collection("saved")
            .get()
            .addOnSuccessListener { result ->
                val list = result.documents.mapNotNull { it.toObject(Book::class.java) }
                _saved.value = list
            }
            .addOnFailureListener { e ->
                Log.e("BookViewModel", "Error fetching books", e)
            }
    }

    // Function for selecting a book to view on tablet
    fun selectBook(book: Book) {
        _selectedBook.value = book
    }

    // Function for selecting a favourite book to view on tablet
    fun selectFavouriteBook(book: Book) {
        _favouriteSelectBook.value = book
    }

    // Function for selecting a saved book to view on tablet
    fun selectSavedBook(book: Book) {
        _savedSelectBook.value = book
    }
}