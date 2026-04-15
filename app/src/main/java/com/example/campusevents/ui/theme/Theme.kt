package com.example.campusevents.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = GarnetPrimaryDark,
    secondary = GarnetSecondaryDark,
    tertiary = SandAccentDark,
    surface = Ink,
    background = Ink
)

private val LightColorScheme = lightColorScheme(
    primary = GarnetPrimary,
    secondary = GarnetSecondary,
    tertiary = SandAccent
)

@Composable
fun CampusEventsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
