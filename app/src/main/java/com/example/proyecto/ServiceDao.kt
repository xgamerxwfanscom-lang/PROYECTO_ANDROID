package com.example.proyecto

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {
    @Query("SELECT * FROM users WHERE schoolId = :id LIMIT 1")
    suspend fun getUserById(id: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM service_hours WHERE userId = :userId ORDER BY date DESC")
    fun getHoursByUserId(userId: String): Flow<List<ServiceHour>>

    @Insert
    suspend fun insertHour(hour: ServiceHour)

    @Query("SELECT SUM(hours) FROM service_hours WHERE userId = :userId")
    fun getTotalHours(userId: String): Flow<Double?>
}
