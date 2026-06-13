package com.example.presentation.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.firebase.MockFirebase
import com.example.data.repository.GameRepository
import com.example.data.repository.GameRepositoryImpl
import com.example.domain.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepository = GameRepositoryImpl(application)

    // --- Core Flows from Repository ---
    val currentUser = repository.currentUser.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val inventoryItems = repository.inventoryItems.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val skinsCatalog = repository.skinsCatalog.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val leaderboards = repository.leaderboards.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val holidayEvents = repository.holidayEvents.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val shopCatalog = repository.shopCatalog.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val friendsList = repository.friendsList.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val gameSettings = repository.gameSettings.stateIn(viewModelScope, SharingStarted.Eagerly, GameSetting())
    val activeAchievements = repository.activeAchievements.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val matchRooms = repository.matchRooms.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())
    val globalChatMessages = repository.globalChatMessages.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val gameStats = repository.gameStats.stateIn(viewModelScope, SharingStarted.Eagerly, GameStatistics())
    val offlineHistory = repository.offlineHistory.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- UI/Game States ---

    // Weather Simulation
    private val _currentWeather = MutableStateFlow("CLEAR") // CLEAR, AFTERNOON, NIGHT, RAINY
    val currentWeather = _currentWeather.asStateFlow()

    // Sound FX active state
    private val _soundState = MutableStateFlow(true)
    val soundState = _soundState.asStateFlow()

    // Active screen navigation holder
    private val _currentScreen = MutableStateFlow("splash")
    val currentScreen = _currentScreen.asStateFlow()

    // Matchmaking room
    private val _activeOnlineMatch = MutableStateFlow<MatchRoom?>(null)
    val activeOnlineMatch = _activeOnlineMatch.asStateFlow()

    // Single Player / Cooking Simulation Engine States
    private val _selectedNoodle = MutableStateFlow<NoodleType?>(null)
    val selectedNoodle = _selectedNoodle.asStateFlow()

    private val _waterLevelMl = MutableStateFlow(0f)
    val waterLevelMl = _waterLevelMl.asStateFlow()

    private val _temperature = MutableStateFlow(20f)
    val temperature = _temperature.asStateFlow()

    private val _stoveOn = MutableStateFlow(false)
    val stoveOn = _stoveOn.asStateFlow()

    private val _noodleDonenessVal = MutableStateFlow(0f) // 0 to 1.5f (1.0 is Perfect, over is Overcooked/Mushy)
    val noodleDonenessVal = _noodleDonenessVal.asStateFlow()

    private val _addedToppings = MutableStateFlow<List<ToppingItem>>(emptyList())
    val addedToppings = _addedToppings.asStateFlow()

    private val _hasSeasoning = MutableStateFlow(false)
    val hasSeasoning = _hasSeasoning.asStateFlow()

    private val _isNoodlesAdded = MutableStateFlow(false)
    val isNoodlesAdded = _isNoodlesAdded.asStateFlow()

    private val _stoveBurnTimeSec = MutableStateFlow(0)
    val stoveBurnTimeSec = _stoveBurnTimeSec.asStateFlow()

    private val _potBurned = MutableStateFlow(false) // Hardcore Mode: stove on too long without cooking or turning off
    val potBurned = _potBurned.asStateFlow()

    // Cooking Step Log list
    private val _cookingStepsStr = MutableStateFlow<List<String>>(emptyList())
    val cookingStepsStr = _cookingStepsStr.asStateFlow()

    // Active Mini-Game Session States
    private val _activeMiniGameType = MutableStateFlow<String?>(null) // EGG, VEGGIE, STOVE_DIAL, TIME_TARGET
    val activeMiniGameType = _activeMiniGameType.asStateFlow()

    private val _miniGameProgress = MutableStateFlow(0f)
    val miniGameProgress = _miniGameProgress.asStateFlow()

    private val _miniGameResultText = MutableStateFlow("")
    val miniGameResultText = _miniGameResultText.asStateFlow()

    // Customer Serving States
    private val _activeCustomer = MutableStateFlow<Customer?>(null)
    val activeCustomer = _activeCustomer.asStateFlow()

    // Dish Result Score
    private val _cookScore = MutableStateFlow(0)
    val cookScore = _cookScore.asStateFlow()

    private val _starsAwarded = MutableStateFlow(0)
    val starsAwarded = _starsAwarded.asStateFlow()

    private val _customerReview = MutableStateFlow("")
    val customerReview = _customerReview.asStateFlow()

    private val _rewardCoins = MutableStateFlow(0L)
    val rewardCoins = _rewardCoins.asStateFlow()

    private val _rewardExp = MutableStateFlow(0L)
    val rewardExp = _rewardExp.asStateFlow()

    // Active Modes Configuration
    val isHardcoreMode = MutableStateFlow(false)
    val isSurvivalMode = MutableStateFlow(false)

    // Survival Mode Metrics
    private val _survivalTimerSec = MutableStateFlow(60)
    val survivalTimerSec = _survivalTimerSec.asStateFlow()

    private val _survivalScore = MutableStateFlow(0)
    val survivalScore = _survivalScore.asStateFlow()

    private val _survivalServedCount = MutableStateFlow(0)
    val survivalServedCount = _survivalServedCount.asStateFlow()

    // Gacha Pull Animation State
    private val _isGachaAnimating = MutableStateFlow(false)
    val isGachaAnimating = _isGachaAnimating.asStateFlow()

    private val _pulledSkin = MutableStateFlow<SkinItem?>(null)
    val pulledSkin = _pulledSkin.asStateFlow()

    // ASMR Loop state
    private val _playingASMRType = MutableStateFlow<String?>(null) // WATER_BOIL, EGG_CRACK, VEGGIE_CUT, SLURP_EAT, sizzle
    val playingASMRType = _playingASMRType.asStateFlow()

    // --- Timers & Threads Hooks ---
    private var cookingJob: Job? = null
    private var weatherJob: Job? = null
    private var survivalJob: Job? = null

    // --- Predefined Ingredients ---
    val allNoodleTypes = listOf(
        NoodleType("n_hao_hao", "Mì Hảo Hảo Chua Cay", "Mì tôm quốc dân định hình bản sắc thế hệ Việt.", 400f, 15, 1, 0),
        NoodleType("n_omachi", "Mì Khoai Tây Omachi", "Nấu bằng khoai tây thanh mát, sần sật mượt mà.", 450f, 18, 2, 100),
        NoodleType("n_ly_cay", "Mì Ly Ăn Liền", "Nhỏ gọn tiện lợi, chỉ cần đổ nước sôi và nêm sốt.", 350f, 12, 3, 200),
        NoodleType("n_samyang", "Mì Cay Hàn Quốc", "Cấp độ 7 lửa xanh vốc mặt, bốc hơi cay đỉnh điểm.", 500f, 20, 5, 500),
        NoodleType("n_ramen", "Ramen Tonkotsu Nhật", "Sợi vàng óng ả tắm mình trong cốt hầm béo ngậy.", 550f, 22, 10, 1000),
        NoodleType("n_udon", "Udon Bát Ngọc", "To khỏe, ngập tràn sức sống thanh đạm biển.", 500f, 25, 15, 1500),
        NoodleType("n_pho", "Phở Bò Gia Truyền", "Tinh túy ẩm thực thủ đô đất Thăng Long xưa.", 450f, 16, 20, 2000),
        NoodleType("n_bun_bo", "Bún Bò Huế Cay Nồng", "Nốt thăng thăng trầm đậm vị sả ớt miền Cố Đô.", 480f, 18, 25, 2500),
        NoodleType("n_pasta", "Mì Ý Spaghetti Sốt Bò", "Gia điệu ẩm thực Âu Châu quyến rũ nồng say.", 600f, 30, 30, 4000)
    )

    val allToppings = listOf(
        ToppingItem("t_egg", "Trứng Gà Ta", 3, 50, 1),
        ToppingItem("t_sausage", "Xúc Xích Đức", 4, 80, 1),
        ToppingItem("t_beef", "Thịt Bò Mỹ", 5, 200, 3),
        ToppingItem("t_cheese", "Phô Mai Cheddar", 3, 150, 4),
        ToppingItem("t_kimchi", "Kim Chi Hàn Quốc", 2, 100, 2),
        ToppingItem("t_meatball", "Bò Viên Gân", 4, 120, 5),
        ToppingItem("t_shrimp", "Tôm Sú Đỏ", 5, 220, 8),
        ToppingItem("t_seafood", "Hải Sản Thủy Thần", 6, 300, 12),
        ToppingItem("t_veggies", "Rau Cải Cúc", 2, 40, 2),
        ToppingItem("t_seaweed", "Rong Biển Nhật Bản", 2, 90, 6)
    )

    val allCustomers = listOf(
        Customer("c_easy", "Khách Dễ Tính", "Dễ Tính", "Nấu sao cũng khen, yêu thích đồ ngọt.", "Mì chín chín ấm nóng tớ chấm điểm cao!"),
        Customer("c_hard", "Khách Khó Tính", "Khó Tính", "Yêu cầu tuyệt đối chính xác thời gian và nước.", "Chỉ có nước sôi vừa sủi sồi 100 độ tớ mới sướng!"),
        Customer("c_spicy", "Khách Ghiền Cay", "Thích Cay", "Mì phải có kim chi hoặc ớt bột cay.", "Ớt đỏ ngập bát mới hạp khẩu vị của tớ nhé!"),
        Customer("c_noveg", "Khách Ghét Rau", "Không Rau", "Trừ sạch mọi cọng rau cải xanh ra ngoài.", "Một hạt hành tăm tớ cũng trừ sạch sao đấy!"),
        Customer("c_meat", "Khách Đại Gia Tông Súc", "Thích Nhiều Thịt", "Phải thêm thịt bò, tôm thịt phủ đầy.", "Nhiều bò bắp tôm thịt ngập bát húp nước sướng lừ!")
    )

    // Weather Realtime Settings StateFlows
    private val _realtimeWeatherEnabled = MutableStateFlow(true)
    val realtimeWeatherEnabled = _realtimeWeatherEnabled.asStateFlow()

    private val _weatherCityIndex = MutableStateFlow(0)
    val weatherCityIndex = _weatherCityIndex.asStateFlow()

    init {
        // Start Global Weather simulation looping changes
        startWeatherCycle()
        
        // Load settings state on creation
        viewModelScope.launch {
            gameSettings.collect { setting ->
                _soundState.value = setting?.soundEnabled ?: true
                _realtimeWeatherEnabled.value = setting?.isRealtimeWeatherEnabled ?: true
                _weatherCityIndex.value = setting?.weatherCityIndex ?: 0
            }
        }
    }

    // --- Navigation ---
    fun navigateTo(screenId: String) {
        _currentScreen.value = screenId
        playClickSound()
    }

    // --- Sound Controller ---
    fun toggleSound() {
        val next = !_soundState.value
        _soundState.value = next
        viewModelScope.launch {
            repository.updateSettings(gameSettings.value?.copy(soundEnabled = next) ?: GameSetting(soundEnabled = next))
        }
        if (next) {
            playClickSound()
        }
    }

    fun playClickSound() {
        if (!_soundState.value) return
        com.example.core.sound.SoundSynth.playClick()
    }

    private fun triggerASMR(type: String) {
        if (!_soundState.value) return
        _playingASMRType.value = type
        // Play actual physical synthesized audio frequencies dynamically depending on action
        viewModelScope.launch {
            when (type.uppercase().trim()) {
                "WATER_BOIL" -> com.example.core.sound.SoundSynth.playBubbleSound()
                "SIZZLE" -> com.example.core.sound.SoundSynth.playSizzleSound()
                "NOODLES_IN" -> com.example.core.sound.SoundSynth.playNoodleSplash()
                "EGG_CRACK", "VEGGIE_CUT" -> com.example.core.sound.SoundSynth.playTickCut()
                "SLURP_EAT" -> com.example.core.sound.SoundSynth.playSlurp()
                "SUCCESS" -> com.example.core.sound.SoundSynth.playSuccessTheme()
            }
            delay(1500)
            if (_playingASMRType.value == type) {
                _playingASMRType.value = null
            }
        }
    }

    fun toggleRealtimeWeather() {
        val next = !_realtimeWeatherEnabled.value
        _realtimeWeatherEnabled.value = next
        viewModelScope.launch {
            val current = gameSettings.value ?: GameSetting()
            repository.updateSettings(current.copy(isRealtimeWeatherEnabled = next))
            refreshWeatherInfo()
        }
    }

    fun setWeatherCityIndex(index: Int) {
        _weatherCityIndex.value = index
        viewModelScope.launch {
            val current = gameSettings.value ?: GameSetting()
            repository.updateSettings(current.copy(weatherCityIndex = index))
            refreshWeatherInfo()
        }
    }

    // --- Weather Cycles Simulator & Real-time Live Weather Integration ---
    private var weatherCycleIdx = 0

    private fun startWeatherCycle() {
        weatherJob?.cancel()
        weatherJob = viewModelScope.launch {
            while (true) {
                refreshWeatherInfo()
                // Update every 60 seconds when real-time is enabled, or every 30 seconds for simulation cycle
                val interval = if (_realtimeWeatherEnabled.value) 60000L else 30000L
                delay(interval)
            }
        }
    }

    fun refreshWeatherInfo() {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            if (_realtimeWeatherEnabled.value) {
                // Pinpoint coordinates based on city index
                val (lat, lon) = when (_weatherCityIndex.value) {
                    0 -> Pair(10.82, 106.63) // Sài Gòn
                    1 -> Pair(21.03, 105.85) // Hà Nội
                    2 -> Pair(16.05, 108.20) // Đà Nẵng
                    else -> Pair(10.82, 106.63)
                }
                val realWeather = fetchLiveWeather(lat, lon)
                if (realWeather != null) {
                    _currentWeather.value = realWeather
                } else {
                    _currentWeather.value = determineWeatherBySystemTime()
                }
            } else {
                // Use manual/automatic game simulation cycles
                val states = listOf("SÁNG", "CHIỀU", "ĐÊM", "MƯA")
                _currentWeather.value = states[weatherCycleIdx]
                weatherCycleIdx = (weatherCycleIdx + 1) % states.size
            }
        }
    }

    private fun fetchLiveWeather(lat: Double, lon: Double): String? {
        try {
            val url = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current_weather=true"
            val client = okhttp3.OkHttpClient.Builder()
                .connectTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
                .build()
            val request = okhttp3.Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return null
                val body = response.body?.string() ?: return null
                
                val codeMatch = java.util.regex.Pattern.compile("\"weathercode\"\\s*:\\s*(\\d+)").matcher(body)
                val isDayMatch = java.util.regex.Pattern.compile("\"is_day\"\\s*:\\s*(\\d+)").matcher(body)
                
                var weatherCode = -1
                var isDay = 1
                
                if (codeMatch.find()) {
                    weatherCode = codeMatch.group(1)?.toIntOrNull() ?: -1
                }
                if (isDayMatch.find()) {
                    isDay = isDayMatch.group(1)?.toIntOrNull() ?: 1
                }
                
                val rainyCodes = setOf(51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82, 85, 86, 95, 96, 99)
                if (weatherCode in rainyCodes) {
                    return "MƯA"
                }
                
                if (isDay == 0) {
                    return "ĐÊM"
                }
                
                val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
                return when (hour) {
                    in 16..18 -> "CHIỀU"
                    in 19..23, in 0..4 -> "ĐÊM"
                    else -> "SÁNG"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun determineWeatherBySystemTime(): String {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..15 -> "SÁNG"
            in 16..18 -> "CHIỀU"
            else -> "ĐÊM"
        }
    }

    // --- Firebase Authentication simulation proxies ---
    fun doGuestLogin() {
        viewModelScope.launch {
            repository.signInAsGuest()
            navigateTo("home")
        }
    }

    fun doGoogleLogin(email: String) {
        viewModelScope.launch {
            repository.signInWithGoogle(email)
            navigateTo("home")
        }
    }

    fun doLogOut() {
        viewModelScope.launch {
            repository.signOut()
            navigateTo("login")
        }
    }

    // --- Friends ---
    fun makeFriend(name: String) = viewModelScope.launch { repository.inviteFriend(name) }
    fun deleteFriend(uid: String) = viewModelScope.launch { repository.deleteFriend(uid) }
    fun acceptFriend(uid: String) = viewModelScope.launch { repository.acceptFriend(uid) }

    // --- Shop, Skins, Equips ---
    fun buyCatalogItem(item: ShopCatalogItem, qty: Int) = viewModelScope.launch {
        repository.purchaseShopCatalogItem(item, qty)
    }

    fun handleEquipToggle(id: String, type: String, isEquipped: Boolean) = viewModelScope.launch {
        repository.equipItem(id, type, isEquipped)
        // If equipping a chef/restaurant skin, update selected in user profile
        if (type == "SKIN") {
            val isChef = skinsCatalog.value.find { it.id == id }?.type == "CHEF"
            if (isChef) repository.selectChefSkin(id) else repository.selectShopSkin(id)
        }
    }

    // --- Realtime Global Message Chat ---
    fun sendTextChat(text: String, isEmoji: Boolean = false) {
        viewModelScope.launch {
            repository.sendChatMessage(text, isEmoji)
        }
    }

    // --- Gacha System ---
    fun pullGacha(isGemDraw: Boolean) {
        if (_isGachaAnimating.value) return
        _isGachaAnimating.value = true
        _pulledSkin.value = null
        viewModelScope.launch {
            delay(1500) // dramatic spinner delay
            val skin = repository.drawGachaItem(isGemDraw)
            _pulledSkin.value = skin
            _isGachaAnimating.value = false
        }
    }

    // --- Online Matchmaking system ---
    fun findOnlineMatch(mode: String = "PVP") {
        viewModelScope.launch {
            repository.createAndFindMatch(mode) { room ->
                _activeOnlineMatch.value = room
                if (mode == "PVP") {
                    // Navigate to PvP
                    startCookingRoutine(allNoodleTypes.random())
                    navigateTo("cooking")
                } else if (mode == "COOP") {
                    navigateTo("co_op")
                } else {
                    navigateTo("party")
                }
            }
        }
    }

    fun quitOnlineMatchRoom() {
        val room = _activeOnlineMatch.value
        if (room != null) {
            viewModelScope.launch {
                repository.leaveMatch(room.roomId)
            }
        }
        _activeOnlineMatch.value = null
        navigateTo("online")
    }

    // --- Cooking Engine Simulator ---
        
    fun startCookingRoutine(noodle: NoodleType) {
        // Reset states
        _selectedNoodle.value = noodle
        _waterLevelMl.value = 0f
        _temperature.value = 20f
        _stoveOn.value = false
        _noodleDonenessVal.value = 0f
        _addedToppings.value = emptyList()
        _hasSeasoning.value = false
        _isNoodlesAdded.value = false
        _stoveBurnTimeSec.value = 0
        _potBurned.value = false
        _cookingStepsStr.value = listOf("Bước 1: Đổ nước vào nồi.")
        _cookScore.value = 0
        _starsAwarded.value = 0
        _customerReview.value = ""
        _activeCustomer.value = allCustomers.random()

        cookingJob?.cancel()
        cookingJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (_stoveOn.value) {
                    _stoveBurnTimeSec.value += 1
                    
                    // Temp Heating Simulator
                    val currentTemp = _temperature.value
                    if (currentTemp < 100f) {
                        // heat rises based on stove item
                        _temperature.value = (currentTemp + 10f).coerceAtMost(100f)
                    } else if (currentTemp >= 100f) {
                        // water slowly evaporates
                        if (_waterLevelMl.value > 0f) {
                            _waterLevelMl.value = (_waterLevelMl.value - 5f).coerceAtLeast(0f)
                        }
                    }

                    // Sound loop trigger for boiling
                    if (_temperature.value >= 80f && Random.nextFloat() < 0.3f) {
                        triggerASMR("WATER_BOIL")
                    }

                    // Noodle cooking progress simulation
                    if (_isNoodlesAdded.value && _waterLevelMl.value > 0f) {
                        val tempRate = when {
                            _temperature.value >= 100f -> 0.08f // perfect rate boiling
                            _temperature.value >= 80f -> 0.04f  // slow cooking rate
                            _temperature.value >= 50f -> 0.01f  // barely cooking
                            else -> 0f
                        }
                        _noodleDonenessVal.value += tempRate
                    }

                    // Hardcore Mode Overheat Pot-Burner check
                    if (isHardcoreMode.value && _stoveBurnTimeSec.value > 40) {
                        _potBurned.value = true
                        _stoveOn.value = false
                        logCookingStep("CẢNH BÁO: Bếp ga để quên quá lâu! NồI CHÁY ĐEN XÌ!")
                    }
                }
            }
        }
    }

    fun PourWater(amountMl: Float) {
        if (_waterLevelMl.value >= 1000f) return
        _waterLevelMl.value = (_waterLevelMl.value + amountMl).coerceAtMost(1000f)
        logCookingStep("Đã đổ thêm ${amountMl.toInt()}ml nước.")
    }

    fun toggleStove() {
        val turningOn = !_stoveOn.value
        _stoveOn.value = turningOn
        logCookingStep(if (turningOn) "Đã bật bếp ga." else "Đã tắt bếp ga.")
        triggerASMR("sizzle")
    }

    fun addNoodles() {
        if (_waterLevelMl.value <= 10f) {
            logCookingStep("LỖI: Chưa có nước gõ nổ nồi rồi!")
            return
        }
        _isNoodlesAdded.value = true
        logCookingStep("Đã cho vắt mì nấu chín.")
        triggerASMR("noodles_in")
    }

    fun addSeasoning() {
        _hasSeasoning.value = true
        logCookingStep("Đã trút gói nước sốt gia vị bí xanh.")
    }

    fun addTopping(topping: ToppingItem) {
        val list = _addedToppings.value
        if (list.size >= 5) return // max 5 toppings
        _addedToppings.value = list + topping
        logCookingStep("Bỏ thêm Topping: ${topping.name}")
    }

    private fun logCookingStep(step: String) {
        _cookingStepsStr.value = _cookingStepsStr.value + step
    }

    // --- Interactive Minigames simulator ---
    fun launchMiniGame(type: String) {
        _activeMiniGameType.value = type
        _miniGameProgress.value = 0f
        _miniGameResultText.value = ""
        
        // Trigger specific start audio
        if (type == "EGG") triggerASMR("EGG_CRACK")
        if (type == "VEGGIE") triggerASMR("VEGGIE_CUT")
    }

    fun playMiniGameTaps() {
        val currProgress = _miniGameProgress.value
        if (currProgress >= 1.0f) return

        val type = _activeMiniGameType.value ?: return
        val increment = if (type == "EGG") 0.15f else 0.1f // veggie slicing needs more taps
        val next = (currProgress + increment).coerceAtMost(1.0f)
        _miniGameProgress.value = next

        if (next >= 1.0f) {
            _miniGameResultText.value = "THÀNH CÔNG!"
            viewModelScope.launch {
                delay(800)
                _activeMiniGameType.value = null
                // Reward player on cooking topping additions
                if (type == "EGG") {
                    addTopping(allToppings.find { it.id == "t_egg" } ?: allToppings.first())
                } else if (type == "VEGGIE") {
                    addTopping(allToppings.find { it.id == "t_veggies" } ?: allToppings.first())
                }
            }
        }
    }

    // --- Scoring & Customer Reviews Serve ---
    fun finishAndServeDish() {
        cookingJob?.cancel()
        _stoveOn.value = false

        val noodle = _selectedNoodle.value ?: return
        val customer = _activeCustomer.value ?: return
        
        var scoreSum = 100

        // 1. Check Water Amount (Ideal targets defined inside NoodleType)
        val waterError = Math.abs(_waterLevelMl.value - noodle.targetWaterLevel)
        scoreSum -= when {
            waterError > 200f -> 30
            waterError > 100f -> 15
            waterError > 50f -> 5
            else -> 0
        }

        // 2. Check Doneness (Ideal range PERFECT: 0.75 - 1.0)
        val doneness = _noodleDonenessVal.value
        val donenessScorePenalty = when {
            doneness < 0.25f -> 60 // raw
            doneness < 0.50f -> 30 // undercooked
            doneness < 0.75f -> 10 // semi perfect
            doneness <= 1.0f -> 0  // perfect!
            doneness <= 1.25f -> 25 // overcooked mushy
            else -> 50 // dead mush
        }
        scoreSum -= donenessScorePenalty

        // 3. No Seasoning Penalty
        if (!_hasSeasoning.value) {
            scoreSum -= 20
        }

        // 4. Hardcore burned penalty
        if (_potBurned.value) {
            scoreSum = 0
        }

        val finalScore = scoreSum.coerceIn(0, 100)
        _cookScore.value = finalScore

        // Star calculations (1 to 5 stars)
        val stars = when {
            finalScore >= 95 -> 5
            finalScore >= 80 -> 4
            finalScore >= 60 -> 3
            finalScore >= 35 -> 2
            else -> 1
        }
        _starsAwarded.value = stars

        // Customer specific requirements review adjustment
        var reviewText = ""
        val isPerfect = finalScore >= 80

        if (_potBurned.value) {
            reviewText = "NỒI CHÁY ĐEN THUI RỒI! Tôi KHÔNG dám ăn!"
        } else {
            reviewText = when (customer.id) {
                "c_easy" -> if (isPerfect) MockFirebase.commentsGood.random() else MockFirebase.commentsNeutral.random()
                "c_hard" -> {
                    if (doneness in 0.75f..1.0f && waterError < 50f) {
                        MockFirebase.commentsGood.random()
                    } else {
                        "Nấu quá cẩu thả, không khớp tí tẹo nào tỉ lệ vàng của tôi!"
                    }
                }
                "c_spicy" -> {
                    val hasSpice = _addedToppings.value.any { it.id == "t_kimchi" } || noodle.id == "n_samyang"
                    if (hasSpice) MockFirebase.commentsGood.random() else "Nhạt nhẽo thiếu vị tỏi ớt kim chi cay bốc hông rồi!"
                }
                "c_noveg" -> {
                    val hasVeg = _addedToppings.value.any { it.id == "t_veggies" }
                    if (!hasVeg) MockFirebase.commentsGood.random() else "Eo ơi có hành và rau cải kìa! Cút ngay!"
                }
                "c_meat" -> {
                    val meatCount = _addedToppings.value.count { it.id == "t_beef" || it.id == "t_shrimp" || it.id == "t_meatball" }
                    if (meatCount >= 2) MockFirebase.commentsGood.random() else "Ít thịt bò tôm sú bắp gân quá, chán mồm thế!"
                }
                else -> MockFirebase.commentsGood.random()
            }
        }
        _customerReview.value = reviewText

        // Currency Coin and Exp Earning Math based on stars and weather
        var coinsEarned = (finalScore * 5L) + (_addedToppings.value.size * 20L)
        var expEarned = finalScore * 10L

        // Weather multiplier (mưa bão vắng khách hoặc súp nóng đắt hàng!)
        if (_currentWeather.value == "MƯA") {
            coinsEarned = (coinsEarned * 1.5f).toLong() // soup is valuable in cold rains
        } else if (_currentWeather.value == "NÊM") {
            coinsEarned = (coinsEarned * 1.2f).toLong()
        }

        _rewardCoins.value = coinsEarned
        _rewardExp.value = expEarned

        triggerASMR("SLURP_EAT")

        // Sync with repository
        viewModelScope.launch {
            repository.addCoinsAndGems(coinsEarned, 0)
            repository.addExp(expEarned)
            repository.incrementNoodlesCookedCount()

            // Save Offline History
            val matchRes = OfflineMatchHistory(
                opponentName = customer.name,
                mode = if (isSurvivalMode.value) "SURVIVAL" else "SINGLE",
                result = if (stars >= 3) "ĐẠT CHUẨN (${stars} Sao)" else "BỊ CHÊ (${stars} Sao)",
                score = finalScore
            )
            repository.saveMatchHistoryOffline(matchRes)

            // Online match room result sync if PvP was active
            val activeRoom = _activeOnlineMatch.value
            if (activeRoom != null) {
                // simple simulated resolution
                MockFirebase.incrementPvPWins()
            }
        }

        // Handle Survival Progression
        if (isSurvivalMode.value) {
            _survivalScore.value += finalScore
            _survivalServedCount.value += 1
            // Give extra survival time based on performance
            _survivalTimerSec.value += when {
                finalScore >= 90 -> 15
                finalScore >= 70 -> 8
                else -> 2
            }
        }

        navigateTo("result")
    }

    // --- Survival Mode Engine ---
    fun startSurvivalGame() {
        isSurvivalMode.value = true
        isHardcoreMode.value = false
        _survivalScore.value = 0
        _survivalServedCount.value = 0
        _survivalTimerSec.value = 60

        survivalJob?.cancel()
        survivalJob = viewModelScope.launch {
            startCookingRoutine(allNoodleTypes.random())
            navigateTo("cooking")

            while (_survivalTimerSec.value > 0) {
                delay(1000)
                _survivalTimerSec.value -= 1
            }

            // Game over survival mode
            isSurvivalMode.value = false
            navigateTo("home")
        }
    }

    fun startHardcoreGame() {
        isHardcoreMode.value = true
        isSurvivalMode.value = false
        startCookingRoutine(allNoodleTypes.random())
        navigateTo("cooking")
    }

    fun stopSurvivalJob() {
        survivalJob?.cancel()
        isSurvivalMode.value = false
    }

    fun resetCookingDirect() {
        _selectedNoodle.value = null
        stopSurvivalJob()
        navigateTo("home")
    }
}
