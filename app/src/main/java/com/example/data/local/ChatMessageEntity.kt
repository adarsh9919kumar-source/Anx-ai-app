package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: String,
    val sender: String, // "user" or "adarsh_ai"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val language: String = "auto"
)

@Entity(tableName = "chat_sessions")
data class ChatSessionEntity(
    @PrimaryKey
    val sessionId: String,
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val lastUpdated: Long = System.currentTimeMillis()
)
