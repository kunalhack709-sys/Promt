package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val prompt: String,
    val language: String, // React, Next.js, HTML, Python, Node, Vue, FastAPI, Kotlin, etc.
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val deployedUrl: String? = null,
    val gitBranch: String = "main",
    val status: String = "READY" // BUILDING, READY, ERROR
)
