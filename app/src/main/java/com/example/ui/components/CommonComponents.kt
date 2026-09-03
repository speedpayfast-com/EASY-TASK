package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.UserEntity
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
import com.example.ui.theme.GeoCommissionPill
import com.example.ui.theme.GeoOnCommission
import com.example.ui.theme.GeoOnPrimaryContainer
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryContainer
import com.example.ui.theme.GeoPrimaryPill
import com.example.ui.theme.GeoSecondary
import com.example.ui.theme.GeoSurfaceContainer
import com.example.ui.theme.GeoSurfaceContainerHigh
import com.example.ui.theme.GeoSurfaceContainerLow
import com.example.ui.theme.GeoTextMuted
import com.example.ui.theme.GeoTextPrimary
import com.example.ui.theme.GeoTextSecondary
import com.example.ui.theme.NagadOrange
import com.example.ui.theme.NagadOrangeLight
import com.example.ui.theme.NagadRed
import com.example.ui.viewmodel.OtpDialogState

@Composable
fun AppHeader(
    user: UserEntity?,
    isAdminMode: Boolean,
    onToggleAdmin: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    isOfflineMode: Boolean = false,
    onToggleOffline: (() -> Unit)? = null,
    onLogout: (() -> Unit)? = null,
    onProfileClick: (() -> Unit)? = null
) {
    Surface(
        color = GeoBackground,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "EASY TASK",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoPrimary,
                            letterSpacing = (-0.5).sp
                        )
                        if (isOfflineMode) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = AmberGold.copy(alpha = 0.18f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudOff,
                                        contentDescription = "Offline Mode",
                                        tint = AmberGold,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "OFFLINE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AmberGold
                                    )
                                }
                            }
                        }
                    }
                    Text(
                        text = if (isAdminMode) "Admin Control Center" else if (user != null) "ID: ${user.referralCode}" else "Welcome",
                        fontSize = 12.sp,
                        color = GeoTextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Offline / Online Mode Toggle Button
                    if (onToggleOffline != null) {
                        Surface(
                            onClick = onToggleOffline,
                            shape = RoundedCornerShape(20.dp),
                            color = if (isOfflineMode) AmberGold.copy(alpha = 0.15f) else GeoSurfaceContainerHigh,
                            border = if (isOfflineMode) BorderStroke(1.dp, AmberGold.copy(alpha = 0.6f)) else null,
                            modifier = Modifier.testTag("offline_mode_pill")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isOfflineMode) Icons.Default.CloudOff else Icons.Default.CloudDone,
                                    contentDescription = "Toggle Offline Simulation",
                                    tint = if (isOfflineMode) AmberGold else EmeraldGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isOfflineMode) "Offline" else "Online",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isOfflineMode) AmberGold else EmeraldGreen
                                )
                            }
                        }
                    }

                    // Switch between User & Admin Mode
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(GeoSurfaceContainerHigh)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin Mode Toggle",
                            tint = if (isAdminMode) GeoPrimary else GeoTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isAdminMode) "Admin" else "User",
                            fontSize = 11.sp,
                            color = if (isAdminMode) GeoPrimary else GeoTextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Switch(
                            checked = isAdminMode,
                            onCheckedChange = onToggleAdmin,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = GeoPrimary,
                                uncheckedThumbColor = GeoTextSecondary,
                                uncheckedTrackColor = GeoBorder
                            ),
                            modifier = Modifier
                                .size(34.dp, 22.dp)
                                .testTag("admin_mode_switch")
                        )
                    }

                    if (user != null && onLogout != null) {
                        IconButton(
                            onClick = onLogout,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(GeoSurfaceContainerHigh)
                                .testTag("header_logout_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Log out",
                                tint = GeoTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Avatar Circle with initial (clickable to open Profile)
                    val initials = if (user != null && user.name.isNotBlank()) {
                        user.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase()
                    } else "ET"

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(GeoPrimaryContainer)
                            .border(2.dp, Color.White, CircleShape)
                            .then(
                                if (!isAdminMode && onProfileClick != null) {
                                    Modifier.clickable { onProfileClick() }
                                } else Modifier
                            )
                            .testTag("header_avatar_profile_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            color = GeoOnPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Telegram Support Group Quick Bar
            val context = LocalContext.current
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/+9EvTCl-BZYo2ZWQ1"))
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Telegram: https://t.me/+9EvTCl-BZYo2ZWQ1", Toast.LENGTH_LONG).show()
                        }
                    },
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF229ED9).copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, Color(0xFF229ED9).copy(alpha = 0.45f)),
                    modifier = Modifier.testTag("support_group_header_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Telegram Support Group",
                            tint = Color(0xFF229ED9),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Support Group",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF229ED9)
                        )
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF229ED9)
                        ) {
                            Text(
                                text = "TG",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "টেলিগ্রাম সাপোর্ট গ্রুপ",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = GeoTextSecondary
                )
            }

            // Dual balance display for regular user (Geometric Balance Cards)
            if (!isAdminMode && user != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Main Balance Card (Soft Lavender / Lilac)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(120.dp)
                            .testTag("main_balance_header"),
                        colors = CardDefaults.cardColors(containerColor = GeoPrimaryContainer),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "MAIN BALANCE",
                                fontSize = 11.sp,
                                color = GeoOnPrimaryContainer,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "৳ ${String.format("%.2f", user.mainBalance)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeoOnPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(GeoPrimaryPill)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "Withdrawal Available",
                                    fontSize = 10.sp,
                                    color = GeoOnPrimaryContainer,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Commission Balance Card (Muted Rose / Pink)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(120.dp)
                            .testTag("commission_balance_header"),
                        colors = CardDefaults.cardColors(containerColor = GeoCommissionContainer),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "COMMISSION",
                                fontSize = 11.sp,
                                color = GeoOnCommission,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "৳ ${String.format("%.2f", user.commissionBalance)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeoOnCommission
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(GeoCommissionPill)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "4% Rate Active",
                                    fontSize = 10.sp,
                                    color = GeoOnCommission,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppBottomNav(
    activeTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = GeoSurfaceContainer,
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = GeoBorder, shape = RoundedCornerShape(0.dp))
    ) {
        NavigationBar(
            containerColor = GeoSurfaceContainer,
            tonalElevation = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            NavigationBarItem(
                selected = activeTab == "tasks",
                onClick = { onTabSelected("tasks") },
                icon = { Icon(Icons.Default.ListAlt, contentDescription = "Tasks") },
                label = { Text("Tasks", fontWeight = if (activeTab == "tasks") FontWeight.Bold else FontWeight.Medium) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = GeoOnPrimaryContainer,
                    selectedTextColor = GeoOnPrimaryContainer,
                    indicatorColor = GeoPrimaryContainer,
                    unselectedIconColor = GeoTextSecondary,
                    unselectedTextColor = GeoTextSecondary
                ),
                modifier = Modifier.testTag("nav_tasks")
            )
            NavigationBarItem(
                selected = activeTab == "history",
                onClick = { onTabSelected("history") },
                icon = { Icon(Icons.Default.History, contentDescription = "Transaction History") },
                label = { Text("History", fontWeight = if (activeTab == "history") FontWeight.Bold else FontWeight.Medium) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = GeoOnPrimaryContainer,
                    selectedTextColor = GeoOnPrimaryContainer,
                    indicatorColor = GeoPrimaryContainer,
                    unselectedIconColor = GeoTextSecondary,
                    unselectedTextColor = GeoTextSecondary
                ),
                modifier = Modifier.testTag("nav_history")
            )
            NavigationBarItem(
                selected = activeTab == "wallet",
                onClick = { onTabSelected("wallet") },
                icon = { Icon(Icons.Default.Wallet, contentDescription = "Wallet") },
                label = { Text("Withdraw", fontWeight = if (activeTab == "wallet") FontWeight.Bold else FontWeight.Medium) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = GeoOnPrimaryContainer,
                    selectedTextColor = GeoOnPrimaryContainer,
                    indicatorColor = GeoPrimaryContainer,
                    unselectedIconColor = GeoTextSecondary,
                    unselectedTextColor = GeoTextSecondary
                ),
                modifier = Modifier.testTag("nav_wallet")
            )
            NavigationBarItem(
                selected = activeTab == "referral",
                onClick = { onTabSelected("referral") },
                icon = { Icon(Icons.Default.Share, contentDescription = "Referral") },
                label = { Text("Referral", fontWeight = if (activeTab == "referral") FontWeight.Bold else FontWeight.Medium) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = GeoOnPrimaryContainer,
                    selectedTextColor = GeoOnPrimaryContainer,
                    indicatorColor = GeoPrimaryContainer,
                    unselectedIconColor = GeoTextSecondary,
                    unselectedTextColor = GeoTextSecondary
                ),
                modifier = Modifier.testTag("nav_referral")
            )
            NavigationBarItem(
                selected = activeTab == "profile",
                onClick = { onTabSelected("profile") },
                icon = { Icon(Icons.Default.Person, contentDescription = "User Profile") },
                label = { Text("Profile", fontWeight = if (activeTab == "profile") FontWeight.Bold else FontWeight.Medium) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = GeoOnPrimaryContainer,
                    selectedTextColor = GeoOnPrimaryContainer,
                    indicatorColor = GeoPrimaryContainer,
                    unselectedIconColor = GeoTextSecondary,
                    unselectedTextColor = GeoTextSecondary
                ),
                modifier = Modifier.testTag("nav_profile")
            )
        }
    }
}

@Composable
fun PaymentMethodLogo(
    method: String,
    modifier: Modifier = Modifier,
    size: Int = 36
) {
    val isBkash = method.uppercase() == "BKASH"
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isBkash) BkashPinkLight else NagadOrangeLight)
            .border(
                1.dp,
                if (isBkash) BkashPink.copy(alpha = 0.4f) else NagadOrange.copy(alpha = 0.4f),
                RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        val drawableRes = if (isBkash) R.drawable.img_bkash_logo else R.drawable.img_nagad_logo
        Image(
            painter = painterResource(id = drawableRes),
            contentDescription = if (isBkash) "bKash Logo" else "Nagad Logo",
            modifier = Modifier
                .size((size - 6).dp)
                .clip(RoundedCornerShape(6.dp)),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun PaymentMethodBadge(method: String, modifier: Modifier = Modifier) {
    val isBkash = method.uppercase() == "BKASH"
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isBkash) BkashPinkLight else NagadOrangeLight)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        PaymentMethodLogo(method = method, size = 18)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = if (isBkash) "bKash" else "Nagad",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isBkash) BkashPink else NagadRed
        )
    }
}

@Composable
fun StatusBadge(status: String, modifier: Modifier = Modifier) {
    val (bgColor, textColor, label) = when (status.uppercase()) {
        "APPROVED" -> Triple(EmeraldLight, EmeraldGreen, "Approved")
        "REJECTED" -> Triple(Color(0xFFFCE4E4), Color(0xFFB91C1C), "Rejected")
        else -> Triple(GeoSurfaceContainerHigh, GeoTextPrimary, "Pending Review")
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}

@Composable
fun CommissionTag(amount: Double, commissionRate: Double = 0.04, modifier: Modifier = Modifier) {
    val earned = amount * commissionRate
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(GeoCommissionContainer)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Earn 4% : ",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = GeoOnCommission
            )
            Text(
                text = "+৳${String.format("%.1f", earned)}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = GeoOnCommission
            )
        }
    }
}

// --- OTP Verification Dialog ---
@Composable
fun OtpVerificationDialog(
    otpState: OtpDialogState,
    onOtpChange: (String) -> Unit,
    onAutoFill: () -> Unit,
    onVerify: () -> Unit,
    onResend: () -> Unit,
    onDismiss: () -> Unit
) {
    if (otpState.isOpen) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = GeoCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, GeoBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(if (otpState.method == "WHATSAPP") Color(0xFFDCFCE7) else GeoPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (otpState.method == "WHATSAPP") Icons.Default.Message else Icons.Default.Notifications,
                            contentDescription = "OTP Notification",
                            tint = if (otpState.method == "WHATSAPP") Color(0xFF16A34A) else GeoOnPrimaryContainer,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Verify Phone Number",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GeoTextPrimary
                    )
                    Text(
                        text = "We sent a 6-digit verification code to\n${otpState.targetPhone} via ${otpState.method}",
                        fontSize = 13.sp,
                        color = GeoTextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    // Simulated Real Notification Banner
                    if (otpState.simulatedNotification != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = GeoSurfaceContainerLow),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GeoBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = GeoPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "New Message received",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GeoTextPrimary
                                    )
                                    Text(
                                        text = otpState.simulatedNotification,
                                        fontSize = 11.sp,
                                        color = GeoTextSecondary
                                    )
                                }
                                OutlinedButton(
                                    onClick = onAutoFill,
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.testTag("autofill_otp_button")
                                ) {
                                    Text("Fill", fontSize = 11.sp, color = GeoPrimary)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = otpState.enteredOtp,
                        onValueChange = { if (it.length <= 6) onOtpChange(it) },
                        label = { Text("Enter 6-digit OTP") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("otp_input_field"),
                        shape = RoundedCornerShape(16.dp)
                    )

                    if (otpState.errorMessage != null) {
                        Text(
                            text = otpState.errorMessage,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (otpState.secondsLeft > 0) {
                            Text(
                                text = "Resend in ${otpState.secondsLeft}s",
                                fontSize = 12.sp,
                                color = GeoTextSecondary
                            )
                        } else {
                            Text(
                                text = "Resend OTP",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeoPrimary,
                                modifier = Modifier
                                    .clickable { onResend() }
                                    .testTag("resend_otp_text")
                            )
                        }

                        Text(
                            text = "Cancel",
                            fontSize = 12.sp,
                            color = GeoTextSecondary,
                            modifier = Modifier.clickable { onDismiss() }
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = onVerify,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("verify_otp_confirm_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                        shape = RoundedCornerShape(16.dp),
                        enabled = otpState.enteredOtp.length >= 4
                    ) {
                        Text("Verify & Continue", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun RoomCacheBadge(
    modifier: Modifier = Modifier,
    label: String = "Room DB Cache"
) {
    Surface(
        color = GeoSurfaceContainerHigh,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.6f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Storage,
                contentDescription = null,
                tint = GeoTextSecondary,
                modifier = Modifier.size(11.dp)
            )
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = GeoTextSecondary
            )
        }
    }
}

@Composable
fun OfflineStatusBanner(
    isOffline: Boolean,
    cachedTransactionsCount: Int,
    cachedTasksCount: Int,
    lastSyncTimestamp: Long?,
    onToggleOffline: () -> Unit,
    onSync: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isOffline) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .testTag("offline_status_banner"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AmberGold.copy(alpha = 0.12f)),
            border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(AmberGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = "Offline Mode",
                            tint = AmberGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Offline Mode Active",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeoTextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = AmberGold.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Room SQLite Cache",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberGold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "Viewing cached history ($cachedTransactionsCount txns, $cachedTasksCount tasks)",
                            fontSize = 11.sp,
                            color = GeoTextSecondary
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        onClick = onSync,
                        shape = RoundedCornerShape(10.dp),
                        color = GeoCard,
                        border = BorderStroke(1.dp, GeoBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Sync",
                                tint = GeoPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Sync",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeoPrimary
                            )
                        }
                    }

                    Surface(
                        onClick = onToggleOffline,
                        shape = RoundedCornerShape(10.dp),
                        color = AmberGold,
                        border = BorderStroke(1.dp, AmberGold)
                    ) {
                        Text(
                            text = "Go Online",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// TELEGRAM SUPPORT GROUP CARD COMPONENT
// ==========================================
@Composable
fun TelegramSupportCard(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF229ED9).copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, Color(0xFF229ED9).copy(alpha = 0.35f)),
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/+9EvTCl-BZYo2ZWQ1"))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Telegram: https://t.me/+9EvTCl-BZYo2ZWQ1", Toast.LENGTH_SHORT).show()
                }
            }
            .testTag("telegram_support_card")
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
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF229ED9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Telegram Icon",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Support Group",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF229ED9)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = Color(0xFF229ED9).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Official",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0088CC),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "আমাদের অফিসিয়াল টেলিগ্রাম গ্রুপে যুক্ত হোন",
                        fontSize = 12.sp,
                        color = GeoTextSecondary
                    )
                }
            }
            Button(
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/+9EvTCl-BZYo2ZWQ1"))
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Telegram: https://t.me/+9EvTCl-BZYo2ZWQ1", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF229ED9)),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text("Join", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
