package hu.kts.wtracker.ui.main

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import hu.kts.wtracker.data.SessionViewItem
import hu.kts.wtracker.data.SummaryViewState
import hu.kts.wtracker.ui.session.SessionViewModel
import hu.kts.wtracker.ui.summary.SummaryScreenDisplayMode
import hu.kts.wtracker.ui.theme.WTrackerTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: MainViewModel by viewModels()
        val sessionViewModel: SessionViewModel by viewModels()

        enableEdgeToEdge()

        setContent {

            val viewState: SummaryViewState by viewModel.state.collectAsStateWithLifecycle()
            val sessionItems: List<SessionViewItem> by sessionViewModel.items.collectAsStateWithLifecycle()

            val windowSizeClass = calculateWindowSizeClass(this)
            val summaryDisplayMode = when {
                windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact &&
                        windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact -> SummaryScreenDisplayMode.Compact
                windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact -> SummaryScreenDisplayMode.Horizontal
                else -> SummaryScreenDisplayMode.Vertical
            }

            val usePager = windowSizeClass.widthSizeClass < WindowWidthSizeClass.Expanded
                    || windowSizeClass.heightSizeClass < WindowHeightSizeClass.Medium

            WTrackerTheme(viewState.period) {
                keepScreenAwake(viewState.period.isRunning())

                MainScreen(
                    summaryState = viewState,
                    sessionItems = sessionItems,
                    onWorkClick = viewModel::onWorkSegmentClick,
                    onRestClick = viewModel::onRestSegmentClick,
                    onStartButtonClick = viewModel::onStopResetButtonClicked,
                    onSkipNotificationsButtonClick = viewModel::onSkipNotificationsButtonClick,
                    summaryDisplayMode = summaryDisplayMode,
                    usePager = usePager
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
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
