package com.example.pocket_library

import retrofit2.http.GET
import retrofit2.http.Query

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

interface OpenLibraryApi {
    @GET("/search.json")
    suspend fun searchImages(
        @Query("q") query: String
    ): OpenLibraryResponse
}

@JsonClass(generateAdapter = true)
data class OpenLibraryResponse(
    val total: Int,
    val totalHits: Int,
    val hits: List<Hit>
)

@JsonClass(generateAdapter = true)
data class Hit(
    val id: Int,
    @Json(name = "previewURL") val previewUrl: String,
    @Json(name = "webformatURL") val webUrl: String,
    @Json(name = "largeImageURL") val largeUrl: String,
    val tags: String
)

