package com.example.pocket_library

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.pocket_library.local.AppDatabase
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.example.pocket_library.local.BookEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.AndroidViewModel
import android.app.Application
import androidx.room.Room


data class UiState(
    val query: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val results: List<Hit> = emptyList()
)

//data class Book(
//    var id: String? = " ",
//    val author: String? = null,
//    val title: String? = null,
//    val year: Int? = null,
//    val image: String? = null
//)

class BookViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Firebase.firestore
    private val context = getApplication<Application>().applicationContext
    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    private val roomDb = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "pocket_library_database"
    ).build()

    private val bookDao = roomDb.bookDao()
    private val _localBooks = MutableStateFlow<List<Book>>(emptyList())
    val localBooks: StateFlow<List<Book>> = _localBooks

    private val _favourites = MutableStateFlow<List<Book>>(emptyList())

    val favourites: StateFlow<List<Book>> = _favourites

    private val _saved = MutableStateFlow<List<Book>>(emptyList())

    val saved: StateFlow<List<Book>> = _saved

    private val _selectedBook = MutableStateFlow<Book?>(null)

    val selectedBook: StateFlow<Book?> = _selectedBook

    init {
        getFavourites()
        getSavedBooks()
        observeLocalBooks()

        viewModelScope.launch {
            getLastQuery(context).collect { lastQ ->
                if (lastQ.isNotEmpty()) {
                    _state.value = _state.value.copy(query = lastQ)
                    search()
                }
            }
        }
    }

    private fun observeLocalBooks() {
        viewModelScope.launch {
            bookDao.getAllBooks().collect { entities ->
                _localBooks.value = entities.map {
                    Book(it.id.toString(), it.author, it.title, it.year, it.image)
                }
            }
        }
    }

    fun saveBookLocal(book: Book){
        viewModelScope.launch {
            val bookId = book.id?.toIntOrNull() ?: 0
            bookDao.insert(
                BookEntity(
                    id = bookId,
                    title = book.title,
                    author = book.author,
                    year = book.year,
                    image = book.image
                )
            )
        }
    }

    fun deleteBookLocal(book: Book){
        viewModelScope.launch {
            val bookId = book.id?.toIntOrNull() ?: 0
            bookDao.delete(
                BookEntity(
                    id = bookId,
                    title = book.title,
                    author = book.author,
                    year = book.year,
                    image = book.image
                )
            )

            _saved.value = _saved.value.filter {it.id != book.id}
        }
    }

    fun updateBookLocal(book: Book) {
        viewModelScope.launch {
            val bookId = book.id?.toIntOrNull() ?: 0
            bookDao.update(
                BookEntity(
                    id = bookId ?: 0,
                    title = book.title ?: "",
                    author = book.author ?: "",
                    year = book.year ?: 0,
                    image = book.image ?: ""
                )
            )
        }
    }

    fun searchLocal(query: String){
        viewModelScope.launch {
            bookDao.searchBooks(query).collect { entities ->
                _localBooks.value = entities.map {
                    Book(it.id.toString(), it.author, it.title, it.year, it.image)
                }
            }
        }
    }

    var screen by mutableStateOf(0)
    private var searchJob: Job? = null

    fun updateQuery(q: String) {
        _state.value = _state.value.copy(query = q)
        viewModelScope.launch {
            saveLastQuery(context, q)
        }
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

    fun selectBook(book: Book) {
        _selectedBook.value = book
    }
}