package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = MetallicGold,
  onPrimary = CharcoalDark,
  primaryContainer = CharcoalCard,
  onPrimaryContainer = MetallicGoldLight,
  secondary = SteelSilver,
  onSecondary = CharcoalDark,
  secondaryContainer = CharcoalBorder,
  onSecondaryContainer = SteelChrome,
  tertiary = WarmBronze,
  onTertiary = PureWhite,
  background = CharcoalDark,
  onBackground = OffWhite,
  surface = CharcoalSurface,
  onSurface = OffWhite,
  surfaceVariant = CharcoalCard,
  onSurfaceVariant = SteelSilver,
  outline = CharcoalBorder
)

private val LightColorScheme = lightColorScheme(
  primary = WarmBronze,
  onPrimary = PureWhite,
  primaryContainer = MetallicGoldLight,
  onPrimaryContainer = CharcoalDark,
  secondary = TextDarkSecondary,
  onSecondary = PureWhite,
  secondaryContainer = SteelChrome,
  onSecondaryContainer = TextDarkPrimary,
  tertiary = MetallicGoldDark,
  onTertiary = PureWhite,
  background = OffWhite,
  onBackground = TextDarkPrimary,
  surface = PureWhite,
  onSurface = TextDarkPrimary,
  surfaceVariant = Color(0xFFF1F3F5),
  onSurfaceVariant = TextDarkSecondary,
  outline = Color(0xFFD0D7DE)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to stunning Charcoal Black and Metallic Gold
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
