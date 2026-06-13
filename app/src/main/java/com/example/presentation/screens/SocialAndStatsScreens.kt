package com.example.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.viewmodel.GameViewModel
import com.example.domain.LeaderboardEntry

@Composable
fun SocialHubScreen(viewModel: GameViewModel) {
    var activeSocialTab by remember { mutableStateOf("Bảng Xếp Hạng") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        // High-end capsule pill segmented layout for tabs
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("Bảng Xếp Hạng", "Bạn Bè", "Thành Tựu", "Sự Kiện").forEach { tab ->
                    val selected = activeSocialTab == tab
                    val tabBgModifier = if (selected) {
                        Modifier.background(Brush.horizontalGradient(listOf(Color(0xFFFFA726), Color(0xFFFF5722))))
                    } else {
                        Modifier
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .then(tabBgModifier)
                            .clickable { 
                                viewModel.playClickSound()
                                activeSocialTab = tab 
                            }
                            .padding(vertical = 11.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            fontWeight = if (selected) FontWeight.Black else FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        when (activeSocialTab) {
            "Bảng Xếp Hạng" -> LeaderboardScreen(viewModel)
            "Bạn Bè" -> FriendScreen(viewModel)
            "Thành Tựu" -> AchievementScreen(viewModel)
            "Sự Kiện" -> EventScreen(viewModel)
        }
    }
}

@Composable
fun LeaderboardScreen(viewModel: GameViewModel) {
    val ranking by viewModel.leaderboards.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    var sortType by remember { mutableStateOf("LEVEL") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Glowing Outline Filter Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("LEVEL", "NOODLES", "PVP_WINS", "COINS").forEach { mode ->
                val selected = sortType == mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (selected) Color(0xFFFF9800).copy(alpha = 0.12f)
                            else Color.Transparent
                        )
                        .border(
                            width = 1.dp,
                            color = if (selected) Color(0xFFFF9800) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { 
                            viewModel.playClickSound()
                            sortType = mode 
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (mode) {
                            "LEVEL" -> "Cấp Độ"
                            "NOODLES" -> "Số Tô"
                            "PVP_WINS" -> "Thắng PvP"
                            else -> "Coins"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (selected) Color(0xFFFF9800) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Dynamic local user injection & ranking merge logic
        val mergedRanking = remember(ranking, currentUser, sortType) {
            val u = currentUser
            if (u == null) {
                ranking.filter { it.rankType == sortType }
            } else {
                val typeFilteredList = ranking.filter { it.rankType == sortType }
                val containsUser = typeFilteredList.any { it.uid == u.uid }
                
                val baseList = if (containsUser) {
                    typeFilteredList.map { entry ->
                        if (entry.uid == u.uid) {
                            entry.copy(
                                name = u.name,
                                value = when (sortType) {
                                    "LEVEL" -> u.level.toLong()
                                    "NOODLES" -> u.noodlesCooked.toLong()
                                    "PVP_WINS" -> u.pvpWins.toLong()
                                    else -> u.coins
                                }
                            )
                        } else entry
                    }
                } else {
                    val userValue = when (sortType) {
                        "LEVEL" -> u.level.toLong()
                        "NOODLES" -> u.noodlesCooked.toLong()
                        "PVP_WINS" -> u.pvpWins.toLong()
                        else -> u.coins
                    }
                    typeFilteredList + LeaderboardEntry(
                        id = "current_user_$sortType",
                        uid = u.uid,
                        name = u.name,
                        rankType = sortType,
                        value = userValue,
                        rank = 0
                    )
                }
                
                baseList.sortedByDescending { it.value }
                    .mapIndexed { index, entry ->
                        entry.copy(rank = index + 1)
                    }
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(mergedRanking) { rank ->
                val isMe = currentUser != null && rank.uid == currentUser?.uid
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isMe) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isMe) 1.5.dp else 1.dp,
                        color = if (isMe) Color(0xFFFF9800) else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isMe) 4.dp else 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Rank Number with Medal Embellishment
                        Box(
                            modifier = Modifier.width(42.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            when (rank.rank) {
                                1 -> Text("🥇", fontSize = 20.sp)
                                2 -> Text("🥈", fontSize = 20.sp)
                                3 -> Text("🥉", fontSize = 20.sp)
                                else -> Text(
                                    text = "#${rank.rank}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Avatar layout
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isMe) Brush.verticalGradient(listOf(Color(0xFFFFA726), Color(0xFFFF5722)))
                                    else Brush.verticalGradient(listOf(Color(0xFFECEFF1), Color(0xFFCFD8DC)))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isMe) Icons.Default.Star else Icons.Default.Person,
                                contentDescription = null,
                                tint = if (isMe) Color.White else Color(0xFF546E7A),
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Player Profile Detail
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = rank.name,
                                fontWeight = if (isMe) FontWeight.ExtraBold else FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isMe) Color(0xFFFF6F00) else MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                            if (isMe) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFF5722)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        "BẠN",
                                        color = Color.White,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        // Rank Metric Value
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when (sortType) {
                                    "LEVEL" -> Icons.Default.TrendingUp
                                    "NOODLES" -> Icons.Default.SoupKitchen
                                    "PVP_WINS" -> Icons.Default.Stars
                                    else -> Icons.Default.MonetizationOn
                                },
                                contentDescription = null,
                                tint = Color(0xFFFF5722),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when (sortType) {
                                    "COINS" -> "${rank.value}🪙"
                                    "LEVEL" -> "Lv ${rank.value}"
                                    else -> "${rank.value}"
                                },
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = Color(0xFFFF5722)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FriendScreen(viewModel: GameViewModel) {
    val friends by viewModel.friendsList.collectAsState()
    var friendNameInput by remember { mutableStateOf("") }

    // Pulsing breathing animation for online friends
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_glow")
    val animAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = friendNameInput,
                onValueChange = { friendNameInput = it },
                placeholder = { Text("Tên đầu bếp bạn bè...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("friend_input"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF5722),
                    focusedLabelColor = Color(0xFFFF5722)
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (friendNameInput.isNotEmpty()) {
                        viewModel.makeFriend(friendNameInput)
                        friendNameInput = ""
                    }
                },
                modifier = Modifier
                    .height(54.dp)
                    .testTag("add_friend_btn"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722))
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Kết Bạn", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (friends.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Danh sách trống! Kết nối thêm bạn hữu bếp trưởng nhé.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(friends) { friend ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(Color(0xFFECEFF1), Color(0xFFCFD8DC))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = friend.name.take(1).uppercase(),
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFFF5722),
                                    fontSize = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(friend.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Cấp ${friend.level}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    
                                    // Animated breathing glow dot
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (friend.status == "ONLINE") Color(0xFF4CAF50).copy(alpha = animAlpha)
                                                else Color.Gray.copy(alpha = 0.6f)
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = friend.status,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (friend.status == "ONLINE") Color(0xFF4CAF50) else Color.Gray
                                    )
                                }
                            }

                            // Friendship confirmation actions
                            if (friend.isRequestPending) {
                                Button(
                                    onClick = { viewModel.acceptFriend(friend.uid) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Đồng ý", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                IconButton(onClick = { viewModel.deleteFriend(friend.uid) }) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline, 
                                        contentDescription = "Hủy kết bạn", 
                                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementScreen(viewModel: GameViewModel) {
    val achs by viewModel.activeAchievements.collectAsState()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(achs) { ach ->
            val progressNorm = (ach.progress.toFloat() / ach.maxProgress.toFloat()).coerceIn(0f, 1f)

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (ach.isCompleted) Color(0xFFF1F8E9) else MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = if (ach.isCompleted) Color(0xFFC5E1A5) else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(ach.title, fontWeight = FontWeight.Black, fontSize = 14.sp)
                            Text(ach.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        if (ach.isCompleted) {
                            Badge(containerColor = Color(0xFF4CAF50)) {
                                Text(
                                    "ĐÃ ĐẠT 🎉", 
                                    color = Color.White, 
                                    fontWeight = FontWeight.Black, 
                                    fontSize = 9.sp, 
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        } else {
                            Text(
                                "${ach.progress}/${ach.maxProgress}", 
                                fontSize = 11.sp, 
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFF5722)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Progress track of completion
                    LinearProgressIndicator(
                        progress = { progressNorm },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (ach.isCompleted) Color(0xFF4CAF50) else Color(0xFFFF5722),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun EventScreen(viewModel: GameViewModel) {
    val evts by viewModel.holidayEvents.collectAsState()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(evts) { event ->
            val bannerColor = when (event.theme) {
                "TET" -> Color(0xFFF44336) // Tet Crimson Red
                "NOEL" -> Color(0xFF2E7D32) // Forest Pinewood Green
                "HALLOWEEN" -> Color(0xFFFF9100) // Pumpkin Orange
                else -> Color(0xFF2196F3) // Cobalt Blue
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = bannerColor.copy(alpha = 0.08f)),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, bannerColor.copy(alpha = 0.25f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(bannerColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (event.theme) {
                                "TET" -> Icons.Default.Celebration
                                "NOEL" -> Icons.Default.AcUnit
                                else -> Icons.Default.Event
                            }, 
                            contentDescription = null, 
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(event.title, fontWeight = FontWeight.Black, fontSize = 15.sp, color = bannerColor)
                        Text(event.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (event.isActive) Color(0xFF4CAF50) else Color.Gray)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (event.isActive) "SỰ KIỆN ĐANG DIỄN RA" else "SẮP DIỄN RA",
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp,
                                color = if (event.isActive) Color(0xFF4CAF50) else Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

// Settings and Profiles
@Composable
fun SettingsScreen(viewModel: GameViewModel) {
    val sound by viewModel.soundState.collectAsState()
    val realtimeWeatherEnabled by viewModel.realtimeWeatherEnabled.collectAsState()
    val weatherCityIndex by viewModel.weatherCityIndex.collectAsState()
    val statistics by viewModel.gameStats.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Cài Đặt Hệ Thống", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Text("Điều chỉnh âm lượng vật lý nồi, âm ASMR và xem thành tựu thống kê.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(20.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Tùy Chọn Âm Thanh", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFFFF5722))
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Bật Âm thanh & Nhạc ASMR", fontSize = 13.sp)
                    Switch(
                        checked = sound,
                        onCheckedChange = { viewModel.toggleSound() },
                        modifier = Modifier.testTag("sound_fx_switch")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Tùy Chọn Thời Tiết", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Thời gian & Thời tiết Thực tế", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Lấy thời gian và tình hình thời tiết thực tại vị trí chỉ định",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = realtimeWeatherEnabled,
                        onCheckedChange = { viewModel.toggleRealtimeWeather() },
                        modifier = Modifier.testTag("realtime_weather_switch")
                    )
                }

                if (realtimeWeatherEnabled) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Khu Vực Cập Nhật Thời Tiết", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val cities = listOf("Sài Gòn", "Hà Nội", "Đà Nẵng")
                        cities.forEachIndexed { index, name ->
                            val selected = weatherCityIndex == index
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.setWeatherCityIndex(index) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = name,
                                    fontSize = 12.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Đang áp dụng chế độ xoay vòng mô phỏng (Sáng -> Chiều -> Đêm -> Mưa đổi sau mỗi 30s). Bật để dùng ngay thời tiết thực tế tự động!",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stats Display Panel (Statistics screen integration)
        Text("THỐNG KÊ QUÁN MÌ", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 12.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                statistics?.let { s ->
                    StatTextRow(label = "Tổng Số Tô Mì Đã Nấu", value = "${s.totalNoodlesCooked} bát")
                    StatTextRow(label = "Tỉ lệ Chiến Thắng PvP", value = "75%")
                    StatTextRow(label = "Tỉ lệ Hòa Mạng", value = "100%")
                    StatTextRow(label = "Xếp Hạng Đầu Bếp", value = "Đầu Bếp Cao Cấp")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Log out button
        Button(
            onClick = { viewModel.doLogOut() },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("logout_button")
        ) {
            Icon(Icons.Default.Logout, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Đăng Xuất Tài Khoản", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StatTextRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        Text(value, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GachaScreen(viewModel: GameViewModel) {
    val isAnimating by viewModel.isGachaAnimating.collectAsState()
    val pulledSkin by viewModel.pulledSkin.collectAsState()
    val user by viewModel.currentUser.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quay Gacha Đồ Độc", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo("home") }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Trở về")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF3E5151),
                            Color(0xFFDECBA4)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.85f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "XUẤT THẾ GIA VỊ BÍ TRUYỀN",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFFB300),
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "Cơ hội nhận Skins Đầu Bếp Tuyệt Thế (Legendary) & Sợi Mì Thủy Thần",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (isAnimating) {
                        CircularProgressIndicator(color = Color(0xFFFF5722), strokeWidth = 6.dp, modifier = Modifier.size(72.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Đang thổi sáo rước nắp gacha...", color = Color.White, fontWeight = FontWeight.Bold)
                    } else if (pulledSkin != null) {
                        val skin = pulledSkin!!
                        val rarityColor = when (skin.rarity) {
                            "LEGENDARY" -> Color(0xFFFFB300)
                            "EPIC" -> Color(0xFF9C27B0)
                            "RARE" -> Color(0xFF2196F3)
                            else -> Color(0xFF4CAF50)
                        }

                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(rarityColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = rarityColor, modifier = Modifier.size(48.dp))
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "CHÚC MỪNG HOÀN THÀNH!",
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF4CAF50),
                            fontSize = 13.sp
                        )
                        Text(
                            text = skin.name,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "[PHẨN PHÁT: ${skin.rarity}]",
                            fontWeight = FontWeight.Bold,
                            color = rarityColor,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { viewModel.pullGacha(false) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722))
                        ) {
                            Text("Nhận Thêm")
                        }
                    } else {
                        // Standing drawing options
                        Icon(
                            imageVector = Icons.Default.BrightnessLow,
                            contentDescription = null,
                            tint = Color(0xFFFF5722),
                            modifier = Modifier.size(80.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (!isAnimating) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            GachaDraftButton(tint = Color(0xFFFFB300), price = "1000 C", label = "Coin Draw", onClick = { viewModel.pullGacha(false) })
                            GachaDraftButton(tint = Color(0xFF03A9F4), price = "10 G", label = "Gem Draw", onClick = { viewModel.pullGacha(true) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GachaDraftButton(tint: Color, price: String, label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = tint),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(price, color = Color.Black.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}
