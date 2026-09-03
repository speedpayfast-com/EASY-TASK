package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.TaskEntity
import com.example.data.model.TaskSubmissionEntity
import com.example.data.model.TransactionLogEntity
import com.example.data.model.UserEntity
import com.example.data.model.WithdrawalEntity
import com.example.ui.components.PaymentMethodBadge
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AmberGold
import com.example.ui.theme.BkashPink
import com.example.ui.theme.BkashPinkLight
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GeoBackground
import com.example.ui.theme.GeoBorder
import com.example.ui.theme.GeoCard
import com.example.ui.theme.GeoCommissionContainer
import com.example.ui.theme.GeoOnCommission
import com.example.ui.theme.GeoOnPrimaryContainer
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryContainer
import com.example.ui.theme.GeoSecondary
import com.example.ui.theme.GeoSurfaceContainer
import com.example.ui.theme.GeoSurfaceContainerHigh
import com.example.ui.theme.GeoSurfaceContainerLow
import com.example.ui.theme.GeoTextMuted
import com.example.ui.theme.GeoTextPrimary
import com.example.ui.theme.GeoTextSecondary
import com.example.ui.theme.NagadOrange
import com.example.ui.theme.NagadOrangeLight
import com.example.ui.viewmodel.EasyTaskViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

@Composable
fun AdminPanelScreen(
    viewModel: EasyTaskViewModel,
    allTasks: List<TaskEntity>,
    pendingSubmissions: List<TaskSubmissionEntity>,
    pendingWithdrawals: List<WithdrawalEntity>,
    allUsers: List<UserEntity>,
    modifier: Modifier = Modifier
) {
    val isAuthenticated by viewModel.isAdminAuthenticated.collectAsState()
    val allWithdrawals by viewModel.adminAllWithdrawals.collectAsState()
    val allTransactionLogs by viewModel.adminAllTransactionLogs.collectAsState()

    var adminTab by remember { mutableIntStateOf(0) } // 0: Overview, 1: Submissions, 2: Withdrawals, 3: Task Pools, 4: User Activities, 5: Users
    var showCreateTaskModal by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<TaskEntity?>(null) }
    var withdrawalToApprove by remember { mutableStateOf<WithdrawalEntity?>(null) }
    var withdrawalToReject by remember { mutableStateOf<WithdrawalEntity?>(null) }
    var showChangePinModal by remember { mutableStateOf(false) }
    var selectedUserForActivityFilter by remember { mutableStateOf<Long?>(null) }
    var withdrawalStatusFilter by remember { mutableStateOf("PENDING") } // "PENDING", "APPROVED", "REJECTED", "ALL"
    var withdrawalMethodFilter by remember { mutableStateOf("ALL") } // "ALL", "BKASH", "NAGAD"
    var poolFilter by remember { mutableStateOf("ALL") } // "ALL", "BKASH", "NAGAD", "ACTIVE", "PAUSED"
    var taskSearchQuery by remember { mutableStateOf("") }
    var activityCategoryFilter by remember { mutableStateOf("ALL") } // "ALL", "TASK", "COMMISSION", "WITHDRAWAL", "BONUS"
    var activitySearchQuery by remember { mutableStateOf("") }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // If Admin session is NOT authenticated, display the Secure Admin Security Gate
    if (!isAuthenticated) {
        AdminSecurityGateScreen(
            onUnlock = { pin -> viewModel.authenticateAdmin(pin) },
            onCancel = { viewModel.toggleAdminMode(false) }
        )
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GeoBackground)
    ) {
        // --- 1. SECURE ADMIN SESSION HEADER ---
        AdminSecurityHeader(
            onLock = { viewModel.lockAdminSession() },
            onChangePin = { showChangePinModal = true }
        )

        // --- 2. ADMIN NAVIGATION TABS ---
        ScrollableTabRow(
            selectedTabIndex = adminTab,
            containerColor = GeoBackground,
            contentColor = GeoPrimary,
            edgePadding = 12.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[adminTab]),
                    color = GeoPrimary,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = adminTab == 0,
                onClick = { adminTab = 0 },
                text = { Text("Overview", fontWeight = if (adminTab == 0) FontWeight.Bold else FontWeight.Normal, color = if (adminTab == 0) GeoPrimary else GeoTextSecondary) },
                modifier = Modifier.testTag("admin_tab_overview")
            )
            Tab(
                selected = adminTab == 1,
                onClick = { adminTab = 1 },
                text = {
                    Text(
                        "Submissions (${pendingSubmissions.size})",
                        fontWeight = if (adminTab == 1) FontWeight.Bold else FontWeight.Normal,
                        color = if (adminTab == 1) GeoPrimary else GeoTextSecondary
                    )
                },
                modifier = Modifier.testTag("admin_tab_submissions")
            )
            Tab(
                selected = adminTab == 2,
                onClick = { adminTab = 2 },
                text = {
                    Text(
                        "Withdrawals (${pendingWithdrawals.size})",
                        fontWeight = if (adminTab == 2) FontWeight.Bold else FontWeight.Normal,
                        color = if (adminTab == 2) GeoPrimary else GeoTextSecondary
                    )
                },
                modifier = Modifier.testTag("admin_tab_withdrawals")
            )
            Tab(
                selected = adminTab == 3,
                onClick = { adminTab = 3 },
                text = {
                    Text(
                        "Task Pools (${allTasks.size})",
                        fontWeight = if (adminTab == 3) FontWeight.Bold else FontWeight.Normal,
                        color = if (adminTab == 3) GeoPrimary else GeoTextSecondary
                    )
                },
                modifier = Modifier.testTag("admin_tab_task_pools")
            )
            Tab(
                selected = adminTab == 4,
                onClick = { adminTab = 4 },
                text = {
                    Text(
                        "User Activities (${allTransactionLogs.size})",
                        fontWeight = if (adminTab == 4) FontWeight.Bold else FontWeight.Normal,
                        color = if (adminTab == 4) GeoPrimary else GeoTextSecondary
                    )
                },
                modifier = Modifier.testTag("admin_tab_activities")
            )
            Tab(
                selected = adminTab == 5,
                onClick = { adminTab = 5 },
                text = {
                    Text(
                        "Users (${allUsers.size})",
                        fontWeight = if (adminTab == 5) FontWeight.Bold else FontWeight.Normal,
                        color = if (adminTab == 5) GeoPrimary else GeoTextSecondary
                    )
                },
                modifier = Modifier.testTag("admin_tab_users")
            )
        }

        // --- 3. TAB VIEW CONTENT ---
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (adminTab) {
                0 -> {
                    // ==========================================
                    // TAB 0: OVERVIEW & EXECUTIVE CONTROL
                    // ==========================================
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "System Administration",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GeoTextPrimary
                                )
                                Text(
                                    text = "Real-time task pool, withdrawal liability & user audit",
                                    fontSize = 12.sp,
                                    color = GeoTextSecondary
                                )
                            }

                            IconButton(
                                onClick = { viewModel.lockAdminSession() },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(GeoSurfaceContainerHigh)
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = "Lock", tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    // Key Metric Cards
                    item {
                        val pendingLiability = pendingWithdrawals.sumOf { it.amount }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AdminStatCard(
                                title = "Pending Payouts",
                                value = "${pendingWithdrawals.size} reqs",
                                subtitle = "৳${pendingLiability.toInt()} total",
                                iconColor = BkashPink,
                                modifier = Modifier.weight(1f)
                            )
                            AdminStatCard(
                                title = "Pending Tasks",
                                value = "${pendingSubmissions.size} tasks",
                                subtitle = "Needs 4% review",
                                iconColor = GeoPrimary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        val activePoolCount = allTasks.count { it.isActive }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AdminStatCard(
                                title = "Active Task Pool",
                                value = "$activePoolCount / ${allTasks.size}",
                                subtitle = "bKash & Nagad",
                                iconColor = EmeraldGreen,
                                modifier = Modifier.weight(1f)
                            )
                            AdminStatCard(
                                title = "Registered Users",
                                value = "${allUsers.size} accounts",
                                subtitle = "${allTransactionLogs.size} logs recorded",
                                iconColor = AmberGold,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Task Pool Management Quick Actions
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Task Pool & Quick Operations",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoTextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showCreateTaskModal = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("admin_create_task_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Pool Task", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                            }

                            Button(
                                onClick = { viewModel.adminReplenishTaskPool() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("admin_replenish_pool_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = GeoSecondary),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Replenish Pools", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                            }
                        }
                    }

                    // Urgent Action: Pending Withdrawals preview
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Pending Withdrawals Awaiting Payout (${pendingWithdrawals.size})",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeoTextPrimary
                            )
                            if (pendingWithdrawals.isNotEmpty()) {
                                Text(
                                    text = "Manage All",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GeoPrimary,
                                    modifier = Modifier.clickable { adminTab = 2 }
                                )
                            }
                        }
                    }

                    if (pendingWithdrawals.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = GeoCard),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GeoBorder)
                            ) {
                                Text(
                                    text = "✓ Zero pending payout liabilities. All withdrawal requests are processed.",
                                    modifier = Modifier.padding(16.dp),
                                    fontSize = 13.sp,
                                    color = GeoTextSecondary
                                )
                            }
                        }
                    } else {
                        items(pendingWithdrawals.take(2), key = { "ov_w_${it.id}" }) { w ->
                            AdminWithdrawalReviewCard(
                                withdrawal = w,
                                onApproveClick = { withdrawalToApprove = w },
                                onRejectClick = { withdrawalToReject = w }
                            )
                        }
                    }

                    // Urgent Action: Pending Submissions preview
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Pending Task Submissions (${pendingSubmissions.size})",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeoTextPrimary
                            )
                            if (pendingSubmissions.isNotEmpty()) {
                                Text(
                                    text = "Review All",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GeoPrimary,
                                    modifier = Modifier.clickable { adminTab = 1 }
                                )
                            }
                        }
                    }

                    if (pendingSubmissions.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = GeoCard),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GeoBorder)
                            ) {
                                Text(
                                    text = "✓ No pending submissions waiting for verification.",
                                    modifier = Modifier.padding(16.dp),
                                    fontSize = 13.sp,
                                    color = GeoTextSecondary
                                )
                            }
                        }
                    } else {
                        items(pendingSubmissions.take(2), key = { "ov_sub_${it.id}" }) { sub ->
                            AdminSubmissionReviewCard(
                                submission = sub,
                                onApprove = { viewModel.adminApproveSubmission(sub.id) },
                                onReject = { viewModel.adminRejectSubmission(sub.id, "Proof or TrxID invalid") }
                            )
                        }
                    }
                }

                1 -> {
                    // ==========================================
                    // TAB 1: TASK SUBMISSIONS REVIEW
                    // ==========================================
                    item {
                        Text(
                            text = "Review Task Submissions",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoTextPrimary
                        )
                        Text(
                            text = "Verify TrxID and screenshot proof. Approving credits 4% commission to user's Commission Wallet and updates ৳5,000 challenge progress.",
                            fontSize = 12.sp,
                            color = GeoTextSecondary
                        )
                    }

                    if (pendingSubmissions.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = GeoCard),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GeoBorder)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(36.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("All caught up! No pending submissions.", fontWeight = FontWeight.SemiBold, color = GeoTextPrimary)
                                    Text("Users will appear here when they submit new send money tasks.", fontSize = 12.sp, color = GeoTextSecondary)
                                }
                            }
                        }
                    } else {
                        items(pendingSubmissions, key = { it.id }) { sub ->
                            AdminSubmissionReviewCard(
                                submission = sub,
                                onApprove = { viewModel.adminApproveSubmission(sub.id) },
                                onReject = { viewModel.adminRejectSubmission(sub.id, "Proof rejected by Admin") }
                            )
                        }
                    }
                }

                2 -> {
                    // ==========================================
                    // TAB 2: WITHDRAWALS (APPROVE / REJECT)
                    // ==========================================
                    val filteredWithdrawals = allWithdrawals.filter { w ->
                        (withdrawalStatusFilter == "ALL" || w.status == withdrawalStatusFilter) &&
                                (withdrawalMethodFilter == "ALL" || w.method.equals(withdrawalMethodFilter, ignoreCase = true))
                    }

                    item {
                        Column {
                            Text(
                                text = "Withdrawal Requests Management",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeoTextPrimary
                            )
                            Text(
                                text = "Approve payouts with TrxID or reject and refund back to Commission / Main balance.",
                                fontSize = 12.sp,
                                color = GeoTextSecondary
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Status Filters
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                item {
                                    FilterChip(
                                        selected = withdrawalStatusFilter == "PENDING",
                                        onClick = { withdrawalStatusFilter = "PENDING" },
                                        label = { Text("Pending (${pendingWithdrawals.size})") },
                                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GeoPrimaryContainer, selectedLabelColor = GeoOnPrimaryContainer)
                                    )
                                }
                                item {
                                    val approvedCount = allWithdrawals.count { it.status == "APPROVED" }
                                    FilterChip(
                                        selected = withdrawalStatusFilter == "APPROVED",
                                        onClick = { withdrawalStatusFilter = "APPROVED" },
                                        label = { Text("Paid / Approved ($approvedCount)") },
                                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GeoPrimaryContainer, selectedLabelColor = GeoOnPrimaryContainer)
                                    )
                                }
                                item {
                                    val rejectedCount = allWithdrawals.count { it.status == "REJECTED" }
                                    FilterChip(
                                        selected = withdrawalStatusFilter == "REJECTED",
                                        onClick = { withdrawalStatusFilter = "REJECTED" },
                                        label = { Text("Rejected ($rejectedCount)") },
                                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GeoPrimaryContainer, selectedLabelColor = GeoOnPrimaryContainer)
                                    )
                                }
                                item {
                                    FilterChip(
                                        selected = withdrawalStatusFilter == "ALL",
                                        onClick = { withdrawalStatusFilter = "ALL" },
                                        label = { Text("All (${allWithdrawals.size})") },
                                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GeoPrimaryContainer, selectedLabelColor = GeoOnPrimaryContainer)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Method Filters
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = withdrawalMethodFilter == "ALL",
                                    onClick = { withdrawalMethodFilter = "ALL" },
                                    label = { Text("All Methods") }
                                )
                                FilterChip(
                                    selected = withdrawalMethodFilter == "BKASH",
                                    onClick = { withdrawalMethodFilter = "BKASH" },
                                    label = { Text("bKash Only") },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BkashPinkLight, selectedLabelColor = BkashPink)
                                )
                                FilterChip(
                                    selected = withdrawalMethodFilter == "NAGAD",
                                    onClick = { withdrawalMethodFilter = "NAGAD" },
                                    label = { Text("Nagad Only") },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = NagadOrangeLight, selectedLabelColor = NagadOrange)
                                )
                            }
                        }
                    }

                    if (filteredWithdrawals.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = GeoCard),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GeoBorder)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("No withdrawal records match the selected filter.", color = GeoTextSecondary)
                                }
                            }
                        }
                    } else {
                        items(filteredWithdrawals, key = { it.id }) { w ->
                            AdminWithdrawalReviewCard(
                                withdrawal = w,
                                onApproveClick = { withdrawalToApprove = w },
                                onRejectClick = { withdrawalToReject = w }
                            )
                        }
                    }
                }

                3 -> {
                    // ==========================================
                    // TAB 3: TASK POOLS MANAGEMENT
                    // ==========================================
                    val activeCount = allTasks.count { it.isActive }
                    val pausedCount = allTasks.count { !it.isActive }

                    val displayedTasks = allTasks.filter { task ->
                        val matchesFilter = when (poolFilter) {
                            "BKASH" -> task.method == "BKASH"
                            "NAGAD" -> task.method == "NAGAD"
                            "ACTIVE" -> task.isActive
                            "PAUSED" -> !task.isActive
                            else -> true
                        }
                        val matchesSearch = taskSearchQuery.isBlank() ||
                                task.title.contains(taskSearchQuery, ignoreCase = true) ||
                                task.targetNumber.contains(taskSearchQuery, ignoreCase = true) ||
                                task.amount.toInt().toString().contains(taskSearchQuery)

                        matchesFilter && matchesSearch
                    }

                    item {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Task Pools Management (${allTasks.size})",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GeoTextPrimary
                                    )
                                    Text(
                                        text = "Control task availability, amounts, 4% commission & numbers",
                                        fontSize = 12.sp,
                                        color = GeoTextSecondary
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    OutlinedButton(
                                        onClick = { viewModel.adminReplenishTaskPool() },
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Replenish", fontSize = 11.sp)
                                    }

                                    Button(
                                        onClick = { showCreateTaskModal = true },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("New Task", fontSize = 11.sp, color = Color.White)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Search bar for tasks
                            OutlinedTextField(
                                value = taskSearchQuery,
                                onValueChange = { taskSearchQuery = it },
                                placeholder = { Text("Search task pool by title, target number or amount...") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GeoTextSecondary) },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = GeoCard,
                                    unfocusedContainerColor = GeoCard
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Pool Filter Chips
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                item {
                                    FilterChip(
                                        selected = poolFilter == "ALL",
                                        onClick = { poolFilter = "ALL" },
                                        label = { Text("All Pools (${allTasks.size})") }
                                    )
                                }
                                item {
                                    FilterChip(
                                        selected = poolFilter == "BKASH",
                                        onClick = { poolFilter = "BKASH" },
                                        label = { Text("bKash Pool (${allTasks.count { it.method == "BKASH" }})") },
                                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BkashPinkLight, selectedLabelColor = BkashPink)
                                    )
                                }
                                item {
                                    FilterChip(
                                        selected = poolFilter == "NAGAD",
                                        onClick = { poolFilter = "NAGAD" },
                                        label = { Text("Nagad Pool (${allTasks.count { it.method == "NAGAD" }})") },
                                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = NagadOrangeLight, selectedLabelColor = NagadOrange)
                                    )
                                }
                                item {
                                    FilterChip(
                                        selected = poolFilter == "ACTIVE",
                                        onClick = { poolFilter = "ACTIVE" },
                                        label = { Text("Active ($activeCount)") }
                                    )
                                }
                                item {
                                    FilterChip(
                                        selected = poolFilter == "PAUSED",
                                        onClick = { poolFilter = "PAUSED" },
                                        label = { Text("Paused ($pausedCount)") }
                                    )
                                }
                            }
                        }
                    }

                    if (displayedTasks.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = GeoCard),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GeoBorder)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(28.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("No tasks found in this pool criteria.", color = GeoTextSecondary)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = { viewModel.adminReplenishTaskPool() },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary)
                                    ) {
                                        Text("Auto-Generate Balanced Tasks", color = Color.White)
                                    }
                                }
                            }
                        }
                    } else {
                        items(displayedTasks, key = { it.id }) { task ->
                            AdminTaskPoolItemCard(
                                task = task,
                                onToggleActive = { isActive -> viewModel.adminToggleTaskActive(task.id, isActive) },
                                onEdit = { taskToEdit = task },
                                onDelete = { viewModel.adminDeleteTask(task) }
                            )
                        }
                    }
                }

                4 -> {
                    // ==========================================
                    // TAB 4: VIEW ALL USER ACTIVITIES & AUDIT TRAIL
                    // ==========================================
                    val filteredLogs = allTransactionLogs.filter { log ->
                        val matchesUser = selectedUserForActivityFilter == null || log.userId == selectedUserForActivityFilter
                        val matchesCategory = when (activityCategoryFilter) {
                            "ALL" -> true
                            "TASK" -> log.category == "TASK" && log.type != "COMMISSION_EARNED"
                            "COMMISSION" -> log.category == "COMMISSION" || log.commissionAmount > 0
                            "WITHDRAWAL" -> log.category == "WITHDRAWAL"
                            "BONUS" -> log.type == "BONUS" || log.type == "REFUND"
                            else -> true
                        }
                        val matchesSearch = activitySearchQuery.isBlank() ||
                                log.title.contains(activitySearchQuery, ignoreCase = true) ||
                                log.description.contains(activitySearchQuery, ignoreCase = true) ||
                                (log.trxId?.contains(activitySearchQuery, ignoreCase = true) == true) ||
                                (log.senderNumber?.contains(activitySearchQuery) == true) ||
                                (log.targetNumber?.contains(activitySearchQuery) == true)

                        matchesUser && matchesCategory && matchesSearch
                    }

                    item {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Platform Audit & User Activities",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GeoTextPrimary
                                    )
                                    Text(
                                        text = "Comprehensive real-time ledger of all user submissions, payouts & commissions",
                                        fontSize = 12.sp,
                                        color = GeoTextSecondary
                                    )
                                }

                                if (selectedUserForActivityFilter != null) {
                                    OutlinedButton(
                                        onClick = { selectedUserForActivityFilter = null },
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("Clear User Filter", fontSize = 11.sp, color = GeoPrimary)
                                    }
                                }
                            }

                            if (selectedUserForActivityFilter != null) {
                                val u = allUsers.find { it.id == selectedUserForActivityFilter }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(GeoPrimaryContainer)
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "Showing activities specifically for: ${u?.name ?: "User #${selectedUserForActivityFilter}"} (${u?.phone ?: ""})",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GeoOnPrimaryContainer
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Search bar for activities
                            OutlinedTextField(
                                value = activitySearchQuery,
                                onValueChange = { activitySearchQuery = it },
                                placeholder = { Text("Search by TrxID, phone, keyword...") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GeoTextSecondary) },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = GeoCard,
                                    unfocusedContainerColor = GeoCard
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Activity Type Filter Chips
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                item {
                                    FilterChip(
                                        selected = activityCategoryFilter == "ALL",
                                        onClick = { activityCategoryFilter = "ALL" },
                                        label = { Text("All Activities (${allTransactionLogs.size})") }
                                    )
                                }
                                item {
                                    FilterChip(
                                        selected = activityCategoryFilter == "TASK",
                                        onClick = { activityCategoryFilter = "TASK" },
                                        label = { Text("Tasks") }
                                    )
                                }
                                item {
                                    FilterChip(
                                        selected = activityCategoryFilter == "COMMISSION",
                                        onClick = { activityCategoryFilter = "COMMISSION" },
                                        label = { Text("4% Commissions") },
                                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GeoCommissionContainer, selectedLabelColor = GeoOnCommission)
                                    )
                                }
                                item {
                                    FilterChip(
                                        selected = activityCategoryFilter == "WITHDRAWAL",
                                        onClick = { activityCategoryFilter = "WITHDRAWAL" },
                                        label = { Text("Withdrawals") }
                                    )
                                }
                                item {
                                    FilterChip(
                                        selected = activityCategoryFilter == "BONUS",
                                        onClick = { activityCategoryFilter = "BONUS" },
                                        label = { Text("Bonuses & Refunds") }
                                    )
                                }
                            }
                        }
                    }

                    if (filteredLogs.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = GeoCard),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GeoBorder)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(28.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("No activity logs found for this filter.", color = GeoTextSecondary)
                                }
                            }
                        }
                    } else {
                        items(filteredLogs, key = { it.id }) { log ->
                            val userObj = allUsers.find { it.id == log.userId }
                            AdminActivityLogCard(
                                log = log,
                                userName = userObj?.name ?: "User #${log.userId}",
                                userPhone = userObj?.phone ?: "N/A",
                                onFilterThisUser = {
                                    selectedUserForActivityFilter = log.userId
                                },
                                onCopyTrx = { trx ->
                                    clipboardManager.setText(AnnotatedString(trx))
                                    Toast.makeText(context, "Copied TrxID: $trx", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }

                5 -> {
                    // ==========================================
                    // TAB 5: USERS DIRECTORY & LEDGER
                    // ==========================================
                    item {
                        Text(
                            text = "Registered Platform Users (${allUsers.size})",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoTextPrimary
                        )
                        Text(
                            text = "View user balances, referral codes, and jump to specific user activity ledgers.",
                            fontSize = 12.sp,
                            color = GeoTextSecondary
                        )
                    }

                    items(allUsers, key = { it.id }) { u ->
                        AdminUserDirectoryCard(
                            user = u,
                            onViewActivity = {
                                selectedUserForActivityFilter = u.id
                                adminTab = 4 // Switch to User Activities tab!
                            },
                            onTopUpMain = {
                                viewModel.adminTopUpUser(u.id, 2000.0)
                            }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // --- MODALS & DIALOGS ---

    // 1. Create Task Modal
    if (showCreateTaskModal) {
        AdminCreateTaskDialog(
            onDismiss = { showCreateTaskModal = false },
            onCreate = { title, method, number, amount, instructions, commRate ->
                viewModel.adminCreateTask(title, method, number, amount, instructions, commRate) {
                    showCreateTaskModal = false
                }
            }
        )
    }

    // 2. Edit Task Modal
    taskToEdit?.let { task ->
        AdminEditTaskDialog(
            task = task,
            onDismiss = { taskToEdit = null },
            onSave = { updated ->
                viewModel.adminUpdateTask(updated) {
                    taskToEdit = null
                }
            }
        )
    }

    // 3. Approve Withdrawal Modal (with Payout TrxID)
    withdrawalToApprove?.let { w ->
        AdminApproveWithdrawalDialog(
            withdrawal = w,
            onDismiss = { withdrawalToApprove = null },
            onConfirm = { payoutTrxId, note ->
                viewModel.adminApproveWithdrawal(w.id, payoutTrxId, note)
                withdrawalToApprove = null
            }
        )
    }

    // 4. Reject Withdrawal Modal (with Reason)
    withdrawalToReject?.let { w ->
        AdminRejectWithdrawalDialog(
            withdrawal = w,
            onDismiss = { withdrawalToReject = null },
            onConfirm = { reason ->
                viewModel.adminRejectWithdrawal(w.id, reason)
                withdrawalToReject = null
            }
        )
    }

    // 5. Change Admin Passcode Dialog
    if (showChangePinModal) {
        AdminChangePasscodeDialog(
            onDismiss = { showChangePinModal = false },
            onSave = { newPin ->
                viewModel.changeAdminPasscode(newPin)
                showChangePinModal = false
            }
        )
    }
}

// ==========================================
// ADMIN SECURITY GATE COMPONENT
// ==========================================
@Composable
fun AdminSecurityGateScreen(
    onUnlock: (String) -> Boolean,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var enteredPassword by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GeoBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = GeoCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, GeoBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("admin_security_gate_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Shield Security Icon
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(GeoPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Admin Security Shield",
                        tint = GeoPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Admin Panel Login",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = GeoTextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "শুধুমাত্র অনুমোদিত এডমিন পাসওয়ার্ড দিয়ে প্রবেশ করতে পারবেন",
                    fontSize = 12.sp,
                    color = GeoTextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = enteredPassword,
                    onValueChange = {
                        enteredPassword = it
                        hasError = false
                    },
                    label = { Text("Admin Password (এডমিন পাসওয়ার্ড)") },
                    placeholder = { Text("পাসওয়ার্ড দিন...") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.LockOpen else Icons.Default.Lock,
                                contentDescription = "Toggle Password Visibility",
                                tint = GeoTextSecondary
                            )
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_password_input_field"),
                    isError = hasError,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = GeoBackground,
                        unfocusedContainerColor = GeoBackground
                    )
                )

                if (hasError) {
                    Text(
                        text = "ভুল পাসওয়ার্ড! শুধুমাত্র এডমিন লগইন করতে পারবে।",
                        color = Color(0xFFDC2626),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                Button(
                    onClick = {
                        val ok = onUnlock(enteredPassword)
                        if (!ok) {
                            hasError = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("admin_unlock_submit_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                    enabled = enteredPassword.isNotBlank()
                ) {
                    Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Login to Admin Panel (লগইন)", fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Return to User App", color = GeoTextSecondary)
                }
            }
        }
    }
}

// ==========================================
// ADMIN SECURITY HEADER
// ==========================================
@Composable
fun AdminSecurityHeader(
    onLock: () -> Unit,
    onChangePin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = GeoSurfaceContainerHigh,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(EmeraldGreen)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "SECURE ADMIN SESSION ACTIVE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GeoPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Audit Logging: Real-Time Room DB",
                        fontSize = 10.sp,
                        color = GeoTextSecondary
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val context = LocalContext.current
                Surface(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/+9EvTCl-BZYo2ZWQ1"))
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Telegram: https://t.me/+9EvTCl-BZYo2ZWQ1", Toast.LENGTH_SHORT).show()
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF229ED9).copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF229ED9).copy(alpha = 0.4f)),
                    modifier = Modifier.testTag("admin_telegram_support_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Support Group",
                            tint = Color(0xFF229ED9),
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Support Group",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF229ED9)
                        )
                    }
                }

                IconButton(
                    onClick = onChangePin,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Security, contentDescription = "Security Settings", tint = GeoPrimary, modifier = Modifier.size(16.dp))
                }

                Button(
                    onClick = onLock,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("admin_lock_session_button")
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Lock Session", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

// ==========================================
// ADMIN KPI STAT CARD
// ==========================================
@Composable
fun AdminStatCard(
    title: String,
    value: String,
    subtitle: String,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GeoCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, GeoBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, fontSize = 11.sp, color = GeoTextSecondary, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = iconColor)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 10.sp, color = GeoTextSecondary)
        }
    }
}

// ==========================================
// SUBMISSION REVIEW CARD
// ==========================================
@Composable
fun AdminSubmissionReviewCard(
    submission: TaskSubmissionEntity,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val dateStr = dateFormat.format(Date(submission.submittedAt))

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GeoCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, GeoBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("admin_submission_card_${submission.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PaymentMethodBadge(method = submission.method)
                Text(dateStr, fontSize = 11.sp, color = GeoTextSecondary)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${submission.userName} (${submission.userPhone})",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = GeoTextPrimary
            )
            Text(
                text = submission.taskTitle,
                fontSize = 12.sp,
                color = GeoTextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(GeoSurfaceContainerHigh)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Send Money Volume", fontSize = 11.sp, color = GeoTextSecondary)
                    Text("৳${submission.taskAmount.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GeoTextPrimary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("4% Commission Credited", fontSize = 11.sp, color = GeoTextSecondary)
                    Text("+৳${String.format("%.1f", submission.commissionEarned)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GeoPrimary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Submitted TrxID: ${submission.trxId}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = GeoPrimary)
            Text("Sender Account: ${submission.senderNumber}", fontSize = 11.sp, color = GeoTextSecondary)
            Text("Proof Note: ${submission.screenshotNote}", fontSize = 11.sp, color = GeoTextSecondary)

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("admin_reject_sub_${submission.id}"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reject & Refund")
                }

                Button(
                    onClick = onApprove,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("admin_approve_sub_${submission.id}"),
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Approve (4%)", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

// ==========================================
// WITHDRAWAL REVIEW CARD
// ==========================================
@Composable
fun AdminWithdrawalReviewCard(
    withdrawal: WithdrawalEntity,
    onApproveClick: () -> Unit,
    onRejectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCommission = withdrawal.walletType == "COMMISSION"
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val dateStr = dateFormat.format(Date(withdrawal.requestedAt))
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GeoCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, GeoBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("admin_withdrawal_card_${withdrawal.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PaymentMethodBadge(method = withdrawal.method)
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (isCommission) GeoCommissionContainer else GeoPrimaryContainer)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isCommission) "Commission Wallet" else "Main Wallet",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCommission) GeoOnCommission else GeoOnPrimaryContainer
                        )
                    }
                }

                StatusBadge(status = withdrawal.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "${withdrawal.userName} (${withdrawal.userPhone})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = GeoTextPrimary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Target ${withdrawal.method}: ${withdrawal.targetNumber}",
                            fontSize = 12.sp,
                            color = GeoPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(withdrawal.targetNumber))
                                Toast.makeText(context, "Copied number: ${withdrawal.targetNumber}", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy number", tint = GeoPrimary, modifier = Modifier.size(13.dp))
                        }
                    }
                    Text(
                        text = "Requested: $dateStr",
                        fontSize = 11.sp,
                        color = GeoTextSecondary
                    )
                }

                Text(
                    text = "৳${withdrawal.amount.toInt()}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = GeoTextPrimary
                )
            }

            if (withdrawal.status != "PENDING") {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(GeoSurfaceContainerHigh)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Admin Note: ${withdrawal.adminNote.ifBlank { "Processed by Admin" }}",
                        fontSize = 11.sp,
                        color = GeoTextSecondary
                    )
                }
            }

            if (withdrawal.status == "PENDING") {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onRejectClick,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_reject_with_${withdrawal.id}"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reject & Refund")
                    }

                    Button(
                        onClick = onApproveClick,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_approve_with_${withdrawal.id}"),
                        colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Approve Payout", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

// ==========================================
// TASK POOL ITEM CARD
// ==========================================
@Composable
fun AdminTaskPoolItemCard(
    task: TaskEntity,
    onToggleActive: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = GeoCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (task.isActive) GeoBorder else Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("admin_task_pool_card_${task.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PaymentMethodBadge(method = task.method)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (task.isActive) Color(0xFFDCFCE7) else Color(0xFFF1F5F9))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (task.isActive) "Active in Pool" else "Paused",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (task.isActive) EmeraldGreen else GeoTextMuted
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (task.isActive) "Live" else "Paused",
                        fontSize = 11.sp,
                        color = GeoTextSecondary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = task.isActive,
                        onCheckedChange = onToggleActive,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = GeoPrimary,
                            uncheckedThumbColor = GeoTextSecondary,
                            uncheckedTrackColor = GeoBorder
                        ),
                        modifier = Modifier.size(34.dp, 20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = task.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = GeoTextPrimary
            )

            Text(
                text = "Target Number: ${task.targetNumber} • User 4% Comm: ৳${(task.amount * task.commissionRate).toInt()}",
                fontSize = 12.sp,
                color = GeoPrimary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GeoSurfaceContainerHigh, RoundedCornerShape(10.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Task Amount Required", fontSize = 10.sp, color = GeoTextSecondary)
                    Text("৳${task.amount.toInt()}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GeoTextPrimary)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Task", tint = GeoPrimary, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Task", tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

// ==========================================
// USER ACTIVITY LOG CARD (AUDIT TRAIL)
// ==========================================
@Composable
fun AdminActivityLogCard(
    log: TransactionLogEntity,
    userName: String,
    userPhone: String,
    onFilterThisUser: () -> Unit,
    onCopyTrx: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val dateStr = dateFormat.format(Date(log.timestamp))

    val isCommission = log.category == "COMMISSION" || log.type == "COMMISSION_EARNED"
    val isWithdrawal = log.category == "WITHDRAWAL"

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = GeoCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, GeoBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("admin_log_card_${log.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PaymentMethodBadge(method = log.method)
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(
                                when (log.status) {
                                    "COMPLETED" -> Color(0xFFDCFCE7)
                                    "PENDING" -> Color(0xFFFEF3C7)
                                    else -> Color(0xFFFEE2E2)
                                }
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = log.status,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (log.status) {
                                "COMPLETED" -> EmeraldGreen
                                "PENDING" -> AmberGold
                                else -> Color(0xFFDC2626)
                            }
                        )
                    }
                }

                Text(dateStr, fontSize = 11.sp, color = GeoTextSecondary)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // User info bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$userName ($userPhone)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GeoPrimary,
                    modifier = Modifier.clickable { onFilterThisUser() }
                )
                Text(
                    text = "Filter User",
                    fontSize = 10.sp,
                    color = GeoTextSecondary,
                    modifier = Modifier.clickable { onFilterThisUser() }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = log.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = GeoTextPrimary
            )

            Text(
                text = log.description,
                fontSize = 12.sp,
                color = GeoTextSecondary,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GeoSurfaceContainerHigh, RoundedCornerShape(10.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Impact on ${log.walletAffected} Balance", fontSize = 10.sp, color = GeoTextSecondary)
                    Text(
                        text = if (log.balanceImpact >= 0) "+৳${String.format("%.1f", log.balanceImpact)}" else "-৳${String.format("%.1f", -log.balanceImpact)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (log.balanceImpact >= 0) EmeraldGreen else Color(0xFFDC2626)
                    )
                }

                log.trxId?.let { trx ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(GeoBackground)
                            .clickable { onCopyTrx(trx) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("TrxID: $trx", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = GeoPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(12.dp), tint = GeoPrimary)
                    }
                }
            }
        }
    }
}

// ==========================================
// USER DIRECTORY CARD
// ==========================================
@Composable
fun AdminUserDirectoryCard(
    user: UserEntity,
    onViewActivity: () -> Unit,
    onTopUpMain: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = GeoCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, GeoBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = user.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GeoTextPrimary)
                    Text(text = "Phone: ${user.phone} | WA: ${user.whatsapp}", fontSize = 11.sp, color = GeoTextSecondary)
                    Text(text = "Code: ${user.referralCode} | Referred By: ${user.referredBy ?: "Direct"}", fontSize = 11.sp, color = GeoPrimary)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onViewActivity,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Activities", fontSize = 11.sp)
                    }

                    Button(
                        onClick = onTopUpMain,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("+৳2,000", fontSize = 11.sp, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GeoSurfaceContainerHigh, RoundedCornerShape(10.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Main: ৳${String.format("%.1f", user.mainBalance)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GeoTextPrimary
                )
                Text(
                    text = "Commission: ৳${String.format("%.1f", user.commissionBalance)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GeoPrimary
                )
            }
        }
    }
}

// ==========================================
// APPROVE WITHDRAWAL DIALOG (WITH TRXID)
// ==========================================
@Composable
fun AdminApproveWithdrawalDialog(
    withdrawal: WithdrawalEntity,
    onDismiss: () -> Unit,
    onConfirm: (payoutTrxId: String, note: String) -> Unit
) {
    var payoutTrxId by remember { mutableStateOf("PAY-" + (100000 + Random.nextInt(900000))) }
    var note by remember { mutableStateOf("Payout completed successfully via ${withdrawal.method} merchant.") }
    var step by remember { mutableStateOf("INPUT") } // "INPUT", "PROCESSING", "SUCCESS"
    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = { if (step != "PROCESSING") onDismiss() }) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = GeoCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, GeoBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .animateContentSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = if (step == "INPUT") Alignment.Start else Alignment.CenterHorizontally
            ) {
                when (step) {
                    "PROCESSING" -> {
                        Spacer(modifier = Modifier.height(16.dp))
                        CircularProgressIndicator(
                            color = GeoPrimary,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Processing Payout Disbursal...",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Sending ৳${withdrawal.amount.toInt()} to ${withdrawal.method} (${withdrawal.targetNumber})...",
                            fontSize = 12.sp,
                            color = GeoTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    "SUCCESS" -> {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDCFCE7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Success",
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Payout Approved & Recorded!",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "TrxID: $payoutTrxId",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoPrimary
                        )
                        Text(
                            text = "৳${withdrawal.amount.toInt()} disbursed to ${withdrawal.userName}",
                            fontSize = 12.sp,
                            color = GeoTextSecondary
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary)
                        ) {
                            Text("Done", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                    else -> {
                        Text(
                            text = "Approve Withdrawal Payout",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoTextPrimary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(GeoSurfaceContainerHigh)
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(text = "Recipient: ${withdrawal.userName} (${withdrawal.userPhone})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GeoTextPrimary)
                                Text(text = "Send To: ${withdrawal.method} Number ${withdrawal.targetNumber}", fontSize = 12.sp, color = GeoPrimary, fontWeight = FontWeight.SemiBold)
                                Text(text = "Amount: ৳${withdrawal.amount.toInt()} (from ${withdrawal.walletType} balance)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GeoTextPrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = payoutTrxId,
                            onValueChange = { payoutTrxId = it },
                            label = { Text("Payout Reference / TrxID") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            label = { Text("Admin Note to User") },
                            singleLine = false,
                            maxLines = 2,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onDismiss,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Cancel", color = GeoTextSecondary)
                            }

                            Button(
                                onClick = {
                                    step = "PROCESSING"
                                    scope.launch {
                                        delay(850)
                                        onConfirm(payoutTrxId, note)
                                        step = "SUCCESS"
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary)
                            ) {
                                Text("Confirm Paid", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// REJECT WITHDRAWAL DIALOG (WITH REASON)
// ==========================================
@Composable
fun AdminRejectWithdrawalDialog(
    withdrawal: WithdrawalEntity,
    onDismiss: () -> Unit,
    onConfirm: (reason: String) -> Unit
) {
    var reason by remember { mutableStateOf("Invalid account number provided") }
    var step by remember { mutableStateOf("INPUT") } // "INPUT", "PROCESSING", "SUCCESS"
    val scope = rememberCoroutineScope()
    val predefinedReasons = listOf(
        "Invalid phone number",
        "Mismatched bKash account",
        "Suspicious activity detected",
        "Duplicate withdrawal request"
    )

    Dialog(onDismissRequest = { if (step != "PROCESSING") onDismiss() }) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = GeoCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, GeoBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .animateContentSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = if (step == "INPUT") Alignment.Start else Alignment.CenterHorizontally
            ) {
                when (step) {
                    "PROCESSING" -> {
                        Spacer(modifier = Modifier.height(16.dp))
                        CircularProgressIndicator(
                            color = Color(0xFFDC2626),
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Refunding User Wallet...",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Returning ৳${withdrawal.amount.toInt()} to ${withdrawal.userName}'s ${withdrawal.walletType} balance...",
                            fontSize = 12.sp,
                            color = GeoTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    "SUCCESS" -> {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFEE2E2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Rejected",
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Withdrawal Rejected & Refunded",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "৳${withdrawal.amount.toInt()} was refunded to ${withdrawal.userName}.",
                            fontSize = 12.sp,
                            color = GeoTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Text(
                            text = "Reason: $reason",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFDC2626),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                        ) {
                            Text("Done", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                    else -> {
                        Text(
                            text = "Reject & Refund Withdrawal",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Rejecting will immediately refund ৳${withdrawal.amount.toInt()} back to ${withdrawal.userName}'s ${withdrawal.walletType} balance.",
                            fontSize = 12.sp,
                            color = GeoTextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(text = "Quick Rejection Reasons:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = GeoTextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(predefinedReasons) { r ->
                                FilterChip(
                                    selected = reason == r,
                                    onClick = { reason = r },
                                    label = { Text(r, fontSize = 11.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = reason,
                            onValueChange = { reason = it },
                            label = { Text("Rejection Reason") },
                            singleLine = false,
                            maxLines = 3,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onDismiss,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Cancel", color = GeoTextSecondary)
                            }

                            Button(
                                onClick = {
                                    step = "PROCESSING"
                                    scope.launch {
                                        delay(850)
                                        onConfirm(reason)
                                        step = "SUCCESS"
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                enabled = reason.isNotBlank()
                            ) {
                                Text("Reject & Refund", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// CREATE TASK IN POOL DIALOG
// ==========================================
@Composable
fun AdminCreateTaskDialog(
    onDismiss: () -> Unit,
    onCreate: (title: String, method: String, number: String, amount: Double, instructions: String, commRate: Double) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var method by remember { mutableStateOf("BKASH") }
    var targetNumber by remember { mutableStateOf("01823-") }
    var amountText by remember { mutableStateOf("1000") }
    var instructions by remember { mutableStateOf("Send Money to the given number, enter TrxID and upload screenshot.") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = GeoCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, GeoBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Add Task to Pool",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GeoTextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { method = "BKASH" },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (method == "BKASH") Color(0xFFFCE7F3) else Color.Transparent
                        )
                    ) {
                        Text("bKash Pool", fontWeight = FontWeight.Bold, color = BkashPink)
                    }

                    OutlinedButton(
                        onClick = { method = "NAGAD" },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (method == "NAGAD") Color(0xFFFEF3C7) else Color.Transparent
                        )
                    ) {
                        Text("Nagad Pool", fontWeight = FontWeight.Bold, color = NagadOrange)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    placeholder = { Text("e.g. bKash Send Money 1000") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = targetNumber,
                    onValueChange = { targetNumber = it },
                    label = { Text("Target Phone Number") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount (৳)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                val amt = amountText.toDoubleOrNull() ?: 0.0
                Text(
                    text = "User Commission (4%): ৳${String.format("%.1f", amt * 0.04)}",
                    fontSize = 12.sp,
                    color = GeoPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Cancel", color = GeoTextSecondary)
                    }

                    Button(
                        onClick = {
                            val finalTitle = if (title.isBlank()) "$method Send Money (৳${amt.toInt()})" else title
                            onCreate(finalTitle, method, targetNumber, amt, instructions, 0.04)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                        enabled = targetNumber.isNotBlank() && amt > 0
                    ) {
                        Text("Save Task", color = Color.White)
                    }
                }
            }
        }
    }
}

// ==========================================
// EDIT TASK IN POOL DIALOG
// ==========================================
@Composable
fun AdminEditTaskDialog(
    task: TaskEntity,
    onDismiss: () -> Unit,
    onSave: (TaskEntity) -> Unit
) {
    var title by remember { mutableStateOf(task.title) }
    var targetNumber by remember { mutableStateOf(task.targetNumber) }
    var amountText by remember { mutableStateOf(task.amount.toInt().toString()) }
    var instructions by remember { mutableStateOf(task.instructions) }
    var isActive by remember { mutableStateOf(task.isActive) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = GeoCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, GeoBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Edit Task in Pool",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GeoTextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = targetNumber,
                    onValueChange = { targetNumber = it },
                    label = { Text("Target Phone Number") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Task Amount (৳)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Pool Active Status", fontSize = 13.sp, color = GeoTextPrimary)
                    Switch(
                        checked = isActive,
                        onCheckedChange = { isActive = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = GeoPrimary)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Cancel", color = GeoTextSecondary)
                    }

                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull() ?: task.amount
                            onSave(
                                task.copy(
                                    title = title,
                                    targetNumber = targetNumber,
                                    amount = amt,
                                    instructions = instructions,
                                    isActive = isActive
                                )
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                        enabled = targetNumber.isNotBlank() && (amountText.toDoubleOrNull() ?: 0.0) > 0
                    ) {
                        Text("Save Changes", color = Color.White)
                    }
                }
            }
        }
    }
}

// ==========================================
// CHANGE ADMIN PASSCODE DIALOG
// ==========================================
@Composable
fun AdminChangePasscodeDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = GeoCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, GeoBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Update Admin Security PIN",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GeoTextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = newPin,
                    onValueChange = {
                        if (it.length <= 8) newPin = it
                    },
                    label = { Text("New Security PIN (4-8 digits)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = confirmPin,
                    onValueChange = {
                        if (it.length <= 8) confirmPin = it
                    },
                    label = { Text("Confirm PIN") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                errorText?.let {
                    Text(
                        text = it,
                        color = Color(0xFFDC2626),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Cancel", color = GeoTextSecondary)
                    }

                    Button(
                        onClick = {
                            if (newPin.length < 4) {
                                errorText = "PIN must be at least 4 digits"
                            } else if (newPin != confirmPin) {
                                errorText = "PINs do not match"
                            } else {
                                onSave(newPin)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                        enabled = newPin.isNotBlank() && confirmPin.isNotBlank()
                    ) {
                        Text("Update PIN", color = Color.White)
                    }
                }
            }
        }
    }
}
