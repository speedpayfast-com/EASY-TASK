package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CacheMetadataEntity
import com.example.data.model.TaskSubmissionEntity
import com.example.data.model.TransactionLogEntity
import com.example.ui.components.OfflineStatusBanner
import com.example.ui.components.PaymentMethodBadge
import com.example.ui.components.RoomCacheBadge
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AmberGold
import com.example.ui.theme.AmberLight
import com.example.ui.theme.BkashPink
import com.example.ui.theme.BkashPinkLight
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.GeoBackground
import com.example.ui.theme.GeoBorder
import com.example.ui.theme.GeoCard
import com.example.ui.theme.GeoCommissionContainer
import com.example.ui.theme.GeoOnCommission
import com.example.ui.theme.GeoOnPrimaryContainer
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryContainer
import com.example.ui.theme.GeoSurfaceContainer
import com.example.ui.theme.GeoSurfaceContainerHigh
import com.example.ui.theme.GeoSurfaceContainerLow
import com.example.ui.theme.GeoTextMuted
import com.example.ui.theme.GeoTextPrimary
import com.example.ui.theme.GeoTextSecondary
import com.example.ui.theme.NagadOrange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    transactionLogs: List<TransactionLogEntity>,
    submissions: List<TaskSubmissionEntity> = emptyList(),
    isOffline: Boolean = false,
    cacheMetadata: CacheMetadataEntity? = null,
    onToggleOffline: () -> Unit = {},
    onSync: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Primary view toggle: "TRANSACTIONS" or "TASKS"
    var activeViewSection by remember { mutableStateOf("TRANSACTIONS") }
    var categoryFilter by remember { mutableStateOf("ALL") }
    var taskStatusFilter by remember { mutableStateOf("ALL") }
    var methodFilter by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // Aggregate statistics from Room database logs
    val totalCommissionEarned = transactionLogs
        .filter { it.category == "COMMISSION" || it.type == "COMMISSION_EARNED" }
        .filter { it.status == "COMPLETED" }
        .sumOf { it.commissionAmount }

    val totalPendingAmount = transactionLogs
        .filter { it.status == "PENDING" }
        .sumOf { it.amount }

    val totalApprovedTaskVolume = transactionLogs
        .filter { it.category == "TASK" && it.status == "COMPLETED" }
        .sumOf { it.amount }

    val pendingCount = transactionLogs.count { it.status == "PENDING" }
    val commissionCount = transactionLogs.count { it.category == "COMMISSION" || it.type == "COMMISSION_EARNED" }
    val taskCount = transactionLogs.count { it.category == "TASK" }
    val withdrawalCount = transactionLogs.count { it.category == "WITHDRAWAL" }

    // Task submission statistics
    val pendingSubmissionsCount = submissions.count { it.status == "PENDING" }
    val approvedSubmissionsCount = submissions.count { it.status == "APPROVED" }
    val rejectedSubmissionsCount = submissions.count { it.status == "REJECTED" }

    // Filtered Transaction Logs
    val filteredLogs = transactionLogs.filter { log ->
        val matchesCategory = when (categoryFilter) {
            "TASKS" -> log.category == "TASK"
            "PENDING" -> log.status == "PENDING"
            "COMMISSION" -> log.category == "COMMISSION" || log.type == "COMMISSION_EARNED"
            "WITHDRAWALS" -> log.category == "WITHDRAWAL"
            else -> true
        }

        val matchesMethod = when (methodFilter) {
            "BKASH" -> log.method.equals("BKASH", ignoreCase = true)
            "NAGAD" -> log.method.equals("NAGAD", ignoreCase = true)
            else -> true
        }

        val matchesSearch = if (searchQuery.isBlank()) true else {
            log.title.contains(searchQuery, ignoreCase = true) ||
                    log.description.contains(searchQuery, ignoreCase = true) ||
                    (log.trxId?.contains(searchQuery, ignoreCase = true) == true) ||
                    (log.senderNumber?.contains(searchQuery, ignoreCase = true) == true) ||
                    (log.targetNumber?.contains(searchQuery, ignoreCase = true) == true)
        }

        matchesCategory && matchesMethod && matchesSearch
    }

    // Filtered Task Submissions
    val filteredSubmissions = submissions.filter { sub ->
        val matchesStatus = when (taskStatusFilter) {
            "PENDING" -> sub.status == "PENDING"
            "APPROVED" -> sub.status == "APPROVED"
            "REJECTED" -> sub.status == "REJECTED"
            else -> true
        }

        val matchesMethod = when (methodFilter) {
            "BKASH" -> sub.method.equals("BKASH", ignoreCase = true)
            "NAGAD" -> sub.method.equals("NAGAD", ignoreCase = true)
            else -> true
        }

        val matchesSearch = if (searchQuery.isBlank()) true else {
            sub.taskTitle.contains(searchQuery, ignoreCase = true) ||
                    sub.trxId.contains(searchQuery, ignoreCase = true) ||
                    sub.senderNumber.contains(searchQuery, ignoreCase = true) ||
                    sub.status.contains(searchQuery, ignoreCase = true)
        }

        matchesStatus && matchesMethod && matchesSearch
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GeoBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // --- Offline Status Alert Banner ---
        item {
            OfflineStatusBanner(
                isOffline = isOffline,
                cachedTransactionsCount = transactionLogs.size,
                cachedTasksCount = submissions.size,
                lastSyncTimestamp = cacheMetadata?.lastSyncTimestamp,
                onToggleOffline = onToggleOffline,
                onSync = onSync
            )
        }

        // --- Screen Title & Room Database Local Storage Sync Indicator ---
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (activeViewSection == "TRANSACTIONS") "Transaction History" else "Task Statuses",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoTextPrimary,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = if (isOffline) "Offline Room Cache • Local SQLite Storage" else "Live Room DB • Auto-synchronized",
                            fontSize = 12.sp,
                            color = if (isOffline) AmberGold else GeoTextSecondary,
                            fontWeight = if (isOffline) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }

                    // Room Local Database Indicator Badge
                    Surface(
                        color = GeoSurfaceContainerHigh,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, GeoBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storage,
                                contentDescription = "Local Room DB",
                                modifier = Modifier.size(12.dp),
                                tint = if (isOffline) AmberGold else GeoPrimary
                            )
                            Text(
                                text = "Room DB (${transactionLogs.size} txns, ${submissions.size} tasks)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isOffline) AmberGold else GeoPrimary
                            )
                        }
                    }
                }
            }
        }

        // --- View Section Segmented Switcher (Transaction History vs Task Statuses) ---
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = GeoSurfaceContainerHigh,
                border = BorderStroke(1.dp, GeoBorder)
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Tab 1: Transaction History
                    Surface(
                        onClick = { activeViewSection = "TRANSACTIONS" },
                        shape = RoundedCornerShape(10.dp),
                        color = if (activeViewSection == "TRANSACTIONS") GeoCard else Color.Transparent,
                        border = if (activeViewSection == "TRANSACTIONS") BorderStroke(1.dp, GeoBorder) else null,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tab_transaction_history")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (activeViewSection == "TRANSACTIONS") GeoPrimary else GeoTextSecondary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Transactions (${transactionLogs.size})",
                                fontSize = 12.sp,
                                fontWeight = if (activeViewSection == "TRANSACTIONS") FontWeight.Bold else FontWeight.Medium,
                                color = if (activeViewSection == "TRANSACTIONS") GeoTextPrimary else GeoTextSecondary
                            )
                        }
                    }

                    // Tab 2: Task Statuses
                    Surface(
                        onClick = { activeViewSection = "TASKS" },
                        shape = RoundedCornerShape(10.dp),
                        color = if (activeViewSection == "TASKS") GeoCard else Color.Transparent,
                        border = if (activeViewSection == "TASKS") BorderStroke(1.dp, GeoBorder) else null,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tab_task_statuses")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.TaskAlt,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (activeViewSection == "TASKS") GeoPrimary else GeoTextSecondary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Task Statuses (${submissions.size})",
                                fontSize = 12.sp,
                                fontWeight = if (activeViewSection == "TASKS") FontWeight.Bold else FontWeight.Medium,
                                color = if (activeViewSection == "TASKS") GeoTextPrimary else GeoTextSecondary
                            )
                        }
                    }
                }
            }
        }

        // --- Real-Time Instant Search Bar ---
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("history_search_input"),
                placeholder = {
                    Text(
                        text = if (activeViewSection == "TRANSACTIONS") "Search transactions by TrxID, title, phone..." else "Search tasks by title, TrxID, status...",
                        fontSize = 12.sp,
                        color = GeoTextSecondary
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = GeoTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = GeoTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GeoPrimary,
                    unfocusedBorderColor = GeoBorder,
                    focusedContainerColor = GeoCard,
                    unfocusedContainerColor = GeoCard
                )
            )
        }

        // --- Metrics Overview Cards ---
        item {
            if (activeViewSection == "TRANSACTIONS") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Card 1: 4% Commission Earned
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("metric_commission_earned"),
                        colors = CardDefaults.cardColors(containerColor = GeoCommissionContainer),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, GeoBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Commission",
                                    fontSize = 11.sp,
                                    color = GeoOnCommission,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(GeoOnCommission.copy(alpha = 0.12f))
                                        .padding(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Stars,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp),
                                        tint = GeoOnCommission
                                    )
                                }
                            }
                            Text(
                                text = "+৳${String.format("%.1f", totalCommissionEarned)}",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeoOnCommission
                            )
                            Text(
                                text = "4% rate + bonuses",
                                fontSize = 9.sp,
                                color = GeoOnCommission.copy(alpha = 0.8f)
                            )
                        }
                    }

                    // Card 2: Pending Payments
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("metric_pending_payments"),
                        colors = CardDefaults.cardColors(containerColor = AmberLight),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Pending",
                                    fontSize = 11.sp,
                                    color = AmberGold,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(AmberGold.copy(alpha = 0.15f))
                                        .padding(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.HourglassEmpty,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp),
                                        tint = AmberGold
                                    )
                                }
                            }
                            Text(
                                text = "৳${totalPendingAmount.toInt()}",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberGold
                            )
                            Text(
                                text = "$pendingCount awaiting review",
                                fontSize = 9.sp,
                                color = AmberGold
                            )
                        }
                    }

                    // Card 3: Completed Tasks
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("metric_completed_tasks"),
                        colors = CardDefaults.cardColors(containerColor = EmeraldLight),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Completed",
                                    fontSize = 11.sp,
                                    color = EmeraldGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(EmeraldGreen.copy(alpha = 0.15f))
                                        .padding(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp),
                                        tint = EmeraldGreen
                                    )
                                }
                            }
                            Text(
                                text = "৳${totalApprovedTaskVolume.toInt()}",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldGreen
                            )
                            Text(
                                text = "Verified tasks",
                                fontSize = 9.sp,
                                color = EmeraldGreen
                            )
                        }
                    }
                }
            } else {
                // Task Status Overview Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Pending Submissions
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("metric_tasks_pending"),
                        colors = CardDefaults.cardColors(containerColor = AmberLight),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Pending", fontSize = 11.sp, color = AmberGold, fontWeight = FontWeight.SemiBold)
                                Icon(Icons.Default.HourglassEmpty, null, modifier = Modifier.size(14.dp), tint = AmberGold)
                            }
                            Text("$pendingSubmissionsCount", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AmberGold)
                            Text("In Admin Queue", fontSize = 9.sp, color = AmberGold)
                        }
                    }

                    // Approved Submissions
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("metric_tasks_approved"),
                        colors = CardDefaults.cardColors(containerColor = EmeraldLight),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Approved", fontSize = 11.sp, color = EmeraldGreen, fontWeight = FontWeight.SemiBold)
                                Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(14.dp), tint = EmeraldGreen)
                            }
                            Text("$approvedSubmissionsCount", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                            Text("4% Paid", fontSize = 9.sp, color = EmeraldGreen)
                        }
                    }

                    // Rejected Submissions
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("metric_tasks_rejected"),
                        colors = CardDefaults.cardColors(containerColor = GeoSurfaceContainerHigh),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, GeoBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Rejected", fontSize = 11.sp, color = Color(0xFFD32F2F), fontWeight = FontWeight.SemiBold)
                                Icon(Icons.Default.Cancel, null, modifier = Modifier.size(14.dp), tint = Color(0xFFD32F2F))
                            }
                            Text("$rejectedSubmissionsCount", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                            Text("Refunded", fontSize = 9.sp, color = GeoTextSecondary)
                        }
                    }
                }
            }
        }

        // --- Category / Status Filter Chips ---
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (activeViewSection == "TRANSACTIONS") {
                    FilterChip(
                        selected = categoryFilter == "ALL",
                        onClick = { categoryFilter = "ALL" },
                        shape = RoundedCornerShape(12.dp),
                        label = { Text("All (${transactionLogs.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GeoPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = GeoSurfaceContainerHigh,
                            labelColor = GeoTextPrimary
                        ),
                        modifier = Modifier.testTag("filter_all")
                    )

                    FilterChip(
                        selected = categoryFilter == "TASKS",
                        onClick = { categoryFilter = "TASKS" },
                        shape = RoundedCornerShape(12.dp),
                        label = { Text("Tasks ($taskCount)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GeoPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = GeoSurfaceContainerHigh,
                            labelColor = GeoTextPrimary
                        ),
                        modifier = Modifier.testTag("filter_tasks")
                    )

                    FilterChip(
                        selected = categoryFilter == "PENDING",
                        onClick = { categoryFilter = "PENDING" },
                        shape = RoundedCornerShape(12.dp),
                        label = { Text("Pending ($pendingCount)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AmberGold,
                            selectedLabelColor = Color.White,
                            containerColor = GeoSurfaceContainerHigh,
                            labelColor = GeoTextPrimary
                        ),
                        modifier = Modifier.testTag("filter_pending")
                    )

                    FilterChip(
                        selected = categoryFilter == "COMMISSION",
                        onClick = { categoryFilter = "COMMISSION" },
                        shape = RoundedCornerShape(12.dp),
                        label = { Text("Commission (4%) ($commissionCount)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BkashPink,
                            selectedLabelColor = Color.White,
                            containerColor = GeoSurfaceContainerHigh,
                            labelColor = GeoTextPrimary
                        ),
                        modifier = Modifier.testTag("filter_commission")
                    )

                    FilterChip(
                        selected = categoryFilter == "WITHDRAWALS",
                        onClick = { categoryFilter = "WITHDRAWALS" },
                        shape = RoundedCornerShape(12.dp),
                        label = { Text("Payouts ($withdrawalCount)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GeoPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = GeoSurfaceContainerHigh,
                            labelColor = GeoTextPrimary
                        ),
                        modifier = Modifier.testTag("filter_withdrawals")
                    )
                } else {
                    // Task Statuses Filters
                    FilterChip(
                        selected = taskStatusFilter == "ALL",
                        onClick = { taskStatusFilter = "ALL" },
                        shape = RoundedCornerShape(12.dp),
                        label = { Text("All Statuses (${submissions.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GeoPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = GeoSurfaceContainerHigh,
                            labelColor = GeoTextPrimary
                        ),
                        modifier = Modifier.testTag("filter_task_all")
                    )

                    FilterChip(
                        selected = taskStatusFilter == "PENDING",
                        onClick = { taskStatusFilter = "PENDING" },
                        shape = RoundedCornerShape(12.dp),
                        label = { Text("Pending Review ($pendingSubmissionsCount)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AmberGold,
                            selectedLabelColor = Color.White,
                            containerColor = GeoSurfaceContainerHigh,
                            labelColor = GeoTextPrimary
                        ),
                        modifier = Modifier.testTag("filter_task_pending")
                    )

                    FilterChip(
                        selected = taskStatusFilter == "APPROVED",
                        onClick = { taskStatusFilter = "APPROVED" },
                        shape = RoundedCornerShape(12.dp),
                        label = { Text("Approved ($approvedSubmissionsCount)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldGreen,
                            selectedLabelColor = Color.White,
                            containerColor = GeoSurfaceContainerHigh,
                            labelColor = GeoTextPrimary
                        ),
                        modifier = Modifier.testTag("filter_task_approved")
                    )

                    FilterChip(
                        selected = taskStatusFilter == "REJECTED",
                        onClick = { taskStatusFilter = "REJECTED" },
                        shape = RoundedCornerShape(12.dp),
                        label = { Text("Rejected ($rejectedSubmissionsCount)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFD32F2F),
                            selectedLabelColor = Color.White,
                            containerColor = GeoSurfaceContainerHigh,
                            labelColor = GeoTextPrimary
                        ),
                        modifier = Modifier.testTag("filter_task_rejected")
                    )
                }
            }
        }

        // --- Method Quick Sub-Filter (All, bKash, Nagad) ---
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GeoSurfaceContainer, RoundedCornerShape(14.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                MethodTabPill(
                    title = "All Methods",
                    isSelected = methodFilter == "ALL",
                    onClick = { methodFilter = "ALL" },
                    modifier = Modifier.weight(1f)
                )
                MethodTabPill(
                    title = "bKash",
                    badgeColor = BkashPink,
                    isSelected = methodFilter == "BKASH",
                    onClick = { methodFilter = "BKASH" },
                    modifier = Modifier.weight(1f)
                )
                MethodTabPill(
                    title = "Nagad",
                    badgeColor = NagadOrange,
                    isSelected = methodFilter == "NAGAD",
                    onClick = { methodFilter = "NAGAD" },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // --- Main List Content ---
        if (activeViewSection == "TRANSACTIONS") {
            if (filteredLogs.isEmpty()) {
                item {
                    EmptyStateCard(
                        title = "No Transactions Found",
                        message = if (searchQuery.isNotEmpty()) "No cached transactions match '$searchQuery'." else "No log entries found for the selected filter in the local Room database."
                    )
                }
            } else {
                items(filteredLogs, key = { it.id }) { log ->
                    TransactionLogCard(
                        log = log,
                        onCopy = { text, label ->
                            clipboardManager.setText(AnnotatedString(text))
                            Toast.makeText(context, "$label copied", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        } else {
            // Task Statuses List
            if (filteredSubmissions.isEmpty()) {
                item {
                    EmptyStateCard(
                        title = "No Task Submissions Found",
                        message = if (searchQuery.isNotEmpty()) "No cached task submissions match '$searchQuery'." else "No task submissions found in the local Room database for the selected status filter."
                    )
                }
            } else {
                items(filteredSubmissions, key = { it.id }) { submission ->
                    TaskSubmissionStatusCard(
                        submission = submission,
                        onCopy = { text, label ->
                            clipboardManager.setText(AnnotatedString(text))
                            Toast.makeText(context, "$label copied", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun EmptyStateCard(
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("empty_state_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GeoCard),
        border = BorderStroke(1.dp, GeoBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(GeoSurfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = null,
                    tint = GeoTextSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = GeoTextPrimary
            )
            Text(
                text = message,
                fontSize = 12.sp,
                color = GeoTextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun MethodTabPill(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeColor: Color? = null
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) GeoCard else Color.Transparent,
        border = if (isSelected) BorderStroke(1.dp, GeoBorder) else null,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (badgeColor != null) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(badgeColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) GeoTextPrimary else GeoTextSecondary
            )
        }
    }
}

@Composable
fun TransactionLogCard(
    log: TransactionLogEntity,
    onCopy: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val dateStr = dateFormat.format(Date(log.timestamp))

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GeoCard),
        border = BorderStroke(1.dp, GeoBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("transaction_log_item_${log.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Category / Method Badge + Room Cache Badge + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (log.method.equals("BKASH", ignoreCase = true) || log.method.equals("NAGAD", ignoreCase = true)) {
                        PaymentMethodBadge(method = log.method)
                    } else {
                        Surface(
                            color = GeoPrimaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = log.method,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeoOnPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Category Pill
                    Surface(
                        color = GeoSurfaceContainerHigh,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = log.category,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = GeoTextSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    RoomCacheBadge(label = "Room Cache")
                }

                StatusBadge(status = log.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title & Description
            Text(
                text = log.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = GeoTextPrimary
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = log.description,
                fontSize = 12.sp,
                color = GeoTextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Financial Breakdown Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GeoSurfaceContainerLow, RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sent / Processed Amount
                Column {
                    Text(
                        text = if (log.category == "WITHDRAWAL") "Payout Requested" else "Task Volume",
                        fontSize = 10.sp,
                        color = GeoTextSecondary
                    )
                    Text(
                        text = "৳ ${log.amount.toInt()}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GeoTextPrimary
                    )
                }

                // 4% Commission Earned
                if (log.commissionAmount > 0) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "4% Commission",
                            fontSize = 10.sp,
                            color = GeoOnCommission
                        )
                        Text(
                            text = "+৳ ${String.format("%.1f", log.commissionAmount)}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = BkashPink
                        )
                    }
                }

                // Wallet Impact
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${log.walletAffected} Wallet",
                        fontSize = 10.sp,
                        color = GeoTextSecondary
                    )
                    val impactPrefix = if (log.balanceImpact > 0) "+" else ""
                    val impactColor = if (log.balanceImpact > 0) EmeraldGreen else GeoTextPrimary
                    Text(
                        text = "$impactPrefix৳ ${String.format("%.1f", log.balanceImpact)}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = impactColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metadata Row: TrxID, Numbers & Timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    if (!log.trxId.isNullOrBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "TrxID:",
                                fontSize = 11.sp,
                                color = GeoTextSecondary
                            )
                            Surface(
                                color = GeoPrimaryContainer.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.clickable { onCopy(log.trxId, "TrxID") }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(
                                        text = log.trxId,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GeoOnPrimaryContainer
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy TrxID",
                                        tint = GeoOnPrimaryContainer,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (!log.senderNumber.isNullOrBlank()) {
                        Text(
                            text = "Sender: ${log.senderNumber}",
                            fontSize = 11.sp,
                            color = GeoTextSecondary
                        )
                    } else if (!log.targetNumber.isNullOrBlank()) {
                        Text(
                            text = "Target: ${log.targetNumber}",
                            fontSize = 11.sp,
                            color = GeoTextSecondary
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = dateStr,
                        fontSize = 11.sp,
                        color = GeoTextSecondary
                    )
                    Text(
                        text = if (log.syncStatus == "PENDING_SYNC") "Offline Queued" else "Log #${log.id}",
                        fontSize = 10.sp,
                        color = if (log.syncStatus == "PENDING_SYNC") AmberGold else GeoTextMuted,
                        fontWeight = if (log.syncStatus == "PENDING_SYNC") FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun TaskSubmissionStatusCard(
    submission: TaskSubmissionEntity,
    onCopy: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val submittedAtStr = dateFormat.format(Date(submission.submittedAt))
    val reviewedAtStr = submission.reviewedAt?.let { dateFormat.format(Date(it)) }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GeoCard),
        border = BorderStroke(1.dp, GeoBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("task_submission_item_${submission.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Method badge + Room cache badge + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PaymentMethodBadge(method = submission.method)
                    RoomCacheBadge(label = if (submission.syncStatus == "PENDING_SYNC") "Room • Pending Sync" else "Room DB Cache")
                }

                StatusBadge(status = submission.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Task Title & Submission ID
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = submission.taskTitle,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = GeoTextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "#SUB-${submission.id}",
                    fontSize = 11.sp,
                    color = GeoTextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Amount & Commission Highlight Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GeoSurfaceContainerLow, RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Sent Amount", fontSize = 10.sp, color = GeoTextSecondary)
                    Text("৳ ${submission.taskAmount.toInt()}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GeoTextPrimary)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("4% Commission", fontSize = 10.sp, color = GeoOnCommission)
                    Text("+৳ ${String.format("%.1f", submission.commissionEarned)}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BkashPink)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Status", fontSize = 10.sp, color = GeoTextSecondary)
                    val statusText = when (submission.status) {
                        "APPROVED" -> "Approved & Paid"
                        "REJECTED" -> "Rejected & Refunded"
                        else -> "In Verification"
                    }
                    val statusColor = when (submission.status) {
                        "APPROVED" -> EmeraldGreen
                        "REJECTED" -> Color(0xFFD32F2F)
                        else -> AmberGold
                    }
                    Text(statusText, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = statusColor)
                }
            }

            // Admin Review / Verification Note (Crucial for user transparency)
            if (!submission.adminNote.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = when (submission.status) {
                        "APPROVED" -> EmeraldLight
                        "REJECTED" -> Color(0xFFFFEBEE)
                        else -> GeoSurfaceContainerHigh
                    },
                    border = BorderStroke(
                        1.dp,
                        when (submission.status) {
                            "APPROVED" -> EmeraldGreen.copy(alpha = 0.3f)
                            "REJECTED" -> Color(0xFFFFCDD2)
                            else -> GeoBorder
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = when (submission.status) {
                                "APPROVED" -> EmeraldGreen
                                "REJECTED" -> Color(0xFFD32F2F)
                                else -> GeoTextSecondary
                            },
                            modifier = Modifier.size(14.dp)
                        )
                        Column {
                            Text(
                                text = "Admin Note:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (submission.status) {
                                    "APPROVED" -> EmeraldGreen
                                    "REJECTED" -> Color(0xFFD32F2F)
                                    else -> GeoTextPrimary
                                }
                            )
                            Text(
                                text = submission.adminNote,
                                fontSize = 11.sp,
                                color = GeoTextPrimary,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // TrxID & Verification Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("TrxID:", fontSize = 11.sp, color = GeoTextSecondary)
                        Surface(
                            color = GeoPrimaryContainer.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.clickable { onCopy(submission.trxId, "TrxID") }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text(
                                    text = submission.trxId,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GeoOnPrimaryContainer
                                )
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy TrxID",
                                    tint = GeoOnPrimaryContainer,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "Sender: ${submission.senderNumber}",
                        fontSize = 11.sp,
                        color = GeoTextSecondary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Submitted: $submittedAtStr",
                        fontSize = 10.sp,
                        color = GeoTextSecondary
                    )
                    if (reviewedAtStr != null) {
                        Text(
                            text = "Reviewed: $reviewedAtStr",
                            fontSize = 10.sp,
                            color = EmeraldGreen
                        )
                    }
                }
            }
        }
    }
}
