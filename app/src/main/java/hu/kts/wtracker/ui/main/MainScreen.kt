@file:OptIn(ExperimentalLayoutApi::class)

package hu.kts.wtracker.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import hu.kts.wtracker.data.SummaryViewState
import hu.kts.wtracker.ui.summary.SummaryScreen
import hu.kts.wtracker.ui.summary.SummaryScreenDisplayMode

@Composable
fun MainScreen(
    state: SummaryViewState,
    onWorkClick: () -> Unit = {},
    onRestClick: () -> Unit = {},
    onStartButtonClick: () -> Unit = {},
    onSkipNotificationsButtonClick: () -> Unit,
    windowSizeClass: WindowSizeClass
) {
    val displayMode by remember(windowSizeClass) {
        val displayMode = when {
            windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact &&
             windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact -> SummaryScreenDisplayMode.Compact
            windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact -> SummaryScreenDisplayMode.Horizontal
            else -> SummaryScreenDisplayMode.Vertical
        }
        mutableStateOf(displayMode)
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            SummaryScreen(
                state = state,
                onWorkClick = onWorkClick,
                onRestClick = onRestClick,
                onStartButtonClick = onStartButtonClick,
                onSkipNotificationsButtonClick = onSkipNotificationsButtonClick,
                displayMode = displayMode,
            )
        }
    }
}


