package com.example.proyecto

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "service_hours")
data class ServiceHour(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val date: Long = System.currentTimeMillis(),
    val hours: Double,
    val description: String
)
