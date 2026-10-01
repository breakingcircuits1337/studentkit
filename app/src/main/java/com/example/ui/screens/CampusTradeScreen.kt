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
import com.example.data.model.CampusListing
import com.example.ui.StudentKitViewModel
import com.example.ui.theme.*

@Composable
fun CampusTradeScreen(
    viewModel: StudentKitViewModel,
    onBack: () -> Unit
) {
    val listings by viewModel.campusListings.collectAsState()

    var selectedCategory by remember { mutableStateOf("ALL") }
    var selectedQuad by remember { mutableStateOf("ALL") }
    var showPostDialog by remember { mutableStateOf(false) }
    var chatListingTarget by remember { mutableStateOf<CampusListing?>(null) }

    val filteredListings = remember(listings, selectedCategory, selectedQuad) {
        listings.filter { item ->
            val matchCat = if (selectedCategory == "ALL") true else item.category == selectedCategory
            val matchQuad = if (selectedQuad == "ALL") true else item.quadLocation.contains(selectedQuad, ignoreCase = true)
            matchCat && matchQuad
        }
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
                            modifier = Modifier.testTag("campus_trade_back_button")
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "CampusTrade",
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
                                        text = "PEER-TO-PEER",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldNeon,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Micro-Gigs & Student Marketplace",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    // Post Listing Button
                    Button(
                        onClick = { showPostDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("post_trade_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = NavyDeep, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Post", color = NavyDeep, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showPostDialog = true },
                containerColor = EmeraldNeon,
                contentColor = NavyDeep,
                modifier = Modifier.testTag("post_listing_fab")
            ) {
                Icon(Icons.Default.PostAdd, contentDescription = "Post Item")
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
            // Category Chips
            item {
                val categories = listOf("ALL", "TEXTBOOK", "MICRO_GIG", "TECH", "DORM")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.replace("_", " "), fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldNeon.copy(alpha = 0.2f),
                                selectedLabelColor = EmeraldNeon,
                                containerColor = NavySurface,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                selectedBorderColor = EmeraldNeon,
                                borderColor = NavyCardBorder
                            ),
                            modifier = Modifier.testTag("filter_cat_$cat")
                        )
                    }
                }
            }

            // Campus Quad Location Selector
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("ALL", "Engineering", "North", "West", "Union").forEach { quad ->
                        val isSelected = selectedQuad == quad
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) CyanSky.copy(alpha = 0.2f) else NavySurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) CyanSky else NavyCardBorder
                            ),
                            modifier = Modifier
                                .clickable { selectedQuad = quad }
                                .testTag("quad_filter_$quad")
                        ) {
                            Text(
                                text = if (quad == "ALL") "All Quads" else "$quad Quad",
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) CyanSky else TextSecondary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Listings Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Campus Bulletin (${filteredListings.size})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Zero Middleman Fees",
                        fontSize = 11.sp,
                        color = EmeraldNeon
                    )
                }
            }

            // Items
            items(filteredListings, key = { it.id }) { listing ->
                CampusListingCard(
                    listing = listing,
                    onContactSeller = { chatListingTarget = listing }
                )
            }
        }
    }

    // Direct Seller Chat Simulation Dialog
    if (chatListingTarget != null) {
        val target = chatListingTarget!!
        var messageInput by remember { mutableStateOf("Hi ${target.sellerName.split(" ").first()}, is this still available to meet on campus?") }

        AlertDialog(
            onDismissRequest = { chatListingTarget = null },
            containerColor = NavySurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Chat, contentDescription = null, tint = CyanSky)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Chat with ${target.sellerName}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Regarding: ${target.title} ($${String.format("%.2f", target.price)})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EmeraldNeon
                    )
                    OutlinedTextField(
                        value = messageInput,
                        onValueChange = { messageInput = it },
                        label = { Text("Message") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_seller_chat")
                    )
                    Text(
                        text = "Safe meetups recommended at campus library or dining halls.",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.showPushAlert("Message sent to ${target.sellerName}! They usually reply within 15 minutes.")
                        chatListingTarget = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanSky),
                    modifier = Modifier.testTag("send_chat_button")
                ) {
                    Text("Send Message", color = NavyDeep, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { chatListingTarget = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // Post New Listing Dialog
    if (showPostDialog) {
        var titleInput by remember { mutableStateOf("") }
        var priceInput by remember { mutableStateOf("25.00") }
        var categoryInput by remember { mutableStateOf("TEXTBOOK") }
        var quadInput by remember { mutableStateOf("Engineering Quad") }
        var descInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showPostDialog = false },
            containerColor = NavySurface,
            title = { Text("Post to CampusTrade", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("Title / Gig description") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_trade_title")
                    )
                    OutlinedTextField(
                        value = priceInput,
                        onValueChange = { priceInput = it },
                        label = { Text("Price ($)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_trade_price")
                    )
                    OutlinedTextField(
                        value = quadInput,
                        onValueChange = { quadInput = it },
                        label = { Text("Campus Quad / Dorm pickup") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_trade_quad")
                    )
                    OutlinedTextField(
                        value = descInput,
                        onValueChange = { descInput = it },
                        label = { Text("Details (condition, time, etc.)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_trade_desc")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val p = priceInput.toDoubleOrNull() ?: 20.0
                        viewModel.postCampusListing(titleInput, p, categoryInput, quadInput, descInput)
                        showPostDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon),
                    modifier = Modifier.testTag("confirm_post_trade_button")
                ) {
                    Text("Publish to Campus", color = NavyDeep, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPostDialog = false }) { Text("Cancel", color = TextSecondary) }
            }
        )
    }
}

@Composable
fun CampusListingCard(
    listing: CampusListing,
    onContactSeller: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, NavyCardBorder, RoundedCornerShape(18.dp))
            .testTag("campus_listing_${listing.id}"),
        colors = CardDefaults.cardColors(containerColor = NavySurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (listing.category == "MICRO_GIG") AmberBright.copy(alpha = 0.2f) else CyanSky.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = listing.category.replace("_", " "),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (listing.category == "MICRO_GIG") AmberBright else CyanSky,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = "$${String.format("%.2f", listing.price)}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = EmeraldNeon
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = listing.title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = listing.description,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = NavyCardBorder.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "📍 ${listing.quadLocation}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Seller: ${listing.sellerName} • ${listing.timeAgo}",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }

                Button(
                    onClick = onContactSeller,
                    colors = ButtonDefaults.buttonColors(containerColor = NavySurfaceVariant),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("contact_seller_${listing.id}")
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, tint = CyanSky, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Chat", color = CyanSky, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
