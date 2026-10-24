package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val passwordHash: String,
    val photoUrl: String? = null,
    val provider: String = "email",
    val createdAt: Long = System.currentTimeMillis()
)
