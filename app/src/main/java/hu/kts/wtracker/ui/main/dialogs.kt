package hu.kts.wtracker.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import hu.kts.wtracker.R
import hu.kts.wtracker.ui.theme.WTrackerTheme

@Composable
fun ConfirmResetDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        title = {
            Text(stringResource(id = R.string.reset_confirm_dialog_title))
        },
        text = {
            Text(stringResource(id = R.string.reset_confirm_dialog_text))
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(id = R.string.reset))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        },
        onDismissRequest = onDismiss,
    )
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewConfirmResetDialog() {
    WTrackerTheme {
        ConfirmResetDialog(onConfirm = {}, onDismiss = {})
    }
}


@Composable
fun SkipNotificationsDialog(
    onSelect: (minutes: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            shape = MaterialTheme.shapes.extraLarge,
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
            ) {
                var selectedOption by rememberSaveable {
                    mutableIntStateOf(-1)
                }

                Text(
                    modifier = Modifier.padding(bottom = 16.dp),
                    text = stringResource(id = R.string.skip_notifications_dialog_title),
                    style = MaterialTheme.typography.headlineSmall
                )

                OptionRow(minutes = 5, selectedOption = selectedOption, onSelect = { selectedOption = it })
                OptionRow(minutes = 15, selectedOption = selectedOption, onSelect = { selectedOption = it })
                OptionRow(minutes = 30, selectedOption = selectedOption, onSelect = { selectedOption = it })
                OptionRow(minutes = 60, selectedOption = selectedOption, onSelect = { selectedOption = it })

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(id = R.string.cancel))
                    }
                    TextButton(onClick = { onSelect(selectedOption) }, enabled = selectedOption > 0) {
                        Text(stringResource(id = R.string.ok))
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewMuteNotificationsDialog() {
    WTrackerTheme {
        SkipNotificationsDialog({}, {})
    }
}

@Composable
private fun OptionRow(minutes: Int, selectedOption: Int, onSelect: (minutes: Int) -> Unit) {
    Row(
        modifier = Modifier
            .selectable(
                selected = minutes == selectedOption,
                onClick = { onSelect(minutes) }
            )
            .fillMaxWidth()
        ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = minutes == selectedOption, onClick = { onSelect(minutes) })
        Text(text = stringResource(id = R.string.minutes, minutes))
    }
}

@Preview()
@Composable
private fun PreviewOptionRow() {
    WTrackerTheme {
        OptionRow(5, 5) {}
    }
}
