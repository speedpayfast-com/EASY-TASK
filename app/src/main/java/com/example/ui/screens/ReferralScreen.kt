package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ReferralItemUiModel
import com.example.data.model.UserEntity
import com.example.ui.theme.AmberGold
import com.example.ui.theme.AmberLight
import com.example.ui.theme.BkashPink
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.GeoBackground
import com.example.ui.theme.GeoBorder
import com.example.ui.theme.GeoBorderSubtle
import com.example.ui.theme.GeoCard
import com.example.ui.theme.GeoCommissionContainer
import com.example.ui.theme.GeoCommissionPill
import com.example.ui.theme.GeoOnCommission
import com.example.ui.theme.GeoOnPrimaryContainer
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryContainer
import com.example.ui.theme.GeoPrimaryPill
import com.example.ui.theme.GeoSecondary
import com.example.ui.theme.GeoSecondaryContainer
import com.example.ui.theme.GeoSurface
import com.example.ui.theme.GeoSurfaceContainer
import com.example.ui.theme.GeoSurfaceContainerHigh
import com.example.ui.theme.GeoSurfaceContainerLow
import com.example.ui.theme.GeoTextMuted
import com.example.ui.theme.GeoTextPrimary
import com.example.ui.theme.GeoTextSecondary
import com.example.ui.theme.NagadOrange
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ReferralScreen(
    user: UserEntity?,
    referralList: List<ReferralItemUiModel>,
    onRefresh: (() -> Unit)? = null,
    onSimulateFriendTask: ((Long, Double, (Boolean, String) -> Unit) -> Unit)? = null,
    onAddSimulatedFriend: ((String, String, (Boolean) -> Unit) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val refCode = user?.referralCode ?: "ET7861"
    val refLink = "https://easytask.app/ref/$refCode"

    val shareText = "🚀 Hey! Join EASY TASK to earn 4% commission on daily bKash & Nagad tasks!\n\n" +
            "Use my invitation link: $refLink\n" +
            "Or enter Referral Code: $refCode\n\n" +
            "🎁 Complete ৳5,000 in tasks within 2 days to receive ৳100 instant welcome bonus!"

    // Live clock ticker for accurate real-time countdown calculation
    var currentClockTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000) // update countdown every second
            currentClockTime = System.currentTimeMillis()
        }
    }

    // Interactive UI State
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") } // "ALL", "ACTIVE", "WON", "EXPIRED"
    var linkCopiedFeedback by remember { mutableStateOf(false) }
    var codeCopiedFeedback by remember { mutableStateOf(false) }
    var showQrDialog by remember { mutableStateOf(false) }
    var showRulesDialog by remember { mutableStateOf(false) }
    var showInviteDialog by remember { mutableStateOf(false) }
    var selectedFriendForDetails by remember { mutableStateOf<ReferralItemUiModel?>(null) }
    var celebrationFriendName by remember { mutableStateOf<String?>(null) }

    // Aggregate statistics
    val totalFriends = referralList.size
    val wonFriends = referralList.filter { it.isReferrerRewarded }
    val activeFriends = referralList.filter { !it.isReferrerRewarded && !it.isExpired }
    val expiredFriends = referralList.filter { it.isExpired && !it.isReferrerRewarded }

    val totalBonusWon = wonFriends.size * 200.0
    val pendingBonusPotential = activeFriends.size * 200.0

    // Filtered list
    val filteredList = referralList.filter { item ->
        val matchesFilter = when (selectedFilter) {
            "ACTIVE" -> !item.isReferrerRewarded && !item.isExpired
            "WON" -> item.isReferrerRewarded
            "EXPIRED" -> item.isExpired && !item.isReferrerRewarded
            else -> true
        }
        val matchesSearch = item.refereeName.contains(searchQuery, ignoreCase = true) ||
                item.refereePhone.contains(searchQuery, ignoreCase = true)
        matchesFilter && matchesSearch
    }

    // Infinite pulsing animation for real-time live indicator
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_trans")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GeoBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // -------------------------------------------------------------
        // 1. DASHBOARD HEADER & REAL-TIME SYNC STATUS
        // -------------------------------------------------------------
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Referral Dashboard",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoTextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // Real-time live status chip
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(EmeraldLight)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldGreen.copy(alpha = pulseAlpha))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "LIVE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldGreen
                            )
                        }
                    }
                    Text(
                        text = "Track friends' progress & claim ৳200 bonus per milestone",
                        fontSize = 12.sp,
                        color = GeoTextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // Sync / Refresh button
                IconButton(
                    onClick = {
                        onRefresh?.invoke()
                        Toast.makeText(context, "Synced real-time referral data", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(GeoSurfaceContainerHigh)
                        .testTag("refresh_referrals_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh Referrals",
                        tint = GeoPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // -------------------------------------------------------------
        // 2. PERFORMANCE SUMMARY METRICS (4-STAT GRID)
        // -------------------------------------------------------------
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GeoCard),
                border = BorderStroke(1.dp, GeoBorderSubtle),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("referral_stats_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Metric 1: Total Invited
                        MetricItem(
                            label = "Total Friends",
                            value = "$totalFriends",
                            badgeText = "Network",
                            badgeBg = GeoPrimaryContainer,
                            badgeColor = GeoOnPrimaryContainer,
                            modifier = Modifier.weight(1f)
                        )

                        // Metric 2: Bonus Won
                        MetricItem(
                            label = "Bonus Won",
                            value = "৳${totalBonusWon.toInt()}",
                            badgeText = "${wonFriends.size} Paid",
                            badgeBg = EmeraldLight,
                            badgeColor = EmeraldGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Metric 3: Active Challenges
                        MetricItem(
                            label = "Active (48h)",
                            value = "${activeFriends.size}",
                            badgeText = "In Progress",
                            badgeBg = AmberLight,
                            badgeColor = AmberGold,
                            modifier = Modifier.weight(1f)
                        )

                        // Metric 4: Pending Potential
                        MetricItem(
                            label = "Pending Bonus",
                            value = "৳${pendingBonusPotential.toInt()}",
                            badgeText = "To Unlock",
                            badgeBg = GeoSecondaryContainer,
                            badgeColor = GeoSecondary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // 3. INVITATION LINK & CODE SHARING HUB
        // -------------------------------------------------------------
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GeoCard),
                border = BorderStroke(1.dp, GeoBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(GeoPrimaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    tint = GeoPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Your Unique Invitation Link",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeoTextPrimary
                            )
                        }

                        // QR Code Preview Action
                        TextButton(
                            onClick = { showQrDialog = true },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp), tint = GeoPrimary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("QR Code", fontSize = 12.sp, color = GeoPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Invitation Link Container
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(GeoSurfaceContainerLow)
                            .border(1.dp, GeoBorderSubtle, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "INVITE LINK",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeoTextMuted
                            )
                            Text(
                                text = refLink,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = GeoTextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(refLink))
                                linkCopiedFeedback = true
                                Toast.makeText(context, "Invitation link copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (linkCopiedFeedback) EmeraldGreen else GeoPrimary
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("copy_referral_link_button")
                        ) {
                            Icon(
                                imageVector = if (linkCopiedFeedback) Icons.Default.Check else Icons.Default.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (linkCopiedFeedback) "Copied!" else "Copy Link",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Referral Code Box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(GeoSurfaceContainerHigh)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Code: ",
                                fontSize = 12.sp,
                                color = GeoTextSecondary
                            )
                            Text(
                                text = refCode,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = GeoPrimary
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(refCode))
                                codeCopiedFeedback = true
                                Toast.makeText(context, "Code copied: $refCode", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, GeoPrimary),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("copy_referral_code_button")
                        ) {
                            Icon(
                                imageVector = if (codeCopiedFeedback) Icons.Default.Check else Icons.Default.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(13.dp),
                                tint = GeoPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (codeCopiedFeedback) "Code Copied" else "Copy Code",
                                fontSize = 11.sp,
                                color = GeoPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Share Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // System Share
                        Button(
                            onClick = {
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share Invitation Link via")
                                context.startActivity(shareIntent)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("share_referral_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share Link", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        // WhatsApp Direct Share
                        Button(
                            onClick = {
                                try {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                        type = "text/plain"
                                        setPackage("com.whatsapp")
                                    }
                                    context.startActivity(sendIntent)
                                } catch (e: Exception) {
                                    // Fallback to normal chooser if WhatsApp isn't installed
                                    val fallbackIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(fallbackIntent, "Share via"))
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("whatsapp_share_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("WhatsApp", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // 4. 2-DAY ৳5,000 CHALLENGE REWARDS EXPLAINER
        // -------------------------------------------------------------
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GeoPrimaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(GeoPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CardGiftcard,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "2-Day ৳5,000 Challenge",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GeoOnPrimaryContainer
                                )
                                Text(
                                    text = "Earn cash rewards upon friend qualification",
                                    fontSize = 11.sp,
                                    color = GeoPrimary
                                )
                            }
                        }

                        IconButton(
                            onClick = { showRulesDialog = true },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Rules Info",
                                tint = GeoPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Referrer Reward
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = GeoCard),
                            border = BorderStroke(1.dp, GeoBorderSubtle)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("You (Referrer)", fontSize = 11.sp, color = GeoTextSecondary)
                                Text("৳200", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GeoPrimary)
                                Text("Cash Bonus", fontSize = 10.sp, color = GeoTextMuted)
                            }
                        }

                        // Referee Reward
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = GeoCommissionContainer),
                            border = BorderStroke(1.dp, GeoBorderSubtle)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Friend (New User)", fontSize = 11.sp, color = GeoOnCommission)
                                Text("৳100", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GeoOnCommission)
                                Text("Welcome Bonus", fontSize = 10.sp, color = GeoOnCommission.copy(alpha = 0.7f))
                            }
                        }

                        // Lifetime Commission
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = GeoCard),
                            border = BorderStroke(1.dp, GeoBorderSubtle)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Lifetime", fontSize = 11.sp, color = GeoTextSecondary)
                                Text("4%", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                                Text("On Every Task", fontSize = 10.sp, color = GeoTextMuted)
                            }
                        }
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // 5. REFERRED FRIENDS REAL-TIME TRACKER SECTION
        // -------------------------------------------------------------
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Referred Friends Tracker",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoTextPrimary
                        )
                        Text(
                            text = "Real-time task volume & bonus status",
                            fontSize = 11.sp,
                            color = GeoTextSecondary
                        )
                    }

                    // Test simulation button to invite friend
                    OutlinedButton(
                        onClick = { showInviteDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, GeoPrimary),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("simulate_invite_button")
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(14.dp), tint = GeoPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Simulate Invite", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GeoPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search friends by name or phone...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp), tint = GeoTextSecondary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = GeoCard,
                        unfocusedContainerColor = GeoCard,
                        focusedBorderColor = GeoPrimary,
                        unfocusedBorderColor = GeoBorderSubtle
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("search_referrals_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Filter Chips
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == "ALL",
                        onClick = { selectedFilter = "ALL" },
                        label = { Text("All (${referralList.size})", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GeoPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = selectedFilter == "ACTIVE",
                        onClick = { selectedFilter = "ACTIVE" },
                        label = { Text("In Progress (${activeFriends.size})", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GeoPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = selectedFilter == "WON",
                        onClick = { selectedFilter = "WON" },
                        label = { Text("Bonus Won (${wonFriends.size})", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldGreen,
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = selectedFilter == "EXPIRED",
                        onClick = { selectedFilter = "EXPIRED" },
                        label = { Text("Expired (${expiredFriends.size})", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFDC2626),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // -------------------------------------------------------------
        // 6. REFERRED FRIENDS LIST OR EMPTY STATE
        // -------------------------------------------------------------
        if (filteredList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GeoCard),
                    border = BorderStroke(1.dp, GeoBorderSubtle)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(GeoSurfaceContainerHigh),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = GeoTextSecondary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No matching friends found" else "No referred friends in this tab",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoTextPrimary
                        )
                        Text(
                            text = "Share your invite link to earn ৳200 for every friend who completes the 2-day challenge!",
                            fontSize = 12.sp,
                            color = GeoTextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { showInviteDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Simulated Friend", fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            items(filteredList, key = { it.refereePhone + it.registeredAt }) { friendItem ->
                ReferredFriendTrackerCard(
                    item = friendItem,
                    currentClockTime = currentClockTime,
                    onNudge = {
                        val remaining = (friendItem.targetVolume - friendItem.currentVolume).coerceAtLeast(0.0)
                        val nudgeMsg = "Hey ${friendItem.refereeName}! 🚀 You're making great progress on EASY TASK! " +
                                "Only ৳${remaining.toInt()} more in tasks to unlock your ৳100 welcome cash bonus! Complete it before the 48h limit ends."
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, nudgeMsg)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Send Reminder to ${friendItem.refereeName}"))
                    },
                    onSimulateTask = {
                        if (onSimulateFriendTask != null && friendItem.refereeId > 0) {
                            onSimulateFriendTask(friendItem.refereeId, 1000.0) { success, msg ->
                                if (success) {
                                    val willHitTarget = (friendItem.currentVolume + 1000.0) >= friendItem.targetVolume
                                    if (willHitTarget && !friendItem.isReferrerRewarded) {
                                        celebrationFriendName = friendItem.refereeName
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(context, "Task progress simulated (+৳1,000)", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onViewDetails = { selectedFriendForDetails = friendItem }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // -------------------------------------------------------------
    // MODALS & DIALOGS
    // -------------------------------------------------------------

    // 1. QR Code Dialog
    if (showQrDialog) {
        QrCodeDialog(
            refLink = refLink,
            refCode = refCode,
            onDismiss = { showQrDialog = false }
        )
    }

    // 2. Rules & Explainer Dialog
    if (showRulesDialog) {
        ChallengeRulesDialog(onDismiss = { showRulesDialog = false })
    }

    // 3. Add / Simulate Friend Dialog
    if (showInviteDialog) {
        SimulateInviteDialog(
            onDismiss = { showInviteDialog = false },
            onConfirm = { name, phone ->
                onAddSimulatedFriend?.invoke(name, phone) { success ->
                    if (success) {
                        Toast.makeText(context, "Invited $name! 48h challenge started.", Toast.LENGTH_SHORT).show()
                        showInviteDialog = false
                    }
                }
            }
        )
    }

    // 4. Friend Details Dialog
    selectedFriendForDetails?.let { friend ->
        FriendDetailsDialog(
            item = friend,
            currentClockTime = currentClockTime,
            onDismiss = { selectedFriendForDetails = null }
        )
    }

    // 5. Celebration Dialog for Bonus Unlock
    celebrationFriendName?.let { friendName ->
        AlertDialog(
            onDismissRequest = { celebrationFriendName = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.Celebration,
                    contentDescription = null,
                    tint = AmberGold,
                    modifier = Modifier.size(44.dp)
                )
            },
            title = {
                Text(
                    text = "🎉 Referral Bonus Unlocked!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = GeoTextPrimary
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Congratulations! Your friend $friendName has successfully completed the ৳5,000 task challenge within 2 days!",
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        color = GeoTextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(EmeraldLight)
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "৳200 Added to Commission Wallet",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreen
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { celebrationFriendName = null },
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Awesome!", color = Color.White)
                }
            }
        )
    }
}

// -------------------------------------------------------------
// REFERRED FRIEND CARD COMPONENT
// -------------------------------------------------------------
@Composable
fun ReferredFriendTrackerCard(
    item: ReferralItemUiModel,
    currentClockTime: Long,
    onNudge: () -> Unit,
    onSimulateTask: () -> Unit,
    onViewDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = (item.currentVolume / item.targetVolume).coerceIn(0.0, 1.0).toFloat()
    val progressPercent = (progress * 100).toInt()
    val millisLeft = (item.deadlineAt - currentClockTime).coerceAtLeast(0L)
    val totalHoursLeft = (millisLeft / (1000 * 60 * 60)).toInt()
    val minutesLeft = ((millisLeft % (1000 * 60 * 60)) / (1000 * 60)).toInt()
    val isExpiredNow = millisLeft <= 0L && !item.isReferrerRewarded

    // Mask phone for privacy: 01712***210
    val maskedPhone = if (item.refereePhone.length >= 8) {
        item.refereePhone.take(5) + "***" + item.refereePhone.takeLast(3)
    } else {
        item.refereePhone
    }

    // Relative registration time
    val hoursSinceReg = ((currentClockTime - item.registeredAt) / (1000 * 60 * 60)).toInt()
    val registeredLabel = if (hoursSinceReg < 1) "Joined just now" else "Joined ${hoursSinceReg}h ago"

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GeoCard),
        border = BorderStroke(
            width = 1.dp,
            color = when {
                item.isReferrerRewarded -> EmeraldGreen.copy(alpha = 0.5f)
                isExpiredNow -> Color(0xFFFCA5A5)
                else -> GeoBorderSubtle
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onViewDetails() }
            .testTag("referral_friend_card_${item.refereePhone}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Avatar, Name, Phone & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    item.isReferrerRewarded -> EmeraldLight
                                    isExpiredNow -> Color(0xFFFEE2E2)
                                    else -> GeoPrimaryContainer
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.refereeName.take(1).uppercase(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                item.isReferrerRewarded -> EmeraldGreen
                                isExpiredNow -> Color(0xFFDC2626)
                                else -> GeoPrimary
                            }
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = item.refereeName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoTextPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = maskedPhone,
                                fontSize = 11.sp,
                                color = GeoTextSecondary
                            )
                            Text(
                                text = " • $registeredLabel",
                                fontSize = 10.sp,
                                color = GeoTextMuted
                            )
                        }
                    }
                }

                // Status Badge
                if (item.isReferrerRewarded) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(EmeraldLight)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldGreen,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "৳200 Won",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreen
                        )
                    }
                } else if (isExpiredNow) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFFFEE2E2))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "2 Days Expired",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626)
                        )
                    }
                } else {
                    // Active countdown timer badge
                    val timerBg = if (totalHoursLeft < 12) AmberLight else GeoPrimaryContainer
                    val timerColor = if (totalHoursLeft < 12) AmberGold else GeoPrimary
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(timerBg)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = timerColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${totalHoursLeft}h ${minutesLeft}m left",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = timerColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Task Progress Metric & Percentage
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Task Progress: ৳${item.currentVolume.toInt()} / ৳${item.targetVolume.toInt()}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GeoTextPrimary
                )
                Text(
                    text = "$progressPercent%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        item.isReferrerRewarded -> EmeraldGreen
                        isExpiredNow -> Color(0xFFDC2626)
                        else -> GeoPrimary
                    }
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Material 3 Progress Bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = when {
                    item.isReferrerRewarded -> EmeraldGreen
                    isExpiredNow -> Color(0xFFDC2626)
                    else -> GeoPrimary
                },
                trackColor = GeoSurfaceContainerHigh
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Status Description
            if (item.isReferrerRewarded) {
                Text(
                    text = "✅ ৳5,000 goal achieved! ৳200 bonus credited to your Commission Wallet.",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = EmeraldGreen
                )
            } else if (isExpiredNow) {
                Text(
                    text = "⚠️ 48-hour challenge period expired before reaching ৳5,000 volume.",
                    fontSize = 11.sp,
                    color = Color(0xFFDC2626)
                )
            } else {
                val remaining = (item.targetVolume - item.currentVolume).coerceAtLeast(0.0)
                Text(
                    text = "Needs ৳${remaining.toInt()} more in tasks to unlock ৳200 for you + ৳100 for them.",
                    fontSize = 11.sp,
                    color = GeoTextSecondary
                )
            }

            // Action Buttons for active friends
            if (!item.isReferrerRewarded && !isExpiredNow) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Nudge / Remind button
                    OutlinedButton(
                        onClick = onNudge,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, GeoBorder),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(13.dp), tint = GeoTextSecondary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Remind Friend", fontSize = 11.sp, color = GeoTextSecondary, fontWeight = FontWeight.SemiBold)
                    }

                    // Test Advance Task Volume button
                    Button(
                        onClick = onSimulateTask,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GeoPrimaryContainer),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .testTag("simulate_friend_task_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(13.dp), tint = GeoOnPrimaryContainer)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+৳1,000 Task", fontSize = 11.sp, color = GeoOnPrimaryContainer, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// METRIC ITEM HELPER
// -------------------------------------------------------------
@Composable
private fun MetricItem(
    label: String,
    value: String,
    badgeText: String,
    badgeBg: Color,
    badgeColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = GeoSurfaceContainerLow),
        border = BorderStroke(1.dp, GeoBorderSubtle)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = GeoTextSecondary
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(badgeBg)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = GeoTextPrimary
            )
        }
    }
}

// -------------------------------------------------------------
// QR CODE PREVIEW DIALOG
// -------------------------------------------------------------
@Composable
fun QrCodeDialog(
    refLink: String,
    refCode: String,
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = GeoCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Scan to Join",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GeoTextPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = GeoTextSecondary)
                    }
                }

                Text(
                    text = "Friends can scan this code to register directly using your link",
                    fontSize = 12.sp,
                    color = GeoTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // Rendered Stylized QR Canvas
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(2.dp, GeoBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(168.dp)) {
                        val cellSize = size.width / 15f
                        val darkColor = Color(0xFF21005D)

                        // 3 Corner QR Position Detectors
                        fun drawPositionPattern(row: Int, col: Int) {
                            drawRect(
                                color = darkColor,
                                topLeft = Offset(col * cellSize, row * cellSize),
                                size = Size(cellSize * 4, cellSize * 4)
                            )
                            drawRect(
                                color = Color.White,
                                topLeft = Offset((col + 0.8f) * cellSize, (row + 0.8f) * cellSize),
                                size = Size(cellSize * 2.4f, cellSize * 2.4f)
                            )
                            drawRect(
                                color = darkColor,
                                topLeft = Offset((col + 1.4f) * cellSize, (row + 1.4f) * cellSize),
                                size = Size(cellSize * 1.2f, cellSize * 1.2f)
                            )
                        }

                        drawPositionPattern(0, 0)
                        drawPositionPattern(0, 11)
                        drawPositionPattern(11, 0)

                        // Algorithmic pattern data seeded from referral code
                        val seed = refCode.hashCode()
                        for (r in 0 until 15) {
                            for (c in 0 until 15) {
                                val inCorner = (r < 5 && c < 5) || (r < 5 && c >= 10) || (r >= 10 && c < 5)
                                if (!inCorner) {
                                    val cellHash = (seed xor (r * 31 + c * 17))
                                    if (cellHash % 2 == 0) {
                                        drawRect(
                                            color = darkColor,
                                            topLeft = Offset(c * cellSize + 1f, r * cellSize + 1f),
                                            size = Size(cellSize - 2f, cellSize - 2f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Centered app badge inside QR
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(GeoPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ET",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Referral Code: $refCode",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = GeoPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(refLink))
                        Toast.makeText(context, "Link copied!", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy Invitation Link")
                }
            }
        }
    }
}

// -------------------------------------------------------------
// CHALLENGE RULES DIALOG
// -------------------------------------------------------------
@Composable
fun ChallengeRulesDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = GeoPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("2-Day Challenge Rules", fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "1. 48-Hour Window:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = GeoTextPrimary
                )
                Text(
                    text = "The countdown begins the exact second your referred friend registers their account.",
                    fontSize = 12.sp,
                    color = GeoTextSecondary
                )

                Text(
                    text = "2. ৳5,000 Task Volume:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = GeoTextPrimary
                )
                Text(
                    text = "Friend must complete and get approved for a cumulative volume of 5,000৳ in bKash or Nagad Send Money tasks.",
                    fontSize = 12.sp,
                    color = GeoTextSecondary
                )

                Text(
                    text = "3. Instant Payout:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = GeoTextPrimary
                )
                Text(
                    text = "• You earn ৳200 instant cash bonus credited to your Commission Wallet.\n• Your friend receives ৳100 welcome cash bonus.",
                    fontSize = 12.sp,
                    color = GeoTextSecondary
                )

                Text(
                    text = "4. Lifetime 4% Earnings:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = GeoTextPrimary
                )
                Text(
                    text = "Each Send Money task completed also pays out the standard 4% commission independently.",
                    fontSize = 12.sp,
                    color = GeoTextSecondary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary)
            ) {
                Text("Got it")
            }
        }
    )
}

// -------------------------------------------------------------
// SIMULATE INVITE DIALOG
// -------------------------------------------------------------
@Composable
fun SimulateInviteDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    val sampleNames = listOf("Arif Hossain", "Sadia Karim", "Mahmudul Hasan", "Farhana Yeasmin")
    val defaultName = remember { sampleNames.random() }
    val defaultPhone = remember { "01" + (700000000 + (10000000..99999999).random()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Simulate Friend Invitation", fontSize = 17.sp, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Create a friend who joined with your referral code to test real-time progress tracking.",
                    fontSize = 12.sp,
                    color = GeoTextSecondary
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text(defaultName, fontSize = 12.sp) },
                    label = { Text("Friend's Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = { Text(defaultPhone, fontSize = 12.sp) },
                    label = { Text("Phone Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalName = name.ifEmpty { defaultName }
                    val finalPhone = phone.ifEmpty { defaultPhone }
                    onConfirm(finalName, finalPhone)
                },
                colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Start 48h Challenge", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = GeoTextSecondary)
            }
        }
    )
}

// -------------------------------------------------------------
// FRIEND DETAILS DIALOG
// -------------------------------------------------------------
@Composable
fun FriendDetailsDialog(
    item: ReferralItemUiModel,
    currentClockTime: Long,
    onDismiss: () -> Unit
) {
    val progress = (item.currentVolume / item.targetVolume).coerceIn(0.0, 1.0).toFloat()
    val progressPercent = (progress * 100).toInt()
    val millisLeft = (item.deadlineAt - currentClockTime).coerceAtLeast(0L)
    val totalHoursLeft = (millisLeft / (1000 * 60 * 60)).toInt()
    val minutesLeft = ((millisLeft % (1000 * 60 * 60)) / (1000 * 60)).toInt()

    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val regDateStr = sdf.format(Date(item.registeredAt))
    val deadlineDateStr = sdf.format(Date(item.deadlineAt))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(GeoPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.refereeName.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = GeoPrimary
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(item.refereeName, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(item.refereePhone, fontSize = 11.sp, color = GeoTextSecondary)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Volume & Status
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = GeoSurfaceContainerLow)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Challenge Status", fontSize = 11.sp, color = GeoTextSecondary)
                            Text(
                                text = when {
                                    item.isReferrerRewarded -> "BONUS PAID"
                                    item.isExpired -> "EXPIRED"
                                    else -> "ACTIVE ($totalHoursLeft" + "h left)"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    item.isReferrerRewarded -> EmeraldGreen
                                    item.isExpired -> Color(0xFFDC2626)
                                    else -> GeoPrimary
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (item.isReferrerRewarded) EmeraldGreen else GeoPrimary,
                            trackColor = GeoSurfaceContainerHigh
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "৳${item.currentVolume.toInt()} / ৳${item.targetVolume.toInt()} ($progressPercent%) completed",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GeoTextPrimary
                        )
                    }
                }

                // Dates Breakdown
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("• Registered At: $regDateStr", fontSize = 11.sp, color = GeoTextSecondary)
                    Text("• 48h Deadline: $deadlineDateStr", fontSize = 11.sp, color = GeoTextSecondary)
                    Text("• Tasks Completed: ${item.completedTasksCount} approved", fontSize = 11.sp, color = GeoTextSecondary)
                    Text(
                        text = if (item.isReferrerRewarded) {
                            "• Referrer Bonus: ৳200 Credited to Commission Wallet"
                        } else {
                            "• Referrer Bonus: ৳200 Pending milestone completion"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (item.isReferrerRewarded) EmeraldGreen else GeoTextPrimary
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary)
            ) {
                Text("Close")
            }
        }
    )
}
