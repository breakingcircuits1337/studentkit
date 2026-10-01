package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity for StudentKit's Crunch module (Syllabus OCR & Semester Runway).
 * Persists course milestones, assignment weights, and anti-cramming start-by schedules.
 */
@Entity(tableName = "syllabus_items")
data class SyllabusItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val courseCode: String,
    val courseName: String,
    val title: String,
    val dueDateString: String,
    val daysUntilDue: Int,
    val startByDaysBefore: Int,
    val weightPercent: Int,
    val estStudyHours: Int,
    val isCompleted: Boolean = false,
    val urgencyLevel: String = "MEDIUM", // "HIGH", "MEDIUM", "LOW"
    val syllabusDocName: String = "General Syllabus"
)

// Backward-compatibility alias
typealias CrunchDeadline = SyllabusItem

/**
 * Entity for StudentKit's Cooling Off module (Impulse Shopping Shield).
 * Persists products intercepted via the Android Share Sheet or manual input,
 * converts retail prices into student labor hours, and enforces 48-hour holds.
 */
@Entity(tableName = "purchases")
data class Purchase(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productTitle: String,
    val price: Double,
    val sourceUrl: String,
    val hourlyWage: Double = 15.0,
    val holdDurationHours: Int = 48,
    val createdTimestamp: Long = System.currentTimeMillis(),
    val status: String = "LOCKED", // "LOCKED", "UNLOCKED", "AVOIDED", "PURCHASED"
    val reflectionNotes: String = ""
) {
    val hoursWorkedEquivalent: Double
        get() = if (hourlyWage > 0) price / hourlyWage else 0.0

    val elapsedHours: Double
        get() = (System.currentTimeMillis() - createdTimestamp) / (1000.0 * 60 * 60)

    val remainingHours: Double
        get() = (holdDurationHours - elapsedHours).coerceAtLeast(0.0)

    val isLockupPassed: Boolean
        get() = elapsedHours >= holdDurationHours
}

// Backward-compatibility alias
typealias CoolingOffHold = Purchase

/**
 * Entity for StudentKit's Walk Me Home module (Localized Safety Timer).
 * Records safety sessions, emergency escalation events, safe PIN verifications,
 * and fake-call escape triggers.
 */
@Entity(tableName = "safety_alerts")
data class SafetyAlert(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val alertType: String, // "SAFE_ARRIVAL", "TIMER_EXPIRED", "EMERGENCY_ESCALATION", "FAKE_CALL_TRIGGERED"
    val destination: String,
    val durationMinutes: Int,
    val escalatedToContact: String,
    val pinVerified: Boolean = false,
    val notes: String = ""
)

@Entity(tableName = "trial_items")
data class TrialItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val serviceName: String,
    val monthlyCost: Double,
    val renewalDateString: String,
    val daysRemaining: Int,
    val category: String, // "Academic", "Entertainment", "Productivity", "Food"
    val cancelUrl: String,
    val isCancelled: Boolean = false
)

@Entity(tableName = "campus_listings")
data class CampusListing(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val price: Double,
    val category: String, // "TEXTBOOK", "TECH", "DORM", "MICRO_GIG"
    val quadLocation: String,
    val sellerName: String,
    val timeAgo: String,
    val description: String,
    val isAvailable: Boolean = true
)

data class WalkMeHomeState(
    val isActive: Boolean = false,
    val durationMinutes: Int = 15,
    val totalSeconds: Int = 15 * 60,
    val remainingSeconds: Int = 15 * 60,
    val destination: String = "North Campus Dorm B",
    val safePin: String = "1234",
    val trustedContact: String = "Sarah (Roommate) & Campus SafeRide",
    val isAlarmTriggered: Boolean = false,
    val isFakeCallActive: Boolean = false
)

data class QuestProfile(
    val level: Int = 7,
    val currentXp: Int = 1850,
    val xpToNextLevel: Int = 2500,
    val className: String = "Code Mage",
    val streakDays: Int = 14,
    val manaPoints: Int = 92,
    val isTimerRunning: Boolean = false,
    val timerSecondsRemaining: Int = 25 * 60,
    val timerMode: String = "FOCUS", // "FOCUS", "SHORT_BREAK", "LONG_BREAK"
    val ambientSound: String = "Cyber Lo-Fi",
    val totalStudyHours: Double = 42.5
)

data class WeekWrappedData(
    val totalStudyHours: Double = 34.5,
    val focusToDoomscrollRatio: Double = 4.2,
    val screenTimeSavedHours: Double = 11.8,
    val impulseMoneySaved: Double = 428.50,
    val peakProductivityWindow: String = "10:30 PM - 2:00 AM",
    val topLocation: String = "4th Fl Science Library - Silent Wing",
    val studentArchetype: String = "The Nocturnal Code Alchemist",
    val campusPercentile: String = "Top 3% of Campus Night-Owls",
    val questsCompleted: Int = 28
)
