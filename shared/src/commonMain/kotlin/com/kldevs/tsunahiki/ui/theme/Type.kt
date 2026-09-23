package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import tsunahiki.shared.generated.resources.Res
import tsunahiki.shared.generated.resources.inter_variable
import tsunahiki.shared.generated.resources.space_grotesk_variable

@Composable
fun bodyFontFamily() = FontFamily(
    Font(Res.font.inter_variable, FontWeight.W100),
    Font(Res.font.inter_variable, FontWeight.W200),
    Font(Res.font.inter_variable, FontWeight.W300),
    Font(Res.font.inter_variable, FontWeight.W400),
    Font(Res.font.inter_variable, FontWeight.W500),
    Font(Res.font.inter_variable, FontWeight.W600),
    Font(Res.font.inter_variable, FontWeight.W700),
    Font(Res.font.inter_variable, FontWeight.W800),
    Font(Res.font.inter_variable, FontWeight.W900),
)

@Composable
fun displayFontFamily() = FontFamily(
    Font(Res.font.space_grotesk_variable, FontWeight.W100),
    Font(Res.font.space_grotesk_variable, FontWeight.W200),
    Font(Res.font.space_grotesk_variable, FontWeight.W300),
    Font(Res.font.space_grotesk_variable, FontWeight.W400),
    Font(Res.font.space_grotesk_variable, FontWeight.W500),
    Font(Res.font.space_grotesk_variable, FontWeight.W600),
    Font(Res.font.space_grotesk_variable, FontWeight.W700),
    Font(Res.font.space_grotesk_variable, FontWeight.W800),
    Font(Res.font.space_grotesk_variable, FontWeight.W900),
)

// Default Material 3 typography values
val baseline = Typography()

@Composable
fun AppTypography() = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = displayFontFamily()),
    displayMedium = baseline.displayMedium.copy(fontFamily = displayFontFamily()),
    displaySmall = baseline.displaySmall.copy(fontFamily = displayFontFamily()),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = displayFontFamily()),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = displayFontFamily()),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = displayFontFamily()),
    titleLarge = baseline.titleLarge.copy(fontFamily = displayFontFamily()),
    titleMedium = baseline.titleMedium.copy(fontFamily = displayFontFamily()),
    titleSmall = baseline.titleSmall.copy(fontFamily = displayFontFamily()),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = bodyFontFamily()),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = bodyFontFamily()),
    bodySmall = baseline.bodySmall.copy(fontFamily = bodyFontFamily()),
    labelLarge = baseline.labelLarge.copy(fontFamily = bodyFontFamily()),
    labelMedium = baseline.labelMedium.copy(fontFamily = bodyFontFamily()),
    labelSmall = baseline.labelSmall.copy(fontFamily = bodyFontFamily()),
)

