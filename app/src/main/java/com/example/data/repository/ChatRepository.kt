package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.local.AppDatabase
import com.example.data.local.ChatEntity
import com.example.data.model.ChatMessage
import com.example.data.model.MessageSender
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ChatRepository(private val context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val chatDao = db.chatDao()

    private fun isFirebaseAvailable(): Boolean {
        return try {
            FirebaseApp.getApps(context).isNotEmpty()
        } catch (_: Exception) {
            false
        }
    }

    fun getMessages(userId: String): Flow<List<ChatMessage>> {
        return chatDao.getMessagesForUser(userId).map { entities ->
            entities.map { entity ->
                ChatMessage(
                    id = entity.id,
                    sender = when (entity.sender) {
                        "user" -> MessageSender.USER
                        "ai" -> MessageSender.AI
                        else -> MessageSender.SYSTEM
                    },
                    text = entity.text,
                    timestamp = entity.timestamp
                )
            }
        }
    }

    fun searchMessages(userId: String, query: String): Flow<List<ChatMessage>> {
        return chatDao.searchMessages(userId, query).map { entities ->
            entities.map { entity ->
                ChatMessage(
                    id = entity.id,
                    sender = when (entity.sender) {
                        "user" -> MessageSender.USER
                        "ai" -> MessageSender.AI
                        else -> MessageSender.SYSTEM
                    },
                    text = entity.text,
                    timestamp = entity.timestamp
                )
            }
        }
    }

    suspend fun saveMessage(userId: String, message: ChatMessage) = withContext(Dispatchers.IO) {
        val entity = ChatEntity(
            id = message.id,
            userId = userId,
            sender = when (message.sender) {
                MessageSender.USER -> "user"
                MessageSender.AI -> "ai"
                MessageSender.SYSTEM -> "system"
            },
            text = message.text,
            timestamp = message.timestamp
        )
        chatDao.insertMessage(entity)

        // Sync with Firestore if active
        if (isFirebaseAvailable()) {
            try {
                val firestore = FirebaseFirestore.getInstance()
                val docData = hashMapOf(
                    "id" to message.id,
                    "sender" to entity.sender,
                    "text" to message.text,
                    "timestamp" to message.timestamp
                )
                firestore.collection("users").document(userId)
                    .collection("chat_history").document(message.id)
                    .set(docData)
            } catch (e: Exception) {
                Log.w("ChatRepository", "Firestore sync note: ${e.message}")
            }
        }
    }

    suspend fun clearChat(userId: String) = withContext(Dispatchers.IO) {
        chatDao.clearMessagesForUser(userId)
    }
}
