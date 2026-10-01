package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CrunchDeadline
import com.example.ui.StudentKitViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrunchScreen(
    viewModel: StudentKitViewModel,
    onBack: () -> Unit
) {
    val deadlines by viewModel.deadlines.collectAsState()
    val isScanning by viewModel.isScanningOcr.collectAsState()
    val scanSuccessMessage by viewModel.ocrScanSuccessMessage.collectAsState()
    val isPro by viewModel.isProUser.collectAsState()

    var showScanSheet by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredDeadlines = remember(deadlines, selectedFilter) {
        if (selectedFilter == "ALL") deadlines else deadlines.filter { it.courseCode == selectedFilter }
    }

    Scaffold(
        containerColor = NavyDeep,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavySurface)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("crunch_back_button")
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Crunch",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = CyanSky.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "ML KIT ON-DEVICE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyanSky,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Syllabus OCR & Semester Runway",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    // Scan Syllabus Action Button
                    Button(
                        onClick = { showScanSheet = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanSky),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("scan_syllabus_button")
                    ) {
                        Icon(Icons.Default.DocumentScanner, contentDescription = null, tint = NavyDeep, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Scan", color = NavyDeep, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = CyanSky,
                contentColor = NavyDeep,
                modifier = Modifier.testTag("add_custom_deadline_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Deadline")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // OCR Scanning Banner or Success message
            if (isScanning) {
                item {
                    OcrScanningLaserCard()
                }
            }

            if (scanSuccessMessage != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = EmeraldNeon.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldNeon)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = scanSuccessMessage ?: "", fontSize = 13.sp, color = TextPrimary)
                        }
                    }
                }
            }

            // Dynamic Semester Heatmap
            item {
                SemesterHeatmapCard(deadlines = deadlines)
            }

            // Course Filters
            item {
                val courses = listOf("ALL") + deadlines.map { it.courseCode }.distinct()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    courses.forEach { course ->
                        val isSelected = selectedFilter == course
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = course },
                            label = { Text(course, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanSky.copy(alpha = 0.2f),
                                selectedLabelColor = CyanSky,
                                containerColor = NavySurface,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                selectedBorderColor = CyanSky,
                                borderColor = NavyCardBorder
                            ),
                            modifier = Modifier.testTag("filter_chip_$course")
                        )
                    }
                }
            }

            // Runway Breakdown Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Anti-Cramming Runway Schedule",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${filteredDeadlines.count { !it.isCompleted }} Active",
                        fontSize = 12.sp,
                        color = CyanSky
                    )
                }
            }

            // List of Deadline items with Runway calculations
            items(filteredDeadlines, key = { it.id }) { deadline ->
                DeadlineRunwayCard(
                    deadline = deadline,
                    onToggleComplete = { completed ->
                        viewModel.toggleDeadlineCompleted(deadline.id, completed)
                    }
                )
            }
        }
    }

    // Modal Sheet: Scan Syllabus Simulator
    if (showScanSheet) {
        ModalBottomSheet(
            onDismissRequest = { showScanSheet = false },
            containerColor = NavySurface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Snap Syllabus Photo",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Google ML Kit on-device parser (Zero Cloud Latency)",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = CyanSky)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Select a demo syllabus document to scan:",
                    fontSize = 13.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(10.dp))

                val sampleSyllabi = listOf(
                    Triple("PHYS 211", "University Physics (Midterms, Quizzes & Labs)", "Oct 29 & Nov 02"),
                    Triple("CHEM 102", "General Chemistry (Exam 3 & Problem Sets)", "Nov 05"),
                    Triple("ENG 105", "Technical Writing (Whitepaper Drafts & Reviews)", "Oct 20")
                )

                sampleSyllabi.forEach { (code, desc, dates) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, NavyCardBorder, RoundedCornerShape(14.dp))
                            .clickable {
                                showScanSheet = false
                                viewModel.simulateOcrSyllabusScan(code)
                            }
                            .testTag("sample_syllabus_$code"),
                        colors = CardDefaults.cardColors(containerColor = NavySurfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(CyanSky.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = code.take(2),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = CyanSky
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "$code: $desc", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(text = "Key Dates: $dates", fontSize = 11.sp, color = TextSecondary)
                            }
                            Icon(Icons.Default.DocumentScanner, contentDescription = null, tint = CyanSky, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Dialog: Add Custom Deadline
    if (showAddDialog) {
        var codeInput by remember { mutableStateOf("") }
        var nameInput by remember { mutableStateOf("") }
        var titleInput by remember { mutableStateOf("") }
        var daysInput by remember { mutableStateOf("7") }
        var startByInput by remember { mutableStateOf("4") }
        var weightInput by remember { mutableStateOf("20") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = NavySurface,
            title = {
                Text("Add Course Milestone", fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = codeInput,
                        onValueChange = { codeInput = it },
                        label = { Text("Course Code (e.g. CS 225)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_course_code")
                    )
                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("Milestone (e.g. Final Exam)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_course_title")
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = daysInput,
                            onValueChange = { daysInput = it },
                            label = { Text("Days Left") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_days_left")
                        )
                        OutlinedTextField(
                            value = startByInput,
                            onValueChange = { startByInput = it },
                            label = { Text("Start-By Days") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_start_by")
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val days = daysInput.toIntOrNull() ?: 7
                        val startBy = startByInput.toIntOrNull() ?: 4
                        val weight = weightInput.toIntOrNull() ?: 20
                        viewModel.addManualDeadline(codeInput, nameInput, titleInput, days, startBy, weight)
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanSky),
                    modifier = Modifier.testTag("confirm_add_deadline_button")
                ) {
                    Text("Add to Runway", color = NavyDeep, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun OcrScanningLaserCard() {
    val infiniteTransition = rememberInfiniteTransition(label = "laser")
    val laserOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_y"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, CyanSky, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = NavySurfaceVariant)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Scanning laser line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = (100 * laserOffset).dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color.Transparent, CyanSky, EmeraldNeon, Color.Transparent)
                        )
                    )
            )

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = CyanSky,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Google ML Kit Extracting Critical Dates & Exam Weights...",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanSky
                )
            }
        }
    }
}

@Composable
fun SemesterHeatmapCard(deadlines: List<CrunchDeadline>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, NavyCardBorder, RoundedCornerShape(20.dp))
            .testTag("semester_heatmap_card"),
        colors = CardDefaults.cardColors(containerColor = NavySurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Dynamic Semester Heatmap",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Stress index calculated across 16 academic weeks",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                // Legend
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(HeatmapLow))
                    Text("Low", fontSize = 10.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(HeatmapMed))
                    Text("Med", fontSize = 10.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(HeatmapHigh))
                    Text("Crunch", fontSize = 10.sp, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 16 weeks grid (2 rows of 8 cells)
            val weeks = (1..16).toList()
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                for (row in 0..1) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (col in 0..7) {
                            val weekNum = row * 8 + col + 1
                            val stressColor = when (weekNum) {
                                7, 8 -> HeatmapHigh // Midterm week
                                15, 16 -> HeatmapHigh // Finals week
                                4, 11 -> HeatmapMed // Quizzes / reports
                                1, 2 -> HeatmapLow // Syllabus & intro
                                else -> HeatmapNone
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(stressColor.copy(alpha = if (stressColor == HeatmapNone) 0.3f else 0.85f))
                                    .border(1.dp, NavyCardBorder, RoundedCornerShape(6.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "W$weekNum",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (stressColor == HeatmapNone) TextMuted else Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeadlineRunwayCard(
    deadline: CrunchDeadline,
    onToggleComplete: (Boolean) -> Unit
) {
    val urgencyColor = when (deadline.urgencyLevel) {
        "HIGH" -> CrimsonAlert
        "MEDIUM" -> AmberBright
        else -> CyanSky
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(
                1.dp,
                if (deadline.isCompleted) NavyCardBorder else urgencyColor.copy(alpha = 0.5f),
                RoundedCornerShape(18.dp)
            )
            .testTag("deadline_card_${deadline.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (deadline.isCompleted) NavySurface.copy(alpha = 0.5f) else NavySurface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Checkbox(
                    checked = deadline.isCompleted,
                    onCheckedChange = onToggleComplete,
                    colors = CheckboxDefaults.colors(
                        checkedColor = EmeraldNeon,
                        uncheckedColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("checkbox_deadline_${deadline.id}")
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = urgencyColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = deadline.courseCode,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = urgencyColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = "${deadline.weightPercent}% OF GRADE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = deadline.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (deadline.isCompleted) TextMuted else TextPrimary
                    )
                    Text(
                        text = deadline.courseName,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = NavyCardBorder.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(10.dp))

            // Runway details: Start-By Recommendation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.FlightTakeoff,
                        contentDescription = null,
                        tint = if (deadline.daysUntilDue <= deadline.startByDaysBefore) CrimsonAlert else CyanSky,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (deadline.daysUntilDue <= deadline.startByDaysBefore)
                            "RUNWAY ACTIVE: Start Today!"
                        else
                            "Start runway: ${deadline.startByDaysBefore} days before",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (deadline.daysUntilDue <= deadline.startByDaysBefore) CrimsonAlert else CyanSky
                    )
                }

                Text(
                    text = "${deadline.estStudyHours}h study runway",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }
    }
}
