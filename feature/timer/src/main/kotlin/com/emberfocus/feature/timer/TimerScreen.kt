package com.emberfocus.feature.timer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.emberfocus.core.model.Soundscape
import com.emberfocus.core.model.TimerPhase
import com.emberfocus.core.model.TimerStatus
import com.emberfocus.core.ui.formatSeconds

@Composable
fun TimerScreen(
    state: TimerUiState,
    onEvent: (TimerEvent) -> Unit,
    onRequestPro: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = state.progress,
        animationSpec = tween(400),
        label = "progress",
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Spacer(Modifier.height(8.dp))

        PresetRow(state = state, onEvent = onEvent)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentAlignment = Alignment.Center,
        ) {
            TimerRing(progress = animatedProgress)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = state.phase.label(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = formatSeconds(state.remainingSeconds),
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "Session ${state.completedFocusSessions + 1}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Controls(status = state.status, onEvent = onEvent)

        Spacer(Modifier.height(4.dp))

        SoundscapeRow(
            selected = state.soundscape,
            isPro = state.isPro,
            onSelect = { onEvent(TimerEvent.SelectSoundscape(it)) },
            onRequestPro = onRequestPro,
        )
    }
}

@Composable
private fun TimerRing(progress: Float) {
    val track = MaterialTheme.colorScheme.surfaceVariant
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary
    androidx.compose.foundation.Canvas(modifier = Modifier.size(260.dp)) {
        val stroke = 22.dp.toPx()
        val inset = stroke / 2
        val arcSize = Size(size.width - stroke, size.height - stroke)
        drawArc(
            color = track,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(inset, inset),
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Round),
        )
        drawArc(
            brush = Brush.sweepGradient(listOf(tertiary, primary, tertiary)),
            startAngle = -90f,
            sweepAngle = 360f * progress.coerceIn(0f, 1f),
            useCenter = false,
            topLeft = Offset(inset, inset),
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Round),
        )
    }
}

@Composable
private fun Controls(status: TimerStatus, onEvent: (TimerEvent) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FilledTonalIconButton(
            onClick = { onEvent(TimerEvent.Reset) },
            modifier = Modifier.size(56.dp),
        ) {
            Icon(Icons.Filled.Refresh, contentDescription = "Reset")
        }
        FilledIconButton(
            onClick = { onEvent(TimerEvent.StartPause) },
            modifier = Modifier.size(80.dp),
        ) {
            Icon(
                imageVector = if (status == TimerStatus.RUNNING) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (status == TimerStatus.RUNNING) "Pause" else "Start",
                modifier = Modifier.size(36.dp),
            )
        }
        FilledTonalIconButton(
            onClick = { onEvent(TimerEvent.Skip) },
            modifier = Modifier.size(56.dp),
        ) {
            Icon(Icons.Filled.SkipNext, contentDescription = "Skip")
        }
    }
}

@Composable
private fun PresetRow(state: TimerUiState, onEvent: (TimerEvent) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        state.availablePresets.forEach { preset ->
            FilterChip(
                selected = preset.id == state.preset.id,
                onClick = { onEvent(TimerEvent.SelectPreset(preset)) },
                label = { Text("${preset.name} · ${preset.focusMinutes}m") },
            )
        }
    }
}

@Composable
private fun SoundscapeRow(
    selected: Soundscape,
    isPro: Boolean,
    onSelect: (Soundscape) -> Unit,
    onRequestPro: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Soundscape.entries.forEach { sound ->
            val locked = sound.isPro && !isPro
            AssistChip(
                onClick = {
                    if (locked) onRequestPro() else onSelect(sound)
                },
                label = { Text(sound.displayName) },
                leadingIcon = if (locked) {
                    { Icon(Icons.Filled.Lock, contentDescription = "Pro", modifier = Modifier.size(16.dp)) }
                } else null,
                colors = if (sound == selected) {
                    AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                    )
                } else {
                    AssistChipDefaults.assistChipColors()
                },
            )
        }
    }
}

private fun TimerPhase.label(): String = when (this) {
    TimerPhase.FOCUS -> "FOCUS"
    TimerPhase.SHORT_BREAK -> "SHORT BREAK"
    TimerPhase.LONG_BREAK -> "LONG BREAK"
}
