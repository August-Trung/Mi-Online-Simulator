package com.example.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

// --- Firebase Firestore Mappings ---

// Collection: "users"
@Entity(tableName = "users")
data class User(
    @PrimaryKey val uid: String = "",
    val name: String = "Đầu Bếp Tập Sự",
    val email: String = "",
    val avatarUrl: String = "",
    val level: Int = 1,
    val exp: Long = 0,
    val coins: Long = 500,
    val gems: Int = 10,
    val noodlesCooked: Int = 0,
    val pvpWins: Int = 0,
    val selectedChefSkinId: String = "skin_chef_default",
    val selectedShopSkinId: String = "skin_shop_vietnam",
    val isGuest: Boolean = false
)

// Collection: "friends"
@Entity(tableName = "friends")
data class Friend(
    @PrimaryKey val uid: String = "",
    val name: String = "",
    val avatarUrl: String = "",
    val level: Int = 1,
    val status: String = "OFFLINE", // ONLINE, OFFLINE, PLAYING
    val isRequestPending: Boolean = false,
    val isIncomingRequest: Boolean = false
)

// Collection: "inventory"
@Entity(tableName = "inventory")
data class InventoryItem(
    @PrimaryKey val id: String = "",
    val itemType: String = "", // INGREDIENT, SKIN, DECORATION, POT, STOVE
    val name: String = "",
    val quantity: Int = 1,
    val isEquipped: Boolean = false,
    val bonusMultiplier: Float = 1.0f
)

// Collection: "skins"
data class SkinItem(
    val id: String = "",
    val type: String = "", // CHEF, RESTAURANT
    val name: String = "",
    val rarity: String = "COMMON", // COMMON, RARE, EPIC, LEGENDARY
    val priceCoins: Long = 0,
    val priceGems: Int = 0,
    val isUnlocked: Boolean = false,
    val bonusExpMultiplier: Float = 1.0f
)

// Collection: "leaderboards"
data class LeaderboardEntry(
    val id: String = "",
    val uid: String = "",
    val name: String = "",
    val rankType: String = "LEVEL", // LEVEL, NOODLES, PVP_WINS, COINS
    val value: Long = 0,
    val rank: Int = 0
)

// Collection: "matches" and "rooms" (Firebase Realtime Database / Firestore)
data class MatchRoom(
    val roomId: String = "",
    val hostId: String = "",
    val hostName: String = "",
    val challengerId: String = "",
    val challengerName: String = "",
    val gameMode: String = "PVP", // PVP, COOP, PARTY
    val status: String = "WAITING", // WAITING, PLAYING, FINISHED
    val weather: String = "CLEAR", // SUNNY, AFTERNOON, NIGHT, RAINY
    val currentTurn: Int = 0,
    val hostScore: Int = 0,
    val challengerScore: Int = 0,
    val winnerId: String = ""
)

// Collection: "chatMessages"
data class ChatMessage(
    val messageId: String = "",
    val roomId: String = "", // "GLOBAL" or "ROOM_ID"
    val senderId: String = "",
    val senderName: String = "",
    val messageText: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isEmojiOnly: Boolean = false
)

// Collection: "achievements"
@Entity(tableName = "achievements")
data class UserAchievement(
    @PrimaryKey val id: String = "",
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
    val progress: Int = 0,
    val maxProgress: Int = 100,
    val rewardCoins: Long = 0,
    val rewardGems: Int = 0
)

// Collection: "events"
data class GameEvent(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val theme: String = "NORMAL", // TET, NOEL, HALLOWEEN, WORLDCUP
    val rewardSkinId: String = "",
    val isActive: Boolean = false
)

// Collection: "shop"
data class ShopCatalogItem(
    val id: String = "",
    val name: String = "",
    val type: String = "INGREDIENT", // INGREDIENT,SKIN,STOVE,POT,DECORATION
    val subCategory: String = "",
    val priceCoins: Long = 0,
    val priceGems: Int = 0,
    val levelRequired: Int = 1,
    val iconResName: String = ""
)

// Collection: "gacha"
data class GachaPool(
    val id: String = "",
    val name: String = "",
    val costCoins: Long = 1000,
    val costGems: Int = 10,
    val itemsAndWeights: Map<String, Float> = emptyMap() // itemId to weight
)

// Collection: "statistics"
@Entity(tableName = "statistics")
data class GameStatistics(
    @PrimaryKey val uid: String = "local_statistics",
    val totalNoodlesCooked: Int = 0,
    val matchesPlayed: Int = 0,
    val pvpWins: Int = 0,
    val pvpLosses: Int = 0,
    val totalRevenue: Long = 0,
    val favoriteNoodleId: String = "Mì Tôm Hảo Hảo",
    val averageScore: Float = 0.0f
)


// --- Room Offline-Only Entities (Required by guidelines) ---

@Entity(tableName = "settings")
data class GameSetting(
    @PrimaryKey val id: String = "app_settings",
    val soundEnabled: Boolean = true,
    val bgMusicEnabled: Boolean = true,
    val hardCoreEnabled: Boolean = false,
    val darkModeEnabled: Boolean = true,
    val currentThemeIndex: Int = 0, // 0: Dark, 1: Light
    val isRealtimeWeatherEnabled: Boolean = true,
    val weatherCityIndex: Int = 0 // 0: Sài Gòn, 1: Hà Nội, 2: Đà Nẵng
)

@Entity(tableName = "match_history")
data class OfflineMatchHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val opponentName: String = "",
    val mode: String = "PVP",
    val result: String = "THẮNG", // THẮNG, THUA, HOÀ
    val score: Int = 0,
    val dateMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "save_game")
data class SaveGameState(
    @PrimaryKey val id: String = "current_save",
    val jsonState: String = "", // Serialized user database progress for offline resilience
    val lastSavedMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "temporary_data")
data class TemporaryData(
    @PrimaryKey val keyRef: String = "",
    val valRef: String = ""
)


// --- Game Core Cooking Logic Models ---

data class NoodleType(
    val id: String,
    val name: String,
    val description: String,
    val targetWaterLevel: Float, // Target Water in ml (e.g. 400ml)
    val targetCookingTimeSec: Int, // e.g. 180 seconds (3 mins)
    val minLevel: Int,
    val unlockCost: Long
)

data class ToppingItem(
    val id: String,
    val name: String,
    val prepTimeSec: Int,
    val cost: Long,
    val requiredLevel: Int
)

enum class TemperatureState(val label: String, val tempRange: IntRange) {
    NORMAL("Bình Thường (20°C - 49°C)", 20..49),
    WARM("Bốc Hơi (50°C - 79°C)", 50..79),
    LIGHT_BOILING("Sôi Nhẹ (80°C - 99°C)", 80..99),
    STRONG_BOILING("Sôi Mạnh (100°C+)", 100..120);

    companion object {
        fun fromTemp(temp: Float): TemperatureState {
            return when {
                temp < 50f -> NORMAL
                temp < 80f -> WARM
                temp < 100f -> LIGHT_BOILING
                else -> STRONG_BOILING
            }
        }
    }
}

enum class NoodleDoneness(val label: String) {
    RAW("Sống"),
    UNDERCOOKED("Hơi Chín"),
    AL_DENTE("Chín Vừa"),
    PERFECT("Hoàn Hảo"),
    OVERCOOKED("Nhũn");

    companion object {
        fun fromProgress(progress: Float): NoodleDoneness {
            return when {
                progress < 0.25f -> RAW
                progress < 0.50f -> UNDERCOOKED
                progress < 0.75f -> AL_DENTE
                progress < 1.0f -> PERFECT
                else -> OVERCOOKED
            }
        }
    }
}

data class Customer(
    val id: String,
    val name: String,
    val typeName: String, // Dễ Tính, Khó Tính, Thích Cay, Không Rau, Thích Thịt
    val description: String,
    val preferenceText: String
)
