package com.aistudio.petcare.data.repository

import com.aistudio.petcare.data.local.*
import kotlinx.coroutines.flow.Flow

class PetRepository(private val petDao: PetDao) {
    val allPets: Flow<List<Pet>> = petDao.getAllPets()

    suspend fun getPetById(id: Int): Pet? = petDao.getPetById(id)
    suspend fun insertPet(pet: Pet) = petDao.insertPet(pet)
    suspend fun deletePet(pet: Pet) = petDao.deletePet(pet)

    fun getMedicalRecords(petId: Int) = petDao.getMedicalRecordsForPet(petId)
    suspend fun insertMedicalRecord(record: MedicalRecord) = petDao.insertMedicalRecord(record)

    fun getVaccines(petId: Int) = petDao.getVaccinesForPet(petId)
    suspend fun insertVaccine(vaccine: Vaccine) = petDao.insertVaccine(vaccine)

    fun getFeedingReminders(petId: Int) = petDao.getFeedingRemindersForPet(petId)
    suspend fun insertFeedingReminder(reminder: FeedingReminder) = petDao.insertFeedingReminder(reminder)
}
