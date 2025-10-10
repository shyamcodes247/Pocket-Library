package com.example.pocket_library

import retrofit2.http.GET
import retrofit2.http.Query

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.Path

interface OpenLibraryApi {
    @GET("/search.json")
    suspend fun searchBooks(
        @Query("q") query: String
    ): OpenLibraryResponse
}

@JsonClass(generateAdapter = true)
data class OpenLibraryResponse(
    val total: Int,
    val totalHits: Int,
    @Json(name = "docs") val hits: List<Hit>
)

@JsonClass(generateAdapter = true)
data class Hit(
    @Json(name = "title") val title: String,
    @Json(name = "author_name") val authorName: List<String>,
    @Json(name = "first_publish_year") val firstPublicYear: Int,
    @Json(name = "cover_i") val coverId: Int
) {
    fun getCoverImage(size: String): String? {
        return coverId?.let {
            "https://covers.openlibrary.org/b/id/$it-$size.jpg"
        }
    }
}

