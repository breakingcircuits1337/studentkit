package com.example.data.repository

import com.example.data.local.PurchasesDao
import com.example.data.local.SafetyAlertsDao
import com.example.data.local.StudentKitDao
import com.example.data.local.SyllabusItemsDao
import com.example.data.model.CampusListing
import com.example.data.model.Purchase
import com.example.data.model.SafetyAlert
import com.example.data.model.SyllabusItem
import com.example.data.model.TrialItem
import kotlinx.coroutines.flow.Flow

class StudentKitRepository(
    private val dao: StudentKitDao,
    val syllabusItemsDao: SyllabusItemsDao? = null,
    val purchasesDao: PurchasesDao? = null,
    val safetyAlertsDao: SafetyAlertsDao? = null
) {

    // Syllabus Items (Crunch)
    val allSyllabusItems: Flow<List<SyllabusItem>> = syllabusItemsDao?.getAll() ?: dao.getAllSyllabusItems()
    val allDeadlines: Flow<List<SyllabusItem>> = allSyllabusItems

    suspend fun addSyllabusItem(item: SyllabusItem): Long =
        syllabusItemsDao?.insert(item) ?: dao.insertSyllabusItem(item)

    suspend fun addSyllabusItems(items: List<SyllabusItem>): List<Long> =
        syllabusItemsDao?.insertAll(items) ?: run {
            dao.insertSyllabusItems(items)
            emptyList()
        }

    suspend fun updateSyllabusItem(item: SyllabusItem) =
        syllabusItemsDao?.update(item) ?: dao.updateSyllabusItem(item)

    suspend fun toggleDeadlineCompleted(id: Long, completed: Boolean) =
        syllabusItemsDao?.setCompleted(id, completed) ?: dao.setSyllabusItemCompleted(id, completed)

    suspend fun deleteSyllabusItem(id: Long) =
        syllabusItemsDao?.deleteById(id)

    suspend fun addDeadline(deadline: SyllabusItem): Long = addSyllabusItem(deadline)
    suspend fun addDeadlines(deadlines: List<SyllabusItem>) { addSyllabusItems(deadlines) }

    // Purchases (Cooling Off)
    val allPurchases: Flow<List<Purchase>> = purchasesDao?.getAll() ?: dao.getAllPurchases()
    val allHolds: Flow<List<Purchase>> = allPurchases

    suspend fun addPurchase(purchase: Purchase): Long =
        purchasesDao?.insert(purchase) ?: dao.insertPurchase(purchase)

    suspend fun addPurchases(purchases: List<Purchase>): List<Long> =
        purchasesDao?.insertAll(purchases) ?: run {
            dao.insertPurchases(purchases)
            emptyList()
        }

    suspend fun updatePurchase(purchase: Purchase) =
        purchasesDao?.update(purchase) ?: dao.updatePurchase(purchase)

    suspend fun updatePurchaseStatus(id: Long, status: String) =
        purchasesDao?.updateStatus(id, status) ?: dao.updatePurchaseStatus(id, status)

    suspend fun deletePurchase(id: Long) =
        purchasesDao?.deleteById(id)

    suspend fun addHold(hold: Purchase): Long = addPurchase(hold)
    suspend fun updateHoldStatus(id: Long, status: String) = updatePurchaseStatus(id, status)

    // Safety Alerts (Walk Me Home)
    val allSafetyAlerts: Flow<List<SafetyAlert>> = safetyAlertsDao?.getAll() ?: dao.getAllSafetyAlerts()
    val recentSafetyAlerts: Flow<List<SafetyAlert>> = safetyAlertsDao?.getRecent(10) ?: dao.getRecentSafetyAlerts(10)

    suspend fun logSafetyAlert(alert: SafetyAlert): Long =
        safetyAlertsDao?.insert(alert) ?: dao.insertSafetyAlert(alert)

    suspend fun logSafetyAlerts(alerts: List<SafetyAlert>): List<Long> =
        safetyAlertsDao?.insertAll(alerts) ?: run {
            dao.insertSafetyAlerts(alerts)
            emptyList()
        }

    suspend fun deleteSafetyAlert(id: Long) =
        safetyAlertsDao?.deleteById(id)

    // TrialSniper
    val allTrials: Flow<List<TrialItem>> = dao.getAllTrials()
    suspend fun addTrial(trial: TrialItem): Long = dao.insertTrial(trial)
    suspend fun setTrialCancelled(id: Long, cancelled: Boolean) = dao.setTrialCancelled(id, cancelled)

    // Campus Trade
    val allListings: Flow<List<CampusListing>> = dao.getAllListings()
    suspend fun addListing(listing: CampusListing): Long = dao.insertListing(listing)
}
