package hu.kts.wtracker.ui.session

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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

@Composable
fun SessionScreen(sessionItems: List<SessionViewItem>) {
    Surface(
        modifier = Modifier
            .wrapContentHeight()
            .padding(16.dp)
            .border(width = 1.dp, color = Grey, shape = MaterialTheme.shapes.small)
            .clip(shape = MaterialTheme.shapes.small),
    ) {
        LazyColumn {
            items(
                items = sessionItems,
                key = { it.timestamp },
            ) {
                SessionItemView(item = it)
            }
        }
    }
}

@Composable
private fun SessionItemView(item: SessionViewItem) {
    Text(
        text = stringResource(id = R.string.history_item_text,  item.timestamp, item.durationMinutes),
        modifier = Modifier
            .fillMaxWidth()
            .height(item.calcHeight())
            .background(color = item.color)
            .wrapContentHeight(),
        color = Grey,
        textAlign = TextAlign.Center
    )

}
@Preview(showSystemUi = true)
@Composable
private fun PreviewSessionScreen() {
    WTrackerTheme {
        SessionScreen(
            sessionItems = listOf(
                SessionViewItem("2023.12.05 8:00:23", 60, Red),
                SessionViewItem("2023.12.05 9:00:23", 5, White),
                SessionViewItem("2023.12.05 8:30:23", 15, Green),
                SessionViewItem("2023.12.05 8:00:231", 60, Red),
                SessionViewItem("2023.12.05 9:00:231", 5, White),
                SessionViewItem("2023.12.05 8:30:231", 15, Green),
                SessionViewItem("2023.12.05 8:00:232", 60, Red),
                SessionViewItem("2023.12.05 9:00:232", 5, White),
                SessionViewItem("2023.12.05 8:30:232", 15, Green),
                SessionViewItem("2023.12.05 8:00:233", 60, Red),
                SessionViewItem("2023.12.05 9:00:233", 5, White),
                SessionViewItem("2023.12.05 8:30:233", 15, Green),
            )
        )
    }
}

private fun SessionViewItem.calcHeight(): Dp {
    //0 duration is in fact 0 - 30s so we set the height to half of a minute to make it visible on the screen
    return ((durationMinutes * SECONDS_TO_DP_RATE).coerceAtLeast(2)).dp
}

private const val SECONDS_TO_DP_RATE = 4