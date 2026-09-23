package com.aistudio.petcare.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {
    @Query("SELECT * FROM pets")
    fun getAllPets(): Flow<List<Pet>>

    @Query("SELECT * FROM pets WHERE id = :id")
    suspend fun getPetById(id: Int): Pet?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPet(pet: Pet)

    @Delete
    suspend fun deletePet(pet: Pet)

    @Query("SELECT * FROM medical_records WHERE petId = :petId ORDER BY date DESC")
    fun getMedicalRecordsForPet(petId: Int): Flow<List<MedicalRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicalRecord(record: MedicalRecord)

    @Query("SELECT * FROM vaccines WHERE petId = :petId ORDER BY dateAdministered DESC")
    fun getVaccinesForPet(petId: Int): Flow<List<Vaccine>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVaccine(vaccine: Vaccine)

    @Query("SELECT * FROM feeding_reminders WHERE petId = :petId")
    fun getFeedingRemindersForPet(petId: Int): Flow<List<FeedingReminder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedingReminder(reminder: FeedingReminder)
}
