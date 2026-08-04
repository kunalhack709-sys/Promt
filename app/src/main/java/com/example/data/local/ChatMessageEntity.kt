package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val sender: String, // "USER" or "AGENT"
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val reasoningStepsJson: String? = null, // JSON string array of steps
    val fileChangesJson: String? = null, // JSON list of modified/created files
    val commandLog: String? = null,
    val status: String = "SUCCESS" // THINKING, GENERATING, SUCCESS, ERROR
)
