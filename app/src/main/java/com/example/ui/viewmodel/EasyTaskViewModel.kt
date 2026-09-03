package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.model.CacheMetadataEntity
import com.example.data.model.ReferralItemUiModel
import com.example.data.model.TaskEntity
import com.example.data.model.TaskSubmissionEntity
import com.example.data.model.TransactionLogEntity
import com.example.data.model.UserEntity
import com.example.data.model.WithdrawalEntity
import com.example.data.repository.EasyTaskRepository
import com.example.util.NetworkMonitor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

data class OtpDialogState(
    val isOpen: Boolean = false,
    val targetPhone: String = "",
    val method: String = "SMS", // "SMS" or "WHATSAPP"
    val generatedOtp: String = "",
    val enteredOtp: String = "",
    val secondsLeft: Int = 60,
    val simulatedNotification: String? = null,
    val errorMessage: String? = null,
    val isVerified: Boolean = false,
    val isSubmitting: Boolean = false
)

class EasyTaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = EasyTaskRepository(AppDatabase.getInstance(application).appDao())
    private val networkMonitor = NetworkMonitor(application)

    // Offline & Network State
    val isDeviceConnected: StateFlow<Boolean> = networkMonitor.isDeviceConnected
    val isSimulatedOffline: StateFlow<Boolean> = networkMonitor.isSimulatedOffline
    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline
    val isOfflineMode: StateFlow<Boolean> = networkMonitor.isOffline

    val cacheMetadata: StateFlow<CacheMetadataEntity?> = repository.cacheMetadata
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        viewModelScope.launch {
            repository.initializeSeedDataIfNeeded()
            // Auto login first user (demo user) so app is immediately interactive
            val firstUser = repository.getUserByPhone("01811223344")
            if (firstUser != null) {
                _currentUser.value = firstUser
                repository.syncExistingDataToLogsIfNeeded(firstUser.id)
                loadReferrals(firstUser.id)
            }
        }
    }

    // --- State Holders ---
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // Live reactive stream for current user balance and status updates
    val liveUser: StateFlow<UserEntity?> = _currentUser
        .flatMapLatest { user ->
            if (user != null) repository.getUserFlow(user.id) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isAdminMode = MutableStateFlow(false)
    val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

    private val _isAdminAuthenticated = MutableStateFlow(false)
    val isAdminAuthenticated: StateFlow<Boolean> = _isAdminAuthenticated.asStateFlow()

    // Admin login password requested: 101024
    private val _adminPasscode = MutableStateFlow("101024")
    val adminPasscode: StateFlow<String> = _adminPasscode.asStateFlow()

    private val _userTab = MutableStateFlow("tasks") // "tasks", "history", "wallet", "referral", "profile"
    val userTab: StateFlow<String> = _userTab.asStateFlow()

    private val _adminTab = MutableStateFlow("overview") // "overview", "tasks", "submissions", "withdrawals", "users"
    val adminTab: StateFlow<String> = _adminTab.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // OTP State
    private val _otpState = MutableStateFlow(OtpDialogState())
    val otpState: StateFlow<OtpDialogState> = _otpState.asStateFlow()
    private var countdownJob: Job? = null

    data class PendingRegistration(
        val name: String,
        val phone: String,
        val whatsapp: String,
        val pin: String,
        val referralCode: String?
    )
    private var pendingRegistration: PendingRegistration? = null

    // Referrals UI list
    private val _referralList = MutableStateFlow<List<ReferralItemUiModel>>(emptyList())
    val referralList: StateFlow<List<ReferralItemUiModel>> = _referralList.asStateFlow()

    // Data streams from repository
    val activeTasks: StateFlow<List<TaskEntity>> = repository.activeTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userSubmissions: StateFlow<List<TaskSubmissionEntity>> = _currentUser
        .flatMapLatest { user ->
            if (user != null) repository.getUserSubmissions(user.id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userWithdrawals: StateFlow<List<WithdrawalEntity>> = _currentUser
        .flatMapLatest { user ->
            if (user != null) repository.getUserWithdrawals(user.id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userTransactionLogs: StateFlow<List<TransactionLogEntity>> = _currentUser
        .flatMapLatest { user ->
            if (user != null) repository.getUserTransactionLogs(user.id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminPendingSubmissions: StateFlow<List<TaskSubmissionEntity>> = repository.pendingSubmissions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminAllSubmissions: StateFlow<List<TaskSubmissionEntity>> = repository.allSubmissions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminPendingWithdrawals: StateFlow<List<WithdrawalEntity>> = repository.pendingWithdrawals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminAllWithdrawals: StateFlow<List<WithdrawalEntity>> = repository.allWithdrawals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminAllUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminAllTransactionLogs: StateFlow<List<TransactionLogEntity>> = repository.allTransactionLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Tab Navigation & Admin Security ---
    fun authenticateAdmin(pin: String): Boolean {
        val trimmed = pin.trim()
        val isValid = trimmed == _adminPasscode.value
        if (isValid) {
            _isAdminAuthenticated.value = true
            _isAdminMode.value = true
            _snackbarMessage.value = "Admin Login Successful! Welcome Admin."
            return true
        } else {
            _snackbarMessage.value = "ভুল পাসওয়ার্ড! শুধুমাত্র এডমিন লগইন করতে পারবে।"
            return false
        }
    }

    fun lockAdminSession() {
        _isAdminAuthenticated.value = false
        _isAdminMode.value = false
        _snackbarMessage.value = "Admin session locked securely"
    }

    fun changeAdminPasscode(newPin: String): Boolean {
        if (newPin.isNotBlank()) {
            _adminPasscode.value = newPin
            _snackbarMessage.value = "Admin login password updated successfully"
            return true
        } else {
            _snackbarMessage.value = "Password cannot be empty"
            return false
        }
    }

    fun setUserTab(tab: String) {
        _userTab.value = tab
        if (tab == "referral") {
            _currentUser.value?.id?.let { loadReferrals(it) }
        }
    }

    fun setAdminTab(tab: String) {
        _adminTab.value = tab
    }

    fun toggleAdminMode(enabled: Boolean) {
        if (enabled) {
            _isAdminMode.value = true
            if (_isAdminAuthenticated.value) {
                _snackbarMessage.value = "Admin Panel Active"
            }
        } else {
            _isAdminMode.value = false
            _isAdminAuthenticated.value = false // Require password 101024 next time entering Admin Mode!
            _snackbarMessage.value = "Switched to User Mode"
        }
    }

    fun updateDisplayName(newName: String, onDone: (Boolean) -> Unit = {}) {
        val user = _currentUser.value ?: return
        val trimmed = newName.trim()
        if (trimmed.length < 2) {
            _snackbarMessage.value = "Display name must be at least 2 characters"
            onDone(false)
            return
        }
        viewModelScope.launch {
            val success = repository.updateUserDisplayName(user.id, trimmed)
            if (success) {
                val updated = user.copy(name = trimmed)
                _currentUser.value = updated
                _snackbarMessage.value = "Display name updated to \"$trimmed\""
                onDone(true)
            } else {
                _snackbarMessage.value = "Failed to update display name"
                onDone(false)
            }
        }
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun refreshReferrals() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val list = repository.getReferralUiList(user.id)
            _referralList.value = list
            val updated = repository.getUserById(user.id)
            if (updated != null) {
                _currentUser.value = updated
            }
        }
    }

    fun loadReferrals(userId: Long) {
        viewModelScope.launch {
            val list = repository.getReferralUiList(userId)
            _referralList.value = list
            val updated = repository.getUserById(userId)
            if (updated != null) {
                _currentUser.value = updated
            }
        }
    }

    fun simulateFriendTask(refereeUserId: Long, amount: Double, onDone: (Boolean, String) -> Unit) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val result = repository.advanceFriendTaskProgress(refereeUserId, amount)
            if (result.isSuccess) {
                val list = repository.getReferralUiList(user.id)
                _referralList.value = list
                val updated = repository.getUserById(user.id)
                if (updated != null) {
                    _currentUser.value = updated
                }
                _snackbarMessage.value = "Friend completed task (+৳${amount.toInt()})! Progress updated."
                onDone(true, "Progress updated successfully!")
            } else {
                onDone(false, result.exceptionOrNull()?.message ?: "Failed to advance progress")
            }
        }
    }

    fun addSimulatedFriend(name: String, phone: String, onDone: (Boolean) -> Unit) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val res = repository.addSimulatedReferredFriend(user.id, name, phone)
            if (res.isSuccess) {
                val list = repository.getReferralUiList(user.id)
                _referralList.value = list
                _snackbarMessage.value = "New friend invited! 48h Challenge started."
                onDone(true)
            } else {
                onDone(false)
            }
        }
    }

    // --- OTP Verification System ---
    fun requestOtp(phone: String, method: String) {
        if (phone.length < 10) {
            _snackbarMessage.value = "Please enter a valid phone number"
            return
        }
        val otp = (100000 + Random.nextInt(900000)).toString()
        val notif = if (method == "WHATSAPP") {
            "WhatsApp: Your EASY TASK verification OTP is $otp. Valid for 2 minutes."
        } else {
            "SMS: Your EASY TASK security code is $otp. Do not share this with anyone."
        }

        _otpState.value = OtpDialogState(
            isOpen = true,
            targetPhone = phone,
            method = method,
            generatedOtp = otp,
            enteredOtp = "",
            secondsLeft = 60,
            simulatedNotification = notif,
            errorMessage = null,
            isVerified = false
        )

        startOtpCountdown()
    }

    private fun startOtpCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            for (sec in 60 downTo 1) {
                _otpState.value = _otpState.value.copy(secondsLeft = sec)
                delay(1000)
            }
            _otpState.value = _otpState.value.copy(secondsLeft = 0)
        }
    }

    fun setEnteredOtp(code: String) {
        _otpState.value = _otpState.value.copy(enteredOtp = code, errorMessage = null)
    }

    fun autoFillOtp() {
        _otpState.value = _otpState.value.copy(
            enteredOtp = _otpState.value.generatedOtp,
            errorMessage = null
        )
    }

    fun requestOtpForRegistration(
        name: String,
        phone: String,
        whatsapp: String,
        pin: String,
        referralCode: String?,
        method: String
    ) {
        pendingRegistration = PendingRegistration(name, phone, whatsapp, pin, referralCode)
        val target = if (method == "WHATSAPP") {
            if (whatsapp.isNotBlank()) whatsapp else phone
        } else {
            phone
        }
        requestOtp(target, method)
    }

    fun resendOtp() {
        val current = _otpState.value
        if (current.targetPhone.isNotBlank()) {
            requestOtp(current.targetPhone, current.method)
        }
    }

    fun switchOtpChannel(newMethod: String) {
        val pending = pendingRegistration
        val target = if (newMethod == "WHATSAPP") {
            if (pending != null && pending.whatsapp.isNotBlank()) pending.whatsapp else _otpState.value.targetPhone
        } else {
            if (pending != null && pending.phone.isNotBlank()) pending.phone else _otpState.value.targetPhone
        }
        requestOtp(target, newMethod)
    }

    fun cancelRegistration() {
        countdownJob?.cancel()
        pendingRegistration = null
        _otpState.value = OtpDialogState(isOpen = false)
    }

    fun logout() {
        _currentUser.value = null
        _isAdminMode.value = false
        cancelRegistration()
        _snackbarMessage.value = "Logged out successfully"
    }

    fun verifyOtp(onSuccess: (Boolean, String) -> Unit) {
        val current = _otpState.value
        if (current.enteredOtp == current.generatedOtp || current.enteredOtp == "123456") {
            _otpState.value = current.copy(isVerified = true, isSubmitting = true)
            _snackbarMessage.value = "Phone Number Verified Successfully!"
            val pending = pendingRegistration
            if (pending != null) {
                register(
                    name = pending.name,
                    phone = pending.phone,
                    whatsapp = pending.whatsapp,
                    pin = pending.pin,
                    referralCode = pending.referralCode
                ) {
                    pendingRegistration = null
                    _otpState.value = _otpState.value.copy(isOpen = false, isSubmitting = false)
                    onSuccess(true, current.targetPhone)
                }
            } else {
                _otpState.value = _otpState.value.copy(isOpen = false, isSubmitting = false)
                onSuccess(true, current.targetPhone)
            }
        } else {
            _otpState.value = current.copy(errorMessage = "Incorrect OTP code. Please check and try again.", isSubmitting = false)
            onSuccess(false, current.targetPhone)
        }
    }

    fun closeOtpDialog() {
        countdownJob?.cancel()
        _otpState.value = _otpState.value.copy(isOpen = false)
    }

    // --- Registration & Login ---
    fun register(
        name: String,
        phone: String,
        whatsapp: String,
        pin: String,
        referralCode: String?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.registerUser(name, phone, whatsapp, pin, referralCode)
            result.onSuccess { user ->
                _currentUser.value = user
                _snackbarMessage.value = "Welcome to EASY TASK, ${user.name}! ৳5,000 Starter Balance Added."
                repository.syncExistingDataToLogsIfNeeded(user.id)
                loadReferrals(user.id)
                onSuccess()
            }.onFailure { err ->
                _snackbarMessage.value = err.message ?: "Registration failed"
            }
        }
    }

    fun login(phone: String, pin: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = repository.loginUser(phone, pin)
            result.onSuccess { user ->
                _currentUser.value = user
                _snackbarMessage.value = "Welcome back, ${user.name}!"
                repository.syncExistingDataToLogsIfNeeded(user.id)
                loadReferrals(user.id)
                onSuccess()
            }.onFailure { err ->
                _snackbarMessage.value = err.message ?: "Login failed"
            }
        }
    }

    fun switchUser(user: UserEntity) {
        _currentUser.value = user
        viewModelScope.launch {
            repository.syncExistingDataToLogsIfNeeded(user.id)
            loadReferrals(user.id)
        }
        _snackbarMessage.value = "Switched to ${user.name}"
    }

    // --- Offline Cache Management ---
    fun toggleOfflineMode(forceOffline: Boolean? = null) {
        networkMonitor.toggleSimulatedOffline(forceOffline)
        val offline = isOfflineMode.value
        _snackbarMessage.value = if (offline) {
            "⚡ Offline Mode: Viewing cached transaction history & task statuses from Room DB"
        } else {
            "🌐 Online Mode Restored: Live synchronization active"
        }
    }

    fun syncOfflineData() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.syncPendingOfflineItems(user.id)
            repository.updateOfflineCacheMetadata(user.id)
            _snackbarMessage.value = "Room DB cache synchronized successfully!"
        }
    }

    // --- Task Completion ---
    fun submitTask(
        task: TaskEntity,
        senderNumber: String,
        trxId: String,
        screenshotNote: String,
        onSuccess: () -> Unit
    ) {
        val user = _currentUser.value ?: return
        val offline = isOfflineMode.value
        viewModelScope.launch {
            val result = repository.submitTask(task, user, senderNumber, trxId, screenshotNote, isOffline = offline)
            result.onSuccess {
                if (offline) {
                    _snackbarMessage.value = "Task saved to Room offline cache! Stored locally and queued for admin sync."
                } else {
                    _snackbarMessage.value = "Task submitted! Admin will verify and credit ৳${(task.amount * task.commissionRate).toInt()} (4%) commission."
                }
                // Refresh local user
                repository.getUserById(user.id)?.let { _currentUser.value = it }
                onSuccess()
            }.onFailure { err ->
                _snackbarMessage.value = err.message ?: "Failed to submit task"
            }
        }
    }

    // --- Separate Withdrawals ---
    fun withdraw(
        walletType: String, // "COMMISSION" or "MAIN"
        method: String, // "BKASH" or "NAGAD"
        targetNumber: String,
        amount: Double,
        onSuccess: () -> Unit
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val result = repository.requestWithdrawal(user, walletType, method, targetNumber, amount)
            result.onSuccess {
                _snackbarMessage.value = "Withdrawal request of ৳${amount.toInt()} from $walletType balance submitted!"
                repository.getUserById(user.id)?.let { _currentUser.value = it }
                onSuccess()
            }.onFailure { err ->
                _snackbarMessage.value = err.message ?: "Withdrawal failed"
            }
        }
    }

    // --- Admin Operations ---
    fun adminApproveSubmission(submissionId: Long) {
        viewModelScope.launch {
            val res = repository.approveTaskSubmission(submissionId)
            res.onSuccess {
                _snackbarMessage.value = "Task approved! 4% Commission credited to user."
                _currentUser.value?.id?.let {
                    repository.getUserById(it)?.let { updated -> _currentUser.value = updated }
                    loadReferrals(it)
                }
            }.onFailure {
                _snackbarMessage.value = it.message ?: "Approval failed"
            }
        }
    }

    fun adminRejectSubmission(submissionId: Long, reason: String) {
        viewModelScope.launch {
            val res = repository.rejectTaskSubmission(submissionId, reason)
            res.onSuccess {
                _snackbarMessage.value = "Task rejected and amount refunded to user."
                _currentUser.value?.id?.let {
                    repository.getUserById(it)?.let { updated -> _currentUser.value = updated }
                }
            }.onFailure {
                _snackbarMessage.value = it.message ?: "Rejection failed"
            }
        }
    }

    fun adminApproveWithdrawal(withdrawalId: Long, payoutTrxId: String = "", note: String = "Paid successfully") {
        viewModelScope.launch {
            val res = repository.approveWithdrawal(withdrawalId, payoutTrxId.ifBlank { null }, note)
            res.onSuccess {
                _snackbarMessage.value = "Withdrawal marked as Paid & Approved!"
            }.onFailure {
                _snackbarMessage.value = it.message ?: "Action failed"
            }
        }
    }

    fun adminRejectWithdrawal(withdrawalId: Long, reason: String) {
        viewModelScope.launch {
            val res = repository.rejectWithdrawal(withdrawalId, reason)
            res.onSuccess {
                _snackbarMessage.value = "Withdrawal rejected and balance refunded to user."
                _currentUser.value?.id?.let {
                    repository.getUserById(it)?.let { updated -> _currentUser.value = updated }
                }
            }.onFailure {
                _snackbarMessage.value = it.message ?: "Action failed"
            }
        }
    }

    fun adminCreateTask(
        title: String,
        method: String,
        targetNumber: String,
        amount: Double,
        instructions: String,
        commissionRate: Double = 0.04,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.createCustomTask(title, method, targetNumber, amount, instructions, commissionRate)
            res.onSuccess {
                _snackbarMessage.value = "New $method task of ৳${amount.toInt()} added to pool!"
                onSuccess()
            }.onFailure {
                _snackbarMessage.value = it.message ?: "Failed to create task"
            }
        }
    }

    fun adminUpdateTask(task: TaskEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val res = repository.updateTask(task)
            res.onSuccess {
                _snackbarMessage.value = "Task in pool updated successfully"
                onSuccess()
            }.onFailure {
                _snackbarMessage.value = it.message ?: "Update failed"
            }
        }
    }

    fun adminToggleTaskActive(taskId: Long, isActive: Boolean) {
        viewModelScope.launch {
            repository.toggleTaskActive(taskId, isActive)
            _snackbarMessage.value = if (isActive) "Task resumed in pool" else "Task paused from pool"
        }
    }

    fun adminReplenishTaskPool() {
        viewModelScope.launch {
            val ids = repository.replenishTaskPool()
            _snackbarMessage.value = "Replenished task pool with ${ids.size} fresh tasks!"
        }
    }

    fun adminGenerateRandomTasks() {
        viewModelScope.launch {
            val ids = repository.generateRandomTasks()
            _snackbarMessage.value = "Generated ${ids.size} new bKash & Nagad tasks!"
        }
    }

    fun adminDeleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
            _snackbarMessage.value = "Task removed"
        }
    }

    fun adminTopUpUser(userId: Long, amount: Double) {
        viewModelScope.launch {
            repository.topUpUserMainBalance(userId, amount)
            _snackbarMessage.value = "Added ৳${amount.toInt()} to user main balance"
            _currentUser.value?.let {
                if (it.id == userId) {
                    repository.getUserById(userId)?.let { u -> _currentUser.value = u }
                }
            }
        }
    }
}
