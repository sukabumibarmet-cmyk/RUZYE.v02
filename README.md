# RUZIE AI - Asisten AI Cerdas Android 🤖✨

**RUZIE AI** adalah aplikasi Android berbasis Kotlin dan Jetpack Compose dengan sistem kecerdasan buatan cerdas, ramah, dan serbaguna. Dilengkapi dengan 3 tema visual yang memukau (Light Mode, Dark Mode, dan Cyber Mode dengan animasi scanline CRT & glowing neon), autentikasi lengkap (Email/Password & Google Sign-In), riwayat chat persisten, Text-to-Speech (TTS), dan input suara (Speech-to-Text).

---

## 🚀 Fitur Utama

### 1. Sistem Autentikasi
- **Login & Sign Up (Email & Password)**: Pendaftaran user baru dengan enkripsi password SHA-256.
- **Login Akun Google**: Dukungan login cepat dengan Google Sign-In.
- **Lupa Kata Sandi (Forgot Password)**: Reset kata sandi dengan modal bottom sheet.
- **Session Management**: Otomatis mengingat status login pengguna antar sesi.
- **Akses Cepat Tamu (Demo Mode)**: Masuk langsung untuk pengujian instan tanpa perlu mendaftar manual.

### 2. Tiga Mode Tema Unik (Light, Dark, Cyber)
- **Tema 1 - Light Mode**: Tampilan bersih bernuansa putih (`#FFFFFF`) dan aksen ungu modern (`#6C5CE7`).
- **Tema 2 - Dark Mode**: Nuansa gelap elegan (`#0F0F1A`) nyaman untuk mata di malam hari.
- **Tema 3 - Cyber Mode**: Efek neon Matrix futuristik (`#000000`, teks neon `#00FF00`, secondary `#001100`, glowing borders, dan animasi scanline CRT dinamis).

### 3. AI Chat Berkinerja Tinggi
- **Persona RUZIE**: Diprogram dalam Bahasa Indonesia yang santun, ramah, dan solutif.
- **Integrasi Ganda**: Mendukung Google Gemini API (1.5 Flash / 2.0 Flash) dan fallback instan Pollinations AI tanpa perlu memasukkan API key terlebih dahulu.
- **Riwayat Chat Persisten**: Semua pesan tersimpan di database lokal (Room) dan tersinkronisasi ke Firestore.
- **Fitur Chat Lengkap**:
  - Salin teks jawaban (Copy with Feedback)
  - Bagikan jawaban (Share ke WhatsApp, Email, Catatan)
  - Ekspor seluruh percakapan ke file teks
  - Input suara (Speech-to-Text microphone)
  - Pembacaan suara (Text-to-Speech bahasa Indonesia)
  - Pencarian pesan dalam riwayat chat
  - Indikator mengetik dinamis (Typing indicator)

---

## 🛠️ Tech Stack
- **Bahasa**: Kotlin 100%
- **UI Framework**: Jetpack Compose (Material Design 3)
- **Arsitektur**: MVVM (Model-View-ViewModel) + Repository Pattern
- **Database Lokal**: Android Room Database (SQLite)
- **Cloud Backend**: Firebase Authentication & Cloud Firestore
- **Networking**: OkHttp 4, Retrofit 2, Moshi
- **Image Loading**: Coil Compose
- **Desain & Animasi**: Custom Canvas Scanlines, Infinite Glow Transitions

---

## 🔑 Panduan Setup API Key & Firebase

### A. Mendapatkan Gemini API Key (Google AI Studio)
1. Kunjungi portal Google AI Studio: [https://aistudio.google.com/app/apikey](https://aistudio.google.com/app/apikey)
2. Klik tombol **"Create API Key"**.
3. Buka panel **Secrets** di AI Studio dan masukkan key Anda dengan nama: `GEMINI_API_KEY`.
*(Catatan: Jika key belum diatur, aplikasi secara otomatis menggunakan provider AI cadangan Pollinations sehingga aplikasi tetap berfungsi normal tanpa error)*

### B. Konfigurasi Firebase (Opsional untuk Sync Online)
1. Buka [Firebase Console](https://console.firebase.google.com/) dan buat project baru.
2. Tambahkan aplikasi Android dengan Package Name: `com.aistudio.ruzieai.app`.
3. Aktifkan **Authentication**:
   - Tab *Sign-in method* -> Aktifkan **Email/Password** dan **Google**.
4. Aktifkan **Cloud Firestore**:
   - Buat database dalam mode *Production* atau *Test Mode*.
5. Unduh file `google-services.json` dan letakkan di dalam folder `/app`.

---

## 📱 Cara Menjalankan Aplikasi
1. Buka project di Android Studio (Ladybug / Meerkat atau versi terbaru).
2. Tunggu Gradle sync selesai secara otomatis.
3. Jalankan di Emulator atau perangkat fisik Android (Min SDK 24 / Android 7.0+, Target SDK 36).
4. Nikmati pengalaman mengobrol dengan asisten pintar **RUZIE AI**!
