package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = CafePrimaryDark,
    onPrimary = Color(0xFF4A1000),
    primaryContainer = Color(0xFF6E1800),
    onPrimaryContainer = Color(0xFFFFECE5),
    secondary = CafeSecondaryDark,
    onSecondary = Color(0xFF4E2C00),
    secondaryContainer = Color(0xFF6B4000),
    onSecondaryContainer = Color(0xFFFFF1D6),
    tertiary = CafeTertiaryDark,
    onTertiary = Color(0xFF003823),
    tertiaryContainer = Color(0xFF00593B),
    onTertiaryContainer = Color(0xFFD1FBF0),
    background = CafeBackgroundDark,
    onBackground = Color(0xFFE5F5EF),
    surface = CafeSurfaceDark,
    onSurface = Color(0xFFE5F5EF),
    surfaceVariant = CafeSurfaceVariantDark,
    onSurfaceVariant = Color(0xFFA5B8AF),
    outline = CafeOutlineDark
  )

private val LightColorScheme =
  lightColorScheme(
    primary = CafePrimaryLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFECE5),
    onPrimaryContainer = Color(0xFF5A0F00),
    secondary = CafeSecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFF2D6),
    onSecondaryContainer = Color(0xFF543400),
    tertiary = CafeTertiaryLight,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD1FAF0),
    onTertiaryContainer = Color(0xFF003823),
    background = CafeBackgroundLight,
    onBackground = Color(0xFF131D1A),
    surface = CafeSurfaceLight,
    onSurface = Color(0xFF131D1A),
    surfaceVariant = CafeSurfaceVariantLight,
    onSurfaceVariant = Color(0xFF56625D),
    outline = CafeOutlineLight
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is disabled to ensure our handcrafted premium food/cooking palette is active
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
