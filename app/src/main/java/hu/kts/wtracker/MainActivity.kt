package hu.kts.wtracker

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import hu.kts.wtracker.ui.main.ConfirmResetDialog
import hu.kts.wtracker.ui.main.MainScreen
import hu.kts.wtracker.ui.main.MainViewModel
import hu.kts.wtracker.ui.main.SkipNotificationsDialog
import hu.kts.wtracker.ui.theme.WTrackerTheme

class MainActivity : AppCompatActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val viewModel = ViewModelProvider(this)[MainViewModel::class.java]

            val viewState = viewModel.state.observeAsState()

            WTrackerTheme(viewState.value?.period ?: MainViewModel.Period.STOPPED) {
                viewState.value?.let { state ->
                    MainScreen(
                        state = state,
                        onWorkClick = viewModel::onWorkSegmentClick,
                        onRestClick = viewModel::onRestSegmentClick,
                        onStartButtonClick = viewModel::onStopResetButtonClicked,
                        onFrequencyButtonClick = viewModel::onNotificationFrequencyButtonClicked,
                        onSkipNotificationsButtonClick = viewModel::onSkipNotificationsButtonClick,
                        windowSizeClass = calculateWindowSizeClass(this)
                    )

                    when (state.dialog) {
                        MainViewModel.DialogType.Reset -> {
                            ConfirmResetDialog(
                                onConfirm = viewModel::confirmReset,
                                onDismiss = viewModel::cancelDialog,
                            )
                        }
                        MainViewModel.DialogType.SkipNotifications -> {
                            SkipNotificationsDialog(onSelect = viewModel::skipNotificationFor, onDismiss = viewModel::cancelDialog)
                        }
                        null -> {}
                    }
                }
            }
        }
    }
}
