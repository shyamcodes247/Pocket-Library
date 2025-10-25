package com.example.pocket_library

data class Book(
    var id: String? = null,
    val title: String? = null,
    val author: String? = null,
    val year: Int? = null,
    val image: String? = null
)