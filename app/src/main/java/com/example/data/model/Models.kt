package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String,
    val whatsapp: String,
    val pin: String = "1234",
    val referralCode: String,
    val referredBy: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val mainBalance: Double = 5000.0, // Initial balance to perform send money tasks
    val commissionBalance: Double = 0.0,
    val isVerified: Boolean = false,
    val isAdmin: Boolean = false
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val method: String, // "BKASH" or "NAGAD"
    val targetNumber: String,
    val amount: Double,
    val commissionRate: Double = 0.04, // 4% Commission
    val instructions: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

@Entity(tableName = "task_submissions")
data class TaskSubmissionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskId: Long,
    val taskTitle: String,
    val method: String, // "BKASH" or "NAGAD"
    val taskAmount: Double,
    val commissionEarned: Double, // 4% of taskAmount
    val userId: Long,
    val userName: String,
    val userPhone: String,
    val senderNumber: String,
    val trxId: String,
    val screenshotNote: String = "Payment successful screenshot attached",
    val status: String = "PENDING", // "PENDING", "APPROVED", "REJECTED"
    val adminNote: String = "",
    val submittedAt: Long = System.currentTimeMillis(),
    val reviewedAt: Long? = null,
    val isOfflineCached: Boolean = true,
    val cachedAt: Long = System.currentTimeMillis(),
    val syncStatus: String = "SYNCED" // "SYNCED", "PENDING_SYNC"
)

@Entity(tableName = "withdrawals")
data class WithdrawalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val userName: String,
    val userPhone: String,
    val walletType: String, // "COMMISSION" or "MAIN"
    val method: String, // "BKASH" or "NAGAD"
    val targetNumber: String,
    val amount: Double,
    val status: String = "PENDING", // "PENDING", "APPROVED", "REJECTED"
    val adminNote: String = "",
    val requestedAt: Long = System.currentTimeMillis(),
    val processedAt: Long? = null
)

@Entity(tableName = "referral_rewards")
data class ReferralRewardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val referrerUserId: Long,
    val referredUserId: Long,
    val referrerCode: String,
    val isReferrerBonusPaid: Boolean = false, // 200 Taka
    val isReferredBonusPaid: Boolean = false, // 100 Taka
    val registeredAt: Long = System.currentTimeMillis(),
    val deadlineAt: Long = System.currentTimeMillis() + (2L * 24L * 60L * 60L * 1000L), // 2 Days deadline
    val targetAmount: Double = 5000.0,
    val currentApprovedAmount: Double = 0.0
)

@Entity(tableName = "transaction_logs")
data class TransactionLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val type: String, // "TASK_COMPLETION", "PENDING_PAYMENT", "COMMISSION_EARNED", "WITHDRAWAL", "BONUS", "REFUND"
    val category: String, // "TASK", "COMMISSION", "WITHDRAWAL", "BONUS"
    val method: String, // "BKASH", "NAGAD", "SYSTEM"
    val title: String,
    val description: String,
    val amount: Double,
    val commissionAmount: Double = 0.0,
    val balanceImpact: Double = 0.0,
    val walletAffected: String = "MAIN", // "MAIN", "COMMISSION", "BOTH"
    val status: String, // "COMPLETED", "PENDING", "REJECTED"
    val trxId: String? = null,
    val targetNumber: String? = null,
    val senderNumber: String? = null,
    val relatedSubmissionId: Long? = null,
    val relatedWithdrawalId: Long? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isOfflineCached: Boolean = true,
    val cachedAt: Long = System.currentTimeMillis(),
    val syncStatus: String = "SYNCED" // "SYNCED", "PENDING_SYNC"
)

@Entity(tableName = "cache_metadata")
data class CacheMetadataEntity(
    @PrimaryKey val cacheKey: String = "primary_cache",
    val lastSyncTimestamp: Long = System.currentTimeMillis(),
    val cachedTransactionsCount: Int = 0,
    val cachedSubmissionsCount: Int = 0,
    val lastSyncStatus: String = "CACHED_OFFLINE_READY",
    val storageEngine: String = "Room SQLite Database"
)

data class ReferralItemUiModel(
    val refereeId: Long = 0L,
    val refereeName: String,
    val refereePhone: String,
    val registeredAt: Long,
    val deadlineAt: Long,
    val currentVolume: Double,
    val targetVolume: Double = 5000.0,
    val completedTasksCount: Int = 0,
    val isReferrerRewarded: Boolean,
    val isRefereeRewarded: Boolean,
    val isExpired: Boolean
)
