package com.example.presentation.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.ChatMessage
import com.example.domain.MatchRoom
import com.example.presentation.viewmodel.GameViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnlineScreen(viewModel: GameViewModel) {
    val activeMatch by viewModel.activeOnlineMatch.collectAsState()
    var searchingState by remember { mutableStateOf(false) }
    var matchesMode by remember { mutableStateOf("PVP") }

    LaunchedEffect(searchingState) {
        if (searchingState) {
            delay(2000) // matchmaking lookup delay
            searchingState = false
            viewModel.findOnlineMatch(matchesMode)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đấu Trường Trực Tuyến", fontWeight = FontWeight.Bold) },
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
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top
            ) {
                // Intro banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Trận Đấu Nấu Súp Toàn Cầu!", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        Text("So tài cùng các đầu bếp khác đoạt danh hiệu Vua Mì Cay và nâng cấp level vượt bậc.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("CHỌN CHẾ ĐỘ THỬ THÁCH", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(12.dp))

                // Mode Grid Selection
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1f)) {
                        OnlineModeCard(
                            title = "PvP 1vs1",
                            desc = "Thi tài so điểm tốc độ & độ chín vàng",
                            icon = Icons.Default.FlashOn,
                            color = Color(0xFFFF5722),
                            onClick = {
                                matchesMode = "PVP"
                                searchingState = true
                            }
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        OnlineModeCard(
                            title = "Co-Op Tổ Đội",
                            desc = "2-4 người chung tay chia nhau thái rau luộc mì",
                            icon = Icons.Default.Handshake,
                            color = Color(0xFF4CAF50),
                            onClick = {
                                matchesMode = "COOP"
                                searchingState = true
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1f)) {
                        OnlineModeCard(
                            title = "Tiệc Quán Mì",
                            desc = "4-10 người ăn nhậu giao lưu chat cực đã",
                            icon = Icons.Default.Celebration,
                            color = Color(0xFF9C27B0),
                            onClick = {
                                matchesMode = "PARTY"
                                searchingState = true
                            }
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        OnlineModeCard(
                            title = "Kênh Chat Trực Tiếp",
                            desc = "Mở khóa phòng chát giao lưu đầu bếp toàn thế giới",
                            icon = Icons.Default.ChatBubble,
                            color = Color(0xFF2196F3),
                            onClick = { viewModel.navigateTo("chat_screen") }
                        )
                    }
                }
            }

            // Mock Matchmaking searching popup
            if (searchingState) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.75f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(0.8f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = Color(0xFFFF5722))
                            Spacer(modifier = Modifier.height(20.dp))
                            Text("ĐANG GHÉP TRẬN ONLINE...", fontWeight = FontWeight.Bold)
                            Text("Đang dò tìm luồng đấu đầu bếp phù hợp...", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OnlineModeCard(title: String, desc: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("online_tile_${title.lowercase().replace(" ", "_")}")
            .border(
                width = 1.5.dp,
                color = color.copy(alpha = 0.15f),
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = title, 
                fontWeight = FontWeight.Black, 
                fontSize = 15.sp, 
                color = color,
                letterSpacing = (-0.3).sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = desc, 
                fontSize = 11.sp, 
                color = MaterialTheme.colorScheme.onSurfaceVariant, 
                maxLines = 2,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
fun CoOpScreen(viewModel: GameViewModel) {
    var activeRole by remember { mutableStateOf("Đắp Topping") }
    val room by viewModel.activeOnlineMatch.collectAsState()

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF1E2022))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.Groups, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("PHÂN CHIA CO-OP NHIỆM VỤ", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 20.sp)
                Text("Trận đấu Co-op 2-4 đầu bếp hỗ trợ nấu tô mì đỉnh cao", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2B2E31)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Phân chia vai trò:", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        RoleStatusRow(role = "Đun Nước (Đầu Bếp 1)", player = "Hải Yến", isDone = true)
                        RoleStatusRow(role = "Chuẩn Bị Topping (Đầu Bếp 2 - BẠN)", player = room?.hostName ?: "BẠN", isDone = false)
                        RoleStatusRow(role = "Nêm Gia Vị (Đầu Bếp 3)", player = room?.challengerName ?: "Jerry", isDone = false)
                        RoleStatusRow(role = "Phục Vụ Bàn (Đầu Bếp 4)", player = "Tommy", isDone = false)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Go to single cooking acting for preparation
                Button(
                    onClick = {
                        viewModel.startCookingRoutine(viewModel.allNoodleTypes.random())
                        viewModel.navigateTo("cooking")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier.testTag("coop_play_button")
                ) {
                    Text("Bắt đầu chuẩn bị Topping thôi!")
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(onClick = { viewModel.quitOnlineMatchRoom() }) {
                    Text("Rời phòng đợi", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun RoleStatusRow(role: String, player: String, isDone: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(role, fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 13.sp)
            Text(player, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
        }
        
        Badge(containerColor = if (isDone) Color(0xFF4CAF50) else Color(0xFFFFB300)) {
            Text(if (isDone) "HOÀN THÀNH" else "ĐANG LÀM", color = Color.White, modifier = Modifier.padding(2.dp))
        }
    }
}

@Composable
fun PartyScreen(viewModel: GameViewModel) {
    val room by viewModel.activeOnlineMatch.collectAsState()
    var emojiTrigger by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(emojiTrigger) {
        if (emojiTrigger != null) {
            delay(1500)
            emojiTrigger = null
        }
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF1F1F1F))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("ĐẠI TIỆC QUÁN MÌ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    IconButton(onClick = { viewModel.quitOnlineMatchRoom() }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Exit", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2F2F2F))
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Phòng Tiệc: ${room?.roomId ?: "Tiệc Giao Lưu"}", color = Color(0xFFFF9800), fontWeight = FontWeight.Bold)
                        Text("Quan sát sảnh ăn và ném Emoji giao lưu trực tiếp!", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Observation tables lists simulation
                Text("KHÁCH CÙNG PHÒNG ĐĂNG BÀN:", color = Color.White, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start))
                Column(modifier = Modifier.fillMaxWidth()) {
                    ObserverSlot(name = room?.hostName ?: "Bếp Trưởng Hải", action = "Đang đun phở bò 400ml")
                    ObserverSlot(name = room?.challengerName ?: "Jerry", action = "Đun sôi ramen 100 độ")
                    ObserverSlot(name = "Kira Đầu Bếp Trẻ", action = "Vừa đập vỡ quả trứng gà")
                }

                Spacer(modifier = Modifier.weight(1f))

                // Emoji Reaction Row launcher
                Text("Thả nhanh Emoji:", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    EmojiBtn(symbol = "🍜", onClick = { emojiTrigger = "🍜" })
                    EmojiBtn(symbol = "🥘", onClick = { emojiTrigger = "🥘" })
                    EmojiBtn(symbol = "🔥", onClick = { emojiTrigger = "🔥" })
                    EmojiBtn(symbol = "✨", onClick = { emojiTrigger = "✨" })
                    EmojiBtn(symbol = "😂", onClick = { emojiTrigger = "😂" })
                }
            }

            // Flying Emoji overlays
            if (emojiTrigger != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 120.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
                        shape = CircleShape
                    ) {
                        Text(
                            emojiTrigger ?: "",
                            fontSize = 64.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ObserverSlot(name: String, action: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LiveTv, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                Text(action, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun EmojiBtn(symbol: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(48.dp)
            .clickable { onClick() }
            .testTag("emoji_btn_$symbol"),
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(symbol, fontSize = 24.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: GameViewModel) {
    val messages by viewModel.globalChatMessages.collectAsState()
    var textInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Phòng Chat Thế Giới", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo("online") }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Trở về")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Message Scroller Feed
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                reverseLayout = false
            ) {
                items(messages) { msg ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .align(Alignment.Start),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(msg.senderName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF5722))
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(msg.messageText, fontSize = 14.sp)
                        }
                    }
                }
            }

            // Quick Emoji Send triggers Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("🍲", "🍜", "🔥", "🔥", "👍", "🤣", "👏").forEach { emoji ->
                    Text(
                        text = emoji,
                        fontSize = 24.sp,
                        modifier = Modifier
                            .clickable { viewModel.sendTextChat(emoji, true) }
                            .padding(4.dp)
                    )
                }
            }

            // Typing block
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Nhập tin nhắn giao lưu...") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_text")
                        .padding(end = 8.dp),
                    maxLines = 1,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF5722)
                    )
                )

                IconButton(
                    onClick = {
                        if (textInput.isNotEmpty()) {
                            viewModel.sendTextChat(textInput)
                            textInput = ""
                        }
                    },
                    modifier = Modifier.testTag("chat_send_button")
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Gửi", tint = Color(0xFFFF5722))
                }
            }
        }
    }
}
