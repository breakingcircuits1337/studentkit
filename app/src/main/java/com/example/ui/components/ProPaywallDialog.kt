package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*

@Composable
fun ProPaywallDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onPurchaseSuccess: () -> Unit
) {
    if (!isOpen) return

    var isAnnualSelected by remember { mutableStateOf(true) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .border(
                        1.dp,
                        Brush.verticalGradient(listOf(CyanSky, VioletQuest, Color.Transparent)),
                        RoundedCornerShape(28.dp)
                    )
                    .testTag("paywall_card"),
                colors = CardDefaults.cardColors(containerColor = NavySurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Close button top right
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = VioletQuest.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, VioletQuest.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AmberBright, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "SHIPATHON PRO TIER",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberBright
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("close_paywall_button")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "StudentKit Pro",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "The Autonomous Operating System for Campus Life",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Feature List
                    val features = listOf(
                        "Unlimited On-Device Google ML Kit Syllabus OCR Scans",
                        "Campus Police & Trusted SMS Dispatch for Walk Me Home",
                        "48-Hour Impulsive Shopping Vault & Wage Multiplier",
                        "TrialSniper Automated 48h & 24h Push Defense",
                        "Spotify-Style Week Wrapped Behavioral Story Export",
                        "RPG QuestLog Mythic Classes & Double XP Streaks"
                    )

                    features.forEach { feature ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldNeon.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = EmeraldNeon,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = feature,
                                fontSize = 12.sp,
                                color = TextPrimary,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Plan Selector (Annual vs Monthly)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Annual Card
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { isAnnualSelected = true }
                                .border(
                                    2.dp,
                                    if (isAnnualSelected) CyanSky else NavyCardBorder,
                                    RoundedCornerShape(16.dp)
                                )
                                .testTag("plan_annual_button"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isAnnualSelected) NavySurfaceVariant else NavyDeep
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmeraldNeon.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        "SAVE 50%",
                                        color = EmeraldNeon,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Annual", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 15.sp)
                                Text("$2.49/mo", fontWeight = FontWeight.ExtraBold, color = CyanSky, fontSize = 18.sp)
                                Text("Billed $29.99/yr", fontSize = 11.sp, color = TextSecondary)
                            }
                        }

                        // Monthly Card
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { isAnnualSelected = false }
                                .border(
                                    2.dp,
                                    if (!isAnnualSelected) CyanSky else NavyCardBorder,
                                    RoundedCornerShape(16.dp)
                                )
                                .testTag("plan_monthly_button"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (!isAnnualSelected) NavySurfaceVariant else NavyDeep
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Flexible", fontSize = 10.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Monthly", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 15.sp)
                                Text("$4.99/mo", fontWeight = FontWeight.ExtraBold, color = TextPrimary, fontSize = 18.sp)
                                Text("Cancel anytime", fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Purchase CTA Button
                    Button(
                        onClick = onPurchaseSuccess,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("subscribe_pro_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyanSky
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = NavyDeep)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAnnualSelected) "Start 7-Day Free Trial" else "Unlock StudentKit Pro",
                                color = NavyDeep,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Powered by RevenueCat SDK • Privacy-First On-Device • Cancel in Play Store",
                        fontSize = 10.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
