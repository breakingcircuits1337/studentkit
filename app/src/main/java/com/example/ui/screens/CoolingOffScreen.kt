package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CoolingOffHold
import com.example.ui.StudentKitViewModel
import com.example.ui.theme.*

@Composable
fun CoolingOffScreen(
    viewModel: StudentKitViewModel,
    onBack: () -> Unit
) {
    val holds by viewModel.holds.collectAsState()
    val wage by viewModel.studentWage.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showWageDialog by remember { mutableStateOf(false) }

    val activeHolds = holds.filter { it.status == "LOCKED" || it.status == "UNLOCKED" }
    val savedHolds = holds.filter { it.status == "AVOIDED" }
    val totalMoneySaved = savedHolds.sumOf { it.price }
    val totalHoursSaved = totalMoneySaved / wage

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
                            modifier = Modifier.testTag("cooling_off_back_button")
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Cooling Off",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmeraldNeon.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "48H SHIELD",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldNeon,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Impulse Purchase Interceptor",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    // Student Wage Setting Chip
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = NavySurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NavyCardBorder),
                        modifier = Modifier
                            .clickable { showWageDialog = true }
                            .testTag("configure_wage_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Paid, contentDescription = null, tint = EmeraldNeon, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$${String.format("%.2f", wage)}/hr",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = EmeraldNeon,
                contentColor = NavyDeep,
                modifier = Modifier.testTag("add_impulse_hold_fab")
            ) {
                Icon(Icons.Default.AddShoppingCart, contentDescription = "Intercept Item")
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
            // Regret Shield Hero Stat Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .border(1.dp, EmeraldNeon.copy(alpha = 0.4f), RoundedCornerShape(22.dp))
                        .testTag("cooling_off_stat_card"),
                    colors = CardDefaults.cardColors(containerColor = NavySurface)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Regret Shield Cumulative Savings",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary
                            )
                            Icon(Icons.Default.Shield, contentDescription = null, tint = EmeraldNeon)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$${String.format("%.2f", totalMoneySaved)}",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = EmeraldNeon
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SAVED",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }

                        Text(
                            text = "Equivalent to ${String.format("%.1f", totalHoursSaved)} hours of student labor avoided",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Share Sheet Quick Simulation Bar
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, NavyCardBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = NavySurfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Share, contentDescription = null, tint = CyanSky, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Android Share Sheet Intercept",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Text(
                                text = "ACTION_SEND",
                                fontSize = 10.sp,
                                color = TextMuted,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Simulate sharing a product link directly into StudentKit hold vault:",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.handleSharedText("AirPods Max Space Gray \$549.00 https://apple.com")
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("simulate_share_headphones"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NavySurface)
                            ) {
                                Text("AirPods ($549)", fontSize = 11.sp, color = CyanSky)
                            }
                            Button(
                                onClick = {
                                    viewModel.handleSharedText("Stanley Tumbler 40oz \$45.00 https://target.com")
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("simulate_share_tumbler"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NavySurface)
                            ) {
                                Text("Tumbler ($45)", fontSize = 11.sp, color = CyanSky)
                            }
                        }
                    }
                }
            }

            // Active Holds Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active Purchasing Holds",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${activeHolds.size} in lockup",
                        fontSize = 12.sp,
                        color = EmeraldNeon
                    )
                }
            }

            // Active Holds Items
            if (activeHolds.isEmpty()) {
                item {
                    Text(
                        text = "No impulse items currently locked. Tap + or share from browser to start a 48h hold.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            } else {
                items(activeHolds, key = { it.id }) { hold ->
                    ActiveHoldCard(
                        hold = hold,
                        onVerdict = { verdict ->
                            viewModel.resolveHoldVerdict(hold.id, verdict)
                        }
                    )
                }
            }

            // Saved / Avoided History
            if (savedHolds.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Averted Impulses (Saved Log)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                items(savedHolds, key = { it.id }) { hold ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, NavyCardBorder, RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = NavySurface.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldNeon.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldNeon, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = hold.productTitle, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                Text(
                                    text = if (hold.reflectionNotes.isNotBlank()) hold.reflectionNotes else "Decided against buying after 48h cool-off.",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                            Text(
                                text = "+$${String.format("%.0f", hold.price)}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = EmeraldNeon
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog: Configure Wage
    if (showWageDialog) {
        var wageInput by remember { mutableStateOf(wage.toString()) }
        AlertDialog(
            onDismissRequest = { showWageDialog = false },
            containerColor = NavySurface,
            title = { Text("Configure Student Hourly Wage", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Column {
                    Text(
                        "StudentKit converts prices into the exact number of hours you must work to pay for it.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = wageInput,
                        onValueChange = { wageInput = it },
                        label = { Text("Hourly Wage ($/hr)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_student_wage")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = wageInput.toDoubleOrNull() ?: 15.0
                        viewModel.updateStudentWage(parsed)
                        showWageDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon),
                    modifier = Modifier.testTag("save_wage_button")
                ) {
                    Text("Save Wage", color = NavyDeep, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWageDialog = false }) { Text("Cancel", color = TextSecondary) }
            }
        )
    }

    // Dialog: Add Impulse Hold manually
    if (showAddDialog) {
        var titleInput by remember { mutableStateOf("") }
        var priceInput by remember { mutableStateOf("") }
        var reflectionInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = NavySurface,
            title = { Text("Place 48h Purchasing Hold", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("Product Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_hold_product_title")
                    )
                    OutlinedTextField(
                        value = priceInput,
                        onValueChange = { priceInput = it },
                        label = { Text("Price ($)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_hold_product_price")
                    )
                    OutlinedTextField(
                        value = reflectionInput,
                        onValueChange = { reflectionInput = it },
                        label = { Text("Why do you want this right now?") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_hold_reflection")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val p = priceInput.toDoubleOrNull() ?: 29.99
                        viewModel.addImpulseHold(titleInput, p, "Manual Entry", reflectionInput)
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon),
                    modifier = Modifier.testTag("confirm_create_hold_button")
                ) {
                    Text("Enforce 48h Lockup", color = NavyDeep, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel", color = TextSecondary) }
            }
        )
    }
}

@Composable
fun ActiveHoldCard(
    hold: CoolingOffHold,
    onVerdict: (String) -> Unit
) {
    val progress = (hold.elapsedHours / hold.holdDurationHours).toFloat().coerceIn(0f, 1f)
    val isReady = hold.isLockupPassed

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                1.dp,
                if (isReady) CyanSky else NavyCardBorder,
                RoundedCornerShape(20.dp)
            )
            .testTag("hold_card_${hold.id}"),
        colors = CardDefaults.cardColors(containerColor = NavySurface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = hold.productTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "${String.format("%.1f", hold.hoursWorkedEquivalent)} hours of campus labor",
                        fontSize = 12.sp,
                        color = AmberBright,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = "$${String.format("%.2f", hold.price)}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress bar and countdown timer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isReady) "48H LOCKUP COMPLETED" else "${String.format("%.0f", hold.remainingHours)}h Remaining in Vault",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isReady) EmeraldNeon else CyanSky
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = if (isReady) EmeraldNeon else CyanSky,
                trackColor = NavySurfaceVariant,
            )

            if (hold.reflectionNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = NavySurfaceVariant
                ) {
                    Text(
                        text = "\"${hold.reflectionNotes}\"",
                        fontSize = 11.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = TextSecondary,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Verdict Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onVerdict("AVOIDED") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("verdict_avoid_${hold.id}"),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Savings, contentDescription = null, tint = NavyDeep, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("I Don't Need It", color = NavyDeep, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = { onVerdict("PURCHASED") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("verdict_buy_${hold.id}"),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NavyCardBorder)
                ) {
                    Text("Proceed to Buy", color = TextSecondary, fontSize = 12.sp)
                }
            }
        }
    }
}
