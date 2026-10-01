package com.example.data.local

import androidx.room.*
import com.example.data.model.SyllabusItem
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for SyllabusItems (Crunch Module).
 * Provides full CRUD operations for course syllabus deadlines,
 * assignment weights, and anti-cramming runway schedules.
 */
@Dao
interface SyllabusItemsDao {

    // ====================================================
    // CREATE
    // ====================================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SyllabusItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SyllabusItem>): List<Long>

    // ====================================================
    // READ
    // ====================================================

    @Query("SELECT * FROM syllabus_items ORDER BY isCompleted ASC, daysUntilDue ASC")
    fun getAll(): Flow<List<SyllabusItem>>

    @Query("SELECT * FROM syllabus_items WHERE id = :id LIMIT 1")
    fun getById(id: Long): Flow<SyllabusItem?>

    @Query("SELECT * FROM syllabus_items WHERE courseCode = :courseCode ORDER BY daysUntilDue ASC")
    fun getByCourse(courseCode: String): Flow<List<SyllabusItem>>

    @Query("SELECT * FROM syllabus_items WHERE isCompleted = 0 ORDER BY daysUntilDue ASC")
    fun getPendingItems(): Flow<List<SyllabusItem>>

    @Query("SELECT * FROM syllabus_items WHERE urgencyLevel = 'HIGH' AND isCompleted = 0 ORDER BY daysUntilDue ASC")
    fun getHighUrgencyItems(): Flow<List<SyllabusItem>>

    // ====================================================
    // UPDATE
    // ====================================================

    @Update
    suspend fun update(item: SyllabusItem)

    @Query("UPDATE syllabus_items SET isCompleted = :completed WHERE id = :id")
    suspend fun setCompleted(id: Long, completed: Boolean)

    @Query("UPDATE syllabus_items SET urgencyLevel = :urgency WHERE id = :id")
    suspend fun updateUrgency(id: Long, urgency: String)

    // ====================================================
    // DELETE
    // ====================================================

    @Delete
    suspend fun delete(item: SyllabusItem)

    @Query("DELETE FROM syllabus_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM syllabus_items WHERE isCompleted = 1")
    suspend fun deleteCompleted()

    @Query("DELETE FROM syllabus_items")
    suspend fun clearAll()
}
