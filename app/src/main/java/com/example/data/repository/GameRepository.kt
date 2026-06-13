package com.example.data.repository

import android.content.Context
import com.example.core.firebase.MockFirebase
import com.example.core.room.AppDatabase
import com.example.domain.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

interface GameRepository {
    // Authentication
    val currentUser: Flow<User?>
    suspend fun signInAsGuest()
    suspend fun signInWithGoogle(email: String)
    suspend fun signOut()
    suspend fun addExp(amount: Long)
    suspend fun addCoinsAndGems(coins: Long, gems: Int)
    suspend fun incrementNoodlesCookedCount()

    // Inventory & Catalog
    val inventoryItems: Flow<List<InventoryItem>>
    suspend fun purchaseShopCatalogItem(item: ShopCatalogItem, quantity: Int)
    suspend fun equipItem(id: String, itemType: String, isEquipped: Boolean)

    // Skins Catalog & Equips
    val skinsCatalog: Flow<List<SkinItem>>
    suspend fun drawGachaItem(isGemDraw: Boolean): SkinItem?
    suspend fun selectChefSkin(skinId: String)
    suspend fun selectShopSkin(skinId: String)

    // Leaderboards & Events
    val leaderboards: Flow<List<LeaderboardEntry>>
    val holidayEvents: Flow<List<GameEvent>>
    val shopCatalog: Flow<List<ShopCatalogItem>>

    // Friends
    val friendsList: Flow<List<Friend>>
    suspend fun inviteFriend(name: String)
    suspend fun deleteFriend(uid: String)
    suspend fun acceptFriend(uid: String)

    // Local Settings (Room SQLite)
    val gameSettings: Flow<GameSetting?>
    suspend fun updateSettings(setting: GameSetting)

    // Achievements
    val activeAchievements: Flow<List<UserAchievement>>

    // Realtime Multiplayer Match & Rooms
    val matchRooms: Flow<Map<String, MatchRoom>>
    suspend fun createAndFindMatch(mode: String, onFound: (MatchRoom) -> Unit)
    suspend fun leaveMatch(roomId: String)

    // Live Instant Chat
    val globalChatMessages: Flow<List<ChatMessage>>
    suspend fun sendChatMessage(text: String, isEmoji: Boolean = false)

    // Statistics
    val gameStats: Flow<GameStatistics?>
    suspend fun updateLocalStats(stats: GameStatistics)

    // Offline Match History logger
    val offlineHistory: Flow<List<OfflineMatchHistory>>
    suspend fun saveMatchHistoryOffline(match: OfflineMatchHistory)
    suspend fun clearOfflineHistory()
}

class GameRepositoryImpl(
    private val context: Context,
    private val db: AppDatabase = AppDatabase.getDatabase(context)
) : GameRepository {

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        // Hydrate local Room from initial Mock firebase details
        scope.launch {
            db.settingsDao().insertSettings(
                GameSetting(
                    id = "app_settings",
                    soundEnabled = true,
                    bgMusicEnabled = true,
                    hardCoreEnabled = false,
                    darkModeEnabled = true,
                    currentThemeIndex = 0,
                    isRealtimeWeatherEnabled = true,
                    weatherCityIndex = 0
                )
            )
            
            // Populate achievements if they are empty
            db.achievementsDao().getAchievements().collect { ach ->
                if (ach.isEmpty()) {
                    db.achievementsDao().insertAchievements(MockFirebase.achievements.value)
                } else {
                    MockFirebase.achievements.value = ach
                }
            }
        }

        // Restore user session if it exists in local DB
        scope.launch {
            db.userDao().getLocalUser().collect { user ->
                if (user != null) {
                    MockFirebase.setCurrentUser(user)
                }
            }
        }

        // Restore/sync friends list - ensure any old fake/mock friends are removed
        scope.launch {
            db.friendsDao().getAllFriends().collect { friends ->
                val realFriends = friends.filter { it.uid != "friend_1" && it.uid != "friend_2" && it.uid != "friend_3" && it.uid != "friend_4" && it.uid != "friend_5" }
                if (friends.size != realFriends.size) {
                    db.friendsDao().clearFriends()
                    if (realFriends.isNotEmpty()) {
                        db.friendsDao().insertFriends(realFriends)
                    }
                }
                MockFirebase.friends.value = realFriends
            }
        }

        // Restore/sync inventory items
        scope.launch {
            db.inventoryDao().getInventory().collect { inv ->
                if (inv.isNotEmpty()) {
                    MockFirebase.inventory.value = inv
                } else {
                    // Seed initial inventory
                    db.inventoryDao().insertInventory(MockFirebase.inventory.value)
                }
            }
        }

        // Restore/sync statistics
        scope.launch {
            db.statsDao().getStatistics("local_statistics").collect { stats ->
                if (stats != null) {
                    MockFirebase.statistics.value = stats
                } else {
                    db.statsDao().insertOrUpdateStats(MockFirebase.statistics.value)
                }
            }
        }
    }

    // Auth
    override val currentUser: Flow<User?> = MockFirebase.currentUser

    override suspend fun signInAsGuest() {
        MockFirebase.signInAsGuest { guestUser ->
            scope.launch {
                db.userDao().insertUser(guestUser)
            }
        }
    }

    override suspend fun signInWithGoogle(email: String) {
        MockFirebase.signInWithGoogle(email) { googleUser ->
            scope.launch {
                db.userDao().insertUser(googleUser)
            }
        }
    }

    override suspend fun signOut() {
        MockFirebase.signOut()
        scope.launch {
            db.userDao().clearAllUsers()
        }
    }

    override suspend fun addExp(amount: Long) {
        MockFirebase.addExperience(amount)
        MockFirebase.currentUser.value?.let {
            db.userDao().insertUser(it)
        }
    }

    override suspend fun addCoinsAndGems(coins: Long, gems: Int) {
        MockFirebase.addCurrency(coins, gems)
        MockFirebase.currentUser.value?.let {
            db.userDao().insertUser(it)
        }
    }

    override suspend fun incrementNoodlesCookedCount() {
        MockFirebase.incrementNoodlesCooked()
        MockFirebase.currentUser.value?.let {
            db.userDao().insertUser(it)
        }
    }

    // Inventory
    override val inventoryItems: Flow<List<InventoryItem>> = MockFirebase.inventory

    override suspend fun purchaseShopCatalogItem(item: ShopCatalogItem, quantity: Int) {
        MockFirebase.purchaseShopItem(item, quantity)
        // sync to local Room
        db.inventoryDao().insertInventory(MockFirebase.inventory.value)
    }

    override suspend fun equipItem(id: String, itemType: String, isEquipped: Boolean) {
        val currentInv = MockFirebase.inventory.value.map { item ->
            if (item.id == id) {
                item.copy(isEquipped = isEquipped)
            } else {
                // For STOVE and POT, we can only equip one at a time, so auto-unequip others of same type
                if (itemType == "STOVE" || itemType == "POT") {
                    if (item.itemType == itemType && isEquipped) item.copy(isEquipped = false) else item
                } else item
            }
        }
        MockFirebase.inventory.value = currentInv
        db.inventoryDao().insertInventory(currentInv)
    }

    // Skins Catalog / Gacha
    override val skinsCatalog: Flow<List<SkinItem>> = MockFirebase.skinsCatalog

    override suspend fun drawGachaItem(isGemDraw: Boolean): SkinItem? {
        val drawn = MockFirebase.drawGacha(isGemDraw)
        if (drawn != null) {
            // sync inventory
            db.inventoryDao().insertInventory(MockFirebase.inventory.value)
        }
        return drawn
    }

    override suspend fun selectChefSkin(skinId: String) {
        MockFirebase.selectChefSkin(skinId)
    }

    override suspend fun selectShopSkin(skinId: String) {
        MockFirebase.selectShopSkin(skinId)
    }

    // Catalog & Events
    override val leaderboards: Flow<List<LeaderboardEntry>> = MockFirebase.leaderboards
    override val holidayEvents: Flow<List<GameEvent>> = MockFirebase.events
    override val shopCatalog: Flow<List<ShopCatalogItem>> = MockFirebase.shopCatalog

    // Friends
    override val friendsList: Flow<List<Friend>> = MockFirebase.friends
    
    override suspend fun inviteFriend(name: String) {
        MockFirebase.sendFriendRequest(name)
        db.friendsDao().insertFriends(MockFirebase.friends.value)
    }

    override suspend fun deleteFriend(uid: String) {
        MockFirebase.removeFriend(uid)
        db.friendsDao().deleteFriend(uid)
    }

    override suspend fun acceptFriend(uid: String) {
        MockFirebase.acceptFriendRequest(uid)
        db.friendsDao().insertFriends(MockFirebase.friends.value)
    }

    // Settings (Room SQLite)
    override val gameSettings: Flow<GameSetting?> = db.settingsDao().getSettings()

    override suspend fun updateSettings(setting: GameSetting) {
        db.settingsDao().insertSettings(setting)
    }

    // Achievements
    override val activeAchievements: Flow<List<UserAchievement>> = MockFirebase.achievements

    // Multiplayer Matches
    override val matchRooms: Flow<Map<String, MatchRoom>> = MockFirebase.activeRooms

    override suspend fun createAndFindMatch(mode: String, onFound: (MatchRoom) -> Unit) {
        MockFirebase.searchOnlineMatch(mode, onFound)
    }

    override suspend fun leaveMatch(roomId: String) {
        MockFirebase.leaveMatchRoom(roomId)
    }

    // InstChat
    override val globalChatMessages: Flow<List<ChatMessage>> = MockFirebase.chatMessages

    override suspend fun sendChatMessage(text: String, isEmoji: Boolean) {
        MockFirebase.sendGlobalChatMessage(text, isEmoji)
    }

    // Statistics
    override val gameStats: Flow<GameStatistics?> = MockFirebase.statistics

    override suspend fun updateLocalStats(stats: GameStatistics) {
        MockFirebase.statistics.value = stats
        db.statsDao().insertOrUpdateStats(stats)
    }

    // Local SQLite Log History
    override val offlineHistory: Flow<List<OfflineMatchHistory>> = db.matchHistoryDao().getMatchHistory()

    override suspend fun saveMatchHistoryOffline(match: OfflineMatchHistory) {
        db.matchHistoryDao().insertMatchHistory(match)
    }

    override suspend fun clearOfflineHistory() {
        db.matchHistoryDao().clearHistory()
    }
}
