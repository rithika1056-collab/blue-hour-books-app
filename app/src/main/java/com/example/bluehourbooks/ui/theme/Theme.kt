package com.example.bluehourbooks.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val BlueHourDarkColorScheme = darkColorScheme(
    primary = Lavender500,
    onPrimary = Cream50,
    primaryContainer = Lavender700,
    onPrimaryContainer = Lavender100,
    secondary = Lavender300,
    onSecondary = Midnight950,
    secondaryContainer = Midnight700,
    onSecondaryContainer = Lavender200,
    tertiary = Gold400,
    onTertiary = Midnight950,
    tertiaryContainer = Gold600,
    onTertiaryContainer = Cream50,
    background = Midnight950,
    onBackground = Cream50,
    surface = Midnight900,
    onSurface = Cream50,
    surfaceVariant = Midnight800,
    onSurfaceVariant = Midnight100,
    outline = Midnight600,
    outlineVariant = Midnight700,
    error = Red400,
    onError = Midnight950
)

@Composable
fun BlueHourBooksTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = BlueHourDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Midnight950.toArgb()
            window.navigationBarColor = Midnight950.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
