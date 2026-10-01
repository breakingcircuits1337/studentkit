package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ProPaywallDialog
import com.example.ui.screens.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentKitApp(viewModel: StudentKitViewModel) {
    val currentDestination by viewModel.currentDestination.collectAsState()
    val notificationBanner by viewModel.notificationBanner.collectAsState()
    val showPaywall by viewModel.showPaywall.collectAsState()
    val isPro by viewModel.isProUser.collectAsState()

    var showLifestyleMenu by remember { mutableStateOf(false) }

    // Intercept hardware or gesture back button to pop sub-screens
    BackHandler(enabled = currentDestination != "DASHBOARD") {
        viewModel.navigateTo("DASHBOARD")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDeep)
    ) {
        Scaffold(
            containerColor = NavyDeep,
            bottomBar = {
                StudentKitBottomNav(
                    currentDestination = currentDestination,
                    onNavigate = { dest ->
                        if (dest == "MORE_MENU") {
                            showLifestyleMenu = true
                        } else {
                            viewModel.navigateTo(dest)
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                when (currentDestination) {
                    "DASHBOARD" -> DashboardScreen(
                        viewModel = viewModel,
                        onNavigate = { dest -> viewModel.navigateTo(dest) }
                    )
                    "CRUNCH" -> CrunchScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateTo("DASHBOARD") }
                    )
                    "COOLING_OFF" -> CoolingOffScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateTo("DASHBOARD") }
                    )
                    "WALK_SAFE" -> WalkMeHomeScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateTo("DASHBOARD") }
                    )
                    "TRIAL_SNIPER" -> TrialSniperScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateTo("DASHBOARD") }
                    )
                    "WRAPPED" -> WeekWrappedScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateTo("DASHBOARD") }
                    )
                    "QUEST_LOG" -> QuestLogScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateTo("DASHBOARD") }
                    )
                    "CAMPUS_TRADE" -> CampusTradeScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateTo("DASHBOARD") }
                    )
                    else -> DashboardScreen(
                        viewModel = viewModel,
                        onNavigate = { dest -> viewModel.navigateTo(dest) }
                    )
                }
            }
        }

        // Top Simulated Push Notification Banner (OneSignal / Alert)
        AnimatedVisibility(
            visible = notificationBanner != null,
            enter = slideInVertically(),
            exit = slideOutVertically(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 44.dp, start = 16.dp, end = 16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, CyanSky, RoundedCornerShape(16.dp))
                    .clickable { viewModel.dismissPushAlert() }
                    .testTag("push_notification_banner"),
                colors = CardDefaults.cardColors(containerColor = NavySurfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AmberBright.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = AmberBright, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "StudentKit Alert",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = notificationBanner ?: "",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 15.sp
                        )
                    }
                    IconButton(
                        onClick = { viewModel.dismissPushAlert() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = TextMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // Lifestyle More Modules Bottom Sheet
        if (showLifestyleMenu) {
            ModalBottomSheet(
                onDismissRequest = { showLifestyleMenu = false },
                containerColor = NavySurface,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "StudentKit Lifestyle Modules",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "Shipathon 2026 Integrated Campus Utilities",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    val items = listOf(
                        Quadruple("TRIAL_SNIPER", "TrialSniper", "48h/24h Free Trial Push Defense", Icons.Default.NotificationsActive, AmberBright),
                        Quadruple("WRAPPED", "Week Wrapped", "Spotify-Style Behavioral Analytics", Icons.Default.BarChart, VioletQuest),
                        Quadruple("QUEST_LOG", "QuestLog", "RPG Pomodoro & Class Progression", Icons.Default.Timer, CyanSky),
                        Quadruple("CAMPUS_TRADE", "CampusTrade", "Localized P2P Gigs & Textbooks", Icons.Default.SwapHoriz, EmeraldNeon)
                    )

                    items.forEach { (dest, title, desc, icon, color) ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.dp, NavyCardBorder, RoundedCornerShape(14.dp))
                                .clickable {
                                    showLifestyleMenu = false
                                    viewModel.navigateTo(dest)
                                }
                                .testTag("menu_item_${dest.lowercase()}"),
                            colors = CardDefaults.cardColors(containerColor = NavySurfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(color.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                                    Text(desc, fontSize = 11.sp, color = TextSecondary)
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Pro Paywall Dialog (RevenueCat simulation)
        ProPaywallDialog(
            isOpen = showPaywall,
            onDismiss = { viewModel.closePaywall() },
            onPurchaseSuccess = { viewModel.purchaseProSuccess() }
        )
    }
}

private data class Quadruple<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)

@Composable
fun StudentKitBottomNav(
    currentDestination: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = NavySurface,
        contentColor = TextPrimary,
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, NavyCardBorder, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .testTag("studentkit_bottom_navigation")
    ) {
        val navItems = listOf(
            Triple("DASHBOARD", "OS HUD", Icons.Default.Dashboard),
            Triple("CRUNCH", "Crunch", Icons.Default.DocumentScanner),
            Triple("COOLING_OFF", "Cooling Off", Icons.Default.ShoppingCartCheckout),
            Triple("WALK_SAFE", "Walk Safe", Icons.Default.Shield),
            Triple("MORE_MENU", "Modules", Icons.Default.Apps)
        )

        navItems.forEach { (dest, label, icon) ->
            val isSelected = currentDestination == dest || (dest == "MORE_MENU" && (currentDestination in listOf("TRIAL_SNIPER", "WRAPPED", "QUEST_LOG", "CAMPUS_TRADE")))
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(dest) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NavyDeep,
                    selectedTextColor = CyanSky,
                    indicatorColor = CyanSky,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                ),
                modifier = Modifier.testTag("nav_item_${dest.lowercase()}")
            )
        }
    }
}
