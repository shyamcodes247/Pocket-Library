package com.example.pocket_library

import retrofit2.http.GET
import retrofit2.http.Query

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.Path

interface OpenLibraryApi {
    @GET("/search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20
    ): OpenLibraryResponse
}

@JsonClass(generateAdapter = true)
data class OpenLibraryResponse(
    @Json(name = "num_found") val total: Int,
    @Json(name = "docs") val hits: List<Hit>
)

@JsonClass(generateAdapter = true)
data class Hit(
    @Json(name = "title") val title: String? = null,
    @Json(name = "author_name") val authorName: List<String>? = null,
    @Json(name = "first_publish_year") val firstPublicYear: Int? = null,
    @Json(name = "cover_i") val coverId: Int? = null
) {
    fun getCoverImage(size: String): String? {
        coverId?.let {
            return "https://covers.openlibrary.org/b/id/$it-$size.jpg"
        }

        return null
    }
}

