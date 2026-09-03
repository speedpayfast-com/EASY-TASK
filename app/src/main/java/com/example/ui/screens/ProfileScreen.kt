package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ReferralItemUiModel
import com.example.data.model.TaskSubmissionEntity
import com.example.data.model.TransactionLogEntity
import com.example.data.model.UserEntity
import com.example.data.model.WithdrawalEntity
import com.example.ui.components.TelegramSupportCard
import com.example.ui.theme.AmberGold
import com.example.ui.theme.BkashPink
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GeoBackground
import com.example.ui.theme.GeoBorder
import com.example.ui.theme.GeoOnPrimaryContainer
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryContainer
import com.example.ui.theme.GeoSurface
import com.example.ui.theme.GeoSurfaceContainer
import com.example.ui.theme.GeoSurfaceContainerHigh
import com.example.ui.theme.GeoTextPrimary
import com.example.ui.theme.GeoTextSecondary
import com.example.ui.theme.NavyDark
import com.example.ui.theme.RoyalBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun ProfileScreen(
    user: UserEntity?,
    submissions: List<TaskSubmissionEntity>,
    withdrawals: List<WithdrawalEntity>,
    transactionLogs: List<TransactionLogEntity>,
    referrals: List<ReferralItemUiModel>,
    onUpdateDisplayName: (String, (Boolean) -> Unit) -> Unit,
    onNavigateToTab: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Display Name Editing State
    var isEditingName by remember { mutableStateOf(false) }
    var inputName by remember(user?.name) { mutableStateOf(user?.name ?: "") }
    var isSaving by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf<String?>(null) }

    // Account Creation Date Calculations
    val createdAt = user?.createdAt ?: System.currentTimeMillis()
    val formattedCreationDate = remember(createdAt) {
        val sdf = SimpleDateFormat("dd MMMM, yyyy", Locale.ENGLISH)
        sdf.format(Date(createdAt))
    }
    val formattedCreationTime = remember(createdAt) {
        val sdf = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        sdf.format(Date(createdAt))
    }
    val daysAsMember = remember(createdAt) {
        val diff = System.currentTimeMillis() - createdAt
        val days = TimeUnit.MILLISECONDS.toDays(diff)
        if (days <= 0) "Joined Today" else "$days days as member"
    }

    // Earnings Calculations
    val approvedSubmissions = remember(submissions) {
        submissions.filter { it.status.equals("APPROVED", ignoreCase = true) }
    }
    val pendingSubmissions = remember(submissions) {
        submissions.filter { it.status.equals("PENDING", ignoreCase = true) }
    }
    val taskCommissionsEarned = remember(approvedSubmissions) {
        approvedSubmissions.sumOf { it.commissionEarned }
    }
    val completedTasksCount = approvedSubmissions.size

    val referralBonusEarned = remember(referrals, user?.referredBy) {
        val referrerCount = referrals.count { it.isReferrerRewarded }
        val bonusFromFriends = referrerCount * 200.0
        val welcomeBonus = if (user?.referredBy != null) 100.0 else 0.0
        bonusFromFriends + welcomeBonus
    }

    val approvedWithdrawals = remember(withdrawals) {
        withdrawals.filter { it.status.equals("APPROVED", ignoreCase = true) }
    }
    val totalWithdrawn = remember(approvedWithdrawals) {
        approvedWithdrawals.sumOf { it.amount }
    }
    val pendingWithdrawals = remember(withdrawals) {
        withdrawals.filter { it.status.equals("PENDING", ignoreCase = true) }
    }
    val pendingWithdrawalAmount = remember(pendingWithdrawals) {
        pendingWithdrawals.sumOf { it.amount }
    }

    // Total Lifetime Earnings (Commissions + Referral Bonuses + Current Commission Wallet)
    val lifetimeTotalEarnings = remember(taskCommissionsEarned, referralBonusEarned, user?.commissionBalance) {
        maxOf(taskCommissionsEarned + referralBonusEarned, user?.commissionBalance ?: 0.0)
    }

    // User Initials
    val initials = remember(user?.name) {
        if (!user?.name.isNullOrBlank()) {
            user.name.split(" ")
                .mapNotNull { it.firstOrNull()?.toString() }
                .take(2)
                .joinToString("")
                .uppercase()
        } else "ET"
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GeoBackground)
            .padding(horizontal = 16.dp)
            .testTag("profile_screen_list"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
    ) {

        // ==========================================
        // 1. USER IDENTITY & AVATAR HERO CARD
        // ==========================================
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = GeoSurfaceContainer),
                border = BorderStroke(1.dp, GeoBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("user_profile_hero_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar Box
                    Box(
                        modifier = Modifier
                            .size(86.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(RoyalBlue, GeoPrimary)
                                )
                            )
                            .border(3.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Display Name
                    Text(
                        text = user?.name ?: "User Profile",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = GeoTextPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("profile_display_name_text")
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Phone number with verified badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Phone",
                            tint = GeoTextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = user?.phone ?: "",
                            fontSize = 14.sp,
                            color = GeoTextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = EmeraldGreen.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified",
                                    tint = EmeraldGreen,
                                    modifier = Modifier.size(11.dp)
                                )
                                Text(
                                    text = "Verified",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldGreen
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Referral ID pill with click-to-copy
                    Surface(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Referral Code", user?.referralCode ?: "")
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Referral Code Copied!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(20.dp),
                        color = GeoSurfaceContainerHigh,
                        border = BorderStroke(1.dp, GeoBorder),
                        modifier = Modifier.testTag("profile_copy_referral_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Referral ID: ${user?.referralCode ?: "---"}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GeoPrimary
                            )
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Referral ID",
                                tint = GeoPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quick Toggle Edit Name Button
                    OutlinedButton(
                        onClick = {
                            isEditingName = !isEditingName
                            if (isEditingName) {
                                inputName = user?.name ?: ""
                                nameError = null
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, GeoPrimary.copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isEditingName) GeoPrimary.copy(alpha = 0.1f) else Color.Transparent
                        ),
                        modifier = Modifier.testTag("profile_toggle_edit_name_btn")
                    ) {
                        Icon(
                            imageVector = if (isEditingName) Icons.Default.Close else Icons.Default.Edit,
                            contentDescription = "Edit Name",
                            tint = GeoPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isEditingName) "Close Name Editor" else "Edit Display Name (নাম পরিবর্তন)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoPrimary
                        )
                    }
                }
            }
        }

        // ==========================================
        // 2. EDIT DISPLAY NAME FORM CARD
        // ==========================================
        AnimatedVisibility(visible = isEditingName) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GeoSurface),
                border = BorderStroke(1.5.dp, GeoPrimary.copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("edit_display_name_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(GeoPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Badge,
                                contentDescription = "Edit Display Name",
                                tint = GeoPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Change Display Name",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeoTextPrimary
                            )
                            Text(
                                text = "আপনার নতুন নাম লিখুন যা অ্যাপের সর্বত্র দেখা যাবে",
                                fontSize = 11.sp,
                                color = GeoTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = inputName,
                        onValueChange = {
                            inputName = it
                            if (nameError != null) nameError = null
                        },
                        label = { Text("Display Name") },
                        placeholder = { Text("e.g. ${user?.name ?: "Sumaiya Akhter"}") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = GeoPrimary
                            )
                        },
                        trailingIcon = {
                            if (inputName.isNotBlank()) {
                                IconButton(onClick = { inputName = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = GeoTextSecondary
                                    )
                                }
                            }
                        },
                        isError = nameError != null,
                        supportingText = {
                            if (nameError != null) {
                                Text(
                                    text = nameError ?: "",
                                    color = Color(0xFFDC2626),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            } else {
                                Text(
                                    text = "Minimum 2 characters • বাংলায় বা ইংরেজিতে লেখা যাবে",
                                    fontSize = 11.sp,
                                    color = GeoTextSecondary
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GeoPrimary,
                            unfocusedBorderColor = GeoBorder,
                            focusedContainerColor = GeoBackground,
                            unfocusedContainerColor = GeoBackground
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("display_name_input_field")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                inputName = user?.name ?: ""
                                isEditingName = false
                                nameError = null
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = GeoTextSecondary)
                        }

                        Button(
                            onClick = {
                                val trimmed = inputName.trim()
                                when {
                                    trimmed.isBlank() -> {
                                        nameError = "Name cannot be empty"
                                    }
                                    trimmed.length < 2 -> {
                                        nameError = "Name must be at least 2 characters"
                                    }
                                    trimmed == user?.name -> {
                                        nameError = "Enter a new name to update"
                                    }
                                    else -> {
                                        isSaving = true
                                        nameError = null
                                        keyboardController?.hide()
                                        onUpdateDisplayName(trimmed) { success ->
                                            isSaving = false
                                            if (success) {
                                                isEditingName = false
                                            }
                                        }
                                    }
                                }
                            },
                            enabled = !isSaving && inputName.trim().isNotBlank(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                            modifier = Modifier
                                .weight(1.5f)
                                .testTag("save_display_name_btn")
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Updating...", color = Color.White)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Save Changes", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // 3. ACCOUNT CREATION DATE & DETAILS CARD
        // ==========================================
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GeoSurface),
                border = BorderStroke(1.dp, GeoBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("account_creation_details_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(RoyalBlue.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Creation Date",
                                tint = RoyalBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Account Creation & Info",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeoTextPrimary
                            )
                            Text(
                                text = "অ্যাকাউন্ট তৈরি ও সদস্যপদের বিস্তারিত বিবরণ",
                                fontSize = 11.sp,
                                color = GeoTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = GeoBorder)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Account Creation Date Row (Highlighted)
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = GeoSurfaceContainerHigh,
                        border = BorderStroke(1.dp, GeoBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(GeoPrimaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = "Created At",
                                        tint = GeoOnPrimaryContainer,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Creation Date (তৈরির তারিখ)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = GeoTextSecondary
                                    )
                                    Text(
                                        text = formattedCreationDate,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GeoTextPrimary,
                                        modifier = Modifier.testTag("profile_creation_date_text")
                                    )
                                    Text(
                                        text = "$formattedCreationTime • $daysAsMember",
                                        fontSize = 11.sp,
                                        color = GeoTextSecondary
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EmeraldGreen.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "Active",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Meta Info Rows
                    ProfileInfoRow(
                        icon = Icons.Default.Badge,
                        label = "Account ID",
                        value = "#${user?.id ?: 1001}"
                    )

                    ProfileInfoRow(
                        icon = Icons.Default.Phone,
                        label = "Registered Mobile",
                        value = user?.phone ?: "018..."
                    )

                    ProfileInfoRow(
                        icon = Icons.Default.Share,
                        label = "Referred By",
                        value = user?.referredBy ?: "Direct Registration (সরাসরি)"
                    )

                    ProfileInfoRow(
                        icon = Icons.Default.Security,
                        label = "Account Security",
                        value = "OTP Verified • PIN Protected"
                    )
                }
            }
        }

        // ==========================================
        // 4. TOTAL EARNINGS SUMMARY CARD
        // ==========================================
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = GeoSurface),
                border = BorderStroke(1.dp, GeoBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("total_earnings_summary_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = "Earnings",
                                    tint = EmeraldGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Total Earnings Summary",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GeoTextPrimary
                                )
                                Text(
                                    text = "আয়ের সর্বমোট সারসংক্ষেপ",
                                    fontSize = 11.sp,
                                    color = GeoTextSecondary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = EmeraldGreen.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "4% Rate",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldGreen,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // HERO LIFETIME EARNINGS BANNER
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = EmeraldGreen.copy(alpha = 0.1f),
                        border = BorderStroke(1.5.dp, EmeraldGreen.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("lifetime_earnings_banner")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "LIFETIME TOTAL EARNINGS (সর্বমোট মোট আয়)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldGreen,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "৳ ${String.format("%.2f", lifetimeTotalEarnings)}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = EmeraldGreen,
                                modifier = Modifier.testTag("lifetime_earnings_amount_text")
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Approved Tasks Commission + Referral Bonuses",
                                fontSize = 11.sp,
                                color = GeoTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // TWO COLUMN DETAILED EARNINGS METRICS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Task Commissions Card
                        EarningsStatBox(
                            title = "Task Commission",
                            bengaliTitle = "টাস্ক কমিশন (৪%)",
                            amount = "৳ ${String.format("%.2f", taskCommissionsEarned)}",
                            subText = "$completedTasksCount Tasks Done",
                            icon = Icons.Default.TaskAlt,
                            accentColor = RoyalBlue,
                            modifier = Modifier.weight(1f)
                        )

                        // Referral Bonuses Card
                        EarningsStatBox(
                            title = "Referral Bonus",
                            bengaliTitle = "রেফারেল বোনাস",
                            amount = "৳ ${String.format("%.2f", referralBonusEarned)}",
                            subText = "${referrals.size} Friends Invited",
                            icon = Icons.Default.Share,
                            accentColor = GeoPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // WALLET BALANCES BREAKDOWN
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Current Commission Balance (Withdrawable)
                        EarningsStatBox(
                            title = "Withdrawable",
                            bengaliTitle = "উত্তোলনযোগ্য কমিশন",
                            amount = "৳ ${String.format("%.2f", user?.commissionBalance ?: 0.0)}",
                            subText = "Ready in Wallet",
                            icon = Icons.Default.AccountBalanceWallet,
                            accentColor = BkashPink,
                            modifier = Modifier.weight(1f)
                        )

                        // Main Working Balance
                        EarningsStatBox(
                            title = "Working Balance",
                            bengaliTitle = "কার্যকর ব্যালেন্স",
                            amount = "৳ ${String.format("%.2f", user?.mainBalance ?: 0.0)}",
                            subText = "For Send Money",
                            icon = Icons.Default.AccountBalance,
                            accentColor = NavyDark,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // PAYOUT SUMMARY ROW
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = GeoSurfaceContainerHigh,
                        border = BorderStroke(1.dp, GeoBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Payments,
                                    contentDescription = "Payouts",
                                    tint = GeoPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = "Total Payouts Withdrawn",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = GeoTextSecondary
                                    )
                                    Text(
                                        text = "৳ ${String.format("%.2f", totalWithdrawn)}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GeoTextPrimary
                                    )
                                }
                            }

                            if (pendingWithdrawalAmount > 0) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = AmberGold.copy(alpha = 0.15f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.HourglassBottom,
                                            contentDescription = null,
                                            tint = AmberGold,
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Text(
                                            text = "৳ ${String.format("%.0f", pendingWithdrawalAmount)} Pending",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AmberGold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ACTION BUTTON: WITHDRAW EARNINGS
                    Button(
                        onClick = { onNavigateToTab("wallet") },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_go_to_withdraw_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Withdraw Earnings (টাকা উত্তোলন করুন)",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // ==========================================
        // 5. QUICK SHORTCUTS & ACTIVITY
        // ==========================================
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GeoSurface),
                border = BorderStroke(1.dp, GeoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Quick Navigation (দ্রুত মেন্যু)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GeoTextPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onNavigateToTab("tasks") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Tasks", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GeoPrimary)
                        }

                        OutlinedButton(
                            onClick = { onNavigateToTab("history") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("History", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GeoPrimary)
                        }

                        OutlinedButton(
                            onClick = { onNavigateToTab("referral") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Referral", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GeoPrimary)
                        }
                    }
                }
            }
        }

        // ==========================================
        // 6. TELEGRAM SUPPORT GROUP
        // ==========================================
        item {
            TelegramSupportCard()
        }
    }
}

// -------------------------------------------------------------
// HELPER COMPOSABLE: Profile Info Row
// -------------------------------------------------------------
@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = GeoTextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                fontSize = 13.sp,
                color = GeoTextSecondary
            )
        }
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = GeoTextPrimary
        )
    }
}

// -------------------------------------------------------------
// HELPER COMPOSABLE: Earnings Stat Box
// -------------------------------------------------------------
@Composable
private fun EarningsStatBox(
    title: String,
    bengaliTitle: String,
    amount: String,
    subText: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = GeoSurfaceContainerHigh,
        border = BorderStroke(1.dp, GeoBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GeoTextSecondary
                )
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = amount,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = GeoTextPrimary
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subText,
                fontSize = 10.sp,
                color = accentColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
