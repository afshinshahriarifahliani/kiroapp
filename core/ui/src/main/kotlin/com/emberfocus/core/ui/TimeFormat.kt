package com.emberfocus.core.ui

/** Formats a number of seconds as mm:ss (or h:mm:ss for long sessions). */
fun formatSeconds(totalSeconds: Int): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        "%d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%02d:%02d".format(minutes, seconds)
    }
}

fun habitColor(colorKey: String): androidx.compose.ui.graphics.Color = when (colorKey) {
    "ember" -> com.emberfocus.core.designsystem.theme.Ember500
    "clay" -> com.emberfocus.core.designsystem.theme.Clay500
    "amber" -> com.emberfocus.core.designsystem.theme.Amber500
    else -> com.emberfocus.core.designsystem.theme.Ember500
}

val habitColorKeys = listOf("ember", "clay", "amber")
val habitEmojis = listOf("🔥", "📚", "🏃", "💧", "🧘", "🌙", "✍️", "🎯", "🥗", "☀️")
