package com.example.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.NoodleType
import com.example.domain.ToppingItem
import com.example.presentation.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SinglePlayerScreen(viewModel: GameViewModel) {
    val user by viewModel.currentUser.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Công Thức Gia Truyền", 
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        letterSpacing = (-0.5).sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo("home") }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Trở về", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "DANH MỤC TRẠM HƯƠNG VỊ",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        "Hãy chọn một loại mì bên dưới để bắt đầu quy trình chế biến chuẩn mực",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(0.85f)
                    )
                }
            }

            items(viewModel.allNoodleTypes) { noodle ->
                val levelRequired = noodle.minLevel
                val isUnlocked = (user?.level ?: 1) >= levelRequired

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = isUnlocked) {
                            viewModel.startCookingRoutine(noodle)
                            viewModel.navigateTo("cooking")
                        }
                        .border(
                            width = if (isUnlocked) 1.5.dp else 1.dp,
                            color = if (isUnlocked) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .testTag("recipe_card_${noodle.id}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isUnlocked) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isUnlocked) 2.dp else 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Graph Indicator/Lock Icon frame
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isUnlocked) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isUnlocked) {
                                com.example.presentation.components.ItemGraphic(itemId = noodle.id, sizeDp = 42.dp)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Bị khóa",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = noodle.name, 
                                fontWeight = FontWeight.Bold, 
                                fontSize = 16.sp,
                                color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            Text(
                                text = noodle.description, 
                                fontSize = 12.sp, 
                                color = if (isUnlocked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.alpha(if (isUnlocked) 1f else 0.5f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WaterDrop, 
                                        contentDescription = null, 
                                        modifier = Modifier.size(13.dp), 
                                        tint = Color(0xFF29B6F6)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${noodle.targetWaterLevel.toInt()}ml", 
                                        fontSize = 11.sp, 
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant, 
                                        modifier = Modifier.padding(end = 12.dp)
                                    )
                                }
                                
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.alpha(if (isUnlocked) 1f else 0.5f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Timer, 
                                        contentDescription = null, 
                                        modifier = Modifier.size(13.dp), 
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${noodle.targetCookingTimeSec}s", 
                                        fontSize = 11.sp, 
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        if (!isUnlocked) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                                    .border(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "YÊU CẦU CẤP $levelRequired", 
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 9.sp
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Bắt đầu nấu",
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CookingScreen(viewModel: GameViewModel) {
    val noodle by viewModel.selectedNoodle.collectAsState()
    val waterLevel by viewModel.waterLevelMl.collectAsState()
    val temp by viewModel.temperature.collectAsState()
    val stoveOn by viewModel.stoveOn.collectAsState()
    val doneness by viewModel.noodleDonenessVal.collectAsState()
    val isNoodlesAdded by viewModel.isNoodlesAdded.collectAsState()
    val addedToppings by viewModel.addedToppings.collectAsState()
    val hasSeasoning by viewModel.hasSeasoning.collectAsState()
    val steps by viewModel.cookingStepsStr.collectAsState()
    val miniGameType by viewModel.activeMiniGameType.collectAsState()
    val burnTime by viewModel.stoveBurnTimeSec.collectAsState()
    val potBurned by viewModel.potBurned.collectAsState()
    val client by viewModel.activeCustomer.collectAsState()

    // Mode banners
    val isHardcore by viewModel.isHardcoreMode.collectAsState()
    val isSurvival by viewModel.isSurvivalMode.collectAsState()
    val survTime by viewModel.survivalTimerSec.collectAsState()

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E24)) // Cinematic stove darkness
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            // Screen Header: Mode banners & Customer Order info
            Row(
                modifier = Modifier
                    .fillHorizontalFlow()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.resetCookingDirect() }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Thoát nấu mì", tint = Color.White)
                }

                if (isSurvival) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE91E63)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "SINH TỒN: ${survTime}s",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                } else if (isHardcore) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF44336)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "HARDCORE: ĐỀ PHÒNG CHÁY NỒI",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                } else {
                    Text("CHƠI ĐƠN TẬP SỰ", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Customer Order Card
            client?.let { c ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2E38)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFF5722).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Face, contentDescription = null, tint = Color(0xFFFF5722), modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(c.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Khẩu vị: ${c.typeName}", color = Color(0xFFFFB300), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "\"${c.preferenceText}\"",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Physics Panel: Water Level and Temp Gauge
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF272732))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("VẬT LÝ NỒI NƯỚC", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // 1. Water Scale
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Mực Nước: ${waterLevel.toInt()} ml", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        noodle?.let {
                            Text("Mục tiêu: ${it.targetWaterLevel.toInt()}ml", color = Color(0xFF2196F3), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    LinearProgressIndicator(
                        progress = { (waterLevel / 800f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF2196F3),
                        trackColor = Color(0xFF1E1E24)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. Temperature scale
                    val waterStateText = when {
                        temp < 50f -> "Nước Lạnh bình thường"
                        temp < 80f -> "Bốc hơi nhẹ (50°C)"
                        temp < 100f -> "Nước Sôi nhẹ (80°C)"
                        else -> "Nước Sôi sùng sục (100°C)"
                    }
                    val tempColor = when {
                        temp < 50f -> Color(0xFF00B0FF)
                        temp < 80f -> Color(0xFFFF9100)
                        else -> Color(0xFFF44336)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Nhiệt Độ: ${temp.toInt()}°C", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        Text(waterStateText, color = tempColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    LinearProgressIndicator(
                        progress = { (temp / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = tempColor,
                        trackColor = Color(0xFF1E1E24)
                    )
                }
            }

            // Noodle physics status
            if (isNoodlesAdded) {
                val donenessState = when {
                    doneness < 0.25f -> Pair("Sợi Mì Sống hoàn toàn", Color(0xFF90A4AE))
                    doneness < 0.50f -> Pair("Hơi Chín rồi đấy", Color(0xFFFFB300))
                    doneness < 0.75f -> Pair("Chín Vừa nhai dai", Color(0xFF4CAF50))
                    doneness <= 1.0f -> Pair("Chín Hoàn Hảo rồi bưng thôi!", Color(0xFF2E7D32))
                    else -> Pair("Mì Nhũn nát bét nuốt ngấy!", Color(0xFFD84315))
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF222831))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("ĐỘ CHÍN SỢI MÌ", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                            Text(donenessState.first, color = donenessState.second, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { (doneness / 1.5f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            color = donenessState.second,
                            trackColor = Color(0xFF2D3748)
                        )
                    }
                }
            }

            // Stove & Ingredients deck controllers
            Text("BẢN ĐIỀU KHIỂN BẾP", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF24242A))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Row 1: Primary Stove Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. Pour Water Button
                        Button(
                            onClick = { viewModel.PourWater(100f) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1E88E5),
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("pour_water_button"),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            Icon(Icons.Default.WaterDrop, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Đổ nước", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // 2. Stove Power Button
                        Button(
                            onClick = { viewModel.toggleStove() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (stoveOn) Color(0xFFD32F2F) else Color(0xFF388E3C),
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1.2f)
                                .height(46.dp)
                                .testTag("toggle_stove_button"),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (stoveOn) Icons.Default.PowerOff else Icons.Default.Power, 
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (stoveOn) "Tắt Bếp" else "Bật Bếp", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // 3. Throw noodle in
                        Button(
                            onClick = { viewModel.addNoodles() },
                            enabled = !isNoodlesAdded,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFB300),
                                disabledContainerColor = Color.White.copy(alpha = 0.12f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("add_noodles_button"),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.SoupKitchen, 
                                contentDescription = null, 
                                tint = if (isNoodlesAdded) Color.White.copy(alpha = 0.3f) else Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Thả Mì", 
                                color = if (isNoodlesAdded) Color.White.copy(alpha = 0.3f) else Color.Black, 
                                fontSize = 11.sp, 
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Row 2: Secondary Cooking & Serving Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 4. Add Seasoning Packet
                        Button(
                            onClick = { viewModel.addSeasoning() },
                            enabled = !hasSeasoning,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF8E24AA),
                                disabledContainerColor = Color.White.copy(alpha = 0.12f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("seasoning_button"),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Icon(Icons.Default.AcUnit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Gói Gia Vị", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // 5. Serving Dish Client button
                        Button(
                            onClick = { viewModel.finishAndServeDish() },
                            enabled = isNoodlesAdded,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE54E20),
                                disabledContainerColor = Color.White.copy(alpha = 0.12f)
                            ),
                            modifier = Modifier
                                .weight(1.4f)
                                .height(48.dp)
                                .testTag("serve_button"),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Phục Vụ Món", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Interactive Topping Board (egg yolk, mini veg chopper minigame trigger!)
            Text("SỬA SOẠN TOPPING (MINIGAMES CẮT THÁI)", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF272733))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Gõ nhấp để tham gia mini-game chuẩn bị thức ăn kèm đắt giá:", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ActionToppingChip(label = "Đập Trứng", icon = Icons.Default.Album, onClick = { viewModel.launchMiniGame("EGG") })
                        ActionToppingChip(label = "Thái Cải", icon = Icons.Default.ContentCut, onClick = { viewModel.launchMiniGame("VEGGIE") })
                    }

                    if (addedToppings.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Toppings trong bát: " + addedToppings.joinToString { it.name },
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Steps logs feed
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF191920))
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    reverseLayout = true
                ) {
                    items(steps) { step ->
                        Text("• $step", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp, modifier = Modifier.padding(vertical = 1.dp))
                    }
                }
            }
        }

        // Overlay Interactive Mini-Game dialog if active
        if (miniGameType != null) {
            val progress by viewModel.miniGameProgress.collectAsState()
            val resultText by viewModel.miniGameResultText.collectAsState()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .clickable { /* absorb clicks */ },
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(0.85f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF33333D)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (miniGameType == "EGG") "MINI-GAME: ĐẬP VỎ TRỨNG!" else "MINI-GAME: THẮT CỔ TAY THÁI RAU!",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Gõ nhấp nút vàng LIÊN TỤC thật nhanh để đổ đầy!",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Progress representation
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp)
                                .clip(CircleShape),
                            color = Color(0xFFFFB300),
                            trackColor = Color.Black
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        if (resultText.isNotEmpty()) {
                            Text(resultText, color = Color(0xFF4CAF50), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Button(
                                onClick = { viewModel.playMiniGameTaps() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                                shape = CircleShape,
                                modifier = Modifier
                                    .size(72.dp)
                                    .testTag("minigame_tap_button")
                            ) {
                                Text("GÕ", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActionToppingChip(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .clickable { onClick() }
            .testTag("topping_chip_${label.lowercase()}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFF5722).copy(alpha = 0.12f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color(0xFFFF5722), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, fontSize = 12.sp, color = Color(0xFFFF5722), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ResultScreen(viewModel: GameViewModel) {
    val score by viewModel.cookScore.collectAsState()
    val stars by viewModel.starsAwarded.collectAsState()
    val comment by viewModel.customerReview.collectAsState()
    val plusCoins by viewModel.rewardCoins.collectAsState()
    val plusExp by viewModel.rewardExp.collectAsState()
    val client by viewModel.activeCustomer.collectAsState()

    // Animating numbers
    var animStart by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = true) {
        animStart = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF232526),
                        Color(0xFF414345)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp)
                .testTag("result_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "HOÀN THÀNH MÓN ĂN!",
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    color = Color(0xFFFF5722),
                    modifier = Modifier.testTag("result_banner_title")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Score presentation
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF5722).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("ĐIỂM SỐ", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$score", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF5722))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stars display (1 to 5)
                Row(
                    modifier = Modifier.padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(5) { index ->
                        val active = index < stars
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (active) Color(0xFFFFB300) else Color.Gray.copy(alpha = 0.4f),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Divider(color = MaterialTheme.colorScheme.outlineVariant)

                Spacer(modifier = Modifier.height(16.dp))

                // Customer quote
                client?.let { c ->
                    Text(
                        "${c.name} đánh giá:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "\"$comment\"",
                        fontSize = 14.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Rewards listing
                Text("PHẦN THƯỞNG NHẬN ĐƯỢC:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    RewardIconLabel(icon = Icons.Default.Paid, text = "+$plusCoins Coins", tint = Color(0xFFFFB300))
                    RewardIconLabel(icon = Icons.Default.Bolt, text = "+$plusExp EXP", tint = Color(0xFF4CAF50))
                }

                // Finish and go home button
                Button(
                    onClick = {
                        viewModel.resetCookingDirect()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("result_home_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722))
                ) {
                    Text("Quay lại sảnh chính", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun RewardIconLabel(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, tint: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = tint)
        Spacer(modifier = Modifier.width(6.dp))
        Text(text, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

// Extension to simulate Flow Row safely
@Composable
fun Modifier.fillHorizontalFlow(): Modifier = this.then(Modifier.fillMaxWidth())
