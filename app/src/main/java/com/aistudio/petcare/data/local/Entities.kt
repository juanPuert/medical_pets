package com.aistudio.petcare.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "pets")
@Serializable
data class Pet(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val species: String,
    val breed: String,
    val birthDate: Long,
    val weight: Float,
    val photoUrl: String? = null
)

@Entity(tableName = "medical_records")
@Serializable
data class MedicalRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val petId: Int,
    val date: Long,
    val diagnosis: String,
    val treatment: String,
    val vetName: String,
    val notes: String? = null
)

@Entity(tableName = "vaccines")
@Serializable
data class Vaccine(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val petId: Int,
    val name: String,
    val dateAdministered: Long,
    val nextDueDate: Long,
    val manufacturer: String? = null
)

@Entity(tableName = "feeding_reminders")
@Serializable
data class FeedingReminder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val petId: Int,
    val time: String, // e.g., "08:00"
    val foodType: String,
    val quantity: String,
    val isActive: Boolean = true
)
