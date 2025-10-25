package com.example.pocket_library

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pocket_library.local.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: BookEntity)

    @Update
    suspend fun update(book: BookEntity)
    @Delete
    suspend fun delete(book: BookEntity)

    @Query("SELECT * FROM books ORDER BY title ASC")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE title LIKE '%' || :query || '%' or author LIKE '%' || :query || '%' ")
    fun searchBooks(query: String): Flow<List<BookEntity>>
}