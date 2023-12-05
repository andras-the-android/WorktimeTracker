package hu.kts.wtracker.ui.main

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import hu.kts.wtracker.data.SummaryViewState
import hu.kts.wtracker.ui.theme.WTrackerTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //WindowCompat.setDecorFitsSystemWindows(window, false)

        val viewModel: MainViewModel by viewModels()

        enableEdgeToEdge()

        setContent {

            val viewState: SummaryViewState by viewModel.state.collectAsStateWithLifecycle()

            WTrackerTheme(viewState.period) {
                keepScreenAwake(viewState.period.isRunning())

                MainScreen(
                    state = viewState,
                    onWorkClick = viewModel::onWorkSegmentClick,
                    onRestClick = viewModel::onRestSegmentClick,
                    onStartButtonClick = viewModel::onStopResetButtonClicked,
                    onSkipNotificationsButtonClick = viewModel::onSkipNotificationsButtonClick,
                    windowSizeClass = calculateWindowSizeClass(this)
                )

                when (viewState.dialog) {
                    SummaryViewState.DialogType.Reset -> {
                        ConfirmResetDialog(
                            onConfirm = viewModel::confirmReset,
                            onDismiss = viewModel::cancelDialog,
                        )
                    }
                    SummaryViewState.DialogType.SkipNotifications -> {
                        SkipNotificationsDialog(onSelect = viewModel::skipNotificationFor, onDismiss = viewModel::cancelDialog)
                    }
                    null -> {}
                }
            }
        }
    }

    private fun keepScreenAwake(keep: Boolean) {
        if (keep) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}
