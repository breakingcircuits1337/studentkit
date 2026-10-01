package com.example.ui.screens

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.StudentKitViewModel
import com.example.ui.theme.*

@Composable
fun WalkMeHomeScreen(
    viewModel: StudentKitViewModel,
    onBack: () -> Unit
) {
    val walkState by viewModel.walkState.collectAsState()
    val safetyAlerts by viewModel.safetyAlerts.collectAsState()

    var showPinDialog by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }
    var selectedWalkMinutes by remember { mutableStateOf(15) }
    var destinationInput by remember { mutableStateOf("North Campus Dorms") }

    // Fake call full screen overlay
    if (walkState.isFakeCallActive) {
        FakeCallSimulationScreen(
            onDismiss = { viewModel.toggleFakeCall(false) }
        )
        return
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
                            modifier = Modifier.testTag("walk_back_button")
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Walk Me Home",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (walkState.isActive) CrimsonAlert.copy(alpha = 0.2f) else EmeraldNeon.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = if (walkState.isActive) "ACTIVE TIMER" else "SAFETY READY",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (walkState.isActive) CrimsonAlert else EmeraldNeon,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Localized Campus Safety Perimeter",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    // Fake Call Simulator Button
                    IconButton(
                        onClick = { viewModel.toggleFakeCall(true) },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(NavySurfaceVariant)
                            .testTag("trigger_fake_call_button")
                    ) {
                        Icon(Icons.Default.PhoneCallback, contentDescription = "Fake Call Escape", tint = CyanSky)
                    }
                }
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
            // Active Countdown Mode OR Setup Mode
            if (walkState.isActive) {
                item {
                    ActiveWalkRadarCard(
                        remainingSeconds = walkState.remainingSeconds,
                        destination = walkState.destination,
                        isAlarmTriggered = walkState.isAlarmTriggered,
                        onOpenPinDialog = {
                            pinInput = ""
                            pinError = false
                            showPinDialog = true
                        }
                    )
                }

                // Escalation Protocol Banner
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                1.dp,
                                if (walkState.isAlarmTriggered) CrimsonAlert else NavyCardBorder,
                                RoundedCornerShape(16.dp)
                            ),
                        colors = CardDefaults.cardColors(containerColor = NavySurface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Security,
                                    contentDescription = null,
                                    tint = if (walkState.isAlarmTriggered) CrimsonAlert else EmeraldNeon
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (walkState.isAlarmTriggered)
                                        "ALARM TRIGGERED: SMS ESCALATION IN PROGRESS"
                                    else
                                        "Automated Escalation Protocol Standby",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (walkState.isAlarmTriggered) CrimsonAlert else TextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Emergency Contacts: ${walkState.trustedContact}\nIf countdown hits 0 without Safe PIN, local siren sounds and high-priority SMS with live GPS location broadcasts immediately.",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            } else {
                // Setup Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .border(1.dp, NavyCardBorder, RoundedCornerShape(22.dp)),
                        colors = CardDefaults.cardColors(containerColor = NavySurface)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Start Protected Campus Walk",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "Set an estimated walk duration. If you don't safely enter your PIN when arriving, alerts escalate automatically.",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                            )

                            // Quick duration pills
                            Text("Estimated Duration:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(5, 10, 15, 25).forEach { mins ->
                                    val isSelected = selectedWalkMinutes == mins
                                    Button(
                                        onClick = { selectedWalkMinutes = mins },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("walk_duration_${mins}m"),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isSelected) CyanSky else NavySurfaceVariant
                                        )
                                    ) {
                                        Text(
                                            text = "${mins}m",
                                            color = if (isSelected) NavyDeep else TextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            OutlinedTextField(
                                value = destinationInput,
                                onValueChange = { destinationInput = it },
                                label = { Text("Destination (e.g. Quad Hall B)") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_walk_destination")
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = {
                                    viewModel.startWalkTimer(selectedWalkMinutes, destinationInput)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("start_walk_timer_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon)
                            ) {
                                Icon(Icons.Default.DirectionsWalk, contentDescription = null, tint = NavyDeep)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Arm Walk Me Home ($selectedWalkMinutes min)",
                                    color = NavyDeep,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
            }

            // Campus Blue Light Emergency Callbox markers
            item {
                Text(
                    text = "Nearby Campus Emergency Blue Light Pillars",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            val pillars = listOf(
                Pair("Pillar #12 - North Library Path", "65 meters away • Active & Illuminated"),
                Pair("Pillar #15 - Science Quad Pavilion", "140 meters away • Video Monitored"),
                Pair("Pillar #08 - Engineering Archway", "220 meters away • 911 Direct Link")
            )

            items(pillars) { (name, dist) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, NavyCardBorder, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = NavySurface)
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
                                .background(CyanSky.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Emergency, contentDescription = null, tint = CyanSky, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                            Text(text = dist, fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            }

            // Persisted Local Safety Alerts History
            if (safetyAlerts.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Local Safety Alert History (${safetyAlerts.size})",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Room Encrypted",
                            fontSize = 11.sp,
                            color = EmeraldNeon
                        )
                    }
                }

                items(safetyAlerts, key = { it.id }) { alert ->
                    SafetyAlertItemCard(alert = alert)
                }
            }
        }
    }

    // PIN Dismissal Dialog
    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            containerColor = NavySurface,
            title = {
                Text("Safe Arrival Check-In", fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                Column {
                    Text(
                        "Enter your 4-digit Safe PIN (Default demo PIN: 1234) to confirm you reached your room safely.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = {
                            pinInput = it.take(4)
                            pinError = false
                        },
                        label = { Text("Safe PIN") },
                        isError = pinError,
                        supportingText = if (pinError) { { Text("Incorrect PIN. Enter 1234", color = CrimsonAlert) } } else null,
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_safe_pin")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val success = viewModel.dismissWalkWithPin(pinInput)
                        if (success) {
                            showPinDialog = false
                        } else {
                            pinError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon),
                    modifier = Modifier.testTag("confirm_pin_dismiss_button")
                ) {
                    Text("Confirm Safe Arrival", color = NavyDeep, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun ActiveWalkRadarCard(
    remainingSeconds: Int,
    destination: String,
    isAlarmTriggered: Boolean,
    onOpenPinDialog: () -> Unit
) {
    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(
                2.dp,
                if (isAlarmTriggered) CrimsonAlert else CyanSky,
                RoundedCornerShape(24.dp)
            )
            .testTag("active_walk_card"),
        colors = CardDefaults.cardColors(
            containerColor = if (isAlarmTriggered) CrimsonAlert.copy(alpha = 0.15f) else NavySurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Radar Icon with Pulsing Halo
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size((110 * pulseScale).dp)
                        .clip(CircleShape)
                        .background(
                            (if (isAlarmTriggered) CrimsonAlert else CyanSky).copy(alpha = 0.15f)
                        )
                )
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            if (isAlarmTriggered) CrimsonAlert else CyanSky
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.DirectionsWalk,
                        contentDescription = null,
                        tint = NavyDeep,
                        modifier = Modifier.size(42.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = timeFormatted,
                fontSize = 44.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isAlarmTriggered) CrimsonAlert else TextPrimary,
                letterSpacing = 2.sp
            )

            Text(
                text = "En route to $destination",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onOpenPinDialog,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("dismiss_safe_pin_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NavyDeep)
                Spacer(modifier = Modifier.width(8.dp))
                Text("I Arrived Safely (Enter PIN)", color = NavyDeep, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
fun FakeCallSimulationScreen(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .testTag("fake_call_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 48.dp)
            ) {
                Text(
                    text = "CAMPUS SAFELINE DISPATCH",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanSky,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Campus Safety Officer",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "Incoming Voice Call • +1 (217) 333-1212",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(36.dp))

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.LocalPolice,
                        contentDescription = null,
                        tint = CyanSky,
                        modifier = Modifier.size(54.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "\"Hey! Where are you? I'm waiting outside the building with the campus shuttle.\"",
                    fontSize = 14.sp,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Decline
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(CrimsonAlert)
                            .testTag("fake_call_decline")
                    ) {
                        Icon(Icons.Default.CallEnd, contentDescription = "Decline", tint = Color.White, modifier = Modifier.size(32.dp))
                    }

                    // Accept
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(EmeraldNeon)
                            .testTag("fake_call_accept")
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "Accept", tint = NavyDeep, modifier = Modifier.size(32.dp))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text("Tap either button to dismiss escape simulation", fontSize = 11.sp, color = TextMuted)
            }
        }
    }
}

@Composable
fun SafetyAlertItemCard(alert: com.example.data.model.SafetyAlert) {
    val (icon, color, label) = when (alert.alertType) {
        "SAFE_ARRIVAL" -> Triple(Icons.Default.VerifiedUser, EmeraldNeon, "Safe Arrival Confirmed")
        "EMERGENCY_ESCALATION" -> Triple(Icons.Default.Warning, CrimsonAlert, "Emergency Escalated")
        "FAKE_CALL_TRIGGERED" -> Triple(Icons.Default.PhoneCallback, CyanSky, "Situational Fake Call")
        else -> Triple(Icons.Default.Shield, AmberBright, alert.alertType)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            .testTag("safety_alert_card_${alert.id}"),
        colors = CardDefaults.cardColors(containerColor = NavySurface)
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
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = label, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                    Text(
                        text = if (alert.pinVerified) "PIN OK" else "ESCALATED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (alert.pinVerified) EmeraldNeon else CrimsonAlert
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "To: ${alert.destination} • Contact: ${alert.escalatedToContact}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                if (alert.notes.isNotBlank()) {
                    Text(
                        text = alert.notes,
                        fontSize = 10.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}
