package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val sender: String,
    val text: String,
    val timestamp: Long
)
