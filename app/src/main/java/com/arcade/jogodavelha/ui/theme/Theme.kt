package com.arcade.jogodavelha.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = ArcadeGold,
    secondary = ArcadeO,
    tertiary = ArcadeX,
    background = ArcadeBackground,
    surface = ArcadeSurface,
    onPrimary = ArcadeBackground,
    onSecondary = ArcadeBackground,
    onBackground = ArcadeText,
    onSurface = ArcadeText
)

@Composable
fun JogoDaVelhaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
