package com.example.proyecto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val schoolId: String,
    val fullName: String,
    val schoolName: String,
    val semester: String,
    val totalMonths: Int,
    val requiredHours: Double
)
