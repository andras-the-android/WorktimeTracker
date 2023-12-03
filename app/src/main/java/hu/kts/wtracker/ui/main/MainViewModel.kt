package hu.kts.wtracker.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kts.wtracker.Timer
import hu.kts.wtracker.data.Period
import hu.kts.wtracker.data.SummaryData
import hu.kts.wtracker.data.SummaryViewState
import hu.kts.wtracker.persistency.DataBase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val timer: Timer,
    private val dataBase: DataBase,
    private val notifications: Notifications,
) : ViewModel() {

    private val summaryData = MutableStateFlow(SummaryData.empty)
    private val dialog = MutableStateFlow<SummaryViewState.DialogType?>(null)

    private val period
        get() = summaryData.value.period

    val state: StateFlow<SummaryViewState> =
        combine(
            summaryData,
            dialog,
        ) { summaryData, dialogType ->
            return@combine SummaryViewState(
                summaryData.workSec.hMmSsFormat(),
                summaryData.restSec.hMmSsFormat(),
                summaryData.workSegmentSec.hMmSsFormat(),
                summaryData.restSegmentSec.hMmSsFormat(),
                period,
                dialogType,
                notifications.calcSkipDisplayText(),
                summaryData.calcEfficiency(),
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SummaryViewState.empty
        )

    init {
        restoreState()
        viewModelScope.launch {
            timer.tickFlow.collect {
                onTimerTick()
            }
        }
    }

    fun onStopResetButtonClicked() {
        if (period.isRunning()) {
            timer.stop()
            summaryData.update { it.copy(period = Period.STOPPED) }
            dataBase.addToHistory(period)
            notifications.resetSkip()
        } else {
            dialog.value = SummaryViewState.DialogType.Reset
        }
    }

    fun confirmReset() {
        assert(!period.isRunning())
        summaryData.value = SummaryData.empty
        dataBase.clearHistory()
        notifications.resetSkip()
        cancelDialog()
    }

    fun onSkipNotificationsButtonClick() {
        dialog.value = SummaryViewState.DialogType.SkipNotifications
    }

    fun skipNotificationFor(minutes: Int) {
        notifications.skipFor(minutes)
        cancelDialog()
    }

    fun cancelDialog() {
        dialog.value = null
    }

    fun onWorkSegmentClick() {
        onTimeSegmentClick(Period.WORK)
    }

    fun onRestSegmentClick() {
        onTimeSegmentClick(Period.REST)
    }

    private fun onTimeSegmentClick(initialPeriod: Period) {
        // if it's already running, switch to the other one regardless which timer was clicked
        if (period.isRunning()) {
            if (period == Period.WORK) {
                summaryData.update { it.copy(period = Period.REST, restSegmentSec = 0) }
            } else {
                summaryData.update { it.copy(period = Period.WORK, workSegmentSec = 0) }
                notifications.resetSkip()
            }
        // set the initial value if it's not running
        } else {
            if (initialPeriod == Period.WORK) {
                summaryData.update { it.copy(period = initialPeriod, workSegmentSec = 0) }
            } else {
                summaryData.update { it.copy(period = initialPeriod, restSegmentSec = 0) }
            }
            timer.start()
            summaryData.update { it.copy(period = initialPeriod) }
        }

        dataBase.addToHistory(period)
    }

    private fun onTimerTick() {
        if (period == Period.WORK) {
            summaryData.update { it.copy(workSec = it.workSec.inc(), workSegmentSec = it.workSegmentSec.inc()) }
        } else {
            summaryData.update { it.copy(restSec = it.restSec.inc(), restSegmentSec = it.restSegmentSec.inc()) }
        }
        notifications.trigger(summaryData.value)
    }

    private fun restoreState() {
        summaryData.value = dataBase.restore()

        if (period.isRunning()) {
            timer.start()
        }
    }
}
