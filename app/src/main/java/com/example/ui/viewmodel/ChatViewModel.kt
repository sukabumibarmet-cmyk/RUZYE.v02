package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ChatMessage
import com.example.data.model.MessageSender
import com.example.data.remote.AiService
import com.example.data.repository.ChatRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ChatViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {
    private val chatRepository = ChatRepository(application)
    private val aiService = AiService()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()

    private val _speakingMessageId = MutableStateFlow<String?>(null)
    val speakingMessageId: StateFlow<String?> = _speakingMessageId.asStateFlow()

    private var currentUserId: String = ""
    private var messagesJob: Job? = null
    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    init {
        try {
            tts = TextToSpeech(application, this)
        } catch (e: Exception) {
            Log.e("ChatViewModel", "TTS Init Error: ${e.message}")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.forLanguageTag("id-ID"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.language = Locale.ENGLISH
            }
            isTtsReady = true
        }
    }

    fun initForUser(userId: String) {
        if (currentUserId == userId && messagesJob != null) return
        currentUserId = userId
        observeMessages()
    }

    private fun observeMessages() {
        messagesJob?.cancel()
        messagesJob = viewModelScope.launch {
            if (_searchQuery.value.isBlank()) {
                chatRepository.getMessages(currentUserId).collectLatest { list ->
                    _messages.value = list
                }
            } else {
                chatRepository.searchMessages(currentUserId, _searchQuery.value.trim()).collectLatest { list ->
                    _messages.value = list
                }
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        observeMessages()
    }

    fun toggleSearch(active: Boolean) {
        _isSearchActive.value = active
        if (!active) {
            _searchQuery.value = ""
            observeMessages()
        }
    }

    fun sendMessage(promptText: String) {
        val trimmed = promptText.trim()
        if (trimmed.isEmpty() || _isGenerating.value || currentUserId.isEmpty()) return

        viewModelScope.launch {
            val userMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                sender = MessageSender.USER,
                text = trimmed,
                timestamp = System.currentTimeMillis()
            )
            chatRepository.saveMessage(currentUserId, userMsg)

            _isGenerating.value = true

            val history = _messages.value.takeLast(6).map {
                (if (it.sender == MessageSender.USER) "user" else "model") to it.text
            }

            val result = aiService.generateAiResponse(trimmed, history)

            _isGenerating.value = false

            result.fold(
                onSuccess = { reply ->
                    val aiMsg = ChatMessage(
                        id = UUID.randomUUID().toString(),
                        sender = MessageSender.AI,
                        text = reply,
                        timestamp = System.currentTimeMillis()
                    )
                    chatRepository.saveMessage(currentUserId, aiMsg)
                },
                onFailure = { err ->
                    val errorMsg = ChatMessage(
                        id = UUID.randomUUID().toString(),
                        sender = MessageSender.AI,
                        text = "Maaf, terjadi kendala koneksi ke server AI (${err.message ?: "Silakan coba beberapa saat lagi"}).",
                        timestamp = System.currentTimeMillis()
                    )
                    chatRepository.saveMessage(currentUserId, errorMsg)
                }
            )
        }
    }

    fun clearChat() {
        if (currentUserId.isEmpty()) return
        viewModelScope.launch {
            stopSpeaking()
            chatRepository.clearChat(currentUserId)
        }
    }

    fun speakMessage(messageId: String, text: String) {
        if (!isTtsReady || tts == null) return

        if (_speakingMessageId.value == messageId) {
            stopSpeaking()
            return
        }

        stopSpeaking()
        _speakingMessageId.value = messageId
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, messageId)
    }

    fun stopSpeaking() {
        tts?.stop()
        _speakingMessageId.value = null
    }

    fun exportChat(context: Context) {
        val list = _messages.value
        if (list.isEmpty()) return

        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val sb = StringBuilder()
        sb.append("=== Riwayat Chat RUZIE AI ===\n\n")
        list.forEach { msg ->
            val senderLabel = when (msg.sender) {
                MessageSender.USER -> "Pengguna"
                MessageSender.AI -> "RUZIE AI"
                MessageSender.SYSTEM -> "Sistem"
            }
            val time = sdf.format(Date(msg.timestamp))
            sb.append("[$time] $senderLabel:\n")
            sb.append(msg.text)
            sb.append("\n\n-------------------------\n\n")
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, sb.toString())
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Ekspor Percakapan RUZIE AI")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    override fun onCleared() {
        super.onCleared()
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (_: Exception) {}
    }
}
