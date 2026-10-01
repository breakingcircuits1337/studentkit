package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CampusListing
import com.example.data.model.Purchase
import com.example.data.model.SafetyAlert
import com.example.data.model.SyllabusItem
import com.example.data.model.TrialItem
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentKitDao {

    // ====================================================
    // 1. SYLLABUS ITEMS (Crunch Module)
    // ====================================================
    @Query("SELECT * FROM syllabus_items ORDER BY isCompleted ASC, daysUntilDue ASC")
    fun getAllSyllabusItems(): Flow<List<SyllabusItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyllabusItems(items: List<SyllabusItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyllabusItem(item: SyllabusItem): Long

    @Update
    suspend fun updateSyllabusItem(item: SyllabusItem)

    @Query("UPDATE syllabus_items SET isCompleted = :completed WHERE id = :id")
    suspend fun setSyllabusItemCompleted(id: Long, completed: Boolean)

    // Aliases for compatibility
    fun getAllDeadlines(): Flow<List<SyllabusItem>> = getAllSyllabusItems()
    suspend fun insertDeadlines(items: List<SyllabusItem>) = insertSyllabusItems(items)
    suspend fun insertDeadline(item: SyllabusItem): Long = insertSyllabusItem(item)
    suspend fun updateDeadline(item: SyllabusItem) = updateSyllabusItem(item)
    suspend fun setDeadlineCompleted(id: Long, completed: Boolean) = setSyllabusItemCompleted(id, completed)

    // ====================================================
    // 2. PURCHASES (Cooling Off Module)
    // ====================================================
    @Query("SELECT * FROM purchases ORDER BY createdTimestamp DESC")
    fun getAllPurchases(): Flow<List<Purchase>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchase(item: Purchase): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchases(items: List<Purchase>)

    @Update
    suspend fun updatePurchase(item: Purchase)

    @Query("UPDATE purchases SET status = :status WHERE id = :id")
    suspend fun updatePurchaseStatus(id: Long, status: String)

    // Aliases for compatibility
    fun getAllHolds(): Flow<List<Purchase>> = getAllPurchases()
    suspend fun insertHold(item: Purchase): Long = insertPurchase(item)
    suspend fun insertHolds(items: List<Purchase>) = insertPurchases(items)
    suspend fun updateHold(item: Purchase) = updatePurchase(item)
    suspend fun updateHoldStatus(id: Long, status: String) = updatePurchaseStatus(id, status)

    // ====================================================
    // 3. SAFETY ALERTS (Walk Me Home Module)
    // ====================================================
    @Query("SELECT * FROM safety_alerts ORDER BY timestamp DESC")
    fun getAllSafetyAlerts(): Flow<List<SafetyAlert>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSafetyAlert(alert: SafetyAlert): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSafetyAlerts(alerts: List<SafetyAlert>)

    @Query("SELECT * FROM safety_alerts ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentSafetyAlerts(limit: Int = 10): Flow<List<SafetyAlert>>

    // ====================================================
    // 4. TRIAL SNIPER
    // ====================================================
    @Query("SELECT * FROM trial_items ORDER BY isCancelled ASC, daysRemaining ASC")
    fun getAllTrials(): Flow<List<TrialItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrials(items: List<TrialItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrial(item: TrialItem): Long

    @Query("UPDATE trial_items SET isCancelled = :cancelled WHERE id = :id")
    suspend fun setTrialCancelled(id: Long, cancelled: Boolean)

    // ====================================================
    // 5. CAMPUS TRADE
    // ====================================================
    @Query("SELECT * FROM campus_listings ORDER BY id DESC")
    fun getAllListings(): Flow<List<CampusListing>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListings(items: List<CampusListing>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListing(item: CampusListing): Long
}
