package com.example.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ItemGraphic(itemId: String, sizeDp: Dp = 64.dp, modifier: Modifier = Modifier) {
    // Ultra-premium, vibrant color gradients for each item
    val gradientColors = when (itemId) {
        "ing_haohao" -> listOf(Color(0xFFFF7043), Color(0xFFF4511E))      // Radiant Coral Hảo Hảo
        "ing_omachi" -> listOf(Color(0xFF880E4F), Color(0xFF5C0632))      // Premium rich dark plum Omachi
        "ing_ramen" -> listOf(Color(0xFF263238), Color(0xFF151B1E))       // Slate/Charcoal premium Ramen
        "ing_udon" -> listOf(Color(0xFFFFF9C4), Color(0xFFFFF176))        // Smooth buttery golden-yellow Udon
        "ing_pho" -> listOf(Color(0xFFE0F2F1), Color(0xFF80CBC4))         // Cool minty Phở broth
        
        "top_egg" -> listOf(Color(0xFFFFEE58), Color(0xFFFBC02D))         // Golden farm egg glow
        "top_sausage" -> listOf(Color(0xFFFF8A65), Color(0xFFD84315))     // Woodfire sausage red
        "top_beef" -> listOf(Color(0xFFB71C1C), Color(0xFF7F0000))        // Premium marble beef crimson
        "top_cheese" -> listOf(Color(0xFFFFD54F), Color(0xFFF9A825))      // Creamy Dutch Gouda gold
        "top_kimchi" -> listOf(Color(0xFFFF5722), Color(0xFFBF360C))      // Fiery organic Kimchi red
        
        "stove_gas" -> listOf(Color(0xFF90A4AE), Color(0xFF455A64))       // Brushed aluminum gas stove
        "stove_induction" -> listOf(Color(0xFF37474F), Color(0xFF102027))  // Sleek touch glass obsidian
        "pot_clay" -> listOf(Color(0xFF8D6E63), Color(0xFF4E342E))         // Warm country terracotta clay
        "pot_gold" -> listOf(Color(0xFFFFEE58), Color(0xFFF9A825))         // Royal Vietnamese gold leaf
        
        "decor_table" -> listOf(Color(0xFFE1F5FE), Color(0xFF4FC3F7))      // Flowery clear sky turquoise
        "decor_chair" -> listOf(Color(0xFFEDE7F6), Color(0xFF9575CD))      // Lavender velvet violet
        
        else -> listOf(Color(0xFFFFD54F), Color(0xFFFF9100))              // General warm orange
    }

    // Determine border color based on theme
    val isLight = itemId == "ing_udon" || itemId == "ing_pho" || itemId == "decor_table" || itemId == "decor_chair"
    val borderColor = if (isLight) Color.Black.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.25f)

    Box(
        modifier = modifier
            .size(sizeDp)
            .shadow(6.dp, shape = RoundedCornerShape(16.dp), clip = false)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.radialGradient(gradientColors))
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(sizeDp * 0.08f)
        ) {
            val width = size.width
            val height = size.height
            
            // We mathematically center ALL operations to be 100% symmetrically aligned around the exact center
            val cx = width / 2f
            val cy = height / 2f
            
            // Standard bounding dimensions for visual balance
            val bowlRadius = width * 0.36f
            val bowlTopY = cy
            val bowlBottomY = cy + height * 0.28f
            
            when (itemId) {
                // 1. MÌ HẢO HẢO: Symmetrical modern bowl + wavy noodles + delicious organic shrimp + steam
                "ing_haohao" -> {
                    // Wavy golden noodle background inside the bowl area
                    val noodlePath = Path().apply {
                        for (offsetY in listOf(-12f, -4f, 4f)) {
                            moveTo(cx - bowlRadius * 0.8f, bowlTopY + offsetY)
                            var x = cx - bowlRadius * 0.8f
                            val step = bowlRadius * 0.4f
                            quadraticTo(x + step * 0.5f, bowlTopY + offsetY - 8f, x + step, bowlTopY + offsetY)
                            x += step
                            quadraticTo(x + step * 0.5f, bowlTopY + offsetY + 8f, x + step, bowlTopY + offsetY)
                            x += step
                            quadraticTo(x + step * 0.5f, bowlTopY + offsetY - 8f, x + step, bowlTopY + offsetY)
                            x += step
                            quadraticTo(x + step * 0.5f, bowlTopY + offsetY + 8f, x + step, bowlTopY + offsetY)
                        }
                    }
                    drawPath(noodlePath, color = Color(0xFFFFEE58), style = Stroke(width = width * 0.06f, cap = StrokeCap.Round))

                    // Center-Balanced Coral Pink Bowl
                    val bowlPath = Path().apply {
                        moveTo(cx - bowlRadius, bowlTopY)
                        lineTo(cx + bowlRadius, bowlTopY)
                        quadraticTo(cx + bowlRadius * 0.95f, bowlTopY + bowlRadius * 0.7f, cx, bowlBottomY)
                        quadraticTo(cx - bowlRadius * 0.95f, bowlTopY + bowlRadius * 0.7f, cx - bowlRadius, bowlTopY)
                        close()
                    }
                    drawPath(bowlPath, color = Color.White)
                    drawPath(bowlPath, color = Color(0xFFFF8A65), style = Stroke(width = width * 0.04f))

                    // Delicious pink shrimp resting inside
                    val shrimpPath = Path().apply {
                        moveTo(cx - width * 0.16f, cy - height * 0.02f)
                        quadraticTo(cx - width * 0.02f, cy - height * 0.18f, cx + width * 0.14f, cy - height * 0.04f)
                        quadraticTo(cx - width * 0.02f, cy - height * 0.08f, cx - width * 0.12f, cy + height * 0.01f)
                        close()
                    }
                    drawPath(shrimpPath, color = Color(0xFFFF7043))
                    // Sliced shrimp white details
                    for (i in 1..3) {
                        drawCircle(color = Color.White.copy(alpha = 0.8f), radius = 3.5f, center = Offset(cx - width * 0.08f + i * 16f, cy - height * 0.07f))
                    }

                    // Steamy swirls above
                    val steamPath = Path().apply {
                        moveTo(cx - 14f, bowlTopY - 14f)
                        quadraticTo(cx - 20f, bowlTopY - 26f, cx - 12f, bowlTopY - 36f)
                        moveTo(cx + 14f, bowlTopY - 12f)
                        quadraticTo(cx + 8f, bowlTopY - 24f, cx + 16f, bowlTopY - 34f)
                    }
                    drawPath(steamPath, color = Color.White.copy(alpha = 0.8f), style = Stroke(width = width * 0.04f, cap = StrokeCap.Round))
                }

                // 2. MÌ OMACHI: Elegant reddish wooden bowl + potato noodles + green leaf garnish + chili
                "ing_omachi" -> {
                    // Golden potato noodles bundle
                    val noodlePath = Path().apply {
                        for (i in -2..2) {
                            val ox = cx + i * (width * 0.12f)
                            moveTo(ox - 8f, bowlTopY)
                            quadraticTo(ox, bowlTopY - 16f, ox + 8f, bowlTopY)
                        }
                    }
                    drawPath(noodlePath, color = Color(0xFFFFD54F), style = Stroke(width = width * 0.06f, cap = StrokeCap.Round))

                    // Premium Mahogany Wood Bowl
                    val bowlPath = Path().apply {
                        moveTo(cx - bowlRadius, bowlTopY)
                        lineTo(cx + bowlRadius, bowlTopY)
                        quadraticTo(cx + bowlRadius * 0.95f, bowlTopY + bowlRadius * 0.7f, cx, bowlBottomY)
                        quadraticTo(cx - bowlRadius * 0.95f, bowlTopY + bowlRadius * 0.7f, cx - bowlRadius, bowlTopY)
                        close()
                    }
                    drawPath(bowlPath, color = Color(0xFF3E2723)) // Mahogany wood base
                    
                    // Royal red horizontal band
                    drawPath(Path().apply {
                        moveTo(cx - bowlRadius * 0.9f, bowlTopY + 12f)
                        quadraticTo(cx, bowlTopY + 22f, cx + bowlRadius * 0.9f, bowlTopY + 12f)
                    }, color = Color(0xFFC2185B), style = Stroke(width = width * 0.05f))

                    // Basil green garnish leaf
                    val leafPath = Path().apply {
                        moveTo(cx - 6f, bowlTopY - 6f)
                        quadraticTo(cx - 18f, bowlTopY - 20f, cx - 22f, bowlTopY - 10f)
                        quadraticTo(cx - 12f, bowlTopY - 2f, cx - 6f, bowlTopY - 6f)
                    }
                    drawPath(leafPath, color = Color(0xFF4CAF50))

                    // Small red chili slice
                    drawCircle(color = Color(0xFFD50000), radius = 6.5f, center = Offset(cx + 12f, bowlTopY - 6f))
                    drawCircle(color = Color(0xFFFFD54F), radius = 2.5f, center = Offset(cx + 12f, bowlTopY - 6f))
                }

                // 3. MÌ RAMEN CAO CẤP: Dark obsidian ceramic bowl + red line + sliced egg + nori + naruto fishcake
                "ing_ramen" -> {
                    // Nori seaweed sheet sticking out behind the bowl
                    drawRect(
                        color = Color(0xFF1B5E20), 
                        topLeft = Offset(cx + bowlRadius * 0.15f, bowlTopY - 28f), 
                        size = Size(width * 0.22f, height * 0.28f)
                    )

                    // Delicious Rich Golden Tonkotsu Broth
                    val brothPath = Path().apply {
                        moveTo(cx - bowlRadius * 0.8f, bowlTopY)
                        lineTo(cx + bowlRadius * 0.8f, bowlTopY)
                        quadraticTo(cx, bowlTopY + 16f, cx - bowlRadius * 0.8f, bowlTopY)
                    }
                    drawPath(brothPath, color = Color(0xFFFFD54F))

                    // Dark Symmetrical Obsidian Bowl
                    val bowlPath = Path().apply {
                        moveTo(cx - bowlRadius, bowlTopY)
                        lineTo(cx + bowlRadius, bowlTopY)
                        quadraticTo(cx + bowlRadius * 0.95f, bowlTopY + bowlRadius * 0.7f, cx, bowlBottomY)
                        quadraticTo(cx - bowlRadius * 0.95f, bowlTopY + bowlRadius * 0.7f, cx - bowlRadius, bowlTopY)
                        close()
                    }
                    drawPath(bowlPath, color = Color(0xFF111111)) // Pitch black
                    
                    // Crimson red top lip outline
                    drawPath(Path().apply {
                        moveTo(cx - bowlRadius, bowlTopY)
                        lineTo(cx + bowlRadius, bowlTopY)
                    }, color = Color(0xFFD50000), style = Stroke(width = width * 0.06f, cap = StrokeCap.Round))

                    // Sliced Half Egg (White base + rich orange yolk)
                    drawCircle(color = Color.White, radius = width * 0.12f, center = Offset(cx - 12f, bowlTopY + 4f))
                    drawCircle(color = Color(0xFFF57C00), radius = width * 0.06f, center = Offset(cx - 12f, bowlTopY + 4f))
                    drawCircle(color = Color.White, radius = 2.5f, center = Offset(cx - 15f, bowlTopY + 2f))

                    // Pink spiral Narutomaki fishcake
                    val fishcakeCenter = Offset(cx + 18f, bowlTopY + 8f)
                    drawCircle(color = Color.White, radius = width * 0.1f, center = fishcakeCenter)
                    drawCircle(color = Color(0xFFE91E63), radius = width * 0.04f, center = fishcakeCenter, style = Stroke(width = 3.5f))
                }

                // 4. MÌ UDON BÉO NGẬY: Cute blue ocean patterned bowl + thick white udon strands + scallions
                "ing_udon" -> {
                    // Thick luscious white udon ropes
                    val noodlePath = Path().apply {
                        moveTo(cx - bowlRadius * 0.7f, bowlTopY - 4f)
                        quadraticTo(cx - bowlRadius * 0.3f, bowlTopY - 16f, cx, bowlTopY - 4f)
                        quadraticTo(cx + bowlRadius * 0.3f, bowlTopY + 8f, cx + bowlRadius * 0.7f, bowlTopY - 4f)
                        
                        moveTo(cx - bowlRadius * 0.5f, bowlTopY + 4f)
                        quadraticTo(cx, bowlTopY - 8f, cx + bowlRadius * 0.5f, bowlTopY + 4f)
                    }
                    drawPath(noodlePath, color = Color(0xFFFAFAFA), style = Stroke(width = width * 0.09f, cap = StrokeCap.Round))

                    // Ocean Blue Bowl
                    val bowlPath = Path().apply {
                        moveTo(cx - bowlRadius, bowlTopY)
                        lineTo(cx + bowlRadius, bowlTopY)
                        quadraticTo(cx + bowlRadius * 0.95f, bowlTopY + bowlRadius * 0.7f, cx, bowlBottomY)
                        quadraticTo(cx - bowlRadius * 0.95f, bowlTopY + bowlRadius * 0.7f, cx - bowlRadius, bowlTopY)
                        close()
                    }
                    drawPath(bowlPath, color = Color(0xFF1976D2))
                    
                    // Wave pattern on bowl
                    drawPath(Path().apply {
                        moveTo(cx - bowlRadius * 0.6f, bowlTopY + bowlRadius * 0.3f)
                        quadraticTo(cx, bowlTopY + bowlRadius * 0.5f, cx + bowlRadius * 0.6f, bowlTopY + bowlRadius * 0.3f)
                    }, color = Color.White.copy(alpha = 0.6f), style = Stroke(width = width * 0.04f))

                    // Green scallion rounds
                    drawCircle(color = Color(0xFF00E676), radius = 4.5f, center = Offset(cx - 16f, bowlTopY + 12f))
                    drawCircle(color = Color(0xFF00E676), radius = 4.5f, center = Offset(cx + 8f, bowlTopY + 16f))
                }

                // 5. PHỞ BÒ HÀ NỘI: Ivory white porcelain bowl / blue floral bottom + tender marbled beef + star anise
                "ing_pho" -> {
                    // Fine flat white rice noodles
                    val noodlesPath = Path().apply {
                        for (i in -1..1) {
                            val ox = cx + i * 12f
                            moveTo(ox - 10f, bowlTopY)
                            lineTo(ox + 10f, bowlTopY + 12f)
                        }
                    }
                    drawPath(noodlesPath, color = Color(0xFFFAFAFA), style = Stroke(width = width * 0.05f))

                    // Symmetrical Pure White Ceramic Bowl
                    val bowlPath = Path().apply {
                        moveTo(cx - bowlRadius, bowlTopY)
                        lineTo(cx + bowlRadius, bowlTopY)
                        quadraticTo(cx + bowlRadius * 0.95f, bowlTopY + bowlRadius * 0.7f, cx, bowlBottomY)
                        quadraticTo(cx - bowlRadius * 0.95f, bowlTopY + bowlRadius * 0.7f, cx - bowlRadius, bowlTopY)
                        close()
                    }
                    drawPath(bowlPath, color = Color(0xFFFFFAF0)) // Ivory bone porcelain
                    drawPath(bowlPath, color = Color(0xFFB0BEC5), style = Stroke(width = width * 0.03f))

                    // Dynasty Blue floral bottom ribbon
                    drawPath(Path().apply {
                        moveTo(cx - bowlRadius * 0.6f, bowlTopY + bowlRadius * 0.45f)
                        quadraticTo(cx, bowlBottomY - 8f, cx + bowlRadius * 0.6f, bowlTopY + bowlRadius * 0.45f)
                    }, color = Color(0xFF0D47A1), style = Stroke(width = width * 0.05f))

                    // Red tender beef slices resting beautifully inside
                    drawOval(
                        color = Color(0xFFD32F2F), 
                        topLeft = Offset(cx - 24f, bowlTopY + 2f), 
                        size = Size(26f, 16f)
                    )
                    drawOval(
                        color = Color(0xFFC62828), 
                        topLeft = Offset(cx + 2f, bowlTopY + 4f), 
                        size = Size(24f, 14f)
                    )

                    // Fresh onion circles
                    drawCircle(color = Color(0xFF4CAF50), radius = 5f, center = Offset(cx - 8f, bowlTopY + 18f), style = Stroke(width = 2f))
                    drawCircle(color = Color(0xFF4CAF50), radius = 5f, center = Offset(cx + 12f, bowlTopY + 20f), style = Stroke(width = 2f))
                }

                // 6. TRỨNG GÀ TA: Perfectly centered, glossy double-shaded fried egg white + yolk
                "top_egg" -> {
                    // Fluid, organic perfectly centered egg white
                    val eggWhite = Path().apply {
                        moveTo(cx, cy - height * 0.32f)
                        quadraticTo(cx + width * 0.38f, cy - height * 0.28f, cx + width * 0.4f, cy)
                        quadraticTo(cx + width * 0.32f, cy + height * 0.32f, cx, cy + height * 0.36f)
                        quadraticTo(cx - width * 0.38f, cy + height * 0.28f, cx - width * 0.4f, cy)
                        quadraticTo(cx - width * 0.32f, cy - height * 0.32f, cx, cy - height * 0.32f)
                        close()
                    }
                    drawPath(eggWhite, color = Color(0xFFFAFAFA))
                    drawPath(eggWhite, color = Color(0xFFE0E0E0), style = Stroke(width = 3f))

                    // Plump shiny golden yolk right in the exact center
                    drawCircle(color = Color(0xFFFF9100), radius = width * 0.18f, center = Offset(cx, cy))
                    // Glassy shiny point
                    drawCircle(color = Color.White, radius = width * 0.05f, center = Offset(cx - 5f, cy - 5f))
                }

                // 7. XÚC XÍCH ĐỨC: Curving sausage slice + cozy cross patterns + yellow mustard drizzle
                "top_sausage" -> {
                    // Curved sausage body perfectly balanced at the center
                    val sausagePath = Path().apply {
                        moveTo(cx - width * 0.35f, cy + height * 0.12f)
                        quadraticTo(cx, cy - height * 0.26f, cx + width * 0.35f, cy + height * 0.12f)
                    }
                    drawPath(sausagePath, color = Color(0xFFBF360C), style = Stroke(width = width * 0.22f, cap = StrokeCap.Round))

                    // Delicious grill stripes
                    for (i in -2..2) {
                        val sx = cx + i * (width * 0.14f)
                        val sy = cy - height * 0.08f + (if (i<0) -i else i) * 6f
                        drawLine(
                            color = Color(0xFFFFCC80).copy(alpha = 0.6f),
                            start = Offset(sx - 8f, sy - 10f),
                            end = Offset(sx + 8f, sy + 10f),
                            strokeWidth = 3.5f,
                            cap = StrokeCap.Round
                        )
                    }
                }

                // 8. THỊT BÒ MỸ: Premium raw Wagyu leaf steak + beautiful white marble fat lines
                "top_beef" -> {
                    val steakPath = Path().apply {
                        moveTo(cx - width * 0.38f, cy - height * 0.22f)
                        quadraticTo(cx + width * 0.22f, cy - height * 0.34f, cx + width * 0.38f, cy - height * 0.16f)
                        quadraticTo(cx + width * 0.42f, cy + height * 0.16f, cx + width * 0.24f, cy + height * 0.32f)
                        quadraticTo(cx - width * 0.18f, cy + height * 0.36f, cx - width * 0.38f, cy + height * 0.2f)
                        quadraticTo(cx - width * 0.44f, cy - height * 0.04f, cx - width * 0.38f, cy - height * 0.22f)
                        close()
                    }
                    // Rich deep meat red
                    drawPath(steakPath, color = Color(0xFFB71C1C))
                    
                    // Cream-yellow outer fat rim
                    drawPath(steakPath, color = Color(0xFFFFF9C4), style = Stroke(width = 5f))

                    // Marbled fat trails branching beautifully
                    val fatPath = Path().apply {
                        moveTo(cx - width * 0.2f, cy - height * 0.12f)
                        quadraticTo(cx - width * 0.05f, cy - height * 0.02f, cx - width * 0.15f, cy + height * 0.12f)
                        
                        moveTo(cx + width * 0.1f, cy - height * 0.15f)
                        quadraticTo(cx + width * 0.18f, cy, cx + width * 0.08f, cy + height * 0.18f)
                        
                        moveTo(cx - width * 0.05f, cy)
                        quadraticTo(cx + width * 0.05f, cy + height * 0.1f, cx, cy + height * 0.2f)
                    }
                    drawPath(fatPath, color = Color(0xFFFFF9C4), style = Stroke(width = 3.5f, cap = StrokeCap.Round))
                }

                // 9. PHÔ MAI DẺO CHEDDAR: 3D Cute Isometric Cheese Wedge with swiss holes
                "top_cheese" -> {
                    // Front side triangle
                    val frontWedge = Path().apply {
                        moveTo(cx - width * 0.35f, cy + height * 0.22f)
                        lineTo(cx + width * 0.35f, cy + height * 0.22f)
                        lineTo(cx, cy - height * 0.32f)
                        close()
                    }
                    drawPath(frontWedge, color = Color(0xFFFFD54F))

                    // Cute Swiss Cheese circular shadow holes
                    drawCircle(color = Color(0xFFF57C00).copy(alpha = 0.8f), radius = 8f, center = Offset(cx - 14f, cy + 10f))
                    drawCircle(color = Color(0xFFF57C00).copy(alpha = 0.8f), radius = 10f, center = Offset(cx + 14f, cy + 12f))
                    drawCircle(color = Color(0xFFF57C00).copy(alpha = 0.8f), radius = 7f, center = Offset(cx, cy - 14f))

                    // Highlight reflections inside holes
                    drawCircle(color = Color.White.copy(alpha = 0.3f), radius = 2.5f, center = Offset(cx - 16f, cy + 8f))
                    drawCircle(color = Color.White.copy(alpha = 0.3f), radius = 3f, center = Offset(cx + 12f, cy + 10f))
                }

                // 10. KIM CHI CHUA CAY: Glazed layers of fermented organic cabbage folds + sesame seeds
                "top_kimchi" -> {
                    // Background cabbage blocks
                    drawRoundRect(
                        color = Color(0xFFD84315),
                        topLeft = Offset(cx - width * 0.3f, cy - height * 0.28f),
                        size = Size(width * 0.6f, height * 0.56f),
                        cornerRadius = CornerRadius(14f, 14f)
                    )

                    // Secondary lighter orange glazing folds
                    val glazePath = Path().apply {
                        moveTo(cx - width * 0.22f, cy - height * 0.22f)
                        lineTo(cx + width * 0.22f, cy - height * 0.22f)
                        quadraticTo(cx, cy + height * 0.1f, cx - width * 0.18f, cy + height * 0.24f)
                        close()
                    }
                    drawPath(glazePath, color = Color(0xFFFF5722))

                    // Symmetrical cabbage leaf ribs
                    drawLine(Color(0xFFFFF9C4), start = Offset(cx, cy - height * 0.26f), end = Offset(cx, cy + height * 0.22f), strokeWidth = 8f, cap = StrokeCap.Round)
                    drawLine(Color(0xFFFFF9C4), start = Offset(cx, cy - height * 0.05f), end = Offset(cx - width * 0.16f, cy - height * 0.15f), strokeWidth = 4.5f, cap = StrokeCap.Round)
                    drawLine(Color(0xFFFFF9C4), start = Offset(cx, cy + height * 0.08f), end = Offset(cx + width * 0.16f, cy + height * 0.02f), strokeWidth = 4.5f, cap = StrokeCap.Round)

                    // Delicious sesame seeds sprinkled
                    drawCircle(color = Color.White, radius = 2.5f, center = Offset(cx - 18f, cy + 14f))
                    drawCircle(color = Color(0xFF212121), radius = 2.5f, center = Offset(cx + 18f, cy - 14f))
                }

                // 11. BẾP GA MINI: Symmetrical industrial stove + energetic multi-point glowing blue fire
                "stove_gas" -> {
                    // Solid gray rounded burner base plate
                    drawRoundRect(
                        color = Color(0xFF455A64),
                        topLeft = Offset(cx - width * 0.38f, cy - height * 0.26f),
                        size = Size(width * 0.76f, height * 0.52f),
                        cornerRadius = CornerRadius(12f, 12f)
                    )
                    
                    // Silver inner burner ring
                    drawCircle(color = Color(0xFFCFD8DC), radius = width * 0.2f, center = Offset(cx, cy - 4f))

                    // Multi-pointed energetic cyan gas flame ring
                    drawCircle(
                        color = Color(0xFF00E5FF),
                        radius = width * 0.14f,
                        center = Offset(cx, cy - 4f),
                        style = Stroke(width = 6f)
                    )
                    // Fire core
                    drawCircle(color = Color(0x7700E5FF), radius = width * 0.08f, center = Offset(cx, cy - 4f))

                    // Symmetrical metal pot supports
                    for (angle in listOf(0f, 90f, 180f, 270f)) {
                        drawRect(
                            color = Color(0xFF263238),
                            topLeft = Offset(cx - 5f, cy - width * 0.22f - 4f),
                            size = Size(10f, 12f)
                        )
                    }

                    // Golden control knob on bottom right
                    drawCircle(color = Color(0xFFFFA000), radius = width * 0.06f, center = Offset(cx + width * 0.24f, cy + height * 0.14f))
                }

                // 12. BẾP TỪ SIÊU TỐC: Sleek obsidian glass cooktop + deep crimson red induction rings + LEDs
                "stove_induction" -> {
                    // Outer square obsidian stove frame
                    drawRoundRect(
                        color = Color(0xFF212121),
                        topLeft = Offset(cx - width * 0.38f, cy - height * 0.26f),
                        size = Size(width * 0.76f, height * 0.52f),
                        cornerRadius = CornerRadius(12f, 12f)
                    )

                    // Hot glowing neon-red main induction ring
                    drawCircle(color = Color(0xFFFF3D00), radius = width * 0.26f, center = Offset(cx, cy - 4f), style = Stroke(width = 4.5f))
                    drawCircle(color = Color(0xFFFF9100).copy(alpha = 0.5f), radius = width * 0.18f, center = Offset(cx, cy - 4f), style = Stroke(width = 3f))
                    
                    // Hot thermal core center indicator
                    drawCircle(color = Color(0xFFFF3D00).copy(alpha = 0.25f), radius = width * 0.14f, center = Offset(cx, cy - 4f))

                    // Green running light indicators
                    for (i in 0..2) {
                        drawCircle(color = Color(0xFF00E676), radius = 3f, center = Offset(cx - 16f + i * 16f, cy + height * 0.16f))
                    }
                }

                // 13. NỒI ĐẤT GIỮ NHIỆT: Traditional rustic claypot with loop handles/lid & chimney steam
                "pot_clay" -> {
                    val potRadius = width * 0.3f
                    val potTopY = cy - height * 0.12f
                    val potBottomY = cy + height * 0.28f

                    // Left/Right clay circular handles
                    drawCircle(color = Color(0xFF3E2723), radius = width * 0.12f, center = Offset(cx - potRadius, cy + 4f), style = Stroke(width = 5f))
                    drawCircle(color = Color(0xFF3E2723), radius = width * 0.12f, center = Offset(cx + potRadius, cy + 4f), style = Stroke(width = 5f))

                    // Earthen pot clay belly
                    val potBody = Path().apply {
                        moveTo(cx - potRadius, potTopY)
                        lineTo(cx + potRadius, potTopY)
                        quadraticTo(cx + potRadius * 0.95f, cy + height * 0.2f, cx, potBottomY)
                        quadraticTo(cx - potRadius * 0.95f, cy + height * 0.2f, cx - potRadius, potTopY)
                        close()
                    }
                    drawPath(potBody, color = Color(0xFF795548))
                    
                    // Terracotta heat glaze finish line
                    drawPath(Path().apply {
                        moveTo(cx - potRadius * 0.8f, cy + 8f)
                        quadraticTo(cx, cy + 20f, cx + potRadius * 0.8f, cy + 8f)
                    }, color = Color(0xFF5D4037), style = Stroke(width = width * 0.04f))

                    // Thick ceramic lid sitting on top
                    drawRoundRect(
                        color = Color(0xFF4E342E),
                        topLeft = Offset(cx - potRadius * 1.05f, potTopY - 10f),
                        size = Size(potRadius * 2.1f, 12f),
                        cornerRadius = CornerRadius(5f, 5f)
                    )
                    // Lid top grab ring handle
                    drawCircle(color = Color(0xFF271714), radius = 8f, center = Offset(cx, potTopY - 16f), style = Stroke(width = 4.5f))
                }

                // 14. NỒI HOÀNG GIA MẠ VÀNG: Extravagant gold leaf pot + precious ruby/emerald/sapphire gems
                "pot_gold" -> {
                    val potRadius = width * 0.3f
                    val potTopY = cy - height * 0.14f
                    val potBottomY = cy + height * 0.28f

                    // Dazzling imperial gold handles
                    drawCircle(color = Color(0xFFFFD54F), radius = width * 0.13f, center = Offset(cx - potRadius, cy + 2f), style = Stroke(width = 6f))
                    drawCircle(color = Color(0xFFFFD54F), radius = width * 0.13f, center = Offset(cx + potRadius, cy + 2f), style = Stroke(width = 6f))

                    // Polished golden body
                    val potBody = Path().apply {
                        moveTo(cx - potRadius, potTopY)
                        lineTo(cx + potRadius, potTopY)
                        quadraticTo(cx + potRadius * 0.95f, cy + height * 0.2f, cx, potBottomY)
                        quadraticTo(cx - potRadius * 0.95f, cy + height * 0.2f, cx - potRadius, potTopY)
                        close()
                    }
                    drawPath(potBody, color = Color(0xFFFFEB3B)) // Bright Gold Leaf
                    drawPath(potBody, color = Color(0xFFFBC02D), style = Stroke(width = 3.5f))

                    // Royal Gems on Pot Belly
                    drawCircle(color = Color(0xFFE53935), radius = 5.5f, center = Offset(cx, cy + 6f)) // Sparkly Ruby
                    drawCircle(color = Color(0xFF1E88E5), radius = 4.5f, center = Offset(cx - 16f, cy + 6f)) // Celestial Sapphire
                    drawCircle(color = Color(0xFF43A047), radius = 4.5f, center = Offset(cx + 16f, cy + 6f)) // Organic Emerald

                    // Golden crown lid crowning
                    drawRoundRect(
                        color = Color(0xFFFFA000),
                        topLeft = Offset(cx - potRadius * 1.05f, potTopY - 10f),
                        size = Size(potRadius * 2.1f, 10f),
                        cornerRadius = CornerRadius(4f, 4f)
                    )
                    // Royal crown finial/knob
                    drawCircle(color = Color(0xFFFFD54F), radius = 8f, center = Offset(cx, potTopY - 16f))
                }

                // 15. DECOR_TABLE: Beautiful elegant dining clear-glass vase + green plants + blossoms
                "decor_table" -> {
                    val vaseTopY = cy
                    val vaseBottomY = cy + height * 0.32f

                    // Glass vase body outline
                    val vasePath = Path().apply {
                        moveTo(cx - 10f, vaseTopY)
                        lineTo(cx + 10f, vaseTopY)
                        quadraticTo(cx + 20f, cy + height * 0.18f, cx + 18f, vaseBottomY)
                        lineTo(cx - 18f, vaseBottomY)
                        quadraticTo(cx - 20f, cy + height * 0.18f, cx - 10f, vaseTopY)
                        close()
                    }
                    // Light clear blue water inside the vase
                    drawPath(vasePath, color = Color(0x55E0F7FA))
                    drawPath(vasePath, color = Color(0xAA00ACC1), style = Stroke(width = 4f))

                    // Fresh green plant stems rising from vase bottom outwards
                    drawLine(Color(0xFF2E7D32), start = Offset(cx, vaseBottomY - 4f), end = Offset(cx, cy - height * 0.3f), strokeWidth = 5f, cap = StrokeCap.Round)
                    drawLine(Color(0xFF2E7D32), start = Offset(cx, cy - height * 0.05f), end = Offset(cx - 18f, cy - height * 0.2f), strokeWidth = 4f, cap = StrokeCap.Round)
                    drawLine(Color(0xFF2E7D32), start = Offset(cx, cy - height * 0.08f), end = Offset(cx + 18f, cy - height * 0.2f), strokeWidth = 4f, cap = StrokeCap.Round)

                    // Blooming pink and yellow spring flower petals
                    drawCircle(color = Color(0xFFFF4081), radius = 9f, center = Offset(cx, cy - height * 0.3f))
                    drawCircle(color = Color(0xFFFFEE58), radius = 4.5f, center = Offset(cx, cy - height * 0.3f))
                    
                    drawCircle(color = Color(0xFFFF1744), radius = 7.5f, center = Offset(cx - 18f, cy - height * 0.2f))
                    drawCircle(color = Color(0xFFFFEA00), radius = 3.5f, center = Offset(cx - 18f, cy - height * 0.2f))

                    drawCircle(color = Color(0xFFFF1744), radius = 7.5f, center = Offset(cx + 18f, cy - height * 0.2f))
                    drawCircle(color = Color(0xFFFFEA00), radius = 3.5f, center = Offset(cx + 18f, cy - height * 0.2f))
                }

                // 16. DECOR_CHAIR: Cozy high-back royal armchair + violet velvet cushioning + gold legs
                "decor_chair" -> {
                    // Cushioned Purple chair backrest
                    val backPath = Path().apply {
                        moveTo(cx - 16f, cy - height * 0.28f)
                        lineTo(cx + 16f, cy - height * 0.28f)
                        quadraticTo(cx + 14f, cy + height * 0.08f, cx + 12f, cy + height * 0.14f)
                        lineTo(cx - 12f, cy + height * 0.14f)
                        quadraticTo(cx - 14f, cy + height * 0.08f, cx - 16f, cy - height * 0.28f)
                        close()
                    }
                    drawPath(backPath, color = Color(0xFF673AB7)) // Royal velvet violet
                    drawPath(backPath, color = Color(0xFFFFD54F), style = Stroke(width = 3.5f)) // Luxury gold wire trim

                    // Thick rounded soft seat cushion
                    drawRoundRect(
                        color = Color(0xFF4527A0), // Deep purple
                        topLeft = Offset(cx - 20f, cy + height * 0.12f),
                        size = Size(40f, 12f),
                        cornerRadius = CornerRadius(5f, 5f)
                    )

                    // Thin, sleek golden-metallic legs
                    drawLine(Color(0xFFFBC02D), start = Offset(cx - 14f, cy + height * 0.22f), end = Offset(cx - 18f, cy + height * 0.38f), strokeWidth = 4.5f, cap = StrokeCap.Round)
                    drawLine(Color(0xFFFBC02D), start = Offset(cx + 14f, cy + height * 0.22f), end = Offset(cx + 18f, cy + height * 0.38f), strokeWidth = 4.5f, cap = StrokeCap.Round)
                }

                else -> {
                    // Symmetrical Red Bowl representation
                    val bowlPath = Path().apply {
                        moveTo(cx - bowlRadius, bowlTopY)
                        lineTo(cx + bowlRadius, bowlTopY)
                        quadraticTo(cx + bowlRadius * 0.95f, bowlTopY + bowlRadius * 0.7f, cx, bowlBottomY)
                        quadraticTo(cx - bowlRadius * 0.95f, bowlTopY + bowlRadius * 0.7f, cx - bowlRadius, bowlTopY)
                        close()
                    }
                    drawPath(bowlPath, color = Color(0xFFE64A19))
                    drawPath(bowlPath, color = Color.White, style = Stroke(width = width * 0.05f))
                }
            }
        }
    }
}
