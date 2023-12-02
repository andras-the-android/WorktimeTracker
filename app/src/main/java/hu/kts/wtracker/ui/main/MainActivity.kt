package hu.kts.wtracker.ui.main

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import hu.kts.wtracker.data.Period
import hu.kts.wtracker.data.SummaryViewState
import hu.kts.wtracker.ui.theme.WTrackerTheme

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val viewModel = ViewModelProvider(this)[MainViewModel::class.java]

            val viewState = viewModel.state.observeAsState()

            WTrackerTheme(viewState.value?.period ?: Period.STOPPED) {
                viewState.value?.let { state ->

                    keepScreenAwake(state.period.isRunning())

                    MainScreen(
                        state = state,
                        onWorkClick = viewModel::onWorkSegmentClick,
                        onRestClick = viewModel::onRestSegmentClick,
                        onStartButtonClick = viewModel::onStopResetButtonClicked,
                        onSkipNotificationsButtonClick = viewModel::onSkipNotificationsButtonClick,
                        windowSizeClass = calculateWindowSizeClass(this)
                    )

                    when (state.dialog) {
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
    }

    private fun keepScreenAwake(keep: Boolean) {
        if (keep) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}