package com.aistudio.petcare.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Pet::class, MedicalRecord::class, Vaccine::class, FeedingReminder::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun petDao(): PetDao
}
