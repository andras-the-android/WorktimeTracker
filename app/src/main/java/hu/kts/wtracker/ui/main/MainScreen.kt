@file:OptIn(ExperimentalLayoutApi::class)

package hu.kts.wtracker.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hu.kts.wtracker.R

@Composable
fun MainScreen(
    state: MainViewModel.ViewState,
    onWorkClick: () -> Unit = {},
    onRestClick: () -> Unit = {},
    onStartButtonClick: () -> Unit = {},
    onFrequencyButtonClick: () -> Unit = {},
) {
    Scaffold { paddingValues ->
        Column(
            //horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            TimeSegments(state, onWorkClick, onRestClick)

            Column {
                Buttons(onStartButtonClick, state, onFrequencyButtonClick)
            }
        }
    }

}

@Composable
private fun Buttons(
    onStartButtonClick: () -> Unit,
    state: MainViewModel.ViewState,
    onFrequencyButtonClick: () -> Unit
) {
    OutlinedButton(onClick = onStartButtonClick) {
        Text(state.buttonText)
    }
    OutlinedButton(onClick = onFrequencyButtonClick) {
        Text(stringResource(state.notificationFrequency.label))
    }
}

@Composable
private fun TimeSegments(state: MainViewModel.ViewState, onWorkClick: () -> Unit, onRestClick: () -> Unit) {
    TimeSegment(
        label = stringResource(id = R.string.work),
        primary = state.workSegment,
        secondary = state.work,
        onClick = onWorkClick,
    )
    Spacer(modifier = Modifier.size(32.dp))
    TimeSegment(
        label = stringResource(id = R.string.rest),
        primary = state.restSegment,
        secondary = state.rest,
        onClick = onRestClick,
    )
}


@Composable
private fun TimeSegment(
    label: String,
    primary: String,
    secondary: String,
    onClick: () -> Unit = {},
) {
    Column(Modifier.clickable(onClick = onClick)) {
        Text(
            label,
            style = MaterialTheme.typography.displaySmall,
        )
        Text(
            primary,
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
        )
        Text(
            secondary,
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.align(alignment = Alignment.End)
        )
    }
}


@Preview(showSystemUi = true)
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
