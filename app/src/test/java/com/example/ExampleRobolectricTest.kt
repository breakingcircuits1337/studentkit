package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.PurchasesDao
import com.example.data.local.SafetyAlertsDao
import com.example.data.local.StudentKitDatabase
import com.example.data.local.SyllabusItemsDao
import com.example.data.model.Purchase
import com.example.data.model.SafetyAlert
import com.example.data.model.SyllabusItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  private lateinit var database: StudentKitDatabase
  private lateinit var syllabusItemsDao: SyllabusItemsDao
  private lateinit var purchasesDao: PurchasesDao
  private lateinit var safetyAlertsDao: SafetyAlertsDao

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    database = Room.inMemoryDatabaseBuilder(context, StudentKitDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    syllabusItemsDao = database.syllabusItemsDao()
    purchasesDao = database.purchasesDao()
    safetyAlertsDao = database.safetyAlertsDao()
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("StudentKit", appName)
  }

  @Test
  fun `syllabusItemsDao CRUD operations`() = runBlocking {
    val item = SyllabusItem(
      courseCode = "CS 301",
      courseName = "Algorithms",
      title = "Midterm Exam",
      dueDateString = "Oct 18, 2026",
      daysUntilDue = 6,
      startByDaysBefore = 5,
      weightPercent = 25,
      estStudyHours = 12,
      urgencyLevel = "HIGH"
    )
    // 1. Create
    val insertedId = syllabusItemsDao.insert(item)
    assertTrue(insertedId > 0)

    // 2. Read
    val allItems = syllabusItemsDao.getAll().first()
    assertEquals(1, allItems.size)
    assertEquals("CS 301", allItems[0].courseCode)

    // 3. Update
    syllabusItemsDao.setCompleted(insertedId, true)
    val updatedItem = syllabusItemsDao.getById(insertedId).first()
    assertNotNull(updatedItem)
    assertTrue(updatedItem!!.isCompleted)

    // 4. Delete
    syllabusItemsDao.deleteById(insertedId)
    val emptyList = syllabusItemsDao.getAll().first()
    assertEquals(0, emptyList.size)
  }

  @Test
  fun `purchasesDao CRUD operations`() = runBlocking {
    val purchase = Purchase(
      productTitle = "Wireless Headphones",
      price = 150.0,
      sourceUrl = "https://example.com",
      hourlyWage = 15.0,
      holdDurationHours = 48
    )
    // 1. Create
    val id = purchasesDao.insert(purchase)
    assertTrue(id > 0)

    // 2. Read
    val list = purchasesDao.getAll().first()
    assertEquals(1, list.size)
    assertEquals("Wireless Headphones", list[0].productTitle)
    assertEquals(10.0, list[0].hoursWorkedEquivalent, 0.01)

    // 3. Update status
    purchasesDao.updateStatus(id, "AVOIDED")
    val updated = purchasesDao.getById(id).first()
    assertNotNull(updated)
    assertEquals("AVOIDED", updated!!.status)

    // 4. Delete
    purchasesDao.deleteById(id)
    val remaining = purchasesDao.getAll().first()
    assertEquals(0, remaining.size)
  }

  @Test
  fun `safetyAlertsDao CRUD operations`() = runBlocking {
    val alert = SafetyAlert(
      alertType = "SAFE_ARRIVAL",
      destination = "North Campus Dorm B",
      durationMinutes = 15,
      escalatedToContact = "Sarah",
      pinVerified = true,
      notes = "Verified PIN 1234"
    )
    // 1. Create
    val id = safetyAlertsDao.insert(alert)
    assertTrue(id > 0)

    // 2. Read
    val alerts = safetyAlertsDao.getAll().first()
    assertEquals(1, alerts.size)
    assertEquals("SAFE_ARRIVAL", alerts[0].alertType)

    // 3. Update
    safetyAlertsDao.updateNotes(id, "Updated notes: Check-in complete")
    val updated = safetyAlertsDao.getById(id).first()
    assertNotNull(updated)
    assertEquals("Updated notes: Check-in complete", updated!!.notes)

    // 4. Delete
    safetyAlertsDao.deleteById(id)
    val remaining = safetyAlertsDao.getAll().first()
    assertEquals(0, remaining.size)
  }
}
