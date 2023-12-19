package hu.kts.wtracker.ui.session

import androidx.compose.ui.graphics.Color.Companion.White
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kts.wtracker.data.Period
import hu.kts.wtracker.data.SessionViewItem
import hu.kts.wtracker.repository.SessionRepository
import hu.kts.wtracker.ui.theme.Chore
import hu.kts.wtracker.ui.theme.Green
import hu.kts.wtracker.ui.theme.Red
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    repository: SessionRepository,
    locale: Locale,
    coroutineScope: CoroutineScope
): ViewModel() {

    val items = repository.sessionItems
        .map {sessionItems ->
            sessionItems.map { sessionItem ->
                SessionViewItem(
                    key = sessionItem.timestamp,
                    timestamp = dateFormat.format(Date(sessionItem.timestamp)),
                    durationMinutes = TimeUnit.SECONDS.toMinutes(sessionItem.durationSeconds.toLong()).toInt(),
                    color = sessionItem.period.color(),
                )
            }
        }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    private fun Period.color() = when (this) {
        Period.STOPPED -> White
        Period.CHORE -> Chore
        Period.WORK -> Green
        Period.REST -> Red
    }

    private val dateFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale)
}
