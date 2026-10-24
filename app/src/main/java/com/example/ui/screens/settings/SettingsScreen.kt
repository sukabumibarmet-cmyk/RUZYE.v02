package com.example.ui.screens.settings

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
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
import com.example.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    user: User,
    settingsViewModel: SettingsViewModel,
    chatViewModel: ChatViewModel,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val currentThemeMode by settingsViewModel.themeMode.collectAsState()
    val themeMode = LocalThemeMode.current

    var showClearDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showRateDialog by remember { mutableStateOf(false) }
    var ratingStars by remember { mutableIntStateOf(5) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pengaturan",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = if (themeMode == AppThemeMode.CYBER) FontFamily.Monospace else FontFamily.Default
                        ),
                        color = if (themeMode == AppThemeMode.CYBER) CyberNeonGreen else MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("btn_settings_back")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = if (themeMode == AppThemeMode.CYBER) CyberNeonGreen else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (themeMode == AppThemeMode.CYBER) CyberSurface else MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.cyberBorder(themeMode, shape = RoundedCornerShape(0.dp))
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            CyberScanlineOverlay()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Profile Section Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .cyberBorder(themeMode, shape = RoundedCornerShape(16.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!user.photoUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = user.photoUrl,
                                contentDescription = "Foto Profil",
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .border(
                                        2.dp,
                                        if (themeMode == AppThemeMode.CYBER) CyberNeonGreen else MaterialTheme.colorScheme.primary,
                                        CircleShape
                                    )
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (themeMode == AppThemeMode.CYBER) Color(0xFF003300) else MaterialTheme.colorScheme.primaryContainer
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (themeMode == AppThemeMode.CYBER) CyberNeonGreen else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = user.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = if (themeMode == AppThemeMode.CYBER) FontFamily.Monospace else FontFamily.Default
                                ),
                                color = if (themeMode == AppThemeMode.CYBER) CyberNeonGreen else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = user.email.ifEmpty { "Akun Anonim / Tamu" },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (user.provider == "google") Color(0xFF4285F4).copy(alpha = 0.2f)
                                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (user.provider == "google") "Akun Google" else "Akun Email",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = if (themeMode == AppThemeMode.CYBER) CyberNeonGreen else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Theme Selection Section
                SectionHeader("TEMA APLIKASI (3 MODE)")

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .cyberBorder(themeMode, shape = RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        AppThemeMode.values().forEach { mode ->
                            ThemeOptionRow(
                                mode = mode,
                                isSelected = currentThemeMode == mode,
                                onSelect = { settingsViewModel.setThemeMode(mode) }
                            )
                            if (mode != AppThemeMode.CYBER) {
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Chat Settings Section
                SectionHeader("PENGATURAN CHAT")

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .cyberBorder(themeMode, shape = RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        SettingsActionRow(
                            icon = Icons.Default.Download,
                            title = "Ekspor Percakapan",
                            subtitle = "Bagikan atau simpan seluruh riwayat chat",
                            onClick = { chatViewModel.exportChat(context) }
                        )

                        SettingsActionRow(
                            icon = Icons.Default.DeleteOutline,
                            title = "Hapus Riwayat Chat",
                            subtitle = "Kosongkan semua pesan yang tersimpan",
                            iconTint = MaterialTheme.colorScheme.error,
                            onClick = { showClearDialog = true }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // App Settings Section
                SectionHeader("TENTANG & BANTUAN")

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .cyberBorder(themeMode, shape = RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        SettingsActionRow(
                            icon = Icons.Default.Info,
                            title = "Tentang RUZIE AI",
                            subtitle = "Versi 1.0 • Asisten AI Cerdas",
                            onClick = { showAboutDialog = true }
                        )

                        SettingsActionRow(
                            icon = Icons.Default.Star,
                            title = "Beri Nilai Aplikasi",
                            subtitle = "Dukung pengembangan RUZIE AI",
                            onClick = { showRateDialog = true }
                        )

                        SettingsActionRow(
                            icon = Icons.Default.Share,
                            title = "Bagikan Aplikasi",
                            subtitle = "Ajak teman merasakan kecerdasan RUZIE AI",
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "Coba RUZIE AI - Asisten AI Cerdas dengan dukungan multi-tema (Light, Dark, Cyber) dan chat bertenaga kecerdasan buatan!"
                                    )
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Bagikan RUZIE AI"))
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Logout Button
                Button(
                    onClick = { showLogoutDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_logout")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Keluar dari Akun (Logout)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Clear Dialog
        if (showClearDialog) {
            AlertDialog(
                onDismissRequest = { showClearDialog = false },
                title = { Text("Hapus Riwayat Chat?") },
                text = { Text("Semua pesan yang tersimpan akan dihapus secara permanen.") },
                confirmButton = {
                    TextButton(onClick = {
                        showClearDialog = false
                        chatViewModel.clearChat()
                        Toast.makeText(context, "Riwayat chat telah dihapus", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Hapus", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDialog = false }) { Text("Batal") }
                }
            )
        }

        // Logout Dialog
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Keluar dari RUZIE AI?") },
                text = { Text("Anda perlu masuk kembali untuk mengakses akun dan obrolan Anda.") },
                confirmButton = {
                    TextButton(onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }) {
                        Text("Keluar", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) { Text("Batal") }
                }
            )
        }

        // About Dialog
        if (showAboutDialog) {
            AlertDialog(
                onDismissRequest = { showAboutDialog = false },
                title = { Text("Tentang RUZIE AI") },
                text = {
                    Column {
                        Text("RUZIE AI v1.0", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("RUZIE adalah asisten kecerdasan buatan cerdas, ramah, dan serbaguna yang dikembangkan khusus untuk mempermudah pekerjaan sehari-hari, penulisan, pemrograman, dan pencarian informasi.")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Fitur:", fontWeight = FontWeight.SemiBold)
                        Text("• 3 Tema: Light, Dark, dan Cyber Mode")
                        Text("• Streaming AI Chat real-time")
                        Text("• Voice Input & Text-to-Speech")
                        Text("• Ekspor & Riwayat percakapan")
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAboutDialog = false }) { Text("Tutup") }
                }
            )
        }

        // Rate Dialog
        if (showRateDialog) {
            AlertDialog(
                onDismissRequest = { showRateDialog = false },
                title = { Text("Beri Nilai RUZIE AI") },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("Bagikan kepuasan Anda menggunakan RUZIE AI:")
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.Center) {
                            (1..5).forEach { starIndex ->
                                IconButton(onClick = { ratingStars = starIndex }) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "$starIndex Bintang",
                                        tint = if (starIndex <= ratingStars) Color(0xFFFFB300) else Color.LightGray,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        showRateDialog = false
                        Toast.makeText(context, "Terima kasih atas penilaian $ratingStars bintang Anda!", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Kirim Penilaian")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRateDialog = false }) { Text("Nanti") }
                }
            )
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    val themeMode = LocalThemeMode.current
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            fontFamily = if (themeMode == AppThemeMode.CYBER) FontFamily.Monospace else FontFamily.Default
        ),
        color = if (themeMode == AppThemeMode.CYBER) CyberNeonGreen else MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
fun ThemeOptionRow(
    mode: AppThemeMode,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val themeMode = LocalThemeMode.current
    val previewBg = when (mode) {
        AppThemeMode.LIGHT -> Color(0xFFFFFFFF)
        AppThemeMode.DARK -> Color(0xFF0F0F1A)
        AppThemeMode.CYBER -> Color(0xFF000000)
    }
    val previewPrimary = when (mode) {
        AppThemeMode.LIGHT -> RuziePurple
        AppThemeMode.DARK -> RuziePurple
        AppThemeMode.CYBER -> CyberNeonGreen
    }
    val previewText = when (mode) {
        AppThemeMode.LIGHT -> Color(0xFF1A1A2E)
        AppThemeMode.DARK -> Color(0xFFFFFFFF)
        AppThemeMode.CYBER -> CyberNeonGreen
    }

    Card(
        onClick = onSelect,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                if (themeMode == AppThemeMode.CYBER) Color(0xFF002200) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(
                1.5.dp,
                if (themeMode == AppThemeMode.CYBER) CyberNeonGreen else MaterialTheme.colorScheme.primary
            )
        } else null,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("theme_option_${mode.name.lowercase()}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Visual Theme Preview Swatch
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(previewBg)
                    .border(1.dp, previewPrimary.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(previewPrimary))
                    Spacer(modifier = Modifier.height(3.dp))
                    Box(modifier = Modifier.size(24.dp, 3.dp).background(previewText))
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mode.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = if (themeMode == AppThemeMode.CYBER) FontFamily.Monospace else FontFamily.Default
                    ),
                    color = if (themeMode == AppThemeMode.CYBER) CyberNeonGreen else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = mode.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(
                    selectedColor = if (themeMode == AppThemeMode.CYBER) CyberNeonGreen else MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
fun SettingsActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    val themeMode = LocalThemeMode.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (themeMode == AppThemeMode.CYBER) Color(0xFF002200) else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (themeMode == AppThemeMode.CYBER) CyberNeonGreen else iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = if (themeMode == AppThemeMode.CYBER) FontFamily.Monospace else FontFamily.Default
                ),
                color = if (themeMode == AppThemeMode.CYBER) CyberNeonGreen else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
