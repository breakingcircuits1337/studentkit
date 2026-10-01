package com.example.data.local

import androidx.room.*
import com.example.data.model.SafetyAlert
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for SafetyAlerts (Walk Me Home Module).
 * Provides full CRUD operations for localized safety sessions,
 * Safe PIN arrivals, countdown expirations, and emergency escalations.
 */
@Dao
interface SafetyAlertsDao {

    // ====================================================
    // CREATE
    // ====================================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alert: SafetyAlert): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(alerts: List<SafetyAlert>): List<Long>

    // ====================================================
    // READ
    // ====================================================

    @Query("SELECT * FROM safety_alerts ORDER BY timestamp DESC")
    fun getAll(): Flow<List<SafetyAlert>>

    @Query("SELECT * FROM safety_alerts WHERE id = :id LIMIT 1")
    fun getById(id: Long): Flow<SafetyAlert?>

    @Query("SELECT * FROM safety_alerts WHERE alertType = :alertType ORDER BY timestamp DESC")
    fun getByType(alertType: String): Flow<List<SafetyAlert>>

    @Query("SELECT * FROM safety_alerts ORDER BY timestamp DESC LIMIT :limit")
    fun getRecent(limit: Int = 10): Flow<List<SafetyAlert>>

    @Query("SELECT * FROM safety_alerts WHERE pinVerified = 0 AND alertType = 'EMERGENCY_ESCALATION' ORDER BY timestamp DESC")
    fun getEscalatedAlerts(): Flow<List<SafetyAlert>>

    // ====================================================
    // UPDATE
    // ====================================================

    @Update
    suspend fun update(alert: SafetyAlert)

    @Query("UPDATE safety_alerts SET pinVerified = :verified WHERE id = :id")
    suspend fun setPinVerified(id: Long, verified: Boolean)

    @Query("UPDATE safety_alerts SET notes = :notes WHERE id = :id")
    suspend fun updateNotes(id: Long, notes: String)

    // ====================================================
    // DELETE
    // ====================================================

    @Delete
    suspend fun delete(alert: SafetyAlert)

    @Query("DELETE FROM safety_alerts WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM safety_alerts")
    suspend fun clearAll()
}
