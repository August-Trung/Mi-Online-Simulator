package com.example.core.firebase

import com.example.domain.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

object MockFirebase {
    private val scope = CoroutineScope(Dispatchers.Default)

    // --- Authentication State ---
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // --- Firestore Collections Simulation ---
    val users = MutableStateFlow<Map<String, User>>(emptyMap())
    val friends = MutableStateFlow<List<Friend>>(emptyList())
    val achievements = MutableStateFlow<List<UserAchievement>>(emptyList())
    val events = MutableStateFlow<List<GameEvent>>(emptyList())
    val shopCatalog = MutableStateFlow<List<ShopCatalogItem>>(emptyList())
    val skinsCatalog = MutableStateFlow<List<SkinItem>>(emptyList())
    val leaderboards = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val activeRooms = MutableStateFlow<Map<String, MatchRoom>>(emptyMap())
    val chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val inventory = MutableStateFlow<List<InventoryItem>>(emptyList())
    val statistics = MutableStateFlow<GameStatistics>(GameStatistics())

    // --- Predefined Comments from Customers (No AI) ---
    val commentsGood = listOf(
        "Mì chín tới cực kỳ hoàn hảo! Quá đỉnh!",
        "Hương vị đậm đà, nước dùng nóng hổi, chấm 10 điểm!",
        "Thịt bò mềm ngon, gia vị nêm rất vừa miệng.",
        "Sợi mì dai giòn sần sật, đúng chuẩn Ramen Nhật Bản!",
        "Đậm đà hương vị truyền thống, ăn một bát muốn ăn thêm!"
    )

    val commentsNeutral = listOf(
        "Nấu cũng khá ổn, sợi mì hơi nát một xíu nhưng vẫn ngon.",
        "Nước dùng hơi loãng, bù lại topping đầy ặn vớt vát.",
        "Mì tôm bình thường thôi, không có gì quá nổi bật.",
        "Ăn vừa sướng vừa cay xè, nếu cho thêm đá thì tốt."
    )

    val commentsBad = listOf(
        "Mì nát bét như cháo thế này nếm sao nổi!",
        "Quên cho gia vị à? Nhạt nhẽo cực kỳ!",
        "Nước lạnh ngắt, mì thì chưa chín, siêu tệ!",
        "Mì mặn chát quá, lượng nước quá ít, cháy cả đáy nồi!",
        "Quá nhiều nước loãng toẹt, ăn chán không tả được."
    )

    init {
        initializeStaticData()
    }

    private fun initializeStaticData() {
        // Init Skins Catalog
        skinsCatalog.value = listOf(
            SkinItem("skin_chef_default", "CHEF", "Đầu bếp Mặc Định", "COMMON", 0, 0, true, 1.0f),
            SkinItem("skin_chef_kimono", "CHEF", "Ninja Nấu Lực", "RARE", 2000, 10, false, 1.2f),
            SkinItem("skin_chef_panda", "CHEF", "Đại Hiệp Panda Noodles", "EPIC", 5000, 30, false, 1.5f),
            SkinItem("skin_chef_goku", "CHEF", "Siêu Saiza Mì Cay", "LEGENDARY", 15000, 100, false, 2.0f),

            SkinItem("skin_shop_vietnam", "RESTAURANT", "Cổ Kính Việt Nam", "COMMON", 0, 0, true, 1.0f),
            SkinItem("skin_shop_korea", "RESTAURANT", "Lãng Mạn Hàn Quốc", "RARE", 3000, 15, false, 1.3f),
            SkinItem("skin_shop_japan", "RESTAURANT", "Bàn Gỗ Sakura Nhật", "EPIC", 6000, 40, false, 1.6f),
            SkinItem("skin_shop_future", "RESTAURANT", "Cyberpunk Noodles 2077", "LEGENDARY", 20000, 120, false, 2.5f)
        )

        // Init Achievements Catalog
        achievements.value = listOf(
            UserAchievement("ach_100_bowls", "Đầu Bếp Tập Sự", "Nấu thành công 5 tô mì.", false, 0, 5, 500, 5),
            UserAchievement("ach_1000_bowls", "Đồng Hành Cùng Mì", "Nấu thành công 25 tô mì.", false, 0, 25, 2000, 20),
            UserAchievement("ach_master_chef", "Huyền Thoại Đầu Bếp", "Nấu thành công 100 tô mì.", false, 0, 100, 10000, 100),
            UserAchievement("ach_pvp_king", "Vua Mì Cay", "Thắng 10 trận PvP Online.", false, 0, 10, 3000, 15),
            UserAchievement("ach_rich", "Đại Gia Quán Mì", "Tích lũy 50,000 Coins.", false, 0, 50000, 1000, 5),
            UserAchievement("ach_gacha_god", "Bàn Tay May Mắn", "Quay Gacha 10 lần.", false, 0, 10, 1500, 8)
        )

        // Init Holiday Events
        events.value = listOf(
            GameEvent("evt_tet", "Sự Kiện Tết Nguyên Đán", "Nấu mì bánh chưng nhận lì xì đỏ!", "TET", "skin_chef_panda", true),
            GameEvent("evt_noel", "Mùa Đông Ấm Áp Noel", "Mở bán nước dùng tuyết rơi mát rượi!", "NOEL", "skin_chef_kimono", false),
            GameEvent("evt_halloween", "Lễ Hội Hóa Trang Ma Quỷ", "Topping bí ngô bay, ăn là rợn tóc gáy!", "HALLOWEEN", "skin_chef_goku", false),
            GameEvent("evt_worldcup", "Cuồng Nhiệt World Cup", "Thu thập bóng vàng, nhận tủ kem cực đã!", "WORLDCUP", "skin_shop_future", false)
        )

        // Init Shop Items Catalog
        shopCatalog.value = listOf(
            ShopCatalogItem("ing_haohao", "Mì Hảo Hảo", "INGREDIENT", "NOODLE", 100, 0, 1, "ic_noodle"),
            ShopCatalogItem("ing_omachi", "Mì Omachi", "INGREDIENT", "NOODLE", 150, 0, 2, "ic_noodle"),
            ShopCatalogItem("ing_ramen", "Mì Ramen Cao Cấp", "INGREDIENT", "NOODLE", 300, 2, 5, "ic_noodle"),
            ShopCatalogItem("ing_udon", "Mì Udon Béo Ngậy", "INGREDIENT", "NOODLE", 400, 3, 8, "ic_noodle"),
            ShopCatalogItem("ing_pho", "Phở Bò Hà Nội", "INGREDIENT", "NOODLE", 350, 2, 10, "ic_noodle"),
            
            ShopCatalogItem("top_egg", "Trứng Gà Ta", "INGREDIENT", "TOPPING", 50, 0, 1, "ic_topping"),
            ShopCatalogItem("top_sausage", "Xúc Xích Đức", "INGREDIENT", "TOPPING", 80, 0, 1, "ic_topping"),
            ShopCatalogItem("top_beef", "Thịt Bò Mỹ", "INGREDIENT", "TOPPING", 200, 1, 3, "ic_topping"),
            ShopCatalogItem("top_cheese", "Phô Mai Dẻo Cheddar", "INGREDIENT", "TOPPING", 150, 1, 4, "ic_topping"),
            ShopCatalogItem("top_kimchi", "Kim Chi Chua Cay", "INGREDIENT", "TOPPING", 100, 0, 2, "ic_topping"),

            ShopCatalogItem("stove_gas", "Bếp Ga Mini", "STOVE", "UPGRADE", 1000, 5, 2, "ic_stove"),
            ShopCatalogItem("stove_induction", "Bếp Từ Siêu Tốc", "STOVE", "UPGRADE", 5000, 20, 10, "ic_stove"),
            ShopCatalogItem("pot_clay", "Nồi Đất Giữ Nhiệt", "POT", "UPGRADE", 800, 4, 3, "ic_pot"),
            ShopCatalogItem("pot_gold", "Nồi Hoàng Gia Mạ Vàng", "POT", "UPGRADE", 10000, 50, 20, "ic_pot"),

            ShopCatalogItem("decor_table", "Bếp Trưởng Đựng Hoa", "DECORATION", "SHOP", 1200, 5, 2, "ic_decor"),
            ShopCatalogItem("decor_chair", "Ghê Tựa Sang Sang", "DECORATION", "SHOP", 800, 3, 1, "ic_decor")
        )

        // Init Mock Online Friends
        friends.value = emptyList()

        // Init Core Inventory
        inventory.value = listOf(
            InventoryItem("ing_haohao", "INGREDIENT", "Mì Hảo Hảo", 10, true),
            InventoryItem("top_egg", "INGREDIENT", "Trứng Gà Ta", 5, false),
            InventoryItem("top_sausage", "INGREDIENT", "Xúc Xích Đức", 3, false),
            InventoryItem("pot_normal", "POT", "Nồi Nhôm Tập Sự", 1, true),
            InventoryItem("stove_normal", "STOVE", "Bếp Ga Du Lịch", 1, true)
        )

        // Init Global Chat Messages
        chatMessages.value = emptyList()

        syncLeaderboards()
    }

    fun syncLeaderboards() {
        val list = mutableListOf<LeaderboardEntry>()
        val types = listOf("LEVEL", "NOODLES", "PVP_WINS", "COINS")
        val currentUsers = users.value.values.toList()

        types.forEach { type ->
            val sortedUsers = currentUsers.sortedByDescending { u ->
                when (type) {
                    "LEVEL" -> u.level.toLong()
                    "NOODLES" -> u.noodlesCooked.toLong()
                    "PVP_WINS" -> u.pvpWins.toLong()
                    else -> u.coins
                }
            }
            sortedUsers.forEachIndexed { index, u ->
                list.add(
                    LeaderboardEntry(
                        id = "${type.lowercase()}_${u.uid}",
                        uid = u.uid,
                        name = u.name,
                        rankType = type,
                        value = when (type) {
                            "LEVEL" -> u.level.toLong()
                            "NOODLES" -> u.noodlesCooked.toLong()
                            "PVP_WINS" -> u.pvpWins.toLong()
                            else -> u.coins
                        },
                        rank = index + 1
                    )
                )
            }
        }
        leaderboards.value = list
    }

    // --- API functions ---

    fun signInAsGuest(onSuccess: (User) -> Unit) {
        val user = User(
            uid = "guest_" + UUID.randomUUID().toString().take(6),
            name = "Khách Hào Sảng",
            level = 1,
            exp = 0,
            coins = 800,
            gems = 15,
            isGuest = true
        )
        _currentUser.value = user
        users.value = users.value + (user.uid to user)
        syncLeaderboards()
        onSuccess(user)
    }

    fun signInWithGoogle(email: String, onSuccess: (User) -> Unit) {
        val username = email.substringBefore("@")
        val user = when (email) {
            "nguyenminhtrung01082003@gmail.com" -> User(
                uid = "google_minhtrung",
                name = "Nguyễn Minh Trung",
                email = email,
                level = 1,
                exp = 120,
                coins = 1500,
                gems = 30,
                isGuest = false,
                noodlesCooked = 0
            )
            "minhtrung.chef@gmail.com" -> User(
                uid = "google_chef_trung",
                name = "Chef Minh Trung",
                email = email,
                level = 15,
                exp = 4800,
                coins = 24500,
                gems = 180,
                isGuest = false,
                noodlesCooked = 237
            )
            else -> User(
                uid = "google_" + UUID.randomUUID().toString().take(6),
                name = username.split(".","_").joinToString(" ") { it.replaceFirstChar { c -> if (c.isLowerCase()) c.titlecase() else c.toString() } },
                email = email,
                level = 1,
                exp = 0,
                coins = 800,
                gems = 15,
                isGuest = false,
                noodlesCooked = 0
            )
        }
        _currentUser.value = user
        users.value = users.value + (user.uid to user)
        syncLeaderboards()
        onSuccess(user)
    }

    fun setCurrentUser(user: User?) {
        _currentUser.value = user
    }

    fun signOut() {
        _currentUser.value = null
    }

    fun addExperience(amount: Long) {
        val current = _currentUser.value ?: return
        var newExp = current.exp + amount
        var newLevel = current.level
        
        // Simulating level up formula: level * 1000 exp
        var expNeeded = newLevel * 1000
        while (newExp >= expNeeded && newLevel < 100) {
            newExp -= expNeeded
            newLevel++
            expNeeded = newLevel * 1000
        }

        val updated = current.copy(
            level = newLevel,
            exp = newExp
        )
        _currentUser.value = updated
        users.value = users.value + (updated.uid to updated)
        syncLeaderboards()
    }

    fun addCurrency(coins: Long, gems: Int) {
        val current = _currentUser.value ?: return
        val updated = current.copy(
            coins = current.coins + coins,
            gems = current.gems + gems
        )
        _currentUser.value = updated
        users.value = users.value + (updated.uid to updated)
        syncLeaderboards()
    }

    fun incrementNoodlesCooked() {
        val current = _currentUser.value ?: return
        val updated = current.copy(noodlesCooked = current.noodlesCooked + 1)
        _currentUser.value = updated
        users.value = users.value + (updated.uid to updated)
        syncLeaderboards()

        // Update statistics
        val stats = statistics.value
        statistics.value = stats.copy(
            totalNoodlesCooked = stats.totalNoodlesCooked + 1
        )

        // Increment achievements
        updateAchievementsProgress("ach_100_bowls", 1)
        updateAchievementsProgress("ach_1000_bowls", 1)
        updateAchievementsProgress("ach_master_chef", 1)
    }

    fun incrementPvPWins() {
        val current = _currentUser.value ?: return
        val updated = current.copy(pvpWins = current.pvpWins + 1)
        _currentUser.value = updated
        users.value = users.value + (updated.uid to updated)
        syncLeaderboards()

        val stats = statistics.value
        statistics.value = stats.copy(
            matchesPlayed = stats.matchesPlayed + 1,
            pvpWins = stats.pvpWins + 1
        )
        updateAchievementsProgress("ach_pvp_king", 1)
    }

    fun incrementPvPLosses() {
        val stats = statistics.value
        statistics.value = stats.copy(
            matchesPlayed = stats.matchesPlayed + 1,
            pvpLosses = stats.pvpLosses + 1
        )
    }

    private fun updateAchievementsProgress(achId: String, increment: Int) {
        val list = achievements.value.map { ach ->
            if (ach.id == achId && !ach.isCompleted) {
                val newProgress = (ach.progress + increment).coerceAtMost(ach.maxProgress)
                val isDone = newProgress >= ach.maxProgress
                if (isDone) {
                    addCurrency(ach.rewardCoins, ach.rewardGems)
                }
                ach.copy(progress = newProgress, isCompleted = isDone)
            } else ach
        }
        achievements.value = list
    }

    // --- Friends Action Simulators ---
    fun sendFriendRequest(friendName: String) {
        val currentList = friends.value
        val newFriend = Friend(
            uid = "friend_" + Random.nextInt(100, 999),
            name = friendName,
            avatarUrl = "avatar_" + Random.nextInt(1, 6),
            level = Random.nextInt(1, 10),
            status = "PENDING",
            isRequestPending = true,
            isIncomingRequest = false
        )
        friends.value = currentList + newFriend
    }

    fun acceptFriendRequest(uid: String) {
        friends.value = friends.value.map { f ->
            if (f.uid == uid) {
                f.copy(isRequestPending = false, status = "ONLINE", level = 15)
            } else f
        }
    }

    fun removeFriend(uid: String) {
        friends.value = friends.value.filter { f -> f.uid != uid }
    }

    // --- Shop & Purchasing Simulators ---
    fun purchaseShopItem(item: ShopCatalogItem, quantity: Int = 1) {
        val current = _currentUser.value ?: return
        if (current.coins >= item.priceCoins && current.gems >= item.priceGems) {
            addCurrency(-item.priceCoins * quantity, -item.priceGems * quantity)
            
            // Add to inventory
            val inv = inventory.value
            val existing = inv.find { it.id == item.id }
            if (existing != null) {
                inventory.value = inv.map {
                    if (it.id == item.id) it.copy(quantity = it.quantity + quantity) else it
                }
            } else {
                inventory.value = inv + InventoryItem(
                    id = item.id,
                    itemType = item.type,
                    name = item.name,
                    quantity = quantity,
                    isEquipped = false
                )
            }
        }
    }

    // --- Gacha Drawing Simulator ---
    fun drawGacha(isGemDraw: Boolean): SkinItem? {
        val current = _currentUser.value ?: return null
        val costCoins = 1000L
        val costGems = 10

        if (isGemDraw && current.gems < costGems) return null
        if (!isGemDraw && current.coins < costCoins) return null

        if (isGemDraw) addCurrency(0, -costGems) else addCurrency(-costCoins, 0)
        updateAchievementsProgress("ach_gacha_god", 1)

        // Gacha rate chances: Common 60%, Rare 25%, Epic 12%, Legendary 3%
        val roll = Random.nextFloat()
        val r = when {
            roll < 0.60f -> "COMMON"
            roll < 0.85f -> "RARE"
            roll < 0.97f -> "EPIC"
            else -> "LEGENDARY"
        }

        val availableSkins = skinsCatalog.value.filter { it.rarity == r && it.id != "skin_chef_default" && it.id != "skin_shop_vietnam" }
        if (availableSkins.isEmpty()) return null

        val pulled = availableSkins.random()
        
        // Unlock Skin in Catalog
        skinsCatalog.value = skinsCatalog.value.map {
            if (it.id == pulled.id) it.copy(isUnlocked = true) else it
        }

        // Add to inventory
        val inv = inventory.value
        if (inv.none { it.id == pulled.id }) {
            inventory.value = inv + InventoryItem(
                id = pulled.id,
                itemType = "SKIN",
                name = pulled.name,
                quantity = 1,
                isEquipped = false
            )
        }

        return pulled
    }

    // --- Equipped Skin Simulation ---
    fun selectChefSkin(skinId: String) {
        val user = _currentUser.value ?: return
        _currentUser.value = user.copy(selectedChefSkinId = skinId)
    }

    fun selectShopSkin(skinId: String) {
        val user = _currentUser.value ?: return
        _currentUser.value = user.copy(selectedShopSkinId = skinId)
    }

    // --- Messaging & Chat Simulation ---
    fun sendGlobalChatMessage(text: String, isEmoji: Boolean = false) {
        val user = _currentUser.value ?: return
        val newMsg = ChatMessage(
            messageId = "msg_" + Random.nextInt(1000, 9999),
            roomId = "GLOBAL",
            senderId = user.uid,
            senderName = user.name,
            messageText = text,
            timestamp = System.currentTimeMillis(),
            isEmojiOnly = isEmoji
        )
        chatMessages.value = chatMessages.value + newMsg
    }

    // --- Multiplayer Match Making and Real-time simulator ---
    fun searchOnlineMatch(mode: String = "PVP", onFound: (MatchRoom) -> Unit) {
        val user = _currentUser.value ?: return
        val roomId = "room_" + Random.nextInt(100, 999)
        val opponent = friends.value.filter { it.status == "ONLINE" }.randomOrNull()
        val opponentName = opponent?.name ?: "Đầu Bếp Cao Thủ ${Random.nextInt(50, 99)}"
        val opponentId = opponent?.uid ?: "opponent_${Random.nextInt(1000, 9999)}"

        val room = MatchRoom(
            roomId = roomId,
            hostId = user.uid,
            hostName = user.name,
            challengerId = opponentId,
            challengerName = opponentName,
            gameMode = mode,
            status = "PLAYING",
            weather = listOf("CLEAR", "AFTERNOON", "NIGHT", "RAINY").random(),
            currentTurn = 1,
            hostScore = 0,
            challengerScore = 0
        )
        activeRooms.value = activeRooms.value + (roomId to room)
        onFound(room)
    }

    fun leaveMatchRoom(roomId: String) {
        activeRooms.value = activeRooms.value - roomId
    }
}
