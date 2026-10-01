package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
fun WeekWrappedScreen(
    viewModel: StudentKitViewModel,
    onBack: () -> Unit
) {
    val wrapped by viewModel.weekWrapped.collectAsState()
    var currentCardIndex by remember { mutableStateOf(0) }
    val totalCards = 5

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Story Progress Bars
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (i in 0 until totalCards) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(3.dp)
                                .clip(CircleShape)
                                .background(
                                    if (i <= currentCardIndex) Color.White else Color.White.copy(alpha = 0.25f)
                                )
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("wrapped_close_button")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                        Text(
                            text = "WEEK WRAPPED 2026",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanSky,
                            letterSpacing = 1.5.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = VioletQuest.copy(alpha = 0.3f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VioletQuest)
                    ) {
                        Text(
                            text = "ON-DEVICE USAGESTATS",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = VioletGlow,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Animated Card Transition
            AnimatedContent(
                targetState = currentCardIndex,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "wrapped_story"
            ) { cardIdx ->
                when (cardIdx) {
                    0 -> IntroStoryCard(wrapped = wrapped)
                    1 -> StudyHoursStoryCard(wrapped = wrapped)
                    2 -> DoomscrollRatioStoryCard(wrapped = wrapped)
                    3 -> CampusHabitatStoryCard(wrapped = wrapped)
                    else -> ArchetypeShareableStoryCard(
                        wrapped = wrapped,
                        onShare = {
                            viewModel.showPushAlert("Exported Week Wrapped card to clipboard & social story!")
                        }
                    )
                }
            }

            // Left / Right invisible tap zones to step through stories
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (currentCardIndex > 0) currentCardIndex--
                        }
                        .testTag("story_tap_prev")
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (currentCardIndex < totalCards - 1) currentCardIndex++ else onBack()
                        }
                        .testTag("story_tap_next")
                )
            }
        }
    }
}

@Composable
fun IntroStoryCard(wrapped: com.example.data.model.WeekWrappedData) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF4C1D95), Color(0xFF0F172A), Color.Black),
                    radius = 1200f
                )
            )
            .padding(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "⚡ SHIPATHON 2026 ⚡",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AmberBright,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Ready to see how you ruled campus?",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 40.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "We analyzed your localized Android UsageStats, Pomodoro streaks, and impulse holds without sending a single byte of telemetry off your device.",
                fontSize = 14.sp,
                color = Color.LightGray,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(36.dp))
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.4f))
            ) {
                Text(
                    text = "Tap to view your stories →",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }
        }
    }
}

@Composable
fun StudyHoursStoryCard(wrapped: com.example.data.model.WeekWrappedData) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF064E3B), Color(0xFF065F46), Color.Black)
                )
            )
            .padding(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "ACADEMIC IMMERSION",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = EmeraldGlow,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "${wrapped.totalStudyHours}",
                fontSize = 72.sp,
                fontWeight = FontWeight.Black,
                color = EmeraldNeon
            )
            Text(
                text = "Hours of Pure Study Flow",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "That's 6.2 hours higher than the average college student. Your focus stamina in CS 301 and Biology was exceptional this week.",
                fontSize = 14.sp,
                color = Color.LightGray,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun DoomscrollRatioStoryCard(wrapped: com.example.data.model.WeekWrappedData) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0284C7), Color(0xFF0F172A), Color.Black)
                )
            )
            .padding(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "DOOMSCROLL SHIELD",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = CyanSky,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "${wrapped.focusToDoomscrollRatio}x",
                fontSize = 68.sp,
                fontWeight = FontWeight.Black,
                color = CyanSky
            )
            Text(
                text = "Focus to Doomscroll Ratio",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "For every 15 minutes of social scrolling, you logged over an hour of deep work. You reclaimed +${wrapped.screenTimeSavedHours} hours this week!",
                fontSize = 14.sp,
                color = Color.LightGray,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun CampusHabitatStoryCard(wrapped: com.example.data.model.WeekWrappedData) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF7C2D12), Color(0xFF1E293B), Color.Black)
                )
            )
            .padding(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "YOUR HABITAT & CLOCK",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = AmberBright,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Icon(Icons.Default.MenuBook, contentDescription = null, tint = AmberBright, modifier = Modifier.size(52.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = wrapped.topLocation,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AmberBright.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, AmberBright.copy(alpha = 0.5f))
            ) {
                Text(
                    text = "PEAK FOCUS WINDOW: ${wrapped.peakProductivityWindow}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AmberBright,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun ArchetypeShareableStoryCard(
    wrapped: com.example.data.model.WeekWrappedData,
    onShare: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF581C87), Color(0xFF0F172A), Color.Black)
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .border(2.dp, VioletGlow, RoundedCornerShape(26.dp))
                .testTag("wrapped_shareable_summary_card"),
            colors = CardDefaults.cardColors(containerColor = NavySurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "STUDENTKIT ARCHETYPE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = VioletGlow,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = wrapped.studentArchetype,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = wrapped.campusPercentile,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AmberBright,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = NavyCardBorder)
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${wrapped.totalStudyHours}h", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = EmeraldNeon)
                        Text("Study Flow", fontSize = 11.sp, color = TextSecondary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$${String.format("%.0f", wrapped.impulseMoneySaved)}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AmberBright)
                        Text("Impulse Saved", fontSize = 11.sp, color = TextSecondary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${wrapped.questsCompleted}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = CyanSky)
                        Text("Quests Cleared", fontSize = 11.sp, color = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onShare,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("share_wrapped_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VioletQuest)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share Student Story Card", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
