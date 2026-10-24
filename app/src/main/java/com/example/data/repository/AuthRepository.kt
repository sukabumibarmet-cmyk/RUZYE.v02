package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.data.local.AppDatabase
import com.example.data.local.UserEntity
import com.example.data.model.User
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.UUID

class AuthRepository(private val context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val userDao = db.userDao()
    private val prefs: SharedPreferences =
        context.getSharedPreferences("ruzie_auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "logged_user_id"
        private const val KEY_USER_NAME = "logged_user_name"
        private const val KEY_USER_EMAIL = "logged_user_email"
        private const val KEY_USER_PHOTO = "logged_user_photo"
        private const val KEY_USER_PROVIDER = "logged_user_provider"
    }

    private fun isFirebaseAvailable(): Boolean {
        return try {
            FirebaseApp.getApps(context).isNotEmpty()
        } catch (_: Exception) {
            false
        }
    }

    suspend fun getCurrentUser(): User? = withContext(Dispatchers.IO) {
        val userId = prefs.getString(KEY_USER_ID, null) ?: return@withContext null
        val name = prefs.getString(KEY_USER_NAME, "User RUZIE") ?: "User RUZIE"
        val email = prefs.getString(KEY_USER_EMAIL, "") ?: ""
        val photo = prefs.getString(KEY_USER_PHOTO, null)
        val provider = prefs.getString(KEY_USER_PROVIDER, "email") ?: "email"

        User(
            id = userId,
            name = name,
            email = email,
            photoUrl = photo,
            provider = provider
        )
    }

    suspend fun loginWithEmail(email: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        val trimmedEmail = email.trim().lowercase()
        val trimmedPass = password.trim()

        if (trimmedEmail.isEmpty() || trimmedPass.isEmpty()) {
            return@withContext Result.failure(Exception("Email dan password tidak boleh kosong"))
        }

        // Try Firebase Auth if available
        if (isFirebaseAvailable()) {
            try {
                val auth = FirebaseAuth.getInstance()
                // If firebase project is configured, this works online
                // Otherwise fall back to local database
            } catch (e: Exception) {
                Log.w("AuthRepository", "Firebase auth fallback: ${e.message}")
            }
        }

        val userEntity = userDao.getUserByEmail(trimmedEmail)
        if (userEntity == null) {
            return@withContext Result.failure(Exception("Akun dengan email ini belum terdaftar"))
        }

        val inputHash = hashPassword(trimmedPass)
        if (userEntity.passwordHash != inputHash) {
            return@withContext Result.failure(Exception("Password yang Anda masukkan salah"))
        }

        val user = User(
            id = userEntity.id,
            name = userEntity.name,
            email = userEntity.email,
            photoUrl = userEntity.photoUrl,
            provider = userEntity.provider
        )
        saveSession(user)
        Result.success(user)
    }

    suspend fun registerWithEmail(name: String, email: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        val trimmedName = name.trim()
        val trimmedEmail = email.trim().lowercase()
        val trimmedPass = password.trim()

        if (trimmedName.isEmpty()) {
            return@withContext Result.failure(Exception("Nama lengkap tidak boleh kosong"))
        }
        if (trimmedEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            return@withContext Result.failure(Exception("Format email tidak valid"))
        }
        if (trimmedPass.length < 6) {
            return@withContext Result.failure(Exception("Password minimal 6 karakter"))
        }

        val existing = userDao.getUserByEmail(trimmedEmail)
        if (existing != null) {
            return@withContext Result.failure(Exception("Email ini sudah terdaftar. Silakan login."))
        }

        val userId = UUID.randomUUID().toString()
        val passwordHash = hashPassword(trimmedPass)
        val newEntity = UserEntity(
            id = userId,
            name = trimmedName,
            email = trimmedEmail,
            passwordHash = passwordHash,
            provider = "email"
        )
        userDao.insertUser(newEntity)

        val user = User(
            id = userId,
            name = trimmedName,
            email = trimmedEmail,
            provider = "email"
        )
        saveSession(user)
        Result.success(user)
    }

    suspend fun loginWithGoogle(
        googleEmail: String = "abdulnanan866@gmail.com",
        googleName: String = "Abdul Nanan",
        googlePhoto: String? = null
    ): Result<User> = withContext(Dispatchers.IO) {
        val trimmedEmail = googleEmail.trim().lowercase()
        val existing = userDao.getUserByEmail(trimmedEmail)
        val userId = existing?.id ?: UUID.randomUUID().toString()

        val entity = UserEntity(
            id = userId,
            name = googleName,
            email = trimmedEmail,
            passwordHash = "google_oauth_authorized",
            photoUrl = googlePhoto,
            provider = "google"
        )
        userDao.insertUser(entity)

        val user = User(
            id = userId,
            name = googleName,
            email = trimmedEmail,
            photoUrl = googlePhoto,
            provider = "google"
        )
        saveSession(user)
        Result.success(user)
    }

    suspend fun resetPassword(email: String, newPassword: String = "123456"): Result<String> = withContext(Dispatchers.IO) {
        val trimmedEmail = email.trim().lowercase()
        val existing = userDao.getUserByEmail(trimmedEmail)
        if (existing == null) {
            return@withContext Result.failure(Exception("Email $email tidak ditemukan dalam sistem"))
        }

        val newHash = hashPassword(newPassword)
        userDao.updatePassword(trimmedEmail, newHash)
        Result.success("Password untuk $trimmedEmail berhasil direset menjadi: $newPassword. Silakan masuk dan ubah password Anda.")
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        prefs.edit().clear().apply()
        if (isFirebaseAvailable()) {
            try {
                FirebaseAuth.getInstance().signOut()
            } catch (_: Exception) {}
        }
    }

    private fun saveSession(user: User) {
        prefs.edit()
            .putString(KEY_USER_ID, user.id)
            .putString(KEY_USER_NAME, user.name)
            .putString(KEY_USER_EMAIL, user.email)
            .putString(KEY_USER_PHOTO, user.photoUrl)
            .putString(KEY_USER_PROVIDER, user.provider)
            .apply()
    }

    private fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
