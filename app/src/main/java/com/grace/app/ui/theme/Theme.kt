package com.grace.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = GraceBlue,
    onPrimary = Color.White,
    secondary = GracePink,
    onSecondary = Color.White,
    background = GraceBlue,
    onBackground = Color.White,
    surface = GraceBlue,
    onSurface = Color.White
)

@Composable
fun GraceTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content
    )
}
