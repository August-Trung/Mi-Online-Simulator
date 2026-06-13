package com.example.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import com.example.presentation.components.ItemGraphic
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import com.example.domain.ShopCatalogItem
import com.example.presentation.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantScreen(viewModel: GameViewModel) {
    val inventory by viewModel.inventoryItems.collectAsState()
    val shopItems by viewModel.shopCatalog.collectAsState()

    // Filter upgrades (Stove, Pot, Decoration)
    val upgrades = shopItems.filter { it.type == "STOVE" || it.type == "POT" || it.type == "DECORATION" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Quán Mì Của Tôi", 
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // Visual Restaurant Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🏡 Không Gian Quán Mì Việt Nam", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("Đào tạo nhân sự bếp trưởng chuyên nghiệp và sắm sửa bếp đun siêu tốc.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                }
            }

            Text("TIỆN NGHI & THĂNG CẤP BẾP", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(upgrades) { item ->
                    val ownedItem = inventory.find { it.id == item.id }
                    val isEquipped = ownedItem?.isEquipped == true
                    val isOwned = ownedItem != null

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = if (isEquipped) 0.12f else 0.05f), RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isEquipped) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ItemGraphic(
                                itemId = item.id,
                                sizeDp = 44.dp
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Phân loại: ${if (item.type == "POT") "Nồi" else if (item.type == "STOVE") "Bếp" else "Decor"}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            if (isEquipped) {
                                Button(
                                    onClick = { viewModel.handleEquipToggle(item.id, item.type, false) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                                    modifier = Modifier.testTag("unequip_${item.id}")
                                ) {
                                    Text("Tháo", fontSize = 11.sp)
                                }
                            } else if (isOwned) {
                                Button(
                                    onClick = { viewModel.handleEquipToggle(item.id, item.type, true) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                                    modifier = Modifier.testTag("equip_${item.id}")
                                ) {
                                    Text("Đặt Bếp", fontSize = 11.sp)
                                }
                            } else {
                                TextButton(
                                    onClick = { viewModel.buyCatalogItem(item, 1) },
                                    modifier = Modifier.testTag("buy_upgrade_${item.id}")
                                ) {
                                    Icon(Icons.Default.Paid, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${item.priceCoins} Coins", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFFFB300))
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
fun ShopScreen(viewModel: GameViewModel) {
    val itemsAndGoods by viewModel.shopCatalog.collectAsState()
    val user by viewModel.currentUser.collectAsState()

    var activeShopTab by remember { mutableStateOf("Mì & Toppings") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Siêu Thị Nguyên Liệu", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurface)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFB300).copy(alpha = 0.15f))
            ) {
                Text("${user?.coins ?: 0} Coins", fontWeight = FontWeight.Bold, color = Color(0xFFFFB300), modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Categories tabs (Sleek modern segmented slider layout)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("Mì & Toppings", "Bếp & Vành Nồi").forEach { tab ->
                val selected = activeShopTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (selected) Color(0xFFFF5722) else Color.Transparent)
                        .clickable { activeShopTab = tab }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        tab,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val filteredGoods = if (activeShopTab == "Mì & Toppings") {
            itemsAndGoods.filter { it.type == "INGREDIENT" }
        } else {
            itemsAndGoods.filter { it.type == "STOVE" || it.type == "POT" }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredGoods) { good ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
                        .testTag("shop_good_${good.id}"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // The beautifully modern 100% centered culinary graphic
                        ItemGraphic(
                            itemId = good.id,
                            sizeDp = 68.dp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Category visual tag badge
                        val badgeText = when {
                            good.id.startsWith("ing_") -> "Vắt Mì"
                            good.id.startsWith("top_") -> "Topping"
                            good.id.startsWith("stove_") -> "Bếp Nấu"
                            good.id.startsWith("pot_") -> "Vành Nồi"
                            else -> "Trang Bị"
                        }
                        val badgeBg = when {
                            good.id.startsWith("ing_") -> Color(0xFFFF5722).copy(alpha = 0.1f)
                            good.id.startsWith("top_") -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                            else -> Color(0xFF2196F3).copy(alpha = 0.1f)
                        }
                        val badgeColor = when {
                            good.id.startsWith("ing_") -> Color(0xFFFF5722)
                            good.id.startsWith("top_") -> Color(0xFF4CAF50)
                            else -> Color(0xFF2196F3)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(badgeBg)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(badgeText, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = badgeColor)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = good.name,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // High fidelity button with micro-shadowing
                        Button(
                            onClick = { viewModel.buyCatalogItem(good, 1) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp)
                                .testTag("buy_good_btn_${good.id}"),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(11.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${good.priceCoins} C",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InventoryScreen(viewModel: GameViewModel) {
    val items by viewModel.inventoryItems.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Kho Nguyên Liệu", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurface)
        Text("Nơi lưu trữ các loại vắt mì, gia vị và trang bị bếp sắm sửa được.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(16.dp))

        if (items.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Inbox, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Kho hàng rỗng tuếch! Ra siêu thị mua đồ nha.", color = Color.Gray, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = if (item.isEquipped) 0.15f else 0.05f), RoundedCornerShape(16.dp))
                            .testTag("inventory_item_${item.id}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (item.isEquipped) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ItemGraphic(
                                itemId = item.id,
                                sizeDp = 40.dp
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Phân phối: ${item.itemType}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            // Equip button or count label
                            if (item.itemType == "STOVE" || item.itemType == "POT") {
                                Button(
                                    onClick = { viewModel.handleEquipToggle(item.id, item.itemType, !item.isEquipped) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (item.isEquipped) Color.Gray else Color(0xFFFF5722)
                                    )
                                ) {
                                    Text(if (item.isEquipped) "Gỡ" else "Dùng", fontSize = 11.sp)
                                }
                            } else {
                                Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                    Text("Số lượng: ${item.quantity}", color = Color.White, modifier = Modifier.padding(2.dp), fontSize = 11.sp)
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
fun Modifier.size(size: Int): Modifier = this.then(Modifier.size(size.dp))
