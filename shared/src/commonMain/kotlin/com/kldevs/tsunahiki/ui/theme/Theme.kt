package com.kldevs.tsunahiki.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AppTypography

val lightScheme = lightColorScheme(
    primary = primaryColor,
    secondary = secondaryColor,
    surface = Color(0xFFFFFBF2),
    onSurface = Color(0xFF494038),
    onSurfaceVariant = Color(0xFF000000),
    surfaceContainerLow = Color(0xFFDED5C5),
    surfaceDim = Color(0xFFFFFDF7),
    surfaceBright = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFEADFC9),
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
  val colorScheme = when {
      darkTheme -> lightScheme
      else -> lightScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = AppTypography(),
    content = content
  )
}

