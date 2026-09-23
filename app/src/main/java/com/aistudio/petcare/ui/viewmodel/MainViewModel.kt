package com.aistudio.petcare.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.petcare.data.local.Pet
import com.aistudio.petcare.data.repository.PetRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

import com.aistudio.petcare.data.repository.FirestoreRepository
import com.aistudio.petcare.data.repository.ForumPost

import com.aistudio.petcare.data.repository.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(
    private val repository: PetRepository,
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile = _userProfile.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _userProfile.value = firestoreRepository.getUserProfile()
        }
    }

    fun saveUserProfile(name: String, email: String, phone: String, address: String, country: String, city: String) {
        viewModelScope.launch {
            val profile = UserProfile(name = name, email = email, phone = phone, address = address, country = country, city = city)
            firestoreRepository.saveUserProfile(profile)
            _userProfile.value = profile
        }
    }
    val pets: StateFlow<List<Pet>> = repository.allPets
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val forumPosts: StateFlow<List<ForumPost>> = firestoreRepository.getForumPosts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createForumPost(title: String, content: String, petType: String? = null) {
        viewModelScope.launch {
            firestoreRepository.createPost(title, content, petType)
        }
    }

    fun addPet(name: String, species: String, breed: String, birthDate: Long, weight: Float, photoUri: String? = null) {
        viewModelScope.launch {
            repository.insertPet(
                Pet(
                    name = name,
                    species = species,
                    breed = breed,
                    birthDate = birthDate,
                    weight = weight,
                    photoUrl = photoUri
                )
            )
        }
    }

    fun simulateNotification(context: Context, title: String, message: String) {
        val channelId = "petcare_reminders"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "PetCare Reminders", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1, notification)
    }
}
