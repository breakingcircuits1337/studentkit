package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.CampusListing
import com.example.data.model.Purchase
import com.example.data.model.SafetyAlert
import com.example.data.model.SyllabusItem
import com.example.data.model.TrialItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        SyllabusItem::class,
        Purchase::class,
        SafetyAlert::class,
        TrialItem::class,
        CampusListing::class
    ],
    version = 2,
    exportSchema = false
)
abstract class StudentKitDatabase : RoomDatabase() {
    abstract fun syllabusItemsDao(): SyllabusItemsDao
    abstract fun purchasesDao(): PurchasesDao
    abstract fun safetyAlertsDao(): SafetyAlertsDao
    abstract fun studentKitDao(): StudentKitDao

    companion object {
        @Volatile
        private var INSTANCE: StudentKitDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): StudentKitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StudentKitDatabase::class.java,
                    "studentkit_os_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialDemoData(database.studentKitDao())
                    }
                }
            }
        }

        suspend fun populateInitialDemoData(dao: StudentKitDao) {
            // 1. Crunch SyllabusItems
            dao.insertSyllabusItems(
                listOf(
                    SyllabusItem(
                        courseCode = "CS 301",
                        courseName = "Algorithms & Data Structures",
                        title = "Midterm 1: Dynamic Programming & Graphs",
                        dueDateString = "Oct 18, 2026",
                        daysUntilDue = 6,
                        startByDaysBefore = 5,
                        weightPercent = 25,
                        estStudyHours = 12,
                        isCompleted = false,
                        urgencyLevel = "HIGH",
                        syllabusDocName = "CS301_Fall2026_Syllabus.pdf"
                    ),
                    SyllabusItem(
                        courseCode = "BIO 210",
                        courseName = "Cellular & Molecular Biology",
                        title = "Lab Report: Gel Electrophoresis Protocol",
                        dueDateString = "Oct 22, 2026",
                        daysUntilDue = 10,
                        startByDaysBefore = 4,
                        weightPercent = 15,
                        estStudyHours = 6,
                        isCompleted = false,
                        urgencyLevel = "MEDIUM",
                        syllabusDocName = "BIO210_CourseGuide.pdf"
                    ),
                    SyllabusItem(
                        courseCode = "ECON 101",
                        courseName = "Macroeconomic Theory",
                        title = "Problem Set 4: Federal Reserve Open Market",
                        dueDateString = "Oct 25, 2026",
                        daysUntilDue = 13,
                        startByDaysBefore = 3,
                        weightPercent = 10,
                        estStudyHours = 4,
                        isCompleted = false,
                        urgencyLevel = "LOW",
                        syllabusDocName = "ECON101_Macro_2026.pdf"
                    ),
                    SyllabusItem(
                        courseCode = "CS 301",
                        courseName = "Algorithms & Data Structures",
                        title = "Final Project: Shortest Path Routing Engine",
                        dueDateString = "Nov 14, 2026",
                        daysUntilDue = 33,
                        startByDaysBefore = 14,
                        weightPercent = 30,
                        estStudyHours = 24,
                        isCompleted = false,
                        urgencyLevel = "HIGH",
                        syllabusDocName = "CS301_Fall2026_Syllabus.pdf"
                    )
                )
            )

            // 2. Cooling Off Purchases (Impulse Shopping Shield)
            val now = System.currentTimeMillis()
            dao.insertPurchases(
                listOf(
                    Purchase(
                        productTitle = "Sony WH-1000XM5 Wireless Headphones",
                        price = 348.00,
                        sourceUrl = "https://amazon.com/dp/B09XS7JWHH",
                        hourlyWage = 15.50,
                        holdDurationHours = 48,
                        createdTimestamp = now - (36 * 3600 * 1000L), // 36 hours elapsed, 12h remaining
                        status = "LOCKED",
                        reflectionNotes = "Saw a TikTok study setup. Do my current earbuds still work fine?"
                    ),
                    Purchase(
                        productTitle = "Keychron Q1 Pro Custom Mechanical Keyboard",
                        price = 199.00,
                        sourceUrl = "https://keychron.com/products/q1-pro",
                        hourlyWage = 15.50,
                        holdDurationHours = 48,
                        createdTimestamp = now - (52 * 3600 * 1000L), // Lockup expired
                        status = "UNLOCKED",
                        reflectionNotes = "Wanted banana tactile switches. Realized 13 hours of library desk shifts."
                    ),
                    Purchase(
                        productTitle = "Vintage Leather Messenger Bag",
                        price = 145.00,
                        sourceUrl = "https://nordstrom.com/s/messenger",
                        hourlyWage = 15.50,
                        holdDurationHours = 48,
                        createdTimestamp = now - (120 * 3600 * 1000L),
                        status = "AVOIDED",
                        reflectionNotes = "Pass! My backpack is totally waterproof and holds my laptop safer. Saved $145!"
                    )
                )
            )

            // 3. Walk Me Home SafetyAlerts
            dao.insertSafetyAlerts(
                listOf(
                    SafetyAlert(
                        timestamp = now - (2 * 86400 * 1000L),
                        alertType = "SAFE_ARRIVAL",
                        destination = "North Campus Dorm B",
                        durationMinutes = 15,
                        escalatedToContact = "Sarah (Roommate)",
                        pinVerified = true,
                        notes = "Safe arrival verified with PIN 1234."
                    ),
                    SafetyAlert(
                        timestamp = now - (8 * 3600 * 1000L),
                        alertType = "FAKE_CALL_TRIGGERED",
                        destination = "Library to Engineering Quad",
                        durationMinutes = 10,
                        escalatedToContact = "Campus Police Dispatch",
                        pinVerified = true,
                        notes = "User triggered fake call escape near Pillar #12."
                    ),
                    SafetyAlert(
                        timestamp = now - (3600 * 1000L),
                        alertType = "SAFE_ARRIVAL",
                        destination = "Student Union Hub",
                        durationMinutes = 12,
                        escalatedToContact = "Campus SafeRide",
                        pinVerified = true,
                        notes = "Day walk completed safely without incident."
                    )
                )
            )

            // 4. TrialSniper Initial Trials
            dao.insertTrials(
                listOf(
                    TrialItem(
                        serviceName = "Chegg Study Premium",
                        monthlyCost = 19.95,
                        renewalDateString = "Tomorrow (24h alert)",
                        daysRemaining = 1,
                        category = "Academic",
                        cancelUrl = "https://chegg.com/my/orders",
                        isCancelled = false
                    ),
                    TrialItem(
                        serviceName = "Adobe Creative Cloud Student",
                        monthlyCost = 29.99,
                        renewalDateString = "In 2 days (48h alert)",
                        daysRemaining = 2,
                        category = "Productivity",
                        cancelUrl = "https://account.adobe.com/plans",
                        isCancelled = false
                    ),
                    TrialItem(
                        serviceName = "Spotify Student + Hulu Bundle",
                        monthlyCost = 5.99,
                        renewalDateString = "In 9 days",
                        daysRemaining = 9,
                        category = "Entertainment",
                        cancelUrl = "https://spotify.com/account/subscription",
                        isCancelled = false
                    ),
                    TrialItem(
                        serviceName = "DoorDash DashPass Student",
                        monthlyCost = 4.99,
                        renewalDateString = "In 18 days",
                        daysRemaining = 18,
                        category = "Food",
                        cancelUrl = "https://doordash.com/manage-dashpass",
                        isCancelled = false
                    )
                )
            )

            // 5. Campus Trade Initial Listings
            dao.insertListings(
                listOf(
                    CampusListing(
                        title = "Calculus: Early Transcendentals (Stewart 9th Edition)",
                        price = 45.00,
                        category = "TEXTBOOK",
                        quadLocation = "Engineering Quad",
                        sellerName = "Alex Chen (Junior)",
                        timeAgo = "18m ago",
                        description = "Hardcover with no highlights or writing. Used for MATH 241/242. Can meet at Granger Library."
                    ),
                    CampusListing(
                        title = "TI-84 Plus CE Color Graphing Calculator",
                        price = 55.00,
                        category = "TECH",
                        quadLocation = "North Quad / Dorm B",
                        sellerName = "Maya Patel (Sophomore)",
                        timeAgo = "1h ago",
                        description = "Comes with USB charging cable and slide case. Battery holds 3 weeks charge."
                    ),
                    CampusListing(
                        title = "Need someone to hold spot in line for Hackathon swag hoodie",
                        price = 20.00,
                        category = "MICRO_GIG",
                        quadLocation = "Student Union Hub",
                        sellerName = "Jordan Riley",
                        timeAgo = "3h ago",
                        description = "30-minute line sit between 2:00 PM and 2:30 PM while I finish my chemistry lab quiz."
                    ),
                    CampusListing(
                        title = "Mini Fridge with Separate Freezer (Insignia 3.0 cu ft)",
                        price = 60.00,
                        category = "DORM",
                        quadLocation = "West Campus Quad",
                        sellerName = "Liam Vance",
                        timeAgo = "5h ago",
                        description = "Clean, sanitized, super quiet compressor. Moving to off-campus apartment."
                    )
                )
            )
        }
    }
}
