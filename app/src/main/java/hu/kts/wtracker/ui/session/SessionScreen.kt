@file:OptIn(ExperimentalFoundationApi::class)

package hu.kts.wtracker.ui.session

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import hu.kts.wtracker.R
import hu.kts.wtracker.data.SessionViewItem
import hu.kts.wtracker.ui.theme.Grey
import hu.kts.wtracker.ui.theme.Red
import hu.kts.wtracker.ui.theme.WTrackerTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SessionScreen(sessionItems: List<SessionViewItem>) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .border(width = 1.dp, color = Grey, shape = MaterialTheme.shapes.small)
            .clip(shape = MaterialTheme.shapes.small),
    ) {
        LazyColumn {
            items(
                items = sessionItems,
                key = { it.key },
            ) {
                SessionItemView(Modifier.animateItemPlacement(), item = it)
            }
        }
    }
}

@Composable
private fun SessionItemView(modifier: Modifier = Modifier, item: SessionViewItem) {
    val height = item.calcHeight()
    val label = if (height >= DISPLAY_LABEL_MIN_HEIGHT) {
        stringResource(id = R.string.history_item_text, item.timestamp, item.durationMinutes)
    } else {
        ""
    }
    Text(
        text = label,
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(color = item.color)
            .wrapContentHeight(),
        color = Grey,
        textAlign = TextAlign.Center
    )
}

private fun SessionViewItem.calcHeight(): Dp {
    //0 duration is in fact 0 - 30s so we set the height to half of a minute to make it visible on the screen
    return ((durationMinutes * SECONDS_TO_DP_RATE).coerceAtLeast(MIN_ITEM_HEIGHT)).dp
}

private const val SECONDS_TO_DP_RATE = 2
private const val MIN_ITEM_HEIGHT = 4
private val DISPLAY_LABEL_MIN_HEIGHT = 30.dp

@Preview(showSystemUi = true)
@Composable
private fun PreviewSessionScreen() {
    WTrackerTheme {
        SessionScreen(
            sessionItems = listOf(
                SessionViewItem(1, "2023.12.05 8:00:23", 60, Red),
                SessionViewItem(2, "2023.12.05 9:00:23", 0, White),
                SessionViewItem(3, "2023.12.05 8:30:23", 15, Green),
                SessionViewItem(4, "2023.12.05 8:00:231", 60, Red),
                SessionViewItem(5, "2023.12.05 9:00:231", 5, White),
                SessionViewItem(6, "2023.12.05 8:30:231", 15, Green),
                SessionViewItem(7, "2023.12.05 8:00:232", 60, Red),
                SessionViewItem(8, "2023.12.05 9:00:232", 5, White),
                SessionViewItem(9, "2023.12.05 8:30:232", 15, Green),
                SessionViewItem(10, "2023.12.05 8:00:233", 60, Red),
                SessionViewItem(11, "2023.12.05 9:00:233", 5, White),
                SessionViewItem(12, "2023.12.05 8:30:233", 15, Green),
            )
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewEmptySessionScreen() {
    WTrackerTheme {
        SessionScreen(
            sessionItems = listOf()
        )
    }
}