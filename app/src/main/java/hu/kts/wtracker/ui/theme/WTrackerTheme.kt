package hu.kts.wtracker.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import hu.kts.wtracker.ui.main.MainViewModel

private fun createColorSchemeForPeriod(period: MainViewModel.Period): ColorScheme {
    return lightColorScheme(
        background = period.getComposeColor(),
        outline = textColor,
    )
}

private fun MainViewModel.Period.getComposeColor(): Color {
    return when (this) {
        MainViewModel.Period.STOPPED -> White
        MainViewModel.Period.WORK -> Green
        MainViewModel.Period.REST -> Red
    }
}

@Composable
fun WTrackerTheme(
    period: MainViewModel.Period = MainViewModel.Period.STOPPED,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = createColorSchemeForPeriod(period),
        typography = WTrackerTypography,
        content = content
    )
}
