@file:OptIn(ExperimentalLayoutApi::class)

package hu.kts.wtracker.ui.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import hu.kts.wtracker.data.Period
import hu.kts.wtracker.data.SessionViewItem
import hu.kts.wtracker.data.SummaryViewState
import hu.kts.wtracker.ui.session.SessionScreen
import hu.kts.wtracker.ui.summary.SummaryScreen
import hu.kts.wtracker.ui.summary.SummaryScreenDisplayMode
import hu.kts.wtracker.ui.theme.Red
import hu.kts.wtracker.ui.theme.WTrackerTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    summaryState: SummaryViewState,
    sessionItems: List<SessionViewItem>,
    onWorkClick: () -> Unit = {},
    onRestClick: () -> Unit = {},
    onStartButtonClick: () -> Unit = {},
    onSkipNotificationsButtonClick: () -> Unit,
    summaryDisplayMode: SummaryScreenDisplayMode,
    usePager: Boolean
) {

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            val summaryScreen = @Composable {
                SummaryScreen(
                    state = summaryState,
                    onWorkClick = onWorkClick,
                    onRestClick = onRestClick,
                    onStartButtonClick = onStartButtonClick,
                    onSkipNotificationsButtonClick = onSkipNotificationsButtonClick,
                    displayMode = summaryDisplayMode,
                )
            }

            if (usePager) {
                val pagerState = rememberPagerState(pageCount = { 10 })

                HorizontalPager(state = pagerState) { page ->
                    when (page) {
                        1 -> {
                            SessionScreen(sessionItems)
                        }
                        else -> {
                            summaryScreen()
                        }
                    }
                }
            } else {
                Row {
                    Box(Modifier.weight(1f)) {
                        summaryScreen()
                    }
                    Box(Modifier.weight(1f)) {
                        SessionScreen(sessionItems)
                    }
                }
            }
        }
    }
}

@Preview(device = "id:pixel_6a")
@Composable
fun PreviewMainScreenPhone() {
    WTrackerTheme {
        MainScreen(
            summaryState = previewSummaryState,
            sessionItems = previewSessionItems,
            onSkipNotificationsButtonClick = { },
            summaryDisplayMode = SummaryScreenDisplayMode.Vertical,
            usePager = true
        )
    }
}

@Preview(device = "spec:parent=pixel_6a,orientation=landscape")
@Composable
fun PreviewMainScreenPhoneLandscape() {
    WTrackerTheme {
        MainScreen(
            summaryState = previewSummaryState,
            sessionItems = previewSessionItems,
            onSkipNotificationsButtonClick = { },
            summaryDisplayMode = SummaryScreenDisplayMode.Horizontal,
            usePager = true
        )
    }
}

@Preview(device = "spec:parent=Nexus 7,orientation=landscape")
@Composable
fun PreviewMainScreenTabletLandscape() {
    WTrackerTheme {
        MainScreen(
            summaryState = previewSummaryState,
            sessionItems = previewSessionItems,
            onSkipNotificationsButtonClick = { },
            summaryDisplayMode = SummaryScreenDisplayMode.Vertical,
            usePager = false
        )
    }
}

private val previewSummaryState = SummaryViewState(
    work = "0:00:00",
    rest = "0:00:00",
    workSegment = "0:00:00",
    restSegment = "0:00:00",
    period = Period.STOPPED,
    dialog = null,
    skipNotificationTimeLeft = null,
    efficiency = 95,
)

private val previewSessionItems = listOf(
    SessionViewItem(1, "2023.12.05 8:00:23", 60, Red),
    SessionViewItem(2, "2023.12.05 9:00:23", 5, Color.White),
    SessionViewItem(3, "2023.12.05 8:30:23", 15, Color.Green),
    SessionViewItem(4, "2023.12.05 8:00:231", 60, Red),
    SessionViewItem(5, "2023.12.05 9:00:231", 5, Color.White),
    SessionViewItem(6, "2023.12.05 8:30:231", 15, Color.Green),
    SessionViewItem(7, "2023.12.05 8:00:232", 60, Red),
    SessionViewItem(8, "2023.12.05 9:00:232", 5, Color.White),
    SessionViewItem(9, "2023.12.05 8:30:232", 15, Color.Green),
    SessionViewItem(10, "2023.12.05 8:00:233", 60, Red),
    SessionViewItem(11, "2023.12.05 9:00:233", 5, Color.White),
    SessionViewItem(12, "2023.12.05 8:30:233", 15, Color.Green),
)




