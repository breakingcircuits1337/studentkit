package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.StudentKitViewModel
import com.example.ui.theme.*

@Composable
fun QuestLogScreen(
    viewModel: StudentKitViewModel,
    onBack: () -> Unit
) {
    val quest by viewModel.questProfile.collectAsState()
    var showClassDialog by remember { mutableStateOf(false) }

    val minutes = quest.timerSecondsRemaining / 60
    val seconds = quest.timerSecondsRemaining % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)
    val totalSecondsForMode = if (quest.timerMode == "FOCUS") 25 * 60 else 5 * 60
    val progress = (1f - (quest.timerSecondsRemaining.toFloat() / totalSecondsForMode)).coerceIn(0f, 1f)

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
                            modifier = Modifier.testTag("quest_back_button")
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "QuestLog",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = VioletQuest.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "RPG POMODORO",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = VioletGlow,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Gamified Study Streaks & Progression",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    // Class badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = NavySurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NavyCardBorder),
                        modifier = Modifier
                            .clickable { showClassDialog = true }
                            .testTag("change_class_chip")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.School, contentDescription = null, tint = VioletGlow, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(quest.className, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
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
            // RPG Hero Profile Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .border(1.dp, VioletQuest.copy(alpha = 0.4f), RoundedCornerShape(22.dp))
                        .testTag("quest_profile_card"),
                    colors = CardDefaults.cardColors(containerColor = NavySurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Hero Avatar
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .border(2.dp, VioletGlow, CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_quest_avatar_1790811174182),
                                contentDescription = "Student Hero Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Level ${quest.level} ${quest.className}",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${quest.streakDays}d Streak 🔥",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberBright
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // XP Progress Bar
                            val xpRatio = (quest.currentXp.toFloat() / quest.xpToNextLevel).coerceIn(0f, 1f)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("XP ${quest.currentXp} / ${quest.xpToNextLevel}", fontSize = 11.sp, color = TextSecondary)
                                Text("${(xpRatio * 100).toInt()}%", fontSize = 11.sp, color = VioletGlow, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { xpRatio },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape),
                                color = VioletGlow,
                                trackColor = NavySurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Mana: ${quest.manaPoints}/100 • Total Flow: ${String.format("%.1f", quest.totalStudyHours)}h",
                                fontSize = 11.sp,
                                color = CyanSky
                            )
                        }
                    }
                }
            }

            // Gamified Pomodoro Timer Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, CyanSky.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                        .testTag("pomodoro_card"),
                    colors = CardDefaults.cardColors(containerColor = NavySurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Mode Switcher (Focus 25, Break 5)
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(NavySurfaceVariant)
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf(
                                Pair("FOCUS", "25m Study"),
                                Pair("SHORT_BREAK", "5m Break")
                            ).forEach { (mode, label) ->
                                val isSelected = quest.timerMode == mode
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) CyanSky else Color.Transparent,
                                    modifier = Modifier
                                        .clickable {
                                            viewModel.resetPomodoro(
                                                minutes = if (mode == "FOCUS") 25 else 5,
                                                mode = mode
                                            )
                                        }
                                        .testTag("mode_${mode.lowercase()}")
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) NavyDeep else TextSecondary,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Large Digital Timer
                        Text(
                            text = formattedTime,
                            fontSize = 54.sp,
                            fontWeight = FontWeight.Black,
                            color = if (quest.isTimerRunning) CyanSky else TextPrimary,
                            letterSpacing = 2.sp
                        )

                        Text(
                            text = if (quest.isTimerRunning) "QUEST IN PROGRESS (+250 XP at completion)" else "READY FOR POMODORO RUN",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (quest.isTimerRunning) EmeraldNeon else TextSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Timer Controls
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { viewModel.resetPomodoro(25, "FOCUS") },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(NavySurfaceVariant)
                                    .testTag("reset_pomodoro_button")
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = TextSecondary)
                            }

                            Button(
                                onClick = { viewModel.togglePomodoroTimer() },
                                modifier = Modifier
                                    .height(56.dp)
                                    .widthIn(min = 160.dp)
                                    .testTag("start_pause_pomodoro_button"),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (quest.isTimerRunning) AmberBright else CyanSky
                                )
                            ) {
                                Icon(
                                    imageVector = if (quest.isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = NavyDeep
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (quest.isTimerRunning) "Pause Quest" else "Start Study Run",
                                    color = NavyDeep,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
            }

            // Ambient Soundscapes
            item {
                Text(
                    text = "Background Focus Soundscapes",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            item {
                val sounds = listOf("Cyber Lo-Fi", "Library Rain", "Brown Noise", "Campus Cafe")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    sounds.forEach { sound ->
                        val isSelected = quest.ambientSound == sound
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setAmbientSound(sound) },
                            label = { Text(sound, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = VioletQuest.copy(alpha = 0.2f),
                                selectedLabelColor = VioletGlow,
                                containerColor = NavySurface,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                selectedBorderColor = VioletQuest,
                                borderColor = NavyCardBorder
                            ),
                            modifier = Modifier.testTag("ambient_${sound.replace(" ", "_")}")
                        )
                    }
                }
            }

            // Equipped Perks & Inventory
            item {
                Text(
                    text = "Equipped Study Perks & Relics",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            val perks = listOf(
                Triple("Elixir of Synthesis", "+15% Pomodoro XP Multiplier", Icons.Default.Science),
                Triple("Noise-Cancelling Helm", "Reduces distraction penalties by 40%", Icons.Default.Headphones),
                Triple("Nocturnal Cloak", "2x Streak protection after 10:00 PM", Icons.Default.Nightlight)
            )

            items(perks) { (name, effect, icon) ->
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
                                .background(VioletQuest.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, contentDescription = null, tint = VioletGlow, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                            Text(text = effect, fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }
    }

    // Change Class Dialog
    if (showClassDialog) {
        val classes = listOf("Code Mage", "Bio Alchemist", "Pre-Law Paladin", "Design Artificer", "Quant Necromancer")
        AlertDialog(
            onDismissRequest = { showClassDialog = false },
            containerColor = NavySurface,
            title = { Text("Choose Student RPG Class", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    classes.forEach { cls ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (cls == quest.className) VioletQuest.copy(alpha = 0.2f) else NavySurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (cls == quest.className) VioletQuest else NavyCardBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.changeQuestClass(cls)
                                    showClassDialog = false
                                }
                        ) {
                            Text(
                                text = cls,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showClassDialog = false }) { Text("Close", color = TextSecondary) }
            }
        )
    }
}
