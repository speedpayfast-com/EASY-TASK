package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.AppDao
import com.example.data.model.CacheMetadataEntity
import com.example.data.model.ReferralRewardEntity
import com.example.data.model.TaskEntity
import com.example.data.model.TaskSubmissionEntity
import com.example.data.model.TransactionLogEntity
import com.example.data.model.UserEntity
import com.example.data.model.WithdrawalEntity

@Database(
    entities = [
        UserEntity::class,
        TaskEntity::class,
        TaskSubmissionEntity::class,
        WithdrawalEntity::class,
        ReferralRewardEntity::class,
        TransactionLogEntity::class,
        CacheMetadataEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "easy_task_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
