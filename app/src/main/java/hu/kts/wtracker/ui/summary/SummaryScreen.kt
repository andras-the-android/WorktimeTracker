package hu.kts.wtracker.ui.summary

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.kts.wtracker.R
import hu.kts.wtracker.data.Period
import hu.kts.wtracker.data.SummaryViewState
import hu.kts.wtracker.ui.theme.Grey
import hu.kts.wtracker.ui.theme.WTrackerTheme

enum class SummaryScreenDisplayMode {
    Horizontal, Vertical, Compact
}

@Composable
fun SummaryScreen(
    state: SummaryViewState,
    onWorkClick: () -> Unit,
    onChoreClick: () -> Unit,
    onRestClick: () -> Unit,
    onStartButtonClick: () -> Unit,
    onSkipNotificationsButtonClick: () -> Unit,
    displayMode: SummaryScreenDisplayMode
) {
    if (displayMode == SummaryScreenDisplayMode.Vertical) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            EfficiencyText(state = state)
            TimeSegments(state, onWorkClick, onChoreClick, onRestClick, false)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Buttons(onStartButtonClick, state, onSkipNotificationsButtonClick)
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (displayMode != SummaryScreenDisplayMode.Compact) {
                EfficiencyText(state = state)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TimeSegments(
                    state,
                    onWorkClick,
                    onChoreClick,
                    onRestClick,
                    displayMode == SummaryScreenDisplayMode.Compact
                )
            }

            Row {
                Buttons(onStartButtonClick, state, onSkipNotificationsButtonClick)
            }
        }
    }
}

@Composable
fun EfficiencyText(
    state: SummaryViewState,
) {
    Text(
        text = stringResource(id = R.string.efficiency, state.efficiency),
        style = MaterialTheme.typography.headlineSmall
    )
}

@Composable
private fun Buttons(
    onStartButtonClick: () -> Unit,
    state: SummaryViewState,
    onSkipNotificationsButtonClick: () -> Unit,
) {
    val buttonColors = ButtonDefaults.outlinedButtonColors(contentColor = Grey)
    OutlinedButton(onClick = onStartButtonClick, colors = buttonColors) {
        Text(stringResource(if (state.period.isRunning()) R.string.stop else R.string.reset), fontSize = 20.sp)
    }
    Spacer(modifier = Modifier.size(16.dp))
    OutlinedButton(
        onClick = onSkipNotificationsButtonClick,
        colors = buttonColors,
        enabled = state.period == Period.REST
    ) {
        Text(state.skipNotificationTimeLeft ?: stringResource(R.string.skip_notifications_button), fontSize = 20.sp)
    }
}

@Composable
private fun TimeSegments(
    state: SummaryViewState,
    onWorkClick: () -> Unit,
    onChoreClick: () -> Unit,
    onRestClick: () -> Unit,
    compact: Boolean,
) {
    TimeSegment(
        label = stringResource(id = R.string.work),
        primary = state.workSegment,
        secondary = state.work,
        onClick = onWorkClick,
        compact = compact
    )
    TimeSegment(
        label = stringResource(id = R.string.chore),
        primary = state.choreSegment,
        secondary = state.chore,
        onClick = onChoreClick,
        compact = compact
    )
    TimeSegment(
        label = stringResource(id = R.string.rest),
        primary = state.restSegment,
        secondary = state.rest,
        onClick = onRestClick,
        compact = compact
    )
}

@Composable
private fun TimeSegment(
    label: String,
    primary: String,
    secondary: String,
    onClick: () -> Unit = {},
    compact: Boolean = false,
) {
    Column(Modifier.clickable(onClick = onClick)) {
        Text(
            label,
            style = if (compact) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.displaySmall,
        )
        Text(
            primary,
            style = if (compact) MaterialTheme.typography.headlineLarge else MaterialTheme.typography.displayLarge,
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
        )
        Text(
            secondary,
            style = if (compact) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.displaySmall,
            modifier = Modifier.align(alignment = Alignment.End)
        )
    }
}


@Preview(showSystemUi = true, device = "id:pixel_6")
@Composable
private fun PreviewSummaryScreenVertical() {
    PreviewSummaryScreen(SummaryScreenDisplayMode.Vertical)
}

@Preview(showSystemUi = true, device = "spec:parent=pixel_6,orientation=landscape")
@Composable
private fun PreviewSummaryScreenHorizontal() {
    PreviewSummaryScreen(SummaryScreenDisplayMode.Horizontal)
}

@Preview(showSystemUi = true, device = "spec:width=1080px,height=600px")
@Composable
private fun PreviewSummaryScreenCompact() {
    PreviewSummaryScreen(SummaryScreenDisplayMode.Compact)
}

@Composable
private fun PreviewSummaryScreen(displayMode: SummaryScreenDisplayMode) {
    WTrackerTheme(period = Period.STOPPED) {
        SummaryScreen(
            state = SummaryViewState(
                work = "0:00:00",
                chore = "0:00:00",
                rest = "0:00:00",
                workSegment = "0:00:00",
                choreSegment = "0:00:00",
                restSegment = "0:00:00",
                period = Period.STOPPED,
                dialog = null,
                skipNotificationTimeLeft = null,
                efficiency = 95,
            ),
            displayMode = displayMode,
            onStartButtonClick = {},
            onRestClick = {},
            onChoreClick = {},
            onWorkClick = {},
            onSkipNotificationsButtonClick = {},
        )
    }
}

@Preview
@Composable
private fun PreviewTimeSegment(@PreviewParameter(TimeSegmentProvider::class) compact: Boolean) {
    WTrackerTheme(period = Period.STOPPED) {
        TimeSegment(
            label = "Work",
            primary = "0:00:00",
            secondary = "0:00:00",
            compact = compact
        )
    }
}

class TimeSegmentProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(false, true)
}
