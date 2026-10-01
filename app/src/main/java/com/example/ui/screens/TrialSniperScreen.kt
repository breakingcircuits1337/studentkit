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
import com.example.data.model.TrialItem
import com.example.ui.StudentKitViewModel
import com.example.ui.theme.*

@Composable
fun TrialSniperScreen(
    viewModel: StudentKitViewModel,
    onBack: () -> Unit
) {
    val trials by viewModel.trials.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    val activeTrials = trials.filter { !it.isCancelled }
    val cancelledTrials = trials.filter { it.isCancelled }
    val monthlyBurnProtected = cancelledTrials.sumOf { it.monthlyCost }
    val yearlyBurnProtected = monthlyBurnProtected * 12

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
                            modifier = Modifier.testTag("trial_sniper_back_button")
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "TrialSniper",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = AmberBright.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "ONESIGNAL 48H/24H",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AmberBright,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Subscription Charge Defense",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    // Test push notification trigger
                    IconButton(
                        onClick = {
                            viewModel.showPushAlert("ONESIGNAL HIGH-PRIORITY: Chegg Study renews in 24h (\$19.95/mo charge pending). Cancel now!")
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(NavySurfaceVariant)
                            .testTag("test_push_alert_button")
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = "Test Alert", tint = AmberBright)
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = AmberBright,
                contentColor = NavyDeep,
                modifier = Modifier.testTag("add_trial_fab")
            ) {
                Icon(Icons.Default.AddAlert, contentDescription = "Track Trial")
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
            // Stat Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .border(1.dp, AmberBright.copy(alpha = 0.4f), RoundedCornerShape(22.dp))
                        .testTag("trial_sniper_stat_card"),
                    colors = CardDefaults.cardColors(containerColor = NavySurface)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Annual Unwanted Billing Shield",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary
                            )
                            Icon(Icons.Default.Shield, contentDescription = null, tint = AmberBright)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$${String.format("%.2f", yearlyBurnProtected)}/yr",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AmberBright
                        )
                        Text(
                            text = "Saved by auto-sniping ${cancelledTrials.size} forgotten student free trials",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Active Tracked Trials
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active Free Trials (${activeTrials.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "High-Priority 48h / 24h Alerts",
                        fontSize = 11.sp,
                        color = AmberBright
                    )
                }
            }

            items(activeTrials, key = { it.id }) { trial ->
                TrialItemCard(
                    trial = trial,
                    onCancelClick = {
                        viewModel.cancelTrial(trial.id)
                    }
                )
            }

            // Cancelled / Sniped Trials
            if (cancelledTrials.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Successfully Disarmed Trials",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                items(cancelledTrials, key = { it.id }) { trial ->
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
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldNeon)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(trial.serviceName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                Text("Disarmed • Saved $${String.format("%.2f", trial.monthlyCost)}/mo", fontSize = 11.sp, color = TextSecondary)
                            }
                            Text(
                                "DISARMED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldNeon
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Trial Dialog
    if (showAddDialog) {
        var nameInput by remember { mutableStateOf("") }
        var costInput by remember { mutableStateOf("14.99") }
        var daysInput by remember { mutableStateOf("7") }
        var categoryInput by remember { mutableStateOf("Academic") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = NavySurface,
            title = { Text("Track New Subscription Trial", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Service (e.g. Duolingo Super, Notion)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_trial_name")
                    )
                    OutlinedTextField(
                        value = costInput,
                        onValueChange = { costInput = it },
                        label = { Text("Monthly Cost after trial ($)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_trial_cost")
                    )
                    OutlinedTextField(
                        value = daysInput,
                        onValueChange = { daysInput = it },
                        label = { Text("Days Remaining on Trial") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_trial_days")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cost = costInput.toDoubleOrNull() ?: 9.99
                        val days = daysInput.toIntOrNull() ?: 7
                        viewModel.addTrial(nameInput, cost, days, categoryInput, "https://example.com/cancel")
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberBright),
                    modifier = Modifier.testTag("confirm_add_trial_button")
                ) {
                    Text("Arm Sniper Defense", color = NavyDeep, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel", color = TextSecondary) }
            }
        )
    }
}

@Composable
fun TrialItemCard(
    trial: TrialItem,
    onCancelClick: () -> Unit
) {
    val isCriticalUrgent = trial.daysRemaining <= 2

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(
                1.dp,
                if (isCriticalUrgent) CrimsonAlert else AmberBright.copy(alpha = 0.5f),
                RoundedCornerShape(18.dp)
            )
            .testTag("trial_card_${trial.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isCriticalUrgent) CrimsonAlert.copy(alpha = 0.1f) else NavySurface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = trial.serviceName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "Renews: ${trial.renewalDateString}",
                        fontSize = 12.sp,
                        color = if (isCriticalUrgent) CrimsonAlert else AmberBright,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${String.format("%.2f", trial.monthlyCost)}/mo",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = trial.category,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onCancelClick,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("cancel_trial_button_${trial.id}"),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isCriticalUrgent) CrimsonAlert else AmberBright),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Cancel, contentDescription = null, tint = NavyDeep, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Disarm & Cancel Now", color = NavyDeep, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}
