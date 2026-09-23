package com.aistudio.petcare.ui.components

import android.app.Activity
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.aistudio.petcare.R
import com.aistudio.petcare.ui.screens.AuthScreen
import com.aistudio.petcare.ui.viewmodel.MainViewModel
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun AuthGate(
    viewModel: MainViewModel,
    content: @Composable (com.google.firebase.auth.FirebaseUser) -> Unit
) {
    var user by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            user = auth.currentUser
            if (user != null) {
                viewModel.loadUserProfile()
            }
        }
    }

    if (user == null) {
        AuthScreen(
            isLoading = isLoading,
            errorMessage = errorMessage,
            onGoogleSignInClick = {
                isLoading = true
                errorMessage = null
                scope.launch {
                    try {
                        val credentialManager = CredentialManager.create(context)
                        val googleIdOption = GetSignInWithGoogleOption.Builder(
                            serverClientId = context.getString(R.string.default_web_client_id)
                        ).build()

                        val request = GetCredentialRequest.Builder()
                            .addCredentialOption(googleIdOption)
                            .build()

                        val result = credentialManager.getCredential(context as Activity, request)
                        val credential = result.credential

                        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                            val authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                            FirebaseAuth.getInstance().signInWithCredential(authCredential).await()
                        }
                    } catch (e: Exception) {
                        errorMessage = "Google Sign In failed: ${e.localizedMessage}"
                    } finally {
                        isLoading = false
                    }
                }
            },
            onManualRegisterClick = { name, email, phone, address, country, city, password ->
                isLoading = true
                errorMessage = null
                scope.launch {
                    try {
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()
                        viewModel.saveUserProfile(name, email, phone, address, country, city)
                    } catch (e: Exception) {
                        errorMessage = "Registration failed: ${e.localizedMessage}"
                    } finally {
                        isLoading = false
                    }
                }
            },
            onManualLoginClick = { email, password ->
                isLoading = true
                errorMessage = null
                scope.launch {
                    try {
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
                    } catch (e: Exception) {
                        errorMessage = "Login failed: ${e.localizedMessage}"
                    } finally {
                        isLoading = false
                    }
                }
            }
        )
    } else {
        content(user!!)
    }
}
