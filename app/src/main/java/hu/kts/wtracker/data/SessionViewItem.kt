package hu.kts.wtracker.data

import androidx.compose.ui.graphics.Color

data class SessionViewItem(
    val key: Long,
    val timestamp: String,
    val durationMinutes: Int,
    val color: Color,
)
