package com.emberfocus.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Warm "Ember" light scheme. Cozy sand backgrounds with ember/clay/amber accents.
 */
private val EmberLightColors = lightColorScheme(
    primary = Ember500,
    onPrimary = Color.White,
    primaryContainer = Ember100,
    onPrimaryContainer = Ember900,

    secondary = Clay500,
    onSecondary = Color.White,
    secondaryContainer = Clay100,
    onSecondaryContainer = Clay700,

    tertiary = Amber500,
    onTertiary = Color.White,
    tertiaryContainer = Amber200,
    onTertiaryContainer = Amber700,

    background = Sand50,
    onBackground = Charcoal900,
    surface = Sand50,
    onSurface = Charcoal900,
    surfaceVariant = Sand200,
    onSurfaceVariant = Warm600,
    surfaceContainer = Sand100,
    surfaceContainerHigh = Sand200,
    surfaceContainerHighest = Sand300,

    outline = Warm600,
    outlineVariant = Sand300,

    error = ErrorRed,
    onError = Color.White,
)

/**
 * Warm "Ember" dark scheme. Deep warm charcoal with glowing ember accents.
 */
private val EmberDarkColors = darkColorScheme(
    primary = Ember400,
    onPrimary = Charcoal950,
    primaryContainer = Ember700,
    onPrimaryContainer = Ember100,

    secondary = Clay300,
    onSecondary = Charcoal950,
    secondaryContainer = Clay600,
    onSecondaryContainer = Clay100,

    tertiary = Amber300,
    onTertiary = Charcoal950,
    tertiaryContainer = Amber700,
    onTertiaryContainer = Amber200,

    background = Charcoal900,
    onBackground = Sand100,
    surface = Charcoal900,
    onSurface = Sand100,
    surfaceVariant = Charcoal800,
    onSurfaceVariant = Sand300,
    surfaceContainer = Charcoal850,
    surfaceContainerHigh = Charcoal800,
    surfaceContainerHighest = Warm700,

    outline = Warm600,
    outlineVariant = Warm700,

    error = ErrorRedDark,
    onError = Charcoal950,
)

@Composable
fun EmberFocusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) EmberDarkColors else EmberLightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = EmberTypography,
        shapes = EmberShapes,
        content = content,
    )
}
