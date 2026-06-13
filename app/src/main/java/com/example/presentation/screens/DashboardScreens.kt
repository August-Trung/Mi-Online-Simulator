package com.example.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: GameViewModel, bottomNavSelection: String, onBottomNavChange: (String) -> Unit) {
    val user by viewModel.currentUser.collectAsState()
    val weather by viewModel.currentWeather.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SoupKitchen,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            "Mì Online Simulator",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            letterSpacing = (-0.5).sp
                        )
                    }
                },
                actions = {
                    // Weather Simulator Display
                    WeatherBannerWidget(weather = weather)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                )
            )
        },
        bottomBar = {
            MainBottomNavigation(currentSelection = bottomNavSelection, onSelectionChange = onBottomNavChange)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (bottomNavSelection) {
                "home" -> HomeDashboardContent(viewModel = viewModel)
                "shop" -> ShopScreen(viewModel = viewModel)
                "inventory" -> InventoryScreen(viewModel = viewModel)
                "social" -> SocialHubScreen(viewModel = viewModel)
                "settings" -> SettingsScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun WeatherBannerWidget(weather: String) {
    val (color, label, icon) = when (weather) {
        "SÁNG" -> Triple(Color(0xFFFFB300), "Trời Nắng (Sáng)", Icons.Default.WbSunny)
        "CHIỀU" -> Triple(Color(0xFFE65100), "Trời Chiều (Ấm)", Icons.Default.WbTwilight)
        "ĐÊM" -> Triple(Color(0xFF3F51B5), "Trời Tối (Lạnh)", Icons.Default.Bedtime)
        else -> Triple(Color(0xFF607D8B), "Mưa Rào (Đắt Súp)", Icons.Default.Cloud)
    }

    Card(
        modifier = Modifier
            .padding(end = 12.dp)
            .testTag("weather_widget"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun HomeDashboardContent(viewModel: GameViewModel) {
    val user by viewModel.currentUser.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // High-fidelity Centered Chef Profile card
        user?.let { u ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .testTag("profile_summary_card"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Large Centered Avatar with gold/primary double border
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                                )
                            )
                            .padding(3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = u.name.take(1).uppercase(),
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Black,
                                fontSize = 32.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Centered Chef Name & Title
                    Text(
                        text = u.name,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(50))
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "BẾP TRƯỞNG CẤP ${u.level}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Perfectly Centered Balanced Coins & Gems pills in a row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Coins Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
                                .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f), RoundedCornerShape(50))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "${u.coins}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFFCC33))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Gems Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f))
                                .border(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.25f), RoundedCornerShape(50))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "${u.gems}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                                Box(
                                    modifier = Modifier
                                        .size(9.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(Color(0xFF4AC4B0))
                                )
                            }
                        }
                    }

                    // Level XP Indicator (Centered text and clean progress)
                    Spacer(modifier = Modifier.height(18.dp))
                    val expNeeded = u.level * 1000f
                    val progress = (u.exp / expNeeded).coerceIn(0f, 1f)
                    Column(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Điểm Kinh Nghiệm",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${u.exp.toInt()}/${expNeeded.toInt()} EXP",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Stats summary footer perfectly centered
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WalletElt(
                            icon = Icons.Default.SoupKitchen,
                            value = "Đã nấu phục vụ: ${u.noodlesCooked} bát mì hảo hạng",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Gameplay Section Grid Header
        Text(
            "CÁC SÂN CHƠI NẤU MÌ",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 4.dp, bottom = 12.dp)
        )

        // CHƠI ĐƠN (Styled beautifully as the warm high-end gradient Hero card)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .clickable { viewModel.navigateTo("single_player") }
                .testTag("game_tile_chơi_đơn"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
                        )
                    )
                    .padding(20.dp)
            ) {
                // Background big bowl emoji
                Text(
                    "🍲",
                    fontSize = 72.sp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 10.dp, y = 14.dp)
                        .alpha(0.25f)
                )

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "CHẾ ĐỘ ĐƠN",
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "Tự tay nấu những bát mì hoàn hảo & phục vụ khách hàng.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                        modifier = Modifier.fillMaxWidth(0.65f)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { viewModel.navigateTo("single_player") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onPrimary,
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("NẤU NGAY", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // QUICK ACTIONS LAYER (Left: CHƠI ONLINE [Blue Highlight] | Right: QUÁN CỦA TÔI [Emerald Highlight])
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Chơi Online Quick Action
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.navigateTo("online") }
                    .testTag("game_tile_chơi_online"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFE3F2FD)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🌐", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "CHƠI ONLINE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1565C0)
                    )
                    // Visual bottom border accent simulating HTML design
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(3.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFF90CAF9))
                    )
                }
            }

            // Quán Của Tôi Quick Action
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.navigateTo("restaurant") }
                    .testTag("restaurant_tile"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🏠", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "QUÁN CỦA TÔI",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF2E7D32)
                    )
                    // Visual bottom border accent simulating HTML design
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(3.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFFA5D6A7))
                    )
                }
            }
        }

        // SUPPLEMENTARY MODES: Sinh Tồn & Hardcore (Styled neatly with Vibrant accents)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                GameActivityTile(
                    title = "Chế Độ Sinh Tồn",
                    description = "Nấu không nghỉ với thời gian chạy đua",
                    icon = Icons.Default.Timer,
                    color = Color(0xFFE91E63),
                    onClick = { viewModel.startSurvivalGame() }
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                GameActivityTile(
                    title = "Chế Độ Hardcore",
                    description = "Tránh cháy nồi, nứt gốm và tàn tro",
                    icon = Icons.Default.Whatshot,
                    color = Color(0xFFFF5722),
                    onClick = { viewModel.startHardcoreGame() }
                )
            }
        }

        // UPGRADE & COLLECTION SECTIONS
        Text(
            "QUẢN LÝ QUÁN & MỞ RỘNG",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 4.dp, bottom = 12.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.navigateTo("gacha") }
                .testTag("gacha_tile"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEFEBE9)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏆", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Bộ Sưu Tập Gacha & Nhiệm Vụ", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text("Vòng quay sưu tập Skins & Nồi Đất Hiếm", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        // DAILY QUEST PROGRESS WIDGET
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.08f))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Round exclamation indicator
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiary),
                    contentAlignment = Alignment.Center
                ) {
                    Text("!", color = MaterialTheme.colorScheme.onTertiary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "NHIỆM VỤ HÀNG NGÀY",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Progress bar
                    LinearProgressIndicator(
                        progress = { 0.75f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.tertiary,
                        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                    )
                }

                Text(
                    "3/4",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Composable
fun WalletElt(icon: ImageVector, value: String, tint: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun GameActivityTile(title: String, description: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("game_tile_${title.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = color)
            Spacer(modifier = Modifier.height(4.dp))
            Text(description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
        }
    }
}

@Composable
fun MainBottomNavigation(currentSelection: String, onSelectionChange: (String) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            selected = currentSelection == "home",
            onClick = { onSelectionChange("home") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Trang Chủ") },
            label = { Text("Trang Chủ", fontSize = 11.sp) },
            modifier = Modifier.testTag("nav_btn_home")
        )
        NavigationBarItem(
            selected = currentSelection == "shop",
            onClick = { onSelectionChange("shop") },
            icon = { Icon(Icons.Default.Store, contentDescription = "Cửa Hàng") },
            label = { Text("Cửa Hàng", fontSize = 11.sp) },
            modifier = Modifier.testTag("nav_btn_shop")
        )
        NavigationBarItem(
            selected = currentSelection == "inventory",
            onClick = { onSelectionChange("inventory") },
            icon = { Icon(Icons.Default.BusinessCenter, contentDescription = "Kho Đồ") },
            label = { Text("Kho Đồ", fontSize = 11.sp) },
            modifier = Modifier.testTag("nav_btn_inventory")
        )
        NavigationBarItem(
            selected = currentSelection == "social",
            onClick = { onSelectionChange("social") },
            icon = { Icon(Icons.Default.Groups, contentDescription = "Xã Hội") },
            label = { Text("Xã Hội", fontSize = 11.sp) },
            modifier = Modifier.testTag("nav_btn_social")
        )
        NavigationBarItem(
            selected = currentSelection == "settings",
            onClick = { onSelectionChange("settings") },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Cài Đặt") },
            label = { Text("Cài Đặt", fontSize = 11.sp) },
            modifier = Modifier.testTag("nav_btn_settings")
        )
    }
}
