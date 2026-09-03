package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.UserEntity
import com.example.data.model.WithdrawalEntity
import com.example.ui.components.PaymentMethodBadge
import com.example.ui.components.StatusBadge
import com.example.ui.components.TelegramSupportCard
import com.example.ui.theme.BkashPink
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WalletScreen(
    user: UserEntity?,
    withdrawals: List<WithdrawalEntity>,
    onWithdraw: (walletType: String, method: String, targetNumber: String, amount: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var showWithdrawModal by remember { mutableStateOf(false) }
    var preselectedWalletType by remember { mutableStateOf("COMMISSION") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GeoBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Dual Wallet & Withdrawals",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = GeoTextPrimary
            )
            Text(
                text = "Commission balance and main balance can be withdrawn separately via bKash or Nagad.",
                fontSize = 12.sp,
                color = GeoTextSecondary,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        // 1. COMMISSION BALANCE CARD (Geometric Balance soft rose container)
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = GeoCommissionContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("commission_wallet_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = null,
                                tint = GeoOnCommission,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Commission Wallet",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeoOnCommission
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(GeoCommissionPill)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("4% Profits", fontSize = 10.sp, color = GeoOnCommission, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "৳ ${String.format("%.2f", user?.commissionBalance ?: 0.0)}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = GeoOnCommission
                    )
                    Text(
                        text = "Earnings from 4% task commission + 200৳ referral bonus",
                        fontSize = 11.sp,
                        color = GeoOnCommission.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            preselectedWalletType = "COMMISSION"
                            showWithdrawModal = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("withdraw_commission_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Withdraw Commission", fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }
        }

        // 2. MAIN BALANCE CARD (Geometric Balance lavender container)
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = GeoPrimaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("main_wallet_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = GeoOnPrimaryContainer,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Main Balance Wallet",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeoOnPrimaryContainer
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(GeoPrimaryPill)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("Task Fund", fontSize = 10.sp, color = GeoOnPrimaryContainer, fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "৳ ${String.format("%.2f", user?.mainBalance ?: 0.0)}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = GeoOnPrimaryContainer
                    )
                    Text(
                        text = "Principal task deposit balance available for sending money",
                        fontSize = 11.sp,
                        color = GeoOnPrimaryContainer.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            preselectedWalletType = "MAIN"
                            showWithdrawModal = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("withdraw_main_balance_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Withdraw Main Balance", fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }
        }

        // TELEGRAM SUPPORT GROUP CARD
        item {
            TelegramSupportCard()
        }

        // WITHDRAWAL HISTORY HEADER
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Withdrawal Requests (${withdrawals.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = GeoTextPrimary
                )
                Text(
                    text = "Separate Wallet Records",
                    fontSize = 11.sp,
                    color = GeoTextSecondary
                )
            }
        }

        if (withdrawals.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = GeoCard),
                    border = BorderStroke(1.dp, GeoBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No withdrawal requests yet",
                            fontSize = 14.sp,
                            color = GeoTextSecondary
                        )
                    }
                }
            }
        } else {
            items(withdrawals, key = { it.id }) { item ->
                WithdrawalItemCard(withdrawal = item)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // WITHDRAWAL REQUEST MODAL
    if (showWithdrawModal) {
        WithdrawDialog(
            initialWalletType = preselectedWalletType,
            user = user,
            onDismiss = { showWithdrawModal = false },
            onSubmit = { walletType, method, targetNumber, amount ->
                onWithdraw(walletType, method, targetNumber, amount)
                showWithdrawModal = false
            }
        )
    }
}

@Composable
fun WithdrawalItemCard(withdrawal: WithdrawalEntity, modifier: Modifier = Modifier) {
    val isCommission = withdrawal.walletType == "COMMISSION"
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val dateStr = dateFormat.format(Date(withdrawal.requestedAt))

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GeoCard),
        border = BorderStroke(1.dp, GeoBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("withdrawal_item_${withdrawal.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PaymentMethodBadge(method = withdrawal.method)
                    Spacer(modifier = Modifier.width(8.dp))
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "To: ${withdrawal.targetNumber}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GeoTextPrimary
                    )
                    Text(
                        text = dateStr,
                        fontSize = 11.sp,
                        color = GeoTextSecondary
                    )
                }

                Text(
                    text = "- ৳ ${String.format("%.0f", withdrawal.amount)}",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (withdrawal.status == "REJECTED") GeoTextMuted else GeoTextPrimary
                )
            }

            if (withdrawal.adminNote.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Admin note: ${withdrawal.adminNote}",
                    fontSize = 11.sp,
                    color = if (withdrawal.status == "REJECTED") Color(0xFFDC2626) else GeoPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun WithdrawDialog(
    initialWalletType: String,
    user: UserEntity?,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, Double) -> Unit
) {
    var walletType by remember { mutableStateOf(initialWalletType) } // "COMMISSION" or "MAIN"
    var paymentMethod by remember { mutableStateOf("BKASH") } // "BKASH" or "NAGAD"
    var targetPhone by remember { mutableStateOf(user?.phone ?: "") }
    var amountText by remember { mutableStateOf("") }
    var stepState by remember { mutableStateOf("INPUT") } // "INPUT", "SUBMITTING", "SUCCESS", "ERROR"
    var errorMessage by remember { mutableStateOf("") }
    var submittedAmount by remember { mutableStateOf(0.0) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val currentBalance = if (walletType == "COMMISSION") {
        user?.commissionBalance ?: 0.0
    } else {
        user?.mainBalance ?: 0.0
    }

    Dialog(onDismissRequest = { if (stepState != "SUBMITTING") onDismiss() }) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = GeoCard),
            border = BorderStroke(1.dp, GeoBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .animateContentSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = if (stepState == "INPUT") Alignment.Start else Alignment.CenterHorizontally
            ) {
                when (stepState) {
                    "SUBMITTING" -> {
                        Spacer(modifier = Modifier.height(18.dp))
                        CircularProgressIndicator(
                            color = if (paymentMethod == "BKASH") BkashPink else NagadOrange,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Processing Withdrawal Request...",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Sending ৳${submittedAmount.toInt()} request to $paymentMethod gateway...",
                            fontSize = 12.sp,
                            color = GeoTextSecondary,
                            textAlign = TextAlign.Center
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
                            text = "Withdrawal Submitted!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Admin will review and disburse your funds.",
                            fontSize = 12.sp,
                            color = GeoTextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFF8FAFC))
                                .border(1.dp, GeoBorder, RoundedCornerShape(14.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Amount Requested:", fontSize = 12.sp, color = GeoTextSecondary)
                                    Text("৳${submittedAmount.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GeoTextPrimary)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Account Type:", fontSize = 12.sp, color = GeoTextSecondary)
                                    Text("$paymentMethod ($targetPhone)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = if (paymentMethod == "BKASH") BkashPink else NagadOrange)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Deducted From:", fontSize = 12.sp, color = GeoTextSecondary)
                                    Text("$walletType Balance", fontSize = 12.sp, color = GeoTextPrimary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("withdraw_success_done_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Done", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                    "ERROR" -> {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFEE2E2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.ErrorOutline,
                                contentDescription = "Error",
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Withdrawal Failed",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = errorMessage,
                            fontSize = 12.sp,
                            color = GeoTextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { stepState = "INPUT" },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("withdraw_retry_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Edit Request & Retry", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                    else -> {
                        Text(
                            text = "Request Withdrawal",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoTextPrimary
                        )
                        Text(
                            text = "Admin will verify and send funds to your account.",
                            fontSize = 12.sp,
                            color = GeoTextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // 1. SELECT WALLET TYPE (COMMISSION vs MAIN)
                        Text("Select Wallet to Withdraw From:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GeoTextPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { walletType = "COMMISSION" },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("select_wallet_commission"),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (walletType == "COMMISSION") GeoCommissionContainer else Color.Transparent
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Commission", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GeoOnCommission)
                                    Text("৳${String.format("%.0f", user?.commissionBalance ?: 0.0)}", fontSize = 10.sp, color = GeoTextSecondary)
                                }
                            }

                            OutlinedButton(
                                onClick = { walletType = "MAIN" },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("select_wallet_main"),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (walletType == "MAIN") GeoPrimaryContainer else Color.Transparent
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Main Balance", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GeoOnPrimaryContainer)
                                    Text("৳${String.format("%.0f", user?.mainBalance ?: 0.0)}", fontSize = 10.sp, color = GeoTextSecondary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 2. PAYMENT METHOD (BKASH or NAGAD)
                        Text("Select Payout Method:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GeoTextPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { paymentMethod = "BKASH" },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("select_payout_bkash"),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (paymentMethod == "BKASH") Color(0xFFFCE7F3) else Color.Transparent
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("bKash", fontWeight = FontWeight.Bold, color = BkashPink)
                            }

                            OutlinedButton(
                                onClick = { paymentMethod = "NAGAD" },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("select_payout_nagad"),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (paymentMethod == "NAGAD") Color(0xFFFEF3C7) else Color.Transparent
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Nagad", fontWeight = FontWeight.Bold, color = NagadOrange)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = targetPhone,
                            onValueChange = { targetPhone = it },
                            label = { Text("bKash/Nagad Account Number") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("withdraw_phone_input"),
                            shape = RoundedCornerShape(16.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = amountText,
                            onValueChange = { amountText = it },
                            label = { Text("Amount in Taka (৳)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("withdraw_amount_input"),
                            shape = RoundedCornerShape(16.dp)
                        )

                        // Quick Amount Buttons
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(100.0, 500.0, 1000.0, currentBalance).forEach { amt ->
                                OutlinedButton(
                                    onClick = {
                                        val cleanAmt = minOf(amt, currentBalance)
                                        amountText = cleanAmt.toInt().toString()
                                    },
                                    modifier = Modifier.weight(1f),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = if (amt == currentBalance) "Max" else "৳${amt.toInt()}",
                                        fontSize = 11.sp,
                                        color = GeoPrimary
                                    )
                                }
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
                                    val amount = amountText.toDoubleOrNull() ?: 0.0
                                    if (amount <= 0) {
                                        errorMessage = "Please enter a valid withdrawal amount greater than 0৳."
                                        stepState = "ERROR"
                                        return@Button
                                    }
                                    if (amount > currentBalance) {
                                        errorMessage = "Requested ৳${amount.toInt()} exceeds your current $walletType balance of ৳${currentBalance.toInt()}."
                                        stepState = "ERROR"
                                        return@Button
                                    }
                                    if (targetPhone.length < 10) {
                                        errorMessage = "Please enter a valid 11-digit bKash / Nagad mobile account number."
                                        stepState = "ERROR"
                                        return@Button
                                    }

                                    submittedAmount = amount
                                    stepState = "SUBMITTING"
                                    scope.launch {
                                        delay(850)
                                        onSubmit(walletType, paymentMethod, targetPhone, amount)
                                        stepState = "SUCCESS"
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("confirm_withdraw_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                                shape = RoundedCornerShape(16.dp),
                                enabled = targetPhone.isNotBlank() && amountText.isNotBlank()
                            ) {
                                Text("Withdraw", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
