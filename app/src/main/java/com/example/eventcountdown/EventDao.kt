package com.example.eventcountdown

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Insert
    suspend fun insert(event: Event)

    @Query("SELECT * FROM events ORDER BY date ASC")
    fun getAllEvents(): Flow<List<Event>>

    @Delete
    suspend fun delete(event: Event)

    @Update
    suspend fun update(event: Event)
}