package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prompt_history")
data class PromptHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val promptText: String,
    val category: String = "General",
    val timestamp: Long = System.currentTimeMillis()
)
