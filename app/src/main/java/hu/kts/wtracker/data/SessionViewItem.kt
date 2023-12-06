package hu.kts.wtracker.data

import androidx.compose.ui.graphics.Color

data class SessionViewItem(
    val timestamp: String,
    val durationMinutes: Int,
    val color: Color,
)
