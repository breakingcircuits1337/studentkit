package com.example.data.local

import androidx.room.*
import com.example.data.model.Purchase
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Purchases (Cooling Off Module).
 * Provides full CRUD operations for impulsive shopping shield holds,
 * student wage calculations, and 48-hour lockup enforcement.
 */
@Dao
interface PurchasesDao {

    // ====================================================
    // CREATE
    // ====================================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(purchase: Purchase): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(purchases: List<Purchase>): List<Long>

    // ====================================================
    // READ
    // ====================================================

    @Query("SELECT * FROM purchases ORDER BY createdTimestamp DESC")
    fun getAll(): Flow<List<Purchase>>

    @Query("SELECT * FROM purchases WHERE id = :id LIMIT 1")
    fun getById(id: Long): Flow<Purchase?>

    @Query("SELECT * FROM purchases WHERE status = :status ORDER BY createdTimestamp DESC")
    fun getByStatus(status: String): Flow<List<Purchase>>

    @Query("SELECT * FROM purchases WHERE status IN ('LOCKED', 'UNLOCKED') ORDER BY createdTimestamp DESC")
    fun getActiveHolds(): Flow<List<Purchase>>

    @Query("SELECT * FROM purchases WHERE status = 'AVOIDED' ORDER BY createdTimestamp DESC")
    fun getAvoidedPurchases(): Flow<List<Purchase>>

    // ====================================================
    // UPDATE
    // ====================================================

    @Update
    suspend fun update(purchase: Purchase)

    @Query("UPDATE purchases SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("UPDATE purchases SET reflectionNotes = :notes WHERE id = :id")
    suspend fun updateReflectionNotes(id: Long, notes: String)

    @Query("UPDATE purchases SET hourlyWage = :wage WHERE id = :id")
    suspend fun updateWage(id: Long, wage: Double)

    // ====================================================
    // DELETE
    // ====================================================

    @Delete
    suspend fun delete(purchase: Purchase)

    @Query("DELETE FROM purchases WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM purchases")
    suspend fun clearAll()
}
