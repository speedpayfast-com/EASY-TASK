package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CacheMetadataEntity
import com.example.data.model.ReferralRewardEntity
import com.example.data.model.TaskEntity
import com.example.data.model.TaskSubmissionEntity
import com.example.data.model.TransactionLogEntity
import com.example.data.model.UserEntity
import com.example.data.model.WithdrawalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // --- User Queries ---
    @Query("SELECT * FROM users ORDER BY id ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserByIdFlow(userId: Long): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): UserEntity?

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): UserEntity?

    @Query("SELECT * FROM users WHERE referralCode = :code LIMIT 1")
    suspend fun getUserByReferralCode(code: String): UserEntity?

    @Query("SELECT * FROM users WHERE isAdmin = 1 LIMIT 1")
    suspend fun getAdminUser(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET name = :newName WHERE id = :userId")
    suspend fun updateUserName(userId: Long, newName: String)

    // --- Task Queries ---
    @Query("SELECT * FROM tasks WHERE isActive = 1 ORDER BY id DESC")
    fun getActiveTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    suspend fun getTaskById(taskId: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    // --- Task Submissions Queries ---
    @Query("SELECT * FROM task_submissions ORDER BY submittedAt DESC")
    fun getAllSubmissions(): Flow<List<TaskSubmissionEntity>>

    @Query("SELECT * FROM task_submissions WHERE userId = :userId ORDER BY submittedAt DESC")
    fun getSubmissionsByUserId(userId: Long): Flow<List<TaskSubmissionEntity>>

    @Query("SELECT * FROM task_submissions WHERE status = 'PENDING' ORDER BY submittedAt ASC")
    fun getPendingSubmissions(): Flow<List<TaskSubmissionEntity>>

    @Query("SELECT * FROM task_submissions WHERE id = :submissionId LIMIT 1")
    suspend fun getSubmissionById(submissionId: Long): TaskSubmissionEntity?

    @Query("SELECT COALESCE(SUM(taskAmount), 0.0) FROM task_submissions WHERE userId = :userId AND status = 'APPROVED'")
    suspend fun getTotalApprovedVolumeForUser(userId: Long): Double

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmission(submission: TaskSubmissionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmissions(submissions: List<TaskSubmissionEntity>)

    @Update
    suspend fun updateSubmission(submission: TaskSubmissionEntity)

    @Query("SELECT * FROM task_submissions WHERE userId = :userId AND status = :status ORDER BY submittedAt DESC")
    fun getSubmissionsByStatus(userId: Long, status: String): Flow<List<TaskSubmissionEntity>>

    @Query("SELECT COUNT(*) FROM task_submissions WHERE userId = :userId")
    suspend fun countSubmissionsForUser(userId: Long): Int

    @Query("SELECT COUNT(*) FROM task_submissions WHERE userId = :userId AND status = 'APPROVED'")
    suspend fun countApprovedSubmissionsForUser(userId: Long): Int

    @Query("SELECT COUNT(*) FROM task_submissions WHERE userId = :userId AND status = 'PENDING'")
    suspend fun countPendingSubmissionsForUser(userId: Long): Int

    @Query("SELECT COUNT(*) FROM task_submissions WHERE userId = :userId AND status = 'REJECTED'")
    suspend fun countRejectedSubmissionsForUser(userId: Long): Int

    @Query("SELECT * FROM task_submissions WHERE userId = :userId ORDER BY submittedAt DESC")
    suspend fun getCachedSubmissionsList(userId: Long): List<TaskSubmissionEntity>

    // --- Withdrawals Queries ---
    @Query("SELECT * FROM withdrawals ORDER BY requestedAt DESC")
    fun getAllWithdrawals(): Flow<List<WithdrawalEntity>>

    @Query("SELECT * FROM withdrawals WHERE userId = :userId ORDER BY requestedAt DESC")
    fun getWithdrawalsByUserId(userId: Long): Flow<List<WithdrawalEntity>>

    @Query("SELECT * FROM withdrawals WHERE status = 'PENDING' ORDER BY requestedAt ASC")
    fun getPendingWithdrawals(): Flow<List<WithdrawalEntity>>

    @Query("SELECT * FROM withdrawals WHERE id = :withdrawalId LIMIT 1")
    suspend fun getWithdrawalById(withdrawalId: Long): WithdrawalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWithdrawal(withdrawal: WithdrawalEntity): Long

    @Update
    suspend fun updateWithdrawal(withdrawal: WithdrawalEntity)

    // --- Referral Rewards Queries ---
    @Query("SELECT * FROM referral_rewards WHERE referrerUserId = :referrerUserId ORDER BY registeredAt DESC")
    fun getRewardsForReferrer(referrerUserId: Long): Flow<List<ReferralRewardEntity>>

    @Query("SELECT * FROM referral_rewards WHERE referredUserId = :referredUserId LIMIT 1")
    suspend fun getRewardForReferee(referredUserId: Long): ReferralRewardEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReferralReward(reward: ReferralRewardEntity): Long

    @Update
    suspend fun updateReferralReward(reward: ReferralRewardEntity)

    // --- Transaction Logs Queries (Room Local Storage) ---
    @Query("SELECT * FROM transaction_logs WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTransactionLogsByUserId(userId: Long): Flow<List<TransactionLogEntity>>

    @Query("SELECT * FROM transaction_logs ORDER BY timestamp DESC")
    fun getAllTransactionLogs(): Flow<List<TransactionLogEntity>>

    @Query("SELECT * FROM transaction_logs WHERE userId = :userId AND status = 'PENDING' ORDER BY timestamp DESC")
    fun getPendingLogsForUser(userId: Long): Flow<List<TransactionLogEntity>>

    @Query("SELECT * FROM transaction_logs WHERE userId = :userId AND (category = 'COMMISSION' OR commissionAmount > 0) ORDER BY timestamp DESC")
    fun getCommissionLogsForUser(userId: Long): Flow<List<TransactionLogEntity>>

    @Query("SELECT COUNT(*) FROM transaction_logs WHERE userId = :userId")
    suspend fun countLogsForUser(userId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactionLog(log: TransactionLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactionLogs(logs: List<TransactionLogEntity>)

    @Update
    suspend fun updateTransactionLog(log: TransactionLogEntity)

    @Query("UPDATE transaction_logs SET status = :newStatus, description = :newDescription WHERE relatedSubmissionId = :submissionId")
    suspend fun updateLogStatusBySubmission(submissionId: Long, newStatus: String, newDescription: String)

    @Query("UPDATE transaction_logs SET status = :newStatus, description = :newDescription WHERE relatedWithdrawalId = :withdrawalId")
    suspend fun updateLogStatusByWithdrawal(withdrawalId: Long, newStatus: String, newDescription: String)

    @Query("SELECT * FROM transaction_logs WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getCachedTransactionLogsList(userId: Long): List<TransactionLogEntity>

    // --- Offline Cache Metadata & Sync Queries ---
    @Query("SELECT * FROM cache_metadata WHERE cacheKey = :key LIMIT 1")
    fun getCacheMetadataFlow(key: String = "primary_cache"): Flow<CacheMetadataEntity?>

    @Query("SELECT * FROM cache_metadata WHERE cacheKey = :key LIMIT 1")
    suspend fun getCacheMetadata(key: String = "primary_cache"): CacheMetadataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCacheMetadata(metadata: CacheMetadataEntity)

    @Query("UPDATE task_submissions SET syncStatus = 'SYNCED' WHERE syncStatus = 'PENDING_SYNC'")
    suspend fun markAllSubmissionsSynced()

    @Query("UPDATE transaction_logs SET syncStatus = 'SYNCED' WHERE syncStatus = 'PENDING_SYNC'")
    suspend fun markAllLogsSynced()

    @Query("SELECT COUNT(*) FROM task_submissions WHERE syncStatus = 'PENDING_SYNC'")
    suspend fun countPendingSyncSubmissions(): Int
}
