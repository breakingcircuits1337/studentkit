package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.StudentKitViewModel
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    viewModel: StudentKitViewModel,
    onNavigate: (String) -> Unit
) {
    val isPro by viewModel.isProUser.collectAsState()
    val deadlines by viewModel.deadlines.collectAsState()
    val holds by viewModel.holds.collectAsState()
    val trials by viewModel.trials.collectAsState()
    val walkState by viewModel.walkState.collectAsState()
    val quest by viewModel.questProfile.collectAsState()

    val nextDeadline = deadlines.firstOrNull { !it.isCompleted }
    val activeHolds = holds.filter { it.status == "LOCKED" }
    val urgentTrials = trials.filter { !it.isCancelled && it.daysRemaining <= 2 }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDeep)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top OS Branding & Status
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(EmeraldNeon)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SHIPATHON 2026 ARCHITECTURE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanSky,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "StudentKit OS",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                }

                // Pro Badge / Paywall Trigger
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isPro) VioletQuest.copy(alpha = 0.25f) else AmberGlow.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isPro) VioletQuest else AmberBright
                    ),
                    modifier = Modifier
                        .clickable { viewModel.openPaywall() }
                        .testTag("dashboard_pro_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isPro) Icons.Default.AutoAwesome else Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (isPro) VioletGlow else AmberBright,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isPro) "PRO ACTIVE" else "GET PRO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPro) VioletGlow else AmberBright
                        )
                    }
                }
            }
        }

        // Hero Cyber Campus Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .border(1.dp, NavyCardBorder, RoundedCornerShape(22.dp)),
                colors = CardDefaults.cardColors(containerColor = NavySurface)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_deck_1790811162340),
                        contentDescription = "StudentKit Command Center",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Gradient scrim
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, NavySurface.copy(alpha = 0.95f))
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Unified Campus Command",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Academic Clarity • Financial Shield • Personal Safety",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Emergency / Safety Quick Strip (Walk Me Home)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(
                        1.dp,
                        if (walkState.isActive) CrimsonAlert else NavyCardBorder,
                        RoundedCornerShape(18.dp)
                    )
                    .clickable { onNavigate("WALK_SAFE") }
                    .testTag("dashboard_walk_safe_card"),
                colors = CardDefaults.cardColors(
                    containerColor = if (walkState.isActive) CrimsonAlert.copy(alpha = 0.15f) else NavySurface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (walkState.isActive) CrimsonAlert else EmeraldNeon.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = if (walkState.isActive) Color.White else EmeraldNeon,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (walkState.isActive) "WALK ME HOME ACTIVE" else "Safety Perimeter: Armed",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (walkState.isActive) CrimsonAlert else TextPrimary
                        )
                        Text(
                            text = if (walkState.isActive)
                                "${walkState.remainingSeconds / 60}m ${walkState.remainingSeconds % 60}s left to destination"
                            else
                                "Tap to arm localized campus walk timer",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                }
            }
        }

        // 3 HERO MODULE TILES
        item {
            Text(
                text = "Hero Systems",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 0.5.sp
            )
        }

        // Hero 1: Crunch (Syllabus OCR)
        item {
            HeroFeatureCard(
                title = "Crunch",
                subtitle = "On-Device Google ML Kit Syllabus OCR",
                tagline = if (nextDeadline != null)
                    "Next: ${nextDeadline.courseCode} in ${nextDeadline.daysUntilDue}d • Start by ${nextDeadline.startByDaysBefore}d prior"
                else "No pending crunches",
                accentColor = CyanSky,
                icon = Icons.Default.DocumentScanner,
                stat = "${deadlines.count { !it.isCompleted }} Tasks",
                onClick = { onNavigate("CRUNCH") },
                testTag = "hero_crunch_tile"
            )
        }

        // Hero 2: Cooling Off (Impulse Shopping Shield)
        item {
            HeroFeatureCard(
                title = "Cooling Off",
                subtitle = "Impulse Shopping Shield & Wage Calculator",
                tagline = if (activeHolds.isNotEmpty())
                    "${activeHolds.size} active holds • Saved ${String.format("$%.0f", activeHolds.sumOf { it.price })} from impulse purchases"
                else "Wage: $15.50/hr • 48h Vault Ready",
                accentColor = EmeraldNeon,
                icon = Icons.Default.ShoppingCartCheckout,
                stat = "${activeHolds.size} Holds",
                onClick = { onNavigate("COOLING_OFF") },
                testTag = "hero_cooling_off_tile"
            )
        }

        // 4 LIFESTYLE MODULES GRID
        item {
            Text(
                text = "Lifestyle & Campus Modules",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // TrialSniper
                LifestyleCard(
                    modifier = Modifier.weight(1f),
                    title = "TrialSniper",
                    metric = if (urgentTrials.isNotEmpty()) "${urgentTrials.size} Expiring" else "${trials.size} Guarded",
                    caption = "48h/24h Push Alerts",
                    accentColor = AmberBright,
                    icon = Icons.Default.NotificationsActive,
                    onClick = { onNavigate("TRIAL_SNIPER") },
                    testTag = "lifestyle_trials_tile"
                )

                // Week Wrapped
                LifestyleCard(
                    modifier = Modifier.weight(1f),
                    title = "Week Wrapped",
                    metric = "34.5 hrs",
                    caption = "Spotify-Style Analytics",
                    accentColor = VioletQuest,
                    icon = Icons.Default.BarChart,
                    onClick = { onNavigate("WRAPPED") },
                    testTag = "lifestyle_wrapped_tile"
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // QuestLog
                LifestyleCard(
                    modifier = Modifier.weight(1f),
                    title = "QuestLog",
                    metric = "Lvl ${quest.level} ${quest.className.take(9)}",
                    caption = "${quest.streakDays}d Streak • Pomodoro",
                    accentColor = CyanElectric,
                    icon = Icons.Default.Timer,
                    onClick = { onNavigate("QUEST_LOG") },
                    testTag = "lifestyle_quest_tile"
                )

                // CampusTrade
                LifestyleCard(
                    modifier = Modifier.weight(1f),
                    title = "CampusTrade",
                    metric = "Engineering Quad",
                    caption = "P2P Gigs & Books",
                    accentColor = EmeraldGlow,
                    icon = Icons.Default.SwapHoriz,
                    onClick = { onNavigate("CAMPUS_TRADE") },
                    testTag = "lifestyle_trade_tile"
                )
            }
        }
    }
}

@Composable
fun HeroFeatureCard(
    title: String,
    subtitle: String,
    tagline: String,
    accentColor: Color,
    icon: ImageVector,
    stat: String,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, NavyCardBorder, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = NavySurface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextPrimary)
                        Text(text = subtitle, fontSize = 11.sp, color = TextSecondary)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = accentColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = stat,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = NavyCardBorder.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tagline,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun LifestyleCard(
    modifier: Modifier = Modifier,
    title: String,
    metric: String,
    caption: String,
    accentColor: Color,
    icon: ImageVector,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, NavyCardBorder, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = NavySurface)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(text = metric, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = accentColor)
            Text(text = caption, fontSize = 11.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}
