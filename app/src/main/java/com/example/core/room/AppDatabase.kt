package com.example.core.room

import androidx.room.*
import com.example.domain.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    fun getUserById(uid: String): Flow<User?>

    @Query("SELECT * FROM users LIMIT 1")
    fun getLocalUser(): Flow<User?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("DELETE FROM users")
    suspend fun clearAllUsers()
}

@Dao
interface FriendsDao {
    @Query("SELECT * FROM friends ORDER BY name ASC")
    fun getAllFriends(): Flow<List<Friend>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriend(friend: Friend)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriends(friends: List<Friend>)

    @Query("DELETE FROM friends WHERE uid = :uid")
    suspend fun deleteFriend(uid: String)

    @Query("DELETE FROM friends")
    suspend fun clearFriends()
}

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory ORDER BY name ASC")
    fun getInventory(): Flow<List<InventoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventoryItem(item: InventoryItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventory(items: List<InventoryItem>)

    @Query("UPDATE inventory SET isEquipped = :equipped WHERE id = :id")
    suspend fun setEquippedState(id: String, equipped: Boolean)

    @Query("DELETE FROM inventory WHERE id = :id")
    suspend fun deleteInventoryItem(id: String)
}

@Dao
interface AchievementsDao {
    @Query("SELECT * FROM achievements ORDER BY id ASC")
    fun getAchievements(): Flow<List<UserAchievement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<UserAchievement>)

    @Query("UPDATE achievements SET isCompleted = :completed, progress = :progress WHERE id = :id")
    suspend fun updateAchievementProgress(id: String, completed: Boolean, progress: Int)
}

@Dao
interface StatsDao {
    @Query("SELECT * FROM statistics WHERE uid = :uid LIMIT 1")
    fun getStatistics(uid: String): Flow<GameStatistics?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStats(stats: GameStatistics)
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE id = :id LIMIT 1")
    fun getSettings(id: String = "app_settings"): Flow<GameSetting?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(setting: GameSetting)
}

@Dao
interface MatchHistoryDao {
    @Query("SELECT * FROM match_history ORDER BY dateMillis DESC")
    fun getMatchHistory(): Flow<List<OfflineMatchHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatchHistory(match: OfflineMatchHistory)

    @Query("DELETE FROM match_history")
    suspend fun clearHistory()
}

@Dao
interface SaveGameDao {
    @Query("SELECT * FROM save_game WHERE id = :id LIMIT 1")
    fun getSave(id: String = "current_save"): Flow<SaveGameState?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSave(save: SaveGameState)
}


@Database(
    entities = [
        User::class,
        Friend::class,
        InventoryItem::class,
        UserAchievement::class,
        GameStatistics::class,
        GameSetting::class,
        OfflineMatchHistory::class,
        SaveGameState::class,
        TemporaryData::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun friendsDao(): FriendsDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun achievementsDao(): AchievementsDao
    abstract fun statsDao(): StatsDao
    abstract fun settingsDao(): SettingsDao
    abstract fun matchHistoryDao(): MatchHistoryDao
    abstract fun saveGameDao(): SaveGameDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mi_online_simulator_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
