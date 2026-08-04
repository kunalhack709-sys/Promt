package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val isDarkMode: Boolean = true,
    val autoFixErrors: Boolean = true,
    val activeProjectId: Long = 1L,
    val selectedModel: String = "gemini-3.5-flash"
)
