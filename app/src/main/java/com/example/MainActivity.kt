package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AppBottomNav
import com.example.ui.components.AppHeader
import com.example.ui.components.OtpVerificationDialog
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ReferralScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.EasyTaskViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val viewModel: EasyTaskViewModel = viewModel()
                EasyTaskApp(viewModel)
            }
        }
    }
}

@Composable
fun EasyTaskApp(viewModel: EasyTaskViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isAdminMode by viewModel.isAdminMode.collectAsState()
    val activeTasks by viewModel.activeTasks.collectAsState()
    val allTasks by viewModel.allTasks.collectAsState()
    val submissions by viewModel.userSubmissions.collectAsState()
    val userTransactionLogs by viewModel.userTransactionLogs.collectAsState()
    val withdrawals by viewModel.userWithdrawals.collectAsState()
    val referrals by viewModel.referralList.collectAsState()
    val otpState by viewModel.otpState.collectAsState()
    val snackbarMsg by viewModel.snackbarMessage.collectAsState()

    val adminPendingSubmissions by viewModel.adminPendingSubmissions.collectAsState()
    val adminPendingWithdrawals by viewModel.adminPendingWithdrawals.collectAsState()
    val adminAllUsers by viewModel.adminAllUsers.collectAsState()

    val isOfflineMode by viewModel.isOfflineMode.collectAsState()
    val cacheMetadata by viewModel.cacheMetadata.collectAsState()

    var activeTab by remember { mutableStateOf("tasks") }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            AppHeader(
                user = currentUser,
                isAdminMode = isAdminMode,
                isOfflineMode = isOfflineMode,
                onToggleOffline = { viewModel.toggleOfflineMode() },
                onToggleAdmin = { enabled ->
                    viewModel.toggleAdminMode(enabled)
                },
                onLogout = {
                    viewModel.logout()
                },
                onProfileClick = {
                    activeTab = "profile"
                }
            )
        },
        bottomBar = {
            if (currentUser != null && !isAdminMode) {
                AppBottomNav(
                    activeTab = activeTab,
                    onTabSelected = { activeTab = it }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (currentUser == null) {
                // Authentication Screen (Register with Referral & OTP, or Login)
                AuthScreen(
                    viewModel = viewModel,
                    onAuthSuccess = {
                        Toast.makeText(context, "Welcome to EASY TASK!", Toast.LENGTH_SHORT).show()
                    }
                )
            } else if (isAdminMode) {
                // ADMIN CONTROL CENTER
                AdminPanelScreen(
                    viewModel = viewModel,
                    allTasks = allTasks,
                    pendingSubmissions = adminPendingSubmissions,
                    pendingWithdrawals = adminPendingWithdrawals,
                    allUsers = adminAllUsers
                )
            } else {
                // USER APPLICATION SCREENS
                when (activeTab) {
                    "tasks" -> {
                        TasksScreen(
                            tasks = activeTasks,
                            currentUser = currentUser,
                            submissions = submissions,
                            isOffline = isOfflineMode,
                            cacheMetadata = cacheMetadata,
                            onToggleOffline = { viewModel.toggleOfflineMode() },
                            onSync = { viewModel.syncOfflineData() },
                            onViewTaskStatuses = { activeTab = "history" },
                            onSubmitTask = { task, senderPhone, trxId, note ->
                                viewModel.submitTask(task, senderPhone, trxId, note) {
                                    activeTab = "history"
                                }
                            }
                        )
                    }
                    "history" -> {
                        HistoryScreen(
                            transactionLogs = userTransactionLogs,
                            submissions = submissions,
                            isOffline = isOfflineMode,
                            cacheMetadata = cacheMetadata,
                            onToggleOffline = { viewModel.toggleOfflineMode() },
                            onSync = { viewModel.syncOfflineData() }
                        )
                    }
                    "wallet" -> {
                        WalletScreen(
                            user = currentUser,
                            withdrawals = withdrawals,
                            onWithdraw = { walletType, method, targetNumber, amount ->
                                viewModel.withdraw(walletType, method, targetNumber, amount) {
                                    Toast.makeText(context, "Withdrawal submitted!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                    "referral" -> {
                        ReferralScreen(
                            user = currentUser,
                            referralList = referrals,
                            onRefresh = { viewModel.refreshReferrals() },
                            onSimulateFriendTask = { refereeId, amount, onDone ->
                                viewModel.simulateFriendTask(refereeId, amount, onDone)
                            },
                            onAddSimulatedFriend = { name, phone, onDone ->
                                viewModel.addSimulatedFriend(name, phone, onDone)
                            }
                        )
                    }
                    "profile" -> {
                        ProfileScreen(
                            user = currentUser,
                            submissions = submissions,
                            withdrawals = withdrawals,
                            transactionLogs = userTransactionLogs,
                            referrals = referrals,
                            onUpdateDisplayName = { newName, onDone ->
                                viewModel.updateDisplayName(newName, onDone)
                            },
                            onNavigateToTab = { tab ->
                                activeTab = tab
                            }
                        )
                    }
                }
            }

            // Global OTP verification dialog for logged-in user profile changes
            if (currentUser != null) {
                OtpVerificationDialog(
                    otpState = otpState,
                    onOtpChange = { viewModel.setEnteredOtp(it) },
                    onAutoFill = { viewModel.autoFillOtp() },
                    onVerify = {
                        viewModel.verifyOtp { success, phone ->
                            if (success) {
                                Toast.makeText(context, "Phone $phone verified successfully!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onResend = { viewModel.resendOtp() },
                    onDismiss = { viewModel.closeOtpDialog() }
                )
            }
        }
    }
}
