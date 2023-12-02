package hu.kts.wtracker.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import hu.kts.wtracker.data.Period

private val colorsWork = lightColorScheme(
    primary = work_theme_light_primary,
    onPrimary = work_theme_light_onPrimary,
    primaryContainer = work_theme_light_primaryContainer,
    onPrimaryContainer = work_theme_light_onPrimaryContainer,
    secondary = work_theme_light_secondary,
    onSecondary = work_theme_light_onSecondary,
    secondaryContainer = work_theme_light_secondaryContainer,
    onSecondaryContainer = work_theme_light_onSecondaryContainer,
    tertiary = work_theme_light_tertiary,
    onTertiary = work_theme_light_onTertiary,
    tertiaryContainer = work_theme_light_tertiaryContainer,
    onTertiaryContainer = work_theme_light_onTertiaryContainer,
    error = work_theme_light_error,
    errorContainer = work_theme_light_errorContainer,
    onError = work_theme_light_onError,
    onErrorContainer = work_theme_light_onErrorContainer,
    background = Green,
    onBackground = Grey,
    surface = work_theme_light_surface,
    onSurface = work_theme_light_onSurface,
    surfaceVariant = work_theme_light_surfaceVariant,
    onSurfaceVariant = work_theme_light_onSurfaceVariant,
    outline = Grey,
    inverseOnSurface = work_theme_light_inverseOnSurface,
    inverseSurface = work_theme_light_inverseSurface,
    inversePrimary = work_theme_light_inversePrimary,
    surfaceTint = work_theme_light_surfaceTint,
    outlineVariant = work_theme_light_outlineVariant,
    scrim = work_theme_light_scrim,
)

private val colorsStopped = colorsWork.copy(
    background = work_theme_light_background,
    onBackground = work_theme_light_onBackground,
)

private val colorsRest = lightColorScheme(
    primary = rest_theme_light_primary,
    onPrimary = rest_theme_light_onPrimary,
    primaryContainer = rest_theme_light_primaryContainer,
    onPrimaryContainer = rest_theme_light_onPrimaryContainer,
    secondary = rest_theme_light_secondary,
    onSecondary = rest_theme_light_onSecondary,
    secondaryContainer = rest_theme_light_secondaryContainer,
    onSecondaryContainer = rest_theme_light_onSecondaryContainer,
    tertiary = rest_theme_light_tertiary,
    onTertiary = rest_theme_light_onTertiary,
    tertiaryContainer = rest_theme_light_tertiaryContainer,
    onTertiaryContainer = rest_theme_light_onTertiaryContainer,
    error = rest_theme_light_error,
    errorContainer = rest_theme_light_errorContainer,
    onError = rest_theme_light_onError,
    onErrorContainer = rest_theme_light_onErrorContainer,
    background = Red,
    onBackground = Grey,
    surface = rest_theme_light_surface,
    onSurface = rest_theme_light_onSurface,
    surfaceVariant = rest_theme_light_surfaceVariant,
    onSurfaceVariant = rest_theme_light_onSurfaceVariant,
    outline = Grey,
    inverseOnSurface = rest_theme_light_inverseOnSurface,
    inverseSurface = rest_theme_light_inverseSurface,
    inversePrimary = rest_theme_light_inversePrimary,
    surfaceTint = rest_theme_light_surfaceTint,
    outlineVariant = rest_theme_light_outlineVariant,
    scrim = rest_theme_light_scrim,
)

private fun Period.getColorScheme(): ColorScheme {
    return when (this) {
        Period.STOPPED -> colorsStopped
        Period.WORK -> colorsWork
        Period.REST -> colorsRest
    }
}

@Composable
fun WTrackerTheme(
    period: Period = Period.STOPPED,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = period.getColorScheme(),
        content = content
    )
}
