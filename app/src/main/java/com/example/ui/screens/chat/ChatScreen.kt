package com.example.ui.screens.chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.ChatMessage
import com.example.data.model.MessageSender
import com.example.data.model.User
import com.example.ui.components.CyberScanlineOverlay
import com.example.ui.components.cyberBorder
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberNeonGreen
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.LocalThemeMode
import com.example.ui.theme.RuziePurple
import com.example.ui.viewmodel.ChatViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    user: User,
    chatViewModel: ChatViewModel,
    onNavigateSettings: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val themeMode = LocalThemeMode.current
    val scope = rememberCoroutineScope()

    val messages by chatViewModel.messages.collectAsState()
    val isGenerating by chatViewModel.isGenerating.collectAsState()
    val isSearchActive by chatViewModel.isSearchActive.collectAsState()
    val searchQuery by chatViewModel.searchQuery.collectAsState()
    val speakingId by chatViewModel.speakingMessageId.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var showClearDialog by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    // Speech to text launcher
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val spokenMatches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!spokenMatches.isNullOrEmpty()) {
                val spokenText = spokenMatches[0]
                inputText = if (inputText.isBlank()) spokenText else "$inputText $spokenText"
            }
        }
    }

    LaunchedEffect(user.id) {
        chatViewModel.initForUser(user.id)
    }

    // Auto-scroll to bottom on new message
    LaunchedEffect(messages.size, isGenerating) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearchActive) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { chatViewModel.setSearchQuery(it) },
                            placeholder = { Text("Cari percakapan...") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("search_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (themeMode == AppThemeMode.CYBER) CyberNeonGreen else MaterialTheme.colorScheme.primary
                            )
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .border(
                                        1.5.dp,
                                        if (themeMode == AppThemeMode.CYBER) CyberNeonGreen else MaterialTheme.colorScheme.primary,
                                        CircleShape
                                    )
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_ruzie_logo),
                                    contentDescription = "Logo",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "RUZIE AI",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = if (themeMode == AppThemeMode.CYBER) FontFamily.Monospace else FontFamily.Default
                                    ),
                                    color = if (themeMode == AppThemeMode.CYBER) CyberNeonGreen else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isGenerating) "Sedang mengetik..." else "Online • Siap Membantu",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = if (isGenerating) {
                                        if (themeMode == AppThemeMode.CYBER) CyberNeonGreen else MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }
                    }
                },
                actions = {
                    if (isSearchActive) {
                        IconButton(onClick = { chatViewModel.toggleSearch(false) }) {
                            Icon(Icons.Default.Close, contentDescription = "Tutup Pencarian")
                        }
                    } else {
                        IconButton(
                            onClick = { chatViewModel.toggleSearch(true) },
                            modifier = Modifier.testTag("btn_search_toggle")
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Cari Pesan")
                        }
                        IconButton(
                            onClick = { showClearDialog = true },
                            modifier = Modifier.testTag("btn_clear_chat")
                        ) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Hapus Riwayat Chat")
                        }
                        IconButton(
                            onClick = onNavigateSettings,
                            modifier = Modifier.testTag("btn_open_settings")
                        ) {
                            if (!user.photoUrl.isNullOrEmpty()) {
                                AsyncImage(
                                    model = user.photoUrl,
                                    contentDescription = "Profil",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .border(
                                            1.dp,
                                            if (themeMode == AppThemeMode.CYBER) CyberNeonGreen else MaterialTheme.colorScheme.primary,
                                            CircleShape
                                        )
                                )
                            } else {
                                Icon(Icons.Default.Settings, contentDescription = "Pengaturan")
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (themeMode == AppThemeMode.CYBER) CyberSurface else MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.cyberBorder(themeMode, shape = RoundedCornerShape(0.dp))
            )
        },
        bottomBar = {
            // Chat Input Bar
            Surface(
                color = if (themeMode == AppThemeMode.CYBER) CyberBackground else MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .cyberBorder(themeMode, shape = RoundedCornerShape(0.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Voice input mic button
                    IconButton(
                        onClick = {
                            try {
                                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id-ID")
                                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Bicara sekarang dengan RUZIE AI...")
                                }
                                speechLauncher.launch(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Fitur input suara tidak didukung di perangkat ini", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("btn_mic_input")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Input Suara",
                            tint = if (themeMode == AppThemeMode.CYBER) CyberNeonGreen else MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Tanyakan apa saja ke RUZIE...") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_chat_text"),
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (themeMode == AppThemeMode.CYBER) CyberNeonGreen else MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // Send Button
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (inputText.isNotBlank()) {
                                    if (themeMode == AppThemeMode.CYBER) CyberNeonGreen else MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                }
                            )
                            .clickable(enabled = inputText.isNotBlank() && !isGenerating) {
                                val text = inputText
                                inputText = ""
                                chatViewModel.sendMessage(text)
                            }
                            .testTag("btn_send_chat"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Kirim Pesan",
                            tint = if (inputText.isNotBlank()) {
                                if (themeMode == AppThemeMode.CYBER) Color.Black else Color.White
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            CyberScanlineOverlay()

            if (messages.isEmpty() && !isGenerating) {
                // Empty state: Welcome banner & prompt chips
                EmptyWelcomeState(
                    userName = user.name,
                    onSelectPrompt = { prompt ->
                        chatViewModel.sendMessage(prompt)
                    }
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    items(messages, key = { it.id }) { msg ->
                        ChatMessageBubble(
                            message = msg,
                            isSpeaking = speakingId == msg.id,
                            onCopy = {
                                clipboardManager.setText(AnnotatedString(msg.text))
                                Toast.makeText(context, "Teks berhasil disalin!", Toast.LENGTH_SHORT).show()
                            },
                            onShare = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, msg.text)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Bagikan Jawaban RUZIE")
                                context.startActivity(shareIntent)
                            },
                            onSpeak = {
                                chatViewModel.speakMessage(msg.id, msg.text)
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    if (isGenerating) {
                        item {
                            TypingIndicator()
                        }
                    }
                }
            }
        }

        // Clear Chat Confirmation Dialog
        if (showClearDialog) {
            AlertDialog(
                onDismissRequest = { showClearDialog = false },
                title = { Text("Hapus Semua Percakapan?") },
                text = { Text("Tindakan ini akan menghapus seluruh riwayat obrolan Anda dengan RUZIE AI secara permanen.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showClearDialog = false
                            chatViewModel.clearChat()
                        }
                    ) {
                        Text("Hapus", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}

@Composable
fun EmptyWelcomeState(
    userName: String,
    onSelectPrompt: (String) -> Unit
) {
    val themeMode = LocalThemeMode.current
    val samplePrompts = listOf(
        "💡 Ide bisnis digital modal minim 2026",
        "📝 Bantu tulis draf email permohonan izin",
        "💻 Buatkan kode Kotlin Room Database Android",
        "✨ Jelaskan cara kerja AI dengan bahasa sederhana",
        "🇮🇩 Rekomendasi kuliner khas Nusantara yang wajib dicoba"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .border(
                    2.dp,
                    if (themeMode == AppThemeMode.CYBER) CyberNeonGreen else RuziePurple,
                    CircleShape
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_ruzie_logo),
                contentDescription = "RUZIE Logo",
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Halo, $userName! 👋",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = if (themeMode == AppThemeMode.CYBER) FontFamily.Monospace else FontFamily.Default
            ),
            color = if (themeMode == AppThemeMode.CYBER) CyberNeonGreen else MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Saya RUZIE, asisten AI cerdas Anda. Siap membantu menjawab pertanyaan, membuat konten, ide, dan analisis.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp),
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Coba topik rekomendasi ini:",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        samplePrompts.forEach { prompt ->
            Card(
                onClick = { onSelectPrompt(prompt) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .cyberBorder(themeMode, shape = RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = prompt,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontFamily = if (themeMode == AppThemeMode.CYBER) FontFamily.Monospace else FontFamily.Default
                        ),
                        color = if (themeMode == AppThemeMode.CYBER) CyberNeonGreen else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    isSpeaking: Boolean,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onSpeak: () -> Unit
) {
    val themeMode = LocalThemeMode.current
    val isUser = message.sender == MessageSender.USER
    val sdf = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val timeFormatted = sdf.format(Date(message.timestamp))

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!isUser) {
                // AI Avatar
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .border(
                            1.dp,
                            if (themeMode == AppThemeMode.CYBER) CyberNeonGreen else MaterialTheme.colorScheme.primary,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_ruzie_logo),
                        contentDescription = "RUZIE Avatar",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            // Message Bubble
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        isUser && themeMode == AppThemeMode.CYBER -> Color(0xFF003300)
                        isUser -> MaterialTheme.colorScheme.primary
                        themeMode == AppThemeMode.CYBER -> Color(0xFF001500)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 16.dp
                ),
                modifier = Modifier
                    .widthIn(max = 310.dp)
                    .cyberBorder(themeMode, shape = RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 22.sp,
                            fontFamily = if (themeMode == AppThemeMode.CYBER) FontFamily.Monospace else FontFamily.Default
                        ),
                        color = when {
                            isUser && themeMode == AppThemeMode.CYBER -> CyberNeonGreen
                            isUser -> Color.White
                            themeMode == AppThemeMode.CYBER -> CyberNeonGreen
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = timeFormatted,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = if (isUser) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (!isUser) {
                            Row {
                                // Speak button
                                IconButton(
                                    onClick = onSpeak,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isSpeaking) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Bicara",
                                        tint = if (themeMode == AppThemeMode.CYBER) CyberNeonGreen else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                // Copy button
                                IconButton(
                                    onClick = onCopy,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Salin",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                // Share button
                                IconButton(
                                    onClick = onShare,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Bagikan",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    val themeMode = LocalThemeMode.current
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    val dotAlpha1 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "dot1"
    )
    val dotAlpha2 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600, delayMillis = 200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "dot2"
    )
    val dotAlpha3 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600, delayMillis = 400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "dot3"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .border(
                    1.dp,
                    if (themeMode == AppThemeMode.CYBER) CyberNeonGreen else MaterialTheme.colorScheme.primary,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_ruzie_logo),
                contentDescription = "RUZIE",
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.width(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.cyberBorder(themeMode, shape = RoundedCornerShape(14.dp))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RUZIE sedang berpikir",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                val dotColor = if (themeMode == AppThemeMode.CYBER) CyberNeonGreen else MaterialTheme.colorScheme.primary
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(dotColor.copy(alpha = dotAlpha1)))
                Spacer(modifier = Modifier.width(4.dp))
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(dotColor.copy(alpha = dotAlpha2)))
                Spacer(modifier = Modifier.width(4.dp))
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(dotColor.copy(alpha = dotAlpha3)))
            }
        }
    }
}
