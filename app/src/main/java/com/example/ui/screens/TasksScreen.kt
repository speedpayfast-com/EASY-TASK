package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.CacheMetadataEntity
import com.example.data.model.TaskEntity
import com.example.data.model.TaskSubmissionEntity
import com.example.data.model.UserEntity
import com.example.ui.components.CommissionTag
import com.example.ui.components.OfflineStatusBanner
import com.example.ui.components.PaymentMethodBadge
import com.example.ui.components.PaymentMethodLogo
import com.example.ui.components.RoomCacheBadge
import com.example.ui.components.TelegramSupportCard
import com.example.ui.theme.AmberGold
import com.example.ui.theme.BkashPink
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

@Composable
fun TasksScreen(
    tasks: List<TaskEntity>,
    currentUser: UserEntity?,
    submissions: List<TaskSubmissionEntity> = emptyList(),
    isOffline: Boolean = false,
    cacheMetadata: CacheMetadataEntity? = null,
    onToggleOffline: () -> Unit = {},
    onSync: () -> Unit = {},
    onViewTaskStatuses: () -> Unit = {},
    onSubmitTask: (TaskEntity, String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("ALL") } // "ALL", "BKASH", "NAGAD"
    var taskToSubmit by remember { mutableStateOf<TaskEntity?>(null) }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val filteredTasks = when (selectedFilter) {
        "BKASH" -> tasks.filter { it.method.uppercase() == "BKASH" }
        "NAGAD" -> tasks.filter { it.method.uppercase() == "NAGAD" }
        else -> tasks
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GeoBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Offline Status Banner
        item {
            OfflineStatusBanner(
                isOffline = isOffline,
                cachedTransactionsCount = 0,
                cachedTasksCount = submissions.size,
                lastSyncTimestamp = cacheMetadata?.lastSyncTimestamp,
                onToggleOffline = onToggleOffline,
                onSync = onSync
            )
        }

        // Cached Task Submissions Quick Access Bar (If any cached submissions exist)
        if (submissions.isNotEmpty()) {
            item {
                Surface(
                    onClick = onViewTaskStatuses,
                    shape = RoundedCornerShape(16.dp),
                    color = GeoSurfaceContainerHigh,
                    border = BorderStroke(1.dp, GeoBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cached_task_statuses_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RoomCacheBadge(label = "Room DB")
                            Column {
                                Text(
                                    text = "Cached Task Submissions",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GeoTextPrimary
                                )
                                val pendingCount = submissions.count { it.status == "PENDING" }
                                val approvedCount = submissions.count { it.status == "APPROVED" }
                                Text(
                                    text = "$pendingCount In Review • $approvedCount Approved & Paid",
                                    fontSize = 11.sp,
                                    color = GeoTextSecondary
                                )
                            }
                        }
                        Text(
                            text = "View Statuses →",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoPrimary
                        )
                    }
                }
            }
        }

        // Official Telegram Support Group Card
        item {
            TelegramSupportCard()
        }

        item {
            Spacer(modifier = Modifier.height(6.dp))

            // Hero Banner Card with Geometric Rounded Corners
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = GeoCard),
                border = BorderStroke(1.dp, GeoBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_banner_card")
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_banner),
                        contentDescription = "Hero Banner",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        contentScale = ContentScale.Crop
                    )
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "bKash & Nagad Send Money",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeoTextPrimary
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(GeoPrimary)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "4% Fixed Commission",
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = "Complete Send Money tasks from your bKash or Nagad wallet. Upload screenshot & TrxID to receive instant 4% commission credited to your wallet!",
                            fontSize = 12.sp,
                            color = GeoTextSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // Quick Referral Invite Strip (from Geometric Balance spec)
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = GeoSurfaceContainerHigh),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quick_referral_strip")
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = GeoPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Invite & Earn ৳ 200",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeoTextPrimary
                            )
                            Text(
                                text = "Ref: easytask.app/r/${currentUser?.referralCode ?: "8829"}",
                                fontSize = 11.sp,
                                color = GeoTextSecondary
                            )
                        }
                    }
                    Button(
                        onClick = {
                            val link = "https://easytask.app/r/${currentUser?.referralCode ?: "8829"}"
                            clipboardManager.setText(AnnotatedString(link))
                            Toast.makeText(context, "Referral link copied!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("quick_share_button")
                    ) {
                        Text("Share", color = GeoPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Filter Pills for 'All', 'bKash', and 'Nagad'
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = selectedFilter == "ALL",
                    onClick = { selectedFilter = "ALL" },
                    label = {
                        Text(
                            "All Tasks (${tasks.size})",
                            fontWeight = if (selectedFilter == "ALL") FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GeoPrimary,
                        selectedLabelColor = Color.White,
                        containerColor = GeoSurfaceContainerLow,
                        labelColor = GeoTextPrimary
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.testTag("filter_all")
                )
                FilterChip(
                    selected = selectedFilter == "BKASH",
                    onClick = { selectedFilter = "BKASH" },
                    label = {
                        Text(
                            "bKash (${tasks.count { it.method.uppercase() == "BKASH" }})",
                            fontWeight = if (selectedFilter == "BKASH") FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    leadingIcon = {
                        PaymentMethodLogo(method = "BKASH", size = 16)
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BkashPink,
                        selectedLabelColor = Color.White,
                        containerColor = GeoSurfaceContainerLow,
                        labelColor = GeoTextPrimary
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.testTag("filter_bkash")
                )
                FilterChip(
                    selected = selectedFilter == "NAGAD",
                    onClick = { selectedFilter = "NAGAD" },
                    label = {
                        Text(
                            "Nagad (${tasks.count { it.method.uppercase() == "NAGAD" }})",
                            fontWeight = if (selectedFilter == "NAGAD") FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    leadingIcon = {
                        PaymentMethodLogo(method = "NAGAD", size = 16)
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NagadOrange,
                        selectedLabelColor = Color.White,
                        containerColor = GeoSurfaceContainerLow,
                        labelColor = GeoTextPrimary
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.testTag("filter_nagad")
                )
            }
        }

        if (filteredTasks.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    colors = CardDefaults.cardColors(containerColor = GeoCard),
                    border = BorderStroke(1.dp, GeoBorder),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No active tasks in this category",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = GeoTextSecondary
                        )
                        Text(
                            text = "Switch to Admin Mode at top to generate random tasks!",
                            fontSize = 12.sp,
                            color = GeoPrimary,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }
        } else {
            items(filteredTasks, key = { it.id }) { task ->
                TaskCard(
                    task = task,
                    onCopyNumber = {
                        clipboardManager.setText(AnnotatedString(task.targetNumber))
                        Toast.makeText(context, "Number copied: ${task.targetNumber}", Toast.LENGTH_SHORT).show()
                    },
                    onSubmitClick = { taskToSubmit = task }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Task Submission Dialog
    taskToSubmit?.let { task ->
        TaskSubmissionDialog(
            task = task,
            currentUser = currentUser,
            onDismiss = { taskToSubmit = null },
            onSubmit = { senderPhone, trxId, note ->
                onSubmitTask(task, senderPhone, trxId, note)
                taskToSubmit = null
            }
        )
    }
}

@Composable
fun TaskCard(
    task: TaskEntity,
    onCopyNumber: () -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBkash = task.method.uppercase() == "BKASH"
    val commissionEarned = task.amount * task.commissionRate

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = GeoCard),
        border = BorderStroke(1.dp, GeoBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("task_card_${task.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top row: Title and Today tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pending Task",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = GeoTextPrimary
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(GeoPrimary)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "TODAY",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Inner Task Item with Left Accent Border (Geometric Balance style)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(GeoSurfaceContainerLow)
                    .border(
                        BorderStroke(1.dp, GeoBorder.copy(alpha = 0.5f)),
                        RoundedCornerShape(16.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left color badge / logo
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isBkash) BkashPink else NagadOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        PaymentMethodLogo(method = task.method, size = 38)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Task details
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = task.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GeoTextPrimary
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { onCopyNumber() }
                                .padding(top = 2.dp)
                        ) {
                            Text(
                                text = "Target: ${task.targetNumber}",
                                fontSize = 11.sp,
                                color = GeoTextSecondary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = GeoPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }

                    // Amounts
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "৳ ${String.format("%.0f", task.amount)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoTextPrimary
                        )
                        Text(
                            text = "Earn ৳ ${String.format("%.1f", commissionEarned)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoPrimary
                        )
                    }
                }
            }

            if (task.instructions.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = task.instructions,
                    fontSize = 12.sp,
                    color = GeoTextSecondary,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onSubmitClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("submit_task_button_${task.id}"),
                colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Upload Screenshot & TrxID",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun TaskSubmissionDialog(
    task: TaskEntity,
    currentUser: UserEntity?,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String) -> Unit
) {
    var senderPhone by remember { mutableStateOf(currentUser?.phone ?: "") }
    var trxId by remember { mutableStateOf("") }
    var screenshotAttached by remember { mutableStateOf(true) }
    var screenshotName by remember { mutableStateOf("screenshot_${task.method.lowercase()}_successful.png") }
    val commission = task.amount * task.commissionRate
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = GeoCard),
            border = BorderStroke(1.dp, GeoBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PaymentMethodBadge(method = task.method)
                    Text(
                        text = "Amount: ৳${task.amount.toInt()}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = GeoTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Commission: ৳${String.format("%.1f", commission)} (4%)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = GeoPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GeoSurfaceContainerLow),
                    border = BorderStroke(1.dp, GeoBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Target Send Money Number", fontSize = 11.sp, color = GeoTextSecondary)
                            Text(task.targetNumber, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GeoPrimary)
                        }
                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString(task.targetNumber))
                            Toast.makeText(context, "Copied number", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = GeoPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = senderPhone,
                    onValueChange = { senderPhone = it },
                    label = { Text("Your Sender Phone Number") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sender_phone_input"),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = trxId,
                    onValueChange = { trxId = it.uppercase() },
                    label = { Text("Transaction ID (TrxID)") },
                    placeholder = { Text("e.g. 9K3X72MNQ1") },
                    leadingIcon = { Icon(Icons.Default.Receipt, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("trx_id_input"),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Screenshot Upload Mock Selector
                Text(
                    text = "Payment Screenshot / Proof:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GeoTextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (screenshotAttached) GeoCommissionContainer else GeoSurfaceContainerLow
                    ),
                    border = BorderStroke(1.dp, GeoBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            screenshotAttached = true
                            screenshotName = "proof_${System.currentTimeMillis() % 10000}.png"
                            Toast.makeText(context, "Screenshot selected from Gallery", Toast.LENGTH_SHORT).show()
                        }
                        .testTag("screenshot_selector")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (screenshotAttached) Icons.Default.CheckCircle else Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            tint = if (screenshotAttached) GeoOnCommission else GeoPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (screenshotAttached) "Screenshot Attached" else "Select Screenshot",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (screenshotAttached) GeoOnCommission else GeoTextPrimary
                            )
                            Text(
                                text = screenshotName,
                                fontSize = 11.sp,
                                color = GeoTextSecondary
                            )
                        }
                        Text(
                            text = "Change",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Cancel", color = GeoTextSecondary)
                    }

                    Button(
                        onClick = {
                            onSubmit(senderPhone, trxId, "Attached: $screenshotName")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("confirm_submit_task_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                        shape = RoundedCornerShape(16.dp),
                        enabled = senderPhone.isNotBlank() && trxId.isNotBlank()
                    ) {
                        Text("Submit Task", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
