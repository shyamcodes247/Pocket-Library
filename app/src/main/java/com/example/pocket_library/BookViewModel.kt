package com.example.pocket_library

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val id: Int? = null,
    val author: String? = null,
    val title: String? = null,
    val year: Int? = null,
    val image: String? = null
)

class BookViewModel : ViewModel() {
    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    private val _favourites = MutableStateFlow<List<Book>>(emptyList())

    val favourites: StateFlow<List<Book>> = _favourites

    var screen by mutableStateOf(0)
    private var searchJob: Job? = null

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
        _favourites.value = _favourites.value + book
    }

    fun removeFavourite(book: Book) {
        _favourites.value = _favourites.value.filter {it.id != book.id}
    }

    fun isFavourite(book: Book): Boolean {
        return _favourites.value.any { it.id == book.id }
    }

}