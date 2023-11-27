package hu.kts.wtracker.ui.main

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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
