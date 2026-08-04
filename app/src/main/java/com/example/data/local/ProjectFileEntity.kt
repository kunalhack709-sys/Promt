package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "project_files")
data class ProjectFileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val path: String, // e.g. "src/App.tsx" or "package.json"
    val content: String,
    val language: String, // typescript, javascript, html, css, python, json, markdown, kotlin, etc.
    val isDirectory: Boolean = false,
    val isModified: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
