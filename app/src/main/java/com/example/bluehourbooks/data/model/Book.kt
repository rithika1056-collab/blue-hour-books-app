package com.example.bluehourbooks.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@Entity(tableName = "books")
data class Book(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val author: String? = null,
    val cover: String? = null,
    val isbn: String? = null,
    val publicationYear: Int? = null,
    val publisher: String? = null,
    val description: String? = null,
    val rating: Double = 0.0,
    val dateCompleted: String? = null,
    val catalogSource: String? = null,
    val externalBookId: String? = null,
    val categories: String? = null, // Stored as comma-separated string
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
data class BookSearchResult(
    val id: String,
    val externalBookId: String? = null,
    val catalogSource: String? = null,
    val title: String,
    val author: String? = null,
    val cover: String? = null,
    val isbn: String? = null,
    val publicationYear: Int? = null,
    val publisher: String? = null,
    val description: String? = null,
    val categories: List<String>? = null
)
