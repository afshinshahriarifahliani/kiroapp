package com.emberfocus.feature.timer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun TimerRoute(
    onRequestPro: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TimerViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    TimerScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onRequestPro = onRequestPro,
        modifier = modifier,
    )
}
