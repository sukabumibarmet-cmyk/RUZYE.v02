package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.User
import com.example.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthState {
    data object Idle : AuthState
    data object Loading : AuthState
    data class Success(val message: String? = null) : AuthState
    data class Error(val message: String) : AuthState
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(application)

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _isSessionChecked = MutableStateFlow(false)
    val isSessionChecked: StateFlow<Boolean> = _isSessionChecked.asStateFlow()

    init {
        checkSession()
    }

    fun checkSession() {
        viewModelScope.launch {
            _currentUser.value = authRepository.getCurrentUser()
            _isSessionChecked.value = true
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.loginWithEmail(email, pass)
            result.fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    _authState.value = AuthState.Success("Selamat datang kembali, ${user.name}!")
                },
                onFailure = { err ->
                    _authState.value = AuthState.Error(err.message ?: "Gagal masuk")
                }
            )
        }
    }

    fun register(name: String, email: String, pass: String, passConfirm: String) {
        if (pass != passConfirm) {
            _authState.value = AuthState.Error("Konfirmasi kata sandi tidak cocok")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.registerWithEmail(name, email, pass)
            result.fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    _authState.value = AuthState.Success("Akun berhasil dibuat. Selamat datang di RUZIE AI!")
                },
                onFailure = { err ->
                    _authState.value = AuthState.Error(err.message ?: "Gagal mendaftar")
                }
            )
        }
    }

    fun loginGoogle(email: String = "abdulnanan866@gmail.com", name: String = "Abdul Nanan") {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.loginWithGoogle(
                googleEmail = email,
                googleName = name,
                googlePhoto = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150"
            )
            result.fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    _authState.value = AuthState.Success("Berhasil masuk dengan Google!")
                },
                onFailure = { err ->
                    _authState.value = AuthState.Error(err.message ?: "Gagal masuk dengan Google")
                }
            )
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.resetPassword(email)
            result.fold(
                onSuccess = { msg ->
                    _authState.value = AuthState.Success(msg)
                },
                onFailure = { err ->
                    _authState.value = AuthState.Error(err.message ?: "Gagal mereset password")
                }
            )
        }
    }

    fun clearState() {
        _authState.value = AuthState.Idle
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _currentUser.value = null
            _authState.value = AuthState.Idle
        }
    }
}
