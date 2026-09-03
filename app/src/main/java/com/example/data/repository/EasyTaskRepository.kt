package com.example.data.repository

import com.example.data.dao.AppDao
import com.example.data.model.CacheMetadataEntity
import com.example.data.model.ReferralItemUiModel
import com.example.data.model.ReferralRewardEntity
import com.example.data.model.TaskEntity
import com.example.data.model.TaskSubmissionEntity
import com.example.data.model.TransactionLogEntity
import com.example.data.model.UserEntity
import com.example.data.model.WithdrawalEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlin.random.Random

class EasyTaskRepository(private val dao: AppDao) {

    val allUsers: Flow<List<UserEntity>> = dao.getAllUsers()
    val activeTasks: Flow<List<TaskEntity>> = dao.getActiveTasks()
    val allTasks: Flow<List<TaskEntity>> = dao.getAllTasks()
    val allSubmissions: Flow<List<TaskSubmissionEntity>> = dao.getAllSubmissions()
    val pendingSubmissions: Flow<List<TaskSubmissionEntity>> = dao.getPendingSubmissions()
    val allWithdrawals: Flow<List<WithdrawalEntity>> = dao.getAllWithdrawals()
    val pendingWithdrawals: Flow<List<WithdrawalEntity>> = dao.getPendingWithdrawals()
    val allTransactionLogs: Flow<List<TransactionLogEntity>> = dao.getAllTransactionLogs()
    val cacheMetadata: Flow<CacheMetadataEntity?> = dao.getCacheMetadataFlow("primary_cache")

    fun getUserFlow(userId: Long): Flow<UserEntity?> = dao.getUserByIdFlow(userId)

    fun getUserSubmissions(userId: Long): Flow<List<TaskSubmissionEntity>> =
        dao.getSubmissionsByUserId(userId)

    fun getUserCacheMetadata(userId: Long): Flow<CacheMetadataEntity?> =
        dao.getCacheMetadataFlow("user_${userId}_cache")

    suspend fun getCachedSubmissions(userId: Long): List<TaskSubmissionEntity> =
        dao.getCachedSubmissionsList(userId)

    suspend fun getCachedTransactionLogs(userId: Long): List<TransactionLogEntity> =
        dao.getCachedTransactionLogsList(userId)

    fun getUserWithdrawals(userId: Long): Flow<List<WithdrawalEntity>> =
        dao.getWithdrawalsByUserId(userId)

    fun getUserTransactionLogs(userId: Long): Flow<List<TransactionLogEntity>> =
        dao.getTransactionLogsByUserId(userId)

    fun getPendingLogs(userId: Long): Flow<List<TransactionLogEntity>> =
        dao.getPendingLogsForUser(userId)

    fun getCommissionLogs(userId: Long): Flow<List<TransactionLogEntity>> =
        dao.getCommissionLogsForUser(userId)

    fun getReferralRewards(userId: Long): Flow<List<ReferralRewardEntity>> =
        dao.getRewardsForReferrer(userId)

    suspend fun getUserById(userId: Long): UserEntity? = dao.getUserById(userId)

    suspend fun getUserByPhone(phone: String): UserEntity? = dao.getUserByPhone(phone)

    suspend fun updateUserDisplayName(userId: Long, newName: String): Boolean {
        val user = dao.getUserById(userId) ?: return false
        val trimmed = newName.trim()
        if (trimmed.isBlank()) return false
        dao.updateUserName(userId, trimmed)
        return true
    }

    suspend fun initializeSeedDataIfNeeded() {
        val existingUsers = dao.getAllUsers().firstOrNull() ?: emptyList()
        if (existingUsers.isEmpty()) {
            // Seed Admin
            val adminUser = UserEntity(
                name = "System Admin",
                phone = "01700000000",
                whatsapp = "01700000000",
                pin = "1234",
                referralCode = "ETADMIN",
                mainBalance = 50000.0,
                commissionBalance = 15000.0,
                isVerified = true,
                isAdmin = true
            )
            dao.insertUser(adminUser)

            // Seed Demo User
            val demoUser = UserEntity(
                name = "Sumaiya Akhter",
                phone = "01811223344",
                whatsapp = "01811223344",
                pin = "1234",
                referralCode = "ET7861",
                referredBy = "ETADMIN",
                mainBalance = 6500.0,
                commissionBalance = 420.0,
                isVerified = true,
                isAdmin = false
            )
            val demoUserId = dao.insertUser(demoUser)

            // Seed Referral progress for demo
            val referralReward = ReferralRewardEntity(
                referrerUserId = 1L,
                referredUserId = demoUserId,
                referrerCode = "ETADMIN",
                isReferrerBonusPaid = false,
                isReferredBonusPaid = false,
                registeredAt = System.currentTimeMillis() - (4 * 3600 * 1000), // 4 hours ago
                deadlineAt = System.currentTimeMillis() + (44 * 3600 * 1000),
                targetAmount = 5000.0,
                currentApprovedAmount = 2500.0
            )
            dao.insertReferralReward(referralReward)

            // Seed realistic starter tasks
            val starterTasks = listOf(
                TaskEntity(
                    title = "bKash Send Money - Personal Quick",
                    method = "BKASH",
                    targetNumber = "01823-456789",
                    amount = 1000.0,
                    commissionRate = 0.04,
                    instructions = "Send Money to the bKash personal number provided. Enter your sender number, TrxID, and upload screenshot. You will receive 40৳ (4%) commission immediately after admin approval."
                ),
                TaskEntity(
                    title = "Nagad Send Money - Express Cashback",
                    method = "NAGAD",
                    targetNumber = "01955-889900",
                    amount = 2000.0,
                    commissionRate = 0.04,
                    instructions = "Send Money to the Nagad number. Keep screenshot of successful transaction. You will receive 80৳ (4%) commission upon verification."
                ),
                TaskEntity(
                    title = "bKash Send Money - High Value Task",
                    method = "BKASH",
                    targetNumber = "01712-334455",
                    amount = 5000.0,
                    commissionRate = 0.04,
                    instructions = "Complete this 5,000৳ task to directly unlock your 100৳ Referral Bonus + earn 200৳ (4%) instant task commission!"
                ),
                TaskEntity(
                    title = "Nagad Send Money - Standard Task",
                    method = "NAGAD",
                    targetNumber = "01688-112233",
                    amount = 500.0,
                    commissionRate = 0.04,
                    instructions = "Send 500৳ via Nagad and submit TrxID to earn 20৳ commission."
                )
            )
            dao.insertTasks(starterTasks)
        }
    }

    // --- Authentication & OTP Registration ---
    suspend fun registerUser(
        name: String,
        phone: String,
        whatsapp: String,
        pin: String,
        referredByCode: String?
    ): Result<UserEntity> {
        val existing = dao.getUserByPhone(phone)
        if (existing != null) {
            return Result.failure(Exception("An account with phone $phone already exists!"))
        }

        val cleanCode = referredByCode?.trim()?.uppercase()
        var referrerUser: UserEntity? = null
        if (!cleanCode.isNullOrEmpty()) {
            referrerUser = dao.getUserByReferralCode(cleanCode)
            if (referrerUser == null) {
                return Result.failure(Exception("Invalid referral code: $cleanCode"))
            }
        }

        val generatedCode = "ET" + (1000 + Random.nextInt(9000))
        val newUser = UserEntity(
            name = name.trim(),
            phone = phone.trim(),
            whatsapp = whatsapp.trim(),
            pin = pin,
            referralCode = generatedCode,
            referredBy = cleanCode,
            mainBalance = 5000.0, // Starter demo balance for task execution
            commissionBalance = 0.0,
            isVerified = true,
            isAdmin = false
        )
        val newUserId = dao.insertUser(newUser)
        val createdUser = newUser.copy(id = newUserId)

        // Setup referral challenge if referred
        if (referrerUser != null) {
            val challenge = ReferralRewardEntity(
                referrerUserId = referrerUser.id,
                referredUserId = newUserId,
                referrerCode = referrerUser.referralCode,
                isReferrerBonusPaid = false,
                isReferredBonusPaid = false,
                registeredAt = System.currentTimeMillis(),
                deadlineAt = System.currentTimeMillis() + (2L * 24L * 60L * 60L * 1000L), // 2 Days
                targetAmount = 5000.0,
                currentApprovedAmount = 0.0
            )
            dao.insertReferralReward(challenge)
        }

        return Result.success(createdUser)
    }

    suspend fun loginUser(phone: String, pin: String): Result<UserEntity> {
        val user = dao.getUserByPhone(phone.trim())
            ?: return Result.failure(Exception("User not found with phone: $phone"))
        if (user.pin != pin.trim()) {
            return Result.failure(Exception("Incorrect PIN entered"))
        }
        return Result.success(user)
    }

    // --- Task Submissions ---
    suspend fun submitTask(
        task: TaskEntity,
        user: UserEntity,
        senderNumber: String,
        trxId: String,
        screenshotNote: String,
        isOffline: Boolean = false
    ): Result<Long> {
        if (user.mainBalance < task.amount) {
            return Result.failure(Exception("Insufficient Main Balance! You need at least ৳${task.amount} to send money."))
        }

        val commissionEarned = task.amount * task.commissionRate // 4%
        val syncStatus = if (isOffline) "PENDING_SYNC" else "SYNCED"

        val submission = TaskSubmissionEntity(
            taskId = task.id,
            taskTitle = task.title,
            method = task.method,
            taskAmount = task.amount,
            commissionEarned = commissionEarned,
            userId = user.id,
            userName = user.name,
            userPhone = user.phone,
            senderNumber = senderNumber.trim(),
            trxId = trxId.trim(),
            screenshotNote = screenshotNote,
            status = "PENDING",
            isOfflineCached = true,
            cachedAt = System.currentTimeMillis(),
            syncStatus = syncStatus
        )
        val submissionId = dao.insertSubmission(submission)

        // Deduct money from main balance for the send money task
        val updatedUser = user.copy(mainBalance = user.mainBalance - task.amount)
        dao.updateUser(updatedUser)

        // Record in Room local Transaction Log
        val logDesc = if (isOffline) {
            "Task submitted offline. Cached in Room database. Will sync with admin when back online."
        } else {
            "Task submitted. Pending admin verification for 4% commission (৳${String.format("%.1f", commissionEarned)})."
        }

        val log = TransactionLogEntity(
            userId = user.id,
            type = "PENDING_PAYMENT",
            category = "TASK",
            method = task.method,
            title = "${task.method} Send Money: ৳${task.amount.toInt()}",
            description = logDesc,
            amount = task.amount,
            commissionAmount = commissionEarned,
            balanceImpact = -task.amount,
            walletAffected = "MAIN",
            status = "PENDING",
            trxId = trxId.trim(),
            targetNumber = task.targetNumber,
            senderNumber = senderNumber.trim(),
            relatedSubmissionId = submissionId,
            timestamp = System.currentTimeMillis(),
            isOfflineCached = true,
            cachedAt = System.currentTimeMillis(),
            syncStatus = syncStatus
        )
        dao.insertTransactionLog(log)

        updateOfflineCacheMetadata(user.id)

        return Result.success(submissionId)
    }

    // --- Admin Task Review ---
    suspend fun approveTaskSubmission(submissionId: Long, adminNote: String = "Approved by Admin"): Result<Unit> {
        val submission = dao.getSubmissionById(submissionId)
            ?: return Result.failure(Exception("Submission not found"))
        if (submission.status != "PENDING") {
            return Result.failure(Exception("Submission is already ${submission.status}"))
        }

        val user = dao.getUserById(submission.userId)
            ?: return Result.failure(Exception("User not found"))

        // Credit 4% Commission to user's commission balance!
        val newCommissionBalance = user.commissionBalance + submission.commissionEarned
        val updatedUser = user.copy(commissionBalance = newCommissionBalance)
        dao.updateUser(updatedUser)

        // Update submission status
        val updatedSubmission = submission.copy(
            status = "APPROVED",
            adminNote = adminNote,
            reviewedAt = System.currentTimeMillis()
        )
        dao.updateSubmission(updatedSubmission)

        // Update Room transaction log for the submission
        dao.updateLogStatusBySubmission(
            submissionId = submissionId,
            newStatus = "COMPLETED",
            newDescription = "Approved by Admin. ৳${submission.taskAmount.toInt()} verified via ${submission.method} (TrxID: ${submission.trxId})."
        )

        // Insert Room log for 4% Commission Earned
        dao.insertTransactionLog(
            TransactionLogEntity(
                userId = user.id,
                type = "COMMISSION_EARNED",
                category = "COMMISSION",
                method = submission.method,
                title = "4% Commission Earned (+৳${String.format("%.1f", submission.commissionEarned)})",
                description = "Earned 4% commission on ${submission.method} task (${submission.taskTitle}). TrxID: ${submission.trxId}",
                amount = submission.taskAmount,
                commissionAmount = submission.commissionEarned,
                balanceImpact = submission.commissionEarned,
                walletAffected = "COMMISSION",
                status = "COMPLETED",
                trxId = submission.trxId,
                senderNumber = submission.senderNumber,
                relatedSubmissionId = submissionId,
                timestamp = System.currentTimeMillis()
            )
        )

        // Check 2-day 5,000 Taka Referral Reward Criteria
        checkAndAwardReferralBonuses(user.id)

        return Result.success(Unit)
    }

    suspend fun rejectTaskSubmission(submissionId: Long, reason: String = "Screenshot or TrxID invalid"): Result<Unit> {
        val submission = dao.getSubmissionById(submissionId)
            ?: return Result.failure(Exception("Submission not found"))
        if (submission.status != "PENDING") {
            return Result.failure(Exception("Submission is already ${submission.status}"))
        }

        val user = dao.getUserById(submission.userId)
        if (user != null) {
            // Refund main balance
            val refundedUser = user.copy(mainBalance = user.mainBalance + submission.taskAmount)
            dao.updateUser(refundedUser)
        }

        val updatedSubmission = submission.copy(
            status = "REJECTED",
            adminNote = reason,
            reviewedAt = System.currentTimeMillis()
        )
        dao.updateSubmission(updatedSubmission)

        // Update Room transaction log for the submission
        dao.updateLogStatusBySubmission(
            submissionId = submissionId,
            newStatus = "REJECTED",
            newDescription = "Rejected by Admin: $reason. ৳${submission.taskAmount.toInt()} refunded to Main Balance."
        )

        if (user != null) {
            dao.insertTransactionLog(
                TransactionLogEntity(
                    userId = user.id,
                    type = "REFUND",
                    category = "TASK",
                    method = submission.method,
                    title = "Task Refunded: ৳${submission.taskAmount.toInt()}",
                    description = "Main balance refunded due to task rejection ($reason).",
                    amount = submission.taskAmount,
                    commissionAmount = 0.0,
                    balanceImpact = submission.taskAmount,
                    walletAffected = "MAIN",
                    status = "COMPLETED",
                    trxId = submission.trxId,
                    relatedSubmissionId = submissionId,
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        return Result.success(Unit)
    }

    // --- Referral Logic: 2-Day 5,000৳ Threshold ---
    private suspend fun checkAndAwardReferralBonuses(refereeUserId: Long) {
        val rewardEntity = dao.getRewardForReferee(refereeUserId) ?: return
        val refereeUser = dao.getUserById(refereeUserId) ?: return

        val now = System.currentTimeMillis()
        val isWithin2Days = now <= rewardEntity.deadlineAt

        val totalApprovedAmount = dao.getTotalApprovedVolumeForUser(refereeUserId)
        val updatedReward = rewardEntity.copy(currentApprovedAmount = totalApprovedAmount)

        if (isWithin2Days && totalApprovedAmount >= 5000.0) {
            var updatedEntity = updatedReward

            // Award Referee 100 Taka Bonus if not paid yet
            if (!updatedReward.isReferredBonusPaid) {
                val updatedReferee = refereeUser.copy(
                    commissionBalance = refereeUser.commissionBalance + 100.0
                )
                dao.updateUser(updatedReferee)
                updatedEntity = updatedEntity.copy(isReferredBonusPaid = true)

                // Log referral challenge bonus
                dao.insertTransactionLog(
                    TransactionLogEntity(
                        userId = refereeUser.id,
                        type = "BONUS",
                        category = "COMMISSION",
                        method = "SYSTEM",
                        title = "Referral Bonus Unlocked (+৳100.0)",
                        description = "Completed ৳5,000 challenge within 2 days! ৳100 bonus credited to Commission Wallet.",
                        amount = 100.0,
                        commissionAmount = 100.0,
                        balanceImpact = 100.0,
                        walletAffected = "COMMISSION",
                        status = "COMPLETED",
                        timestamp = System.currentTimeMillis()
                    )
                )
            }

            // Award Referrer 200 Taka Bonus if not paid yet
            if (!updatedReward.isReferrerBonusPaid) {
                val referrerUser = dao.getUserById(updatedReward.referrerUserId)
                if (referrerUser != null) {
                    val updatedReferrer = referrerUser.copy(
                        commissionBalance = referrerUser.commissionBalance + 200.0
                    )
                    dao.updateUser(updatedReferrer)
                    updatedEntity = updatedEntity.copy(isReferrerBonusPaid = true)

                    dao.insertTransactionLog(
                        TransactionLogEntity(
                            userId = referrerUser.id,
                            type = "BONUS",
                            category = "COMMISSION",
                            method = "SYSTEM",
                            title = "Referral Reward Earned (+৳200.0)",
                            description = "Your invited member ${refereeUser.name} completed the ৳5,000 challenge! ৳200 credited.",
                            amount = 200.0,
                            commissionAmount = 200.0,
                            balanceImpact = 200.0,
                            walletAffected = "COMMISSION",
                            status = "COMPLETED",
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
            }

            dao.updateReferralReward(updatedEntity)
        } else {
            dao.updateReferralReward(updatedReward)
        }
    }

    suspend fun seedDemoReferralsForUser(referrerUserId: Long) {
        val referrer = dao.getUserById(referrerUserId) ?: return
        val now = System.currentTimeMillis()

        // Friend 1: Tanvir Hasan (Completed ৳5,000 within 2 days -> ৳200 Bonus Won)
        val user1 = UserEntity(
            name = "Tanvir Hasan",
            phone = "01712984210",
            whatsapp = "01712984210",
            pin = "1234",
            referralCode = "ET" + (1000 + Random.nextInt(9000)),
            referredBy = referrer.referralCode,
            mainBalance = 5000.0,
            commissionBalance = 300.0,
            isVerified = true,
            isAdmin = false
        )
        val id1 = dao.insertUser(user1)
        val sub1a = TaskSubmissionEntity(
            taskId = 1L,
            taskTitle = "bKash Send Money - Personal Quick",
            method = "BKASH",
            taskAmount = 3000.0,
            commissionEarned = 120.0,
            userId = id1,
            userName = user1.name,
            userPhone = user1.phone,
            senderNumber = user1.phone,
            trxId = "9BK55410A",
            screenshotNote = "Verified",
            status = "APPROVED",
            submittedAt = now - (20 * 3600 * 1000),
            reviewedAt = now - (19 * 3600 * 1000)
        )
        val sub1b = TaskSubmissionEntity(
            taskId = 2L,
            taskTitle = "Nagad Send Money - Express Cashback",
            method = "NAGAD",
            taskAmount = 2000.0,
            commissionEarned = 80.0,
            userId = id1,
            userName = user1.name,
            userPhone = user1.phone,
            senderNumber = user1.phone,
            trxId = "8NG77231B",
            screenshotNote = "Verified",
            status = "APPROVED",
            submittedAt = now - (18 * 3600 * 1000),
            reviewedAt = now - (17 * 3600 * 1000)
        )
        dao.insertSubmission(sub1a)
        dao.insertSubmission(sub1b)
        dao.insertReferralReward(
            ReferralRewardEntity(
                referrerUserId = referrerUserId,
                referredUserId = id1,
                referrerCode = referrer.referralCode,
                isReferrerBonusPaid = true,
                isReferredBonusPaid = true,
                registeredAt = now - (22 * 3600 * 1000),
                deadlineAt = now + (26 * 3600 * 1000),
                targetAmount = 5000.0,
                currentApprovedAmount = 5000.0
            )
        )

        // Friend 2: Nafisa Rahman (In Progress: ৳3,500 / ৳5,000 - 34 hours remaining)
        val user2 = UserEntity(
            name = "Nafisa Rahman",
            phone = "01923451872",
            whatsapp = "01923451872",
            pin = "1234",
            referralCode = "ET" + (1000 + Random.nextInt(9000)),
            referredBy = referrer.referralCode,
            mainBalance = 4000.0,
            commissionBalance = 140.0,
            isVerified = true,
            isAdmin = false
        )
        val id2 = dao.insertUser(user2)
        val sub2 = TaskSubmissionEntity(
            taskId = 1L,
            taskTitle = "bKash Send Money - Quick Task",
            method = "BKASH",
            taskAmount = 3500.0,
            commissionEarned = 140.0,
            userId = id2,
            userName = user2.name,
            userPhone = user2.phone,
            senderNumber = user2.phone,
            trxId = "9BK88912C",
            screenshotNote = "Verified",
            status = "APPROVED",
            submittedAt = now - (10 * 3600 * 1000),
            reviewedAt = now - (9 * 3600 * 1000)
        )
        dao.insertSubmission(sub2)
        dao.insertReferralReward(
            ReferralRewardEntity(
                referrerUserId = referrerUserId,
                referredUserId = id2,
                referrerCode = referrer.referralCode,
                isReferrerBonusPaid = false,
                isReferredBonusPaid = false,
                registeredAt = now - (14 * 3600 * 1000),
                deadlineAt = now + (34 * 3600 * 1000),
                targetAmount = 5000.0,
                currentApprovedAmount = 3500.0
            )
        )

        // Friend 3: Rakibul Islam (In Progress: ৳1,000 / ৳5,000 - 22 hours remaining)
        val user3 = UserEntity(
            name = "Rakibul Islam",
            phone = "01684332901",
            whatsapp = "01684332901",
            pin = "1234",
            referralCode = "ET" + (1000 + Random.nextInt(9000)),
            referredBy = referrer.referralCode,
            mainBalance = 5000.0,
            commissionBalance = 40.0,
            isVerified = true,
            isAdmin = false
        )
        val id3 = dao.insertUser(user3)
        val sub3 = TaskSubmissionEntity(
            taskId = 2L,
            taskTitle = "Nagad Send Money - Standard",
            method = "NAGAD",
            taskAmount = 1000.0,
            commissionEarned = 40.0,
            userId = id3,
            userName = user3.name,
            userPhone = user3.phone,
            senderNumber = user3.phone,
            trxId = "8NG33120D",
            screenshotNote = "Verified",
            status = "APPROVED",
            submittedAt = now - (26 * 3600 * 1000),
            reviewedAt = now - (25 * 3600 * 1000)
        )
        dao.insertSubmission(sub3)
        dao.insertReferralReward(
            ReferralRewardEntity(
                referrerUserId = referrerUserId,
                referredUserId = id3,
                referrerCode = referrer.referralCode,
                isReferrerBonusPaid = false,
                isReferredBonusPaid = false,
                registeredAt = now - (26 * 3600 * 1000),
                deadlineAt = now + (22 * 3600 * 1000),
                targetAmount = 5000.0,
                currentApprovedAmount = 1000.0
            )
        )

        // Friend 4: Kamrul Hassan (Expired: ৳1,500 / ৳5,000 - Registered 3 days ago)
        val user4 = UserEntity(
            name = "Kamrul Hassan",
            phone = "01844556112",
            whatsapp = "01844556112",
            pin = "1234",
            referralCode = "ET" + (1000 + Random.nextInt(9000)),
            referredBy = referrer.referralCode,
            mainBalance = 5000.0,
            commissionBalance = 60.0,
            isVerified = true,
            isAdmin = false
        )
        val id4 = dao.insertUser(user4)
        val sub4 = TaskSubmissionEntity(
            taskId = 1L,
            taskTitle = "bKash Send Money",
            method = "BKASH",
            taskAmount = 1500.0,
            commissionEarned = 60.0,
            userId = id4,
            userName = user4.name,
            userPhone = user4.phone,
            senderNumber = user4.phone,
            trxId = "9BK11904E",
            screenshotNote = "Verified",
            status = "APPROVED",
            submittedAt = now - (60 * 3600 * 1000),
            reviewedAt = now - (59 * 3600 * 1000)
        )
        dao.insertSubmission(sub4)
        dao.insertReferralReward(
            ReferralRewardEntity(
                referrerUserId = referrerUserId,
                referredUserId = id4,
                referrerCode = referrer.referralCode,
                isReferrerBonusPaid = false,
                isReferredBonusPaid = false,
                registeredAt = now - (72 * 3600 * 1000),
                deadlineAt = now - (24 * 3600 * 1000),
                targetAmount = 5000.0,
                currentApprovedAmount = 1500.0
            )
        )
    }

    suspend fun getReferralUiList(referrerUserId: Long): List<ReferralItemUiModel> {
        var rewards = dao.getRewardsForReferrer(referrerUserId).firstOrNull() ?: emptyList()
        if (rewards.isEmpty()) {
            seedDemoReferralsForUser(referrerUserId)
            rewards = dao.getRewardsForReferrer(referrerUserId).firstOrNull() ?: emptyList()
        }
        val now = System.currentTimeMillis()
        return rewards.map { r ->
            val referee = dao.getUserById(r.referredUserId)
            val currentVol = dao.getTotalApprovedVolumeForUser(r.referredUserId)
            val isExpired = now > r.deadlineAt && currentVol < r.targetAmount && !r.isReferrerBonusPaid
            val submissionsCount = dao.getSubmissionsByUserId(r.referredUserId).firstOrNull()?.count { it.status == "APPROVED" } ?: 0
            ReferralItemUiModel(
                refereeId = r.referredUserId,
                refereeName = referee?.name ?: "Referred Member",
                refereePhone = referee?.phone ?: "01XXXXXXXXX",
                registeredAt = r.registeredAt,
                deadlineAt = r.deadlineAt,
                currentVolume = currentVol,
                targetVolume = r.targetAmount,
                completedTasksCount = submissionsCount,
                isReferrerRewarded = r.isReferrerBonusPaid,
                isRefereeRewarded = r.isReferredBonusPaid,
                isExpired = isExpired
            )
        }
    }

    suspend fun addSimulatedReferredFriend(referrerUserId: Long, name: String, phone: String): Result<Long> {
        val referrer = dao.getUserById(referrerUserId) ?: return Result.failure(Exception("Referrer not found"))
        val cleanPhone = phone.trim().ifEmpty { "01" + (700000000 + Random.nextInt(99999999)) }
        val targetName = name.trim().ifEmpty { "Invited Friend #${Random.nextInt(100, 999)}" }

        val newUser = UserEntity(
            name = targetName,
            phone = cleanPhone,
            whatsapp = cleanPhone,
            pin = "1234",
            referralCode = "ET" + (1000 + Random.nextInt(9000)),
            referredBy = referrer.referralCode,
            mainBalance = 5000.0,
            commissionBalance = 0.0,
            isVerified = true,
            isAdmin = false
        )
        val newUserId = dao.insertUser(newUser)

        val challenge = ReferralRewardEntity(
            referrerUserId = referrerUserId,
            referredUserId = newUserId,
            referrerCode = referrer.referralCode,
            isReferrerBonusPaid = false,
            isReferredBonusPaid = false,
            registeredAt = System.currentTimeMillis(),
            deadlineAt = System.currentTimeMillis() + (2L * 24L * 60L * 60L * 1000L), // 48 Hours
            targetAmount = 5000.0,
            currentApprovedAmount = 0.0
        )
        dao.insertReferralReward(challenge)
        return Result.success(newUserId)
    }

    suspend fun advanceFriendTaskProgress(refereeUserId: Long, taskAmount: Double): Result<Boolean> {
        val refereeUser = dao.getUserById(refereeUserId)
            ?: return Result.failure(Exception("Friend record not found"))

        val method = if (Random.nextBoolean()) "BKASH" else "NAGAD"
        val prefix = if (method == "BKASH") "9BK" else "8NG"
        val trx = prefix + (100000 + Random.nextInt(899999)) + "S"

        val submission = TaskSubmissionEntity(
            taskId = 1L,
            taskTitle = "$method Send Money (Challenge)",
            method = method,
            taskAmount = taskAmount,
            commissionEarned = taskAmount * 0.04,
            userId = refereeUserId,
            userName = refereeUser.name,
            userPhone = refereeUser.phone,
            senderNumber = refereeUser.phone,
            trxId = trx,
            screenshotNote = "Verified task simulation",
            status = "APPROVED",
            submittedAt = System.currentTimeMillis(),
            reviewedAt = System.currentTimeMillis()
        )
        dao.insertSubmission(submission)

        // Award friend's 4% commission to their balance
        val updatedReferee = refereeUser.copy(
            commissionBalance = refereeUser.commissionBalance + (taskAmount * 0.04)
        )
        dao.updateUser(updatedReferee)

        // Check and award bonuses if threshold reached
        checkAndAwardReferralBonuses(refereeUserId)
        return Result.success(true)
    }

    // --- Withdrawals (Separate Commission vs Main Balance) ---
    suspend fun requestWithdrawal(
        user: UserEntity,
        walletType: String, // "COMMISSION" or "MAIN"
        method: String, // "BKASH" or "NAGAD"
        targetNumber: String,
        amount: Double
    ): Result<Long> {
        if (amount <= 0) {
            return Result.failure(Exception("Please enter a valid amount greater than 0৳"))
        }

        if (walletType == "COMMISSION") {
            if (user.commissionBalance < amount) {
                return Result.failure(Exception("Insufficient Commission Balance (৳${user.commissionBalance})"))
            }
            // Deduct from commission balance
            val updatedUser = user.copy(commissionBalance = user.commissionBalance - amount)
            dao.updateUser(updatedUser)
        } else {
            if (user.mainBalance < amount) {
                return Result.failure(Exception("Insufficient Main Balance (৳${user.mainBalance})"))
            }
            // Deduct from main balance
            val updatedUser = user.copy(mainBalance = user.mainBalance - amount)
            dao.updateUser(updatedUser)
        }

        val withdrawal = WithdrawalEntity(
            userId = user.id,
            userName = user.name,
            userPhone = user.phone,
            walletType = walletType,
            method = method,
            targetNumber = targetNumber.trim(),
            amount = amount,
            status = "PENDING"
        )
        val id = dao.insertWithdrawal(withdrawal)

        // Record in Room local Transaction Log
        val log = TransactionLogEntity(
            userId = user.id,
            type = "WITHDRAWAL",
            category = "WITHDRAWAL",
            method = method,
            title = "$walletType Wallet Payout Request: ৳${amount.toInt()}",
            description = "Withdrawal to $method ($targetNumber) submitted. Pending admin review.",
            amount = amount,
            commissionAmount = 0.0,
            balanceImpact = -amount,
            walletAffected = walletType,
            status = "PENDING",
            targetNumber = targetNumber.trim(),
            relatedWithdrawalId = id,
            timestamp = System.currentTimeMillis()
        )
        dao.insertTransactionLog(log)

        return Result.success(id)
    }

    suspend fun approveWithdrawal(withdrawalId: Long, payoutTrxId: String? = null, note: String = "Paid successfully"): Result<Unit> {
        val withdrawal = dao.getWithdrawalById(withdrawalId)
            ?: return Result.failure(Exception("Withdrawal not found"))
        if (withdrawal.status != "PENDING") {
            return Result.failure(Exception("Withdrawal is already ${withdrawal.status}"))
        }

        val adminNoteWithTrx = if (!payoutTrxId.isNullOrBlank()) {
            "TrxID: ${payoutTrxId.trim()} | $note"
        } else {
            note
        }

        val updated = withdrawal.copy(
            status = "APPROVED",
            adminNote = adminNoteWithTrx,
            processedAt = System.currentTimeMillis()
        )
        dao.updateWithdrawal(updated)

        // Update Room transaction log
        val logDesc = if (!payoutTrxId.isNullOrBlank()) {
            "Payout of ৳${withdrawal.amount.toInt()} paid via ${withdrawal.method} to ${withdrawal.targetNumber}. Payout TrxID: ${payoutTrxId.trim()}."
        } else {
            "Payout of ৳${withdrawal.amount.toInt()} paid via ${withdrawal.method} to ${withdrawal.targetNumber}. Note: $note"
        }
        dao.updateLogStatusByWithdrawal(
            withdrawalId = withdrawalId,
            newStatus = "COMPLETED",
            newDescription = logDesc
        )

        return Result.success(Unit)
    }

    suspend fun rejectWithdrawal(withdrawalId: Long, reason: String = "Invalid account details"): Result<Unit> {
        val withdrawal = dao.getWithdrawalById(withdrawalId)
            ?: return Result.failure(Exception("Withdrawal not found"))
        if (withdrawal.status != "PENDING") {
            return Result.failure(Exception("Withdrawal is already ${withdrawal.status}"))
        }

        // Refund back to user's wallet
        val user = dao.getUserById(withdrawal.userId)
        if (user != null) {
            if (withdrawal.walletType == "COMMISSION") {
                val updatedUser = user.copy(commissionBalance = user.commissionBalance + withdrawal.amount)
                dao.updateUser(updatedUser)
            } else {
                val updatedUser = user.copy(mainBalance = user.mainBalance + withdrawal.amount)
                dao.updateUser(updatedUser)
            }
        }

        val updated = withdrawal.copy(
            status = "REJECTED",
            adminNote = reason,
            processedAt = System.currentTimeMillis()
        )
        dao.updateWithdrawal(updated)

        // Update Room transaction log
        dao.updateLogStatusByWithdrawal(
            withdrawalId = withdrawalId,
            newStatus = "REJECTED",
            newDescription = "Withdrawal rejected: $reason. ৳${withdrawal.amount.toInt()} refunded to ${withdrawal.walletType} balance."
        )

        if (user != null) {
            dao.insertTransactionLog(
                TransactionLogEntity(
                    userId = user.id,
                    type = "REFUND",
                    category = "WITHDRAWAL",
                    method = withdrawal.method,
                    title = "Withdrawal Refunded: ৳${withdrawal.amount.toInt()}",
                    description = "Refunded to ${withdrawal.walletType} balance due to rejection ($reason).",
                    amount = withdrawal.amount,
                    commissionAmount = 0.0,
                    balanceImpact = withdrawal.amount,
                    walletAffected = withdrawal.walletType,
                    status = "COMPLETED",
                    targetNumber = withdrawal.targetNumber,
                    relatedWithdrawalId = withdrawalId,
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        return Result.success(Unit)
    }

    // --- Admin Task Pool Management ---
    suspend fun updateTask(task: TaskEntity): Result<Unit> {
        dao.updateTask(task)
        return Result.success(Unit)
    }

    suspend fun toggleTaskActive(taskId: Long, isActive: Boolean): Result<Unit> {
        val task = dao.getTaskById(taskId) ?: return Result.failure(Exception("Task not found"))
        dao.updateTask(task.copy(isActive = isActive))
        return Result.success(Unit)
    }

    suspend fun createCustomTask(
        title: String,
        method: String,
        targetNumber: String,
        amount: Double,
        instructions: String,
        commissionRate: Double = 0.04
    ): Result<Long> {
        val task = TaskEntity(
            title = title.trim(),
            method = method,
            targetNumber = targetNumber.trim(),
            amount = amount,
            commissionRate = commissionRate,
            instructions = instructions.trim()
        )
        val id = dao.insertTask(task)
        return Result.success(id)
    }

    suspend fun replenishTaskPool(): List<Long> {
        val methods = listOf("BKASH", "NAGAD")
        val poolTiers = listOf(
            Pair(500.0, "Quick Entry Task"),
            Pair(1000.0, "Standard Verification Task"),
            Pair(2000.0, "High Volume Task"),
            Pair(3000.0, "Prime Merchant Pool"),
            Pair(5000.0, "2-Day Referral Accelerator")
        )
        val prefixes = listOf("017", "018", "019", "016", "013")
        val tasks = mutableListOf<TaskEntity>()

        for ((amount, tierLabel) in poolTiers) {
            val method = methods.random()
            val phone = prefixes.random() + (10000000 + Random.nextInt(89999999))
            val comm = amount * 0.04
            tasks.add(
                TaskEntity(
                    title = "$method $tierLabel (৳${amount.toInt()})",
                    method = method,
                    targetNumber = phone,
                    amount = amount,
                    commissionRate = 0.04,
                    instructions = "Send Money ৳${amount.toInt()} via $method to $phone. Submit TrxID to earn ৳${comm.toInt()} (4%) commission."
                )
            )
        }

        val ids = mutableListOf<Long>()
        tasks.forEach {
            ids.add(dao.insertTask(it))
        }
        return ids
    }

    suspend fun generateRandomTasks(): List<Long> {
        val methods = listOf("BKASH", "NAGAD")
        val amounts = listOf(500.0, 1000.0, 1500.0, 2000.0, 2500.0, 3000.0, 5000.0)
        val prefixes = listOf("017", "018", "019", "016", "013")
        val tasks = mutableListOf<TaskEntity>()

        repeat(3) {
            val method = methods.random()
            val amount = amounts.random()
            val phone = prefixes.random() + (10000000 + Random.nextInt(89999999))
            val title = if (method == "BKASH") {
                "bKash Personal Send Money (৳${amount.toInt()})"
            } else {
                "Nagad Express Send Money (৳${amount.toInt()})"
            }
            val comm = amount * 0.04
            tasks.add(
                TaskEntity(
                    title = title,
                    method = method,
                    targetNumber = phone,
                    amount = amount,
                    commissionRate = 0.04,
                    instructions = "Send Money ৳${amount.toInt()} to $phone using $method. Capture screenshot and TrxID to earn ৳${comm.toInt()} (4%) commission."
                )
            )
        }
        val ids = mutableListOf<Long>()
        tasks.forEach {
            ids.add(dao.insertTask(it))
        }
        return ids
    }

    suspend fun deleteTask(task: TaskEntity) {
        dao.deleteTask(task)
    }

    suspend fun topUpUserMainBalance(userId: Long, addAmount: Double) {
        val user = dao.getUserById(userId) ?: return
        val updated = user.copy(mainBalance = user.mainBalance + addAmount)
        dao.updateUser(updated)

        dao.insertTransactionLog(
            TransactionLogEntity(
                userId = userId,
                type = "BONUS",
                category = "TASK",
                method = "SYSTEM",
                title = "Main Balance Top-Up (+৳${addAmount.toInt()})",
                description = "Admin top-up allocated to working main balance for task completions.",
                amount = addAmount,
                commissionAmount = 0.0,
                balanceImpact = addAmount,
                walletAffected = "MAIN",
                status = "COMPLETED",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun syncExistingDataToLogsIfNeeded(userId: Long) {
        val count = dao.countLogsForUser(userId)
        if (count == 0) {
            val user = dao.getUserById(userId) ?: return
            val submissions = dao.getSubmissionsByUserId(userId).firstOrNull() ?: emptyList()
            val withdrawals = dao.getWithdrawalsByUserId(userId).firstOrNull() ?: emptyList()

            val logs = mutableListOf<TransactionLogEntity>()

            // If user has existing submissions, convert them to logs
            for (sub in submissions) {
                logs.add(
                    TransactionLogEntity(
                        userId = userId,
                        type = if (sub.status == "APPROVED") "TASK_COMPLETION" else "PENDING_PAYMENT",
                        category = "TASK",
                        method = sub.method,
                        title = "${sub.method} Send Money: ৳${sub.taskAmount.toInt()}",
                        description = if (sub.status == "APPROVED") {
                            "Task completed & verified by Admin. 4% Commission (৳${String.format("%.1f", sub.commissionEarned)}) credited."
                        } else {
                            "Task submitted. Pending admin verification."
                        },
                        amount = sub.taskAmount,
                        commissionAmount = sub.commissionEarned,
                        balanceImpact = -sub.taskAmount,
                        walletAffected = "MAIN",
                        status = if (sub.status == "APPROVED") "COMPLETED" else sub.status,
                        trxId = sub.trxId,
                        targetNumber = "01811-223344",
                        senderNumber = sub.senderNumber,
                        relatedSubmissionId = sub.id,
                        timestamp = sub.submittedAt
                    )
                )
                if (sub.status == "APPROVED") {
                    logs.add(
                        TransactionLogEntity(
                            userId = userId,
                            type = "COMMISSION_EARNED",
                            category = "COMMISSION",
                            method = sub.method,
                            title = "4% Commission Earned (+৳${String.format("%.1f", sub.commissionEarned)})",
                            description = "Earned 4% commission on ${sub.method} task (${sub.trxId}).",
                            amount = sub.taskAmount,
                            commissionAmount = sub.commissionEarned,
                            balanceImpact = sub.commissionEarned,
                            walletAffected = "COMMISSION",
                            status = "COMPLETED",
                            trxId = sub.trxId,
                            senderNumber = sub.senderNumber,
                            relatedSubmissionId = sub.id,
                            timestamp = sub.reviewedAt ?: sub.submittedAt
                        )
                    )
                }
            }

            for (w in withdrawals) {
                logs.add(
                    TransactionLogEntity(
                        userId = userId,
                        type = "WITHDRAWAL",
                        category = "WITHDRAWAL",
                        method = w.method,
                        title = "${w.walletType} Wallet Payout: ৳${w.amount.toInt()}",
                        description = "Withdrawal to ${w.method} (${w.targetNumber}) - Status: ${w.status}",
                        amount = w.amount,
                        commissionAmount = 0.0,
                        balanceImpact = -w.amount,
                        walletAffected = w.walletType,
                        status = if (w.status == "APPROVED") "COMPLETED" else w.status,
                        targetNumber = w.targetNumber,
                        relatedWithdrawalId = w.id,
                        timestamp = w.requestedAt
                    )
                )
            }

            // If user had no prior submissions or withdrawals (e.g. demo user first launch), seed realistic logs:
            if (logs.isEmpty()) {
                val now = System.currentTimeMillis()
                // 1. Starter Demo balance credit
                logs.add(
                    TransactionLogEntity(
                        userId = userId,
                        type = "BONUS",
                        category = "TASK",
                        method = "SYSTEM",
                        title = "Starter Working Balance Credited",
                        description = "৳5,000 Starter Balance allocated for completing bKash & Nagad send money tasks.",
                        amount = 5000.0,
                        commissionAmount = 0.0,
                        balanceImpact = 5000.0,
                        walletAffected = "MAIN",
                        status = "COMPLETED",
                        timestamp = now - (6 * 3600 * 1000)
                    )
                )
                // 2. Completed bKash task: ৳1000
                logs.add(
                    TransactionLogEntity(
                        userId = userId,
                        type = "TASK_COMPLETION",
                        category = "TASK",
                        method = "BKASH",
                        title = "bKash Send Money - Personal Quick",
                        description = "Task completed & verified by Admin. TrxID: 9BK47721A. 4% Commission (৳40) credited.",
                        amount = 1000.0,
                        commissionAmount = 40.0,
                        balanceImpact = -1000.0,
                        walletAffected = "MAIN",
                        status = "COMPLETED",
                        trxId = "9BK47721A",
                        targetNumber = "01823-456789",
                        senderNumber = user.phone,
                        timestamp = now - (4 * 3600 * 1000)
                    )
                )
                // 3. Commission earned from bKash: ৳40
                logs.add(
                    TransactionLogEntity(
                        userId = userId,
                        type = "COMMISSION_EARNED",
                        category = "COMMISSION",
                        method = "BKASH",
                        title = "4% Commission Earned (+৳40.0)",
                        description = "Earned 4% commission for completed bKash task (TrxID: 9BK47721A).",
                        amount = 1000.0,
                        commissionAmount = 40.0,
                        balanceImpact = 40.0,
                        walletAffected = "COMMISSION",
                        status = "COMPLETED",
                        trxId = "9BK47721A",
                        senderNumber = user.phone,
                        timestamp = now - (4 * 3600 * 1000)
                    )
                )
                // 4. Completed Nagad task: ৳1500
                logs.add(
                    TransactionLogEntity(
                        userId = userId,
                        type = "TASK_COMPLETION",
                        category = "TASK",
                        method = "NAGAD",
                        title = "Nagad Send Money - Express Cashback",
                        description = "Task completed & verified by Admin. TrxID: 8NG63119B. 4% Commission (৳60) credited.",
                        amount = 1500.0,
                        commissionAmount = 60.0,
                        balanceImpact = -1500.0,
                        walletAffected = "MAIN",
                        status = "COMPLETED",
                        trxId = "8NG63119B",
                        targetNumber = "01955-889900",
                        senderNumber = user.phone,
                        timestamp = now - (2 * 3600 * 1000)
                    )
                )
                // 5. Commission earned from Nagad: ৳60
                logs.add(
                    TransactionLogEntity(
                        userId = userId,
                        type = "COMMISSION_EARNED",
                        category = "COMMISSION",
                        method = "NAGAD",
                        title = "4% Commission Earned (+৳60.0)",
                        description = "Earned 4% commission for completed Nagad task (TrxID: 8NG63119B).",
                        amount = 1500.0,
                        commissionAmount = 60.0,
                        balanceImpact = 60.0,
                        walletAffected = "COMMISSION",
                        status = "COMPLETED",
                        trxId = "8NG63119B",
                        senderNumber = user.phone,
                        timestamp = now - (2 * 3600 * 1000)
                    )
                )
                // 6. Pending bKash task: ৳2000
                logs.add(
                    TransactionLogEntity(
                        userId = userId,
                        type = "PENDING_PAYMENT",
                        category = "TASK",
                        method = "BKASH",
                        title = "bKash Send Money - High Value Task",
                        description = "Task submitted. Awaiting Admin verification. 4% Commission (৳80) will be credited upon approval.",
                        amount = 2000.0,
                        commissionAmount = 80.0,
                        balanceImpact = -2000.0,
                        walletAffected = "MAIN",
                        status = "PENDING",
                        trxId = "9BK89230C",
                        targetNumber = "01712-334455",
                        senderNumber = user.phone,
                        timestamp = now - (30 * 60 * 1000)
                    )
                )
                // 7. Referral Challenge progress bonus: ৳100
                logs.add(
                    TransactionLogEntity(
                        userId = userId,
                        type = "BONUS",
                        category = "COMMISSION",
                        method = "SYSTEM",
                        title = "Referral Welcome Bonus (+৳100.0)",
                        description = "Joined via referral code ETADMIN. Bonus added to commission wallet.",
                        amount = 100.0,
                        commissionAmount = 100.0,
                        balanceImpact = 100.0,
                        walletAffected = "COMMISSION",
                        status = "COMPLETED",
                        timestamp = now - (5 * 3600 * 1000)
                    )
                )
                // 8. Pending payout withdrawal: ৳150
                logs.add(
                    TransactionLogEntity(
                        userId = userId,
                        type = "WITHDRAWAL",
                        category = "WITHDRAWAL",
                        method = "BKASH",
                        title = "Commission Wallet Payout: ৳150",
                        description = "Withdrawal request to bKash Personal (01811-223344). Processing in queue.",
                        amount = 150.0,
                        commissionAmount = 0.0,
                        balanceImpact = -150.0,
                        walletAffected = "COMMISSION",
                        status = "PENDING",
                        targetNumber = user.phone,
                        timestamp = now - (15 * 60 * 1000)
                    )
                )
            }

            dao.insertTransactionLogs(logs)

            // Also seed realistic Task Submissions for offline cache viewing if user has none
            val existingSubmissionsCount = dao.countSubmissionsForUser(userId)
            if (existingSubmissionsCount == 0) {
                val demoSubmissions = listOf(
                    TaskSubmissionEntity(
                        taskId = 1,
                        taskTitle = "bKash Send Money - Personal Quick",
                        method = "BKASH",
                        taskAmount = 1000.0,
                        commissionEarned = 40.0,
                        userId = userId,
                        userName = user.name,
                        userPhone = user.phone,
                        senderNumber = user.phone,
                        trxId = "9BK47721A",
                        screenshotNote = "bKash statement screenshot verified",
                        status = "APPROVED",
                        adminNote = "Verified on bKash merchant portal. ৳40 commission credited.",
                        submittedAt = System.currentTimeMillis() - (4 * 3600 * 1000),
                        reviewedAt = System.currentTimeMillis() - (4 * 3600 * 1000) + 300000,
                        isOfflineCached = true,
                        cachedAt = System.currentTimeMillis(),
                        syncStatus = "SYNCED"
                    ),
                    TaskSubmissionEntity(
                        taskId = 2,
                        taskTitle = "Nagad Send Money - Express Cashback",
                        method = "NAGAD",
                        taskAmount = 1500.0,
                        commissionEarned = 60.0,
                        userId = userId,
                        userName = user.name,
                        userPhone = user.phone,
                        senderNumber = user.phone,
                        trxId = "8NG63119B",
                        screenshotNote = "Nagad app transaction receipt attached",
                        status = "APPROVED",
                        adminNote = "Nagad transaction ID verified. ৳60 commission credited.",
                        submittedAt = System.currentTimeMillis() - (2 * 3600 * 1000),
                        reviewedAt = System.currentTimeMillis() - (2 * 3600 * 1000) + 400000,
                        isOfflineCached = true,
                        cachedAt = System.currentTimeMillis(),
                        syncStatus = "SYNCED"
                    ),
                    TaskSubmissionEntity(
                        taskId = 3,
                        taskTitle = "bKash Send Money - High Value Task",
                        method = "BKASH",
                        taskAmount = 2000.0,
                        commissionEarned = 80.0,
                        userId = userId,
                        userName = user.name,
                        userPhone = user.phone,
                        senderNumber = user.phone,
                        trxId = "9BK89230C",
                        screenshotNote = "bKash SMS screenshot attached",
                        status = "PENDING",
                        adminNote = "In admin queue for merchant statement verification",
                        submittedAt = System.currentTimeMillis() - (30 * 60 * 1000),
                        isOfflineCached = true,
                        cachedAt = System.currentTimeMillis(),
                        syncStatus = "SYNCED"
                    ),
                    TaskSubmissionEntity(
                        taskId = 4,
                        taskTitle = "Nagad Send Money - Evening Batch",
                        method = "NAGAD",
                        taskAmount = 500.0,
                        commissionEarned = 20.0,
                        userId = userId,
                        userName = user.name,
                        userPhone = user.phone,
                        senderNumber = user.phone,
                        trxId = "8NG11094X",
                        screenshotNote = "Payment slip uploaded",
                        status = "REJECTED",
                        adminNote = "TrxID mismatch on Nagad statement. ৳500 refunded to main balance.",
                        submittedAt = System.currentTimeMillis() - (7 * 3600 * 1000),
                        reviewedAt = System.currentTimeMillis() - (6 * 3600 * 1000),
                        isOfflineCached = true,
                        cachedAt = System.currentTimeMillis(),
                        syncStatus = "SYNCED"
                    )
                )
                dao.insertSubmissions(demoSubmissions)
            }

            updateOfflineCacheMetadata(userId)
        }
    }

    suspend fun updateOfflineCacheMetadata(userId: Long) {
        val logsCount = dao.countLogsForUser(userId)
        val submissionsCount = dao.countSubmissionsForUser(userId)
        val userMeta = CacheMetadataEntity(
            cacheKey = "user_${userId}_cache",
            lastSyncTimestamp = System.currentTimeMillis(),
            cachedTransactionsCount = logsCount,
            cachedSubmissionsCount = submissionsCount,
            lastSyncStatus = "CACHED_OFFLINE_READY",
            storageEngine = "Room SQLite Database"
        )
        dao.insertOrUpdateCacheMetadata(userMeta)

        val primaryMeta = CacheMetadataEntity(
            cacheKey = "primary_cache",
            lastSyncTimestamp = System.currentTimeMillis(),
            cachedTransactionsCount = logsCount,
            cachedSubmissionsCount = submissionsCount,
            lastSyncStatus = "CACHED_OFFLINE_READY",
            storageEngine = "Room SQLite Database"
        )
        dao.insertOrUpdateCacheMetadata(primaryMeta)
    }

    suspend fun syncPendingOfflineItems(userId: Long) {
        dao.markAllSubmissionsSynced()
        dao.markAllLogsSynced()
        updateOfflineCacheMetadata(userId)
    }
}
