package com.example.eventcountdown.presentation.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = Firebase.auth

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser
                _authState.value = if (currentUser != null) {
                    if (currentUser.isEmailVerified) {
                        AuthState.Authenticated(currentUser.email ?: "")
                    } else {
                        AuthState.EmailNotVerified
                    }
                } else {
                    AuthState.Unauthenticated
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error checking auth state: ${e.message}")
                _authState.value = AuthState.Error("Error checking authentication state")
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                Log.d("AuthViewModel", "Attempting to sign in with email: $email")
                auth.signInWithEmailAndPassword(email, password).await()
                val user = auth.currentUser
                if (user != null && user.isEmailVerified) {
                    Log.d("AuthViewModel", "User signed in successfully")
                    _authState.value = AuthState.Authenticated(user.email ?: "")
                } else {
                    Log.w("AuthViewModel", "User email not verified")
                    _authState.value = AuthState.EmailNotVerified
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Sign in error: ${e.message}")
                _authState.value = AuthState.Error(e.message ?: "Authentication failed")
            }
        }
    }

    fun signUp(email: String, password: String, username: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                Log.d("AuthViewModel", "Attempting to create user with email: $email")
                
                // Check if Firebase is initialized
                if (FirebaseApp.getApps(getApplication()).isEmpty()) {
                    throw Exception("Firebase not initialized")
                }

                auth.createUserWithEmailAndPassword(email, password).await()
                val user = auth.currentUser
                if (user != null) {
                    Log.d("AuthViewModel", "User created successfully, sending verification email")
                    user.sendEmailVerification().await()
                    _authState.value = AuthState.EmailVerificationSent
                } else {
                    throw Exception("User creation failed")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Sign up error: ${e.message}")
                _authState.value = AuthState.Error(e.message ?: "Sign up failed")
            }
        }
    }

    fun signOut() {
        try {
            auth.signOut()
            _authState.value = AuthState.Unauthenticated
            Log.d("AuthViewModel", "User signed out successfully")
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Sign out error: ${e.message}")
            _authState.value = AuthState.Error("Failed to sign out")
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                Log.d("AuthViewModel", "Attempting to send password reset email to: $email")
                auth.sendPasswordResetEmail(email).await()
                _authState.value = AuthState.PasswordResetEmailSent
                Log.d("AuthViewModel", "Password reset email sent successfully")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Password reset error: ${e.message}")
                _authState.value = AuthState.Error(e.message ?: "Password reset failed")
            }
        }
    }
}

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    object EmailNotVerified : AuthState()
    object EmailVerificationSent : AuthState()
    object PasswordResetEmailSent : AuthState()
    data class Authenticated(val email: String) : AuthState()
    data class Error(val message: String) : AuthState()
} 