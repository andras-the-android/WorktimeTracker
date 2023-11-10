package hu.kts.wtracker.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun TimeSegment(
    label: String,
    primary: String,
    secondary: String,
    onClick: () -> Unit = {}
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
fun Preview() {
    MaterialTheme {
        TimeSegment(
            label = "Work",
            primary = "0:00:00",
            secondary = "0:00:00",
        )
    }
}
