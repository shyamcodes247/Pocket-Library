package com.example.pocket_library.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pocket_library.BookDAO

@Database(entities = [BookEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDAO

    companion object {
        @Volatile
        private var INSTANCE: com.example.pocket_library.local.AppDatabase? = null

        fun getDatabase(context: Context): com.example.pocket_library.local.AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    com.example.pocket_library.local.AppDatabase::class.java,
                    "pocket_library"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}