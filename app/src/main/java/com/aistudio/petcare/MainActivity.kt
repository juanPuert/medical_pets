package com.aistudio.petcare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.aistudio.petcare.data.local.AppDatabase
import com.aistudio.petcare.data.repository.PetRepository
import com.aistudio.petcare.ui.screens.PetCareApp
import com.aistudio.petcare.ui.screens.AuthScreen
import com.aistudio.petcare.ui.theme.PetCareTheme
import com.aistudio.petcare.ui.viewmodel.MainViewModel

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import androidx.credentials.CustomCredential
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

import com.aistudio.petcare.data.repository.FirestoreRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "petcare-db"
        ).build()
        val repository = PetRepository(db.petDao())
        val firestoreRepository = FirestoreRepository(applicationContext)

        setContent {
            PetCareTheme {
                AuthGate { user ->
                    val mainViewModel: MainViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return MainViewModel(repository, firestoreRepository) as T
                            }
                        }
                    )
                    PetCareApp(mainViewModel)
                }
            }
        }
    }
}

@Composable
fun AuthGate(content: @Composable (com.google.firebase.auth.FirebaseUser) -> Unit) {
    var user by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            user = auth.currentUser
        }
    }

    if (user == null) {
        AuthScreen(
            isLoading = isLoading,
            onSignInClick = {
                isLoading = true
                scope.launch {
                    try {
                        val credentialManager = CredentialManager.create(context)
                        val googleIdOption = GetSignInWithGoogleOption.Builder(
                            serverClientId = context.getString(R.string.default_web_client_id)
                        ).build()

                        val request = GetCredentialRequest.Builder()
                            .addCredentialOption(googleIdOption)
                            .build()

                        val result = credentialManager.getCredential(context as android.app.Activity, request)
                        val credential = result.credential

                        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                            val authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                            FirebaseAuth.getInstance().signInWithCredential(authCredential).await()
                        }
                    } catch (e: Exception) {
                        // Handle error
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
