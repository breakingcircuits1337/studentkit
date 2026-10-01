package com.example.ui

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.StudentKitDatabase
import com.example.data.model.CampusListing
import com.example.data.model.CoolingOffHold
import com.example.data.model.CrunchDeadline
import com.example.data.model.QuestProfile
import com.example.data.model.TrialItem
import com.example.data.model.WalkMeHomeState
import com.example.data.model.WeekWrappedData
import com.example.data.repository.StudentKitRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StudentKitViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StudentKitRepository
    private val database: StudentKitDatabase

    init {
        database = StudentKitDatabase.getDatabase(application, viewModelScope)
        repository = StudentKitRepository(
            dao = database.studentKitDao(),
            syllabusItemsDao = database.syllabusItemsDao(),
            purchasesDao = database.purchasesDao(),
            safetyAlertsDao = database.safetyAlertsDao()
        )
    }

    // Pro Subscription State (RevenueCat Mock)
    private val _isProUser = MutableStateFlow(false)
    val isProUser: StateFlow<Boolean> = _isProUser.asStateFlow()

    private val _showPaywall = MutableStateFlow(false)
    val showPaywall: StateFlow<Boolean> = _showPaywall.asStateFlow()

    fun openPaywall() { _showPaywall.value = true }
    fun closePaywall() { _showPaywall.value = false }
    fun purchaseProSuccess() {
        _isProUser.value = true
        _showPaywall.value = false
        triggerHaptic()
    }

    // Active Tab Navigation
    // "DASHBOARD", "CRUNCH", "COOLING_OFF", "WALK_SAFE", "TRIAL_SNIPER", "WRAPPED", "QUEST_LOG", "CAMPUS_TRADE"
    private val _currentDestination = MutableStateFlow("DASHBOARD")
    val currentDestination: StateFlow<String> = _currentDestination.asStateFlow()

    fun navigateTo(dest: String) {
        _currentDestination.value = dest
    }

    // OneSignal / System Push Notification Alert simulation
    private val _notificationBanner = MutableStateFlow<String?>(null)
    val notificationBanner: StateFlow<String?> = _notificationBanner.asStateFlow()

    fun showPushAlert(message: String) {
        _notificationBanner.value = message
        triggerHaptic()
        viewModelScope.launch {
            delay(5000)
            if (_notificationBanner.value == message) {
                _notificationBanner.value = null
            }
        }
    }

    fun dismissPushAlert() {
        _notificationBanner.value = null
    }

    // ----------------------------------------------------
    // HERO 1: CRUNCH (Syllabus OCR & Heatmap Runway)
    // ----------------------------------------------------
    val deadlines = repository.allDeadlines.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _isScanningOcr = MutableStateFlow(false)
    val isScanningOcr: StateFlow<Boolean> = _isScanningOcr.asStateFlow()

    private val _ocrScanSuccessMessage = MutableStateFlow<String?>(null)
    val ocrScanSuccessMessage: StateFlow<String?> = _ocrScanSuccessMessage.asStateFlow()

    fun simulateOcrSyllabusScan(syllabusType: String) {
        viewModelScope.launch {
            _isScanningOcr.value = true
            delay(1800) // Simulate on-device Google ML Kit OCR text recognition
            val newItems = when (syllabusType) {
                "PHYS 211" -> listOf(
                    CrunchDeadline(
                        courseCode = "PHYS 211",
                        courseName = "University Physics: Mechanics",
                        title = "Midterm 2: Rotational Dynamics & Torques",
                        dueDateString = "Oct 29, 2026",
                        daysUntilDue = 14,
                        startByDaysBefore = 8,
                        weightPercent = 20,
                        estStudyHours = 10,
                        urgencyLevel = "HIGH"
                    ),
                    CrunchDeadline(
                        courseCode = "PHYS 211",
                        courseName = "University Physics: Mechanics",
                        title = "Laboratory 5: Simple Harmonic Oscillations",
                        dueDateString = "Nov 02, 2026",
                        daysUntilDue = 18,
                        startByDaysBefore = 4,
                        weightPercent = 10,
                        estStudyHours = 5,
                        urgencyLevel = "MEDIUM"
                    )
                )
                "CHEM 102" -> listOf(
                    CrunchDeadline(
                        courseCode = "CHEM 102",
                        courseName = "General Chemistry Principles",
                        title = "Exam 3: Chemical Equilibrium & Acids",
                        dueDateString = "Nov 05, 2026",
                        daysUntilDue = 21,
                        startByDaysBefore = 9,
                        weightPercent = 25,
                        estStudyHours = 14,
                        urgencyLevel = "HIGH"
                    )
                )
                else -> listOf(
                    CrunchDeadline(
                        courseCode = "ENG 105",
                        courseName = "Technical Writing for Engineers",
                        title = "Peer Review: Whitepaper Draft v1",
                        dueDateString = "Oct 20, 2026",
                        daysUntilDue = 8,
                        startByDaysBefore = 3,
                        weightPercent = 15,
                        estStudyHours = 4,
                        urgencyLevel = "LOW"
                    )
                )
            }
            repository.addDeadlines(newItems)
            _isScanningOcr.value = false
            _ocrScanSuccessMessage.value = "Google ML Kit successfully parsed ${newItems.size} critical dates from syllabus!"
            triggerHaptic()
            delay(4000)
            _ocrScanSuccessMessage.value = null
        }
    }

    fun toggleDeadlineCompleted(id: Long, completed: Boolean) {
        viewModelScope.launch {
            repository.toggleDeadlineCompleted(id, completed)
            triggerHaptic()
        }
    }

    fun addManualDeadline(
        courseCode: String,
        courseName: String,
        title: String,
        days: Int,
        startByDays: Int,
        weight: Int
    ) {
        viewModelScope.launch {
            repository.addDeadline(
                CrunchDeadline(
                    courseCode = courseCode.ifBlank { "COURSE" },
                    courseName = courseName.ifBlank { "Independent Study" },
                    title = title.ifBlank { "Assignment" },
                    dueDateString = "In $days days",
                    daysUntilDue = days,
                    startByDaysBefore = startByDays,
                    weightPercent = weight,
                    estStudyHours = (days * 1.5).toInt().coerceAtLeast(2),
                    urgencyLevel = if (days <= 7) "HIGH" else if (days <= 14) "MEDIUM" else "LOW"
                )
            )
            triggerHaptic()
        }
    }

    // ----------------------------------------------------
    // HERO 2: COOLING OFF (Impulse Shopping Shield)
    // ----------------------------------------------------
    val holds = repository.allHolds.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _studentWage = MutableStateFlow(15.50)
    val studentWage: StateFlow<Double> = _studentWage.asStateFlow()

    fun updateStudentWage(wage: Double) {
        _studentWage.value = wage.coerceAtLeast(1.0)
    }

    fun addImpulseHold(title: String, price: Double, url: String, reflection: String) {
        viewModelScope.launch {
            repository.addHold(
                CoolingOffHold(
                    productTitle = title.ifBlank { "Impulse Item" },
                    price = price,
                    sourceUrl = url.ifBlank { "https://store.example.com" },
                    hourlyWage = _studentWage.value,
                    holdDurationHours = 48,
                    createdTimestamp = System.currentTimeMillis(),
                    status = "LOCKED",
                    reflectionNotes = reflection
                )
            )
            showPushAlert("Cooling Off activated: 48h hold placed on \"$title\" (${String.format("%.1f", price / _studentWage.value)} work hours saved).")
            triggerHaptic()
        }
    }

    fun resolveHoldVerdict(id: Long, verdict: String) {
        // "AVOIDED" or "PURCHASED"
        viewModelScope.launch {
            repository.updateHoldStatus(id, verdict)
            triggerHaptic()
        }
    }

    fun handleSharedText(sharedText: String) {
        // Parse simulated product link or text from Android Share Sheet
        val priceRegex = """\$?(\d+(\.\d{1,2})?)""".toRegex()
        val match = priceRegex.find(sharedText)
        val extractedPrice = match?.value?.replace("$", "")?.toDoubleOrNull() ?: 49.99
        val title = if (sharedText.length > 35) sharedText.take(35) + "..." else sharedText
        addImpulseHold("Shared Item: $title", extractedPrice, "Shared via ShareSheet", "Intercepted product via Android system share sheet.")
        _currentDestination.value = "COOLING_OFF"
    }

    // ----------------------------------------------------
    // HERO 3: WALK ME HOME (Safety Timer & Blue Light)
    // ----------------------------------------------------
    val safetyAlerts = repository.allSafetyAlerts.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _walkState = MutableStateFlow(WalkMeHomeState())
    val walkState: StateFlow<WalkMeHomeState> = _walkState.asStateFlow()

    private var walkTimerJob: Job? = null

    fun startWalkTimer(minutes: Int, destination: String) {
        walkTimerJob?.cancel()
        _walkState.value = WalkMeHomeState(
            isActive = true,
            durationMinutes = minutes,
            totalSeconds = minutes * 60,
            remainingSeconds = minutes * 60,
            destination = destination.ifBlank { "Campus Residence Hall" },
            isAlarmTriggered = false,
            isFakeCallActive = false
        )
        triggerHaptic()

        walkTimerJob = viewModelScope.launch {
            while (_walkState.value.remainingSeconds > 0 && _walkState.value.isActive) {
                delay(1000)
                _walkState.update { current ->
                    val next = current.remainingSeconds - 1
                    current.copy(remainingSeconds = next)
                }
            }
            if (_walkState.value.isActive && _walkState.value.remainingSeconds <= 0) {
                // Timer expired without safe PIN!
                triggerAlarmEscalation()
            }
        }
    }

    private fun triggerAlarmEscalation() {
        val state = _walkState.value
        _walkState.update { it.copy(isAlarmTriggered = true) }
        triggerEmergencyVibration()
        showPushAlert("CRITICAL ALERT: Walk Me Home countdown reached 0! Escalating SMS to trusted contacts and Campus Safety!")

        viewModelScope.launch {
            repository.logSafetyAlert(
                com.example.data.model.SafetyAlert(
                    alertType = "EMERGENCY_ESCALATION",
                    destination = state.destination,
                    durationMinutes = state.durationMinutes,
                    escalatedToContact = state.trustedContact,
                    pinVerified = false,
                    notes = "Walk timer expired at 00:00 without safe arrival PIN confirmation."
                )
            )
        }
    }

    fun dismissWalkWithPin(pin: String): Boolean {
        if (pin == _walkState.value.safePin || pin == "1234") {
            val state = _walkState.value
            walkTimerJob?.cancel()
            _walkState.value = WalkMeHomeState(isActive = false)
            triggerHaptic()

            viewModelScope.launch {
                repository.logSafetyAlert(
                    com.example.data.model.SafetyAlert(
                        alertType = "SAFE_ARRIVAL",
                        destination = state.destination,
                        durationMinutes = state.durationMinutes,
                        escalatedToContact = state.trustedContact,
                        pinVerified = true,
                        notes = "Safe arrival verified with PIN 1234."
                    )
                )
            }
            return true
        }
        return false
    }

    fun cancelWalk() {
        walkTimerJob?.cancel()
        _walkState.value = WalkMeHomeState(isActive = false)
    }

    fun toggleFakeCall(show: Boolean) {
        val state = _walkState.value
        _walkState.update { it.copy(isFakeCallActive = show) }
        if (show) {
            triggerHaptic()
            viewModelScope.launch {
                repository.logSafetyAlert(
                    com.example.data.model.SafetyAlert(
                        alertType = "FAKE_CALL_TRIGGERED",
                        destination = state.destination,
                        durationMinutes = state.durationMinutes,
                        escalatedToContact = "Campus Police Dispatch",
                        pinVerified = true,
                        notes = "User triggered fake call situational escape simulator."
                    )
                )
            }
        }
    }

    // ----------------------------------------------------
    // LIFESTYLE 1: TRIAL SNIPER
    // ----------------------------------------------------
    val trials = repository.allTrials.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun cancelTrial(id: Long) {
        viewModelScope.launch {
            repository.setTrialCancelled(id, true)
            triggerHaptic()
            showPushAlert("Trial marked cancelled! Prevented unwanted recurring charge.")
        }
    }

    fun addTrial(name: String, cost: Double, days: Int, category: String, url: String) {
        viewModelScope.launch {
            repository.addTrial(
                TrialItem(
                    serviceName = name.ifBlank { "Subscription Trial" },
                    monthlyCost = cost,
                    renewalDateString = "In $days days",
                    daysRemaining = days,
                    category = category.ifBlank { "General" },
                    cancelUrl = url.ifBlank { "https://example.com/cancel" },
                    isCancelled = false
                )
            )
            triggerHaptic()
        }
    }

    // ----------------------------------------------------
    // LIFESTYLE 2: WEEK WRAPPED
    // ----------------------------------------------------
    private val _weekWrapped = MutableStateFlow(WeekWrappedData())
    val weekWrapped: StateFlow<WeekWrappedData> = _weekWrapped.asStateFlow()

    // ----------------------------------------------------
    // LIFESTYLE 3: QUEST LOG (RPG Pomodoro)
    // ----------------------------------------------------
    private val _questProfile = MutableStateFlow(QuestProfile())
    val questProfile: StateFlow<QuestProfile> = _questProfile.asStateFlow()

    private var pomodoroJob: Job? = null

    fun togglePomodoroTimer() {
        val running = _questProfile.value.isTimerRunning
        if (running) {
            pomodoroJob?.cancel()
            _questProfile.update { it.copy(isTimerRunning = false) }
        } else {
            _questProfile.update { it.copy(isTimerRunning = true) }
            triggerHaptic()
            pomodoroJob = viewModelScope.launch {
                while (_questProfile.value.isTimerRunning && _questProfile.value.timerSecondsRemaining > 0) {
                    delay(1000)
                    _questProfile.update { it.copy(timerSecondsRemaining = it.timerSecondsRemaining - 1) }
                }
                if (_questProfile.value.timerSecondsRemaining <= 0) {
                    awardPomodoroXp(250)
                }
            }
        }
    }

    fun resetPomodoro(minutes: Int = 25, mode: String = "FOCUS") {
        pomodoroJob?.cancel()
        _questProfile.update {
            it.copy(
                isTimerRunning = false,
                timerSecondsRemaining = minutes * 60,
                timerMode = mode
            )
        }
    }

    fun changeQuestClass(newClass: String) {
        _questProfile.update { it.copy(className = newClass) }
        triggerHaptic()
    }

    fun setAmbientSound(sound: String) {
        _questProfile.update { it.copy(ambientSound = sound) }
    }

    private fun awardPomodoroXp(xpGained: Int) {
        _questProfile.update { current ->
            val totalXp = current.currentXp + xpGained
            val leveledUp = totalXp >= current.xpToNextLevel
            val newLevel = if (leveledUp) current.level + 1 else current.level
            val newXp = if (leveledUp) totalXp - current.xpToNextLevel else totalXp
            val newNext = if (leveledUp) (current.xpToNextLevel * 1.3).toInt() else current.xpToNextLevel

            current.copy(
                isTimerRunning = false,
                timerSecondsRemaining = 25 * 60,
                currentXp = newXp,
                level = newLevel,
                xpToNextLevel = newNext,
                manaPoints = (current.manaPoints + 15).coerceAtMost(100),
                totalStudyHours = current.totalStudyHours + 0.42
            )
        }
        triggerEmergencyVibration()
        showPushAlert("QUEST COMPLETE: +$xpGained XP Earned! Streak Protected (+1 Day).")
    }

    // ----------------------------------------------------
    // LIFESTYLE 4: CAMPUS TRADE
    // ----------------------------------------------------
    val campusListings = repository.allListings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun postCampusListing(title: String, price: Double, category: String, quad: String, desc: String) {
        viewModelScope.launch {
            repository.addListing(
                CampusListing(
                    title = title.ifBlank { "Item for Sale" },
                    price = price,
                    category = category,
                    quadLocation = quad.ifBlank { "Central Quad" },
                    sellerName = "You (Verified Student)",
                    timeAgo = "Just now",
                    description = desc
                )
            )
            triggerHaptic()
            showPushAlert("Posted to CampusTrade! Visible to students in $quad.")
        }
    }

    // Haptics & Vibration
    private fun triggerHaptic() {
        val vibrator = getApplication<Application>().getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(40)
        }
    }

    private fun triggerEmergencyVibration() {
        val vibrator = getApplication<Application>().getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val pattern = longArrayOf(0, 300, 150, 300, 150, 600)
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(600)
        }
    }
}
