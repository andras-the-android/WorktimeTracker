@file:OptIn(ExperimentalLayoutApi::class)

package hu.kts.wtracker.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import hu.kts.wtracker.R

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainScreen(
    state: MainViewModel.ViewState,
    onWorkClick: () -> Unit = {},
    onRestClick: () -> Unit = {},
    onStartButtonClick: () -> Unit = {},
    onFrequencyButtonClick: () -> Unit = {},
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FlowColumn {
            TimeSegment(
                label = stringResource(id = R.string.work),
                primary = state.workSegment,
                secondary = state.work,
                onClick = onWorkClick,
            )
            TimeSegment(
                label = stringResource(id = R.string.rest),
                primary = state.restSegment,
                secondary = state.rest,
                onClick = onRestClick,
            )
        }
        OutlinedButton(onClick = onStartButtonClick) {
            Text(state.buttonText)
        }
        OutlinedButton(onClick = onFrequencyButtonClick) {
            Text(stringResource(state.notificationFrequency.label))
        }
    }
}


@Composable
fun TimeSegment(
    label: String,
    primary: String,
    secondary: String,
    onClick: () -> Unit = {},
) {
    Column(Modifier.clickable(onClick = onClick)) {
        Text(
            label,
            style = MaterialTheme.typography.titleSmall,
        )
        Text(
            primary,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
        )
        Text(
            secondary,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.align(alignment = Alignment.End)
        )
    }
}


@Preview
@Composable
private fun PreviewMainScreen() {
    MaterialTheme {
        MainScreen(
            state = MainViewModel.ViewState(
                work = "0:00:00",
                rest = "0:00:00",
                workSegment = "0:00:00",
                restSegment = "0:00:00",
                buttonText = "Start",
                period = MainViewModel.Period.STOPPED,
                notificationFrequency = MainViewModel.NotificationFrequency.MIN1
            )
        )
    }
}

@Preview
@Composable
private fun PreviewTimeSegment() {
    MaterialTheme {
        TimeSegment(
            label = "Work",
            primary = "0:00:00",
            secondary = "0:00:00",
        )
    }
}
