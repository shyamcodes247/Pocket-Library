package com.example.pocket_library.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "book_id")
    val bookId: String  ? = "",
    @ColumnInfo(name = "author")
    val author: String? = "",
    @ColumnInfo(name = "title")
    val title: String? = "",
    @ColumnInfo(name = "year")
    val year: Int? = 0,
    @ColumnInfo(name = "image")
    val image: String? = "",
    @ColumnInfo(name = "firebaseId")
    val firebaseId: String? = ""
)