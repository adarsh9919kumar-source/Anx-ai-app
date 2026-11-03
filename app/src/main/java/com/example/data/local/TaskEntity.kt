package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val reminderTime: String? = null, // e.g., "5:00 PM", "tomorrow 9 AM"
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
