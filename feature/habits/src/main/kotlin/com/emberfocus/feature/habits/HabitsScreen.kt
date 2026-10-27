package com.emberfocus.feature.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emberfocus.core.designsystem.component.EmberCard
import com.emberfocus.core.designsystem.component.EmberPrimaryButton
import com.emberfocus.core.designsystem.component.EmptyState
import com.emberfocus.core.model.HabitWithProgress
import com.emberfocus.core.ui.habitColor
import com.emberfocus.core.ui.habitColorKeys
import com.emberfocus.core.ui.habitEmojis
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsScreen(
    modifier: Modifier = Modifier,
    viewModel: HabitsViewModel = koinViewModel(),
) {
    val habits by viewModel.habits.collectAsStateWithLifecycle()
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = { showSheet = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add habit")
            }
        },
    ) { padding ->
        if (habits.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                EmptyState(
                    icon = Icons.Filled.LocalFireDepartment,
                    title = "Start a streak",
                    subtitle = "Track a daily habit and keep the flame alive.",
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(habits, key = { it.habit.id }) { habit ->
                    HabitRow(
                        habit = habit,
                        onToggle = { viewModel.toggle(habit) },
                        onDelete = { viewModel.delete(habit.habit.id) },
                    )
                }
            }
        }
    }

    if (showSheet) {
        ModalBottomSheet(onDismissRequest = { showSheet = false }, sheetState = sheetState) {
            AddHabitSheet(
                onAdd = { name, emoji, colorKey ->
                    viewModel.addHabit(name, emoji, colorKey)
                    showSheet = false
                },
            )
        }
    }
}

@Composable
private fun HabitRow(
    habit: HabitWithProgress,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
) {
    EmberCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(habit.habit.emoji, style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.size(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(habit.habit.name, style = MaterialTheme.typography.titleMedium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.LocalFireDepartment,
                            contentDescription = null,
                            tint = habitColor(habit.habit.colorKey),
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            " ${habit.currentStreak} day streak · best ${habit.bestStreak}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                CompletionButton(
                    completed = habit.completedToday,
                    color = habitColor(habit.habit.colorKey),
                    onClick = onToggle,
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                habit.last7Days.forEach { done ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .background(
                                if (done) habitColor(habit.habit.colorKey)
                                else MaterialTheme.colorScheme.surfaceVariant,
                            ),
                    )
                }
            }
        }
    }
}

@Composable
private fun CompletionButton(completed: Boolean, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(if (completed) color else MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.Filled.Check,
            contentDescription = if (completed) "Completed" else "Mark complete",
            tint = if (completed) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun AddHabitSheet(onAdd: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf(habitEmojis.first()) }
    var colorKey by remember { mutableStateOf(habitColorKeys.first()) }

    Column(Modifier.fillMaxWidth().padding(24.dp)) {
        Text("New habit", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Habit name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))
        Text("Icon", style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            habitEmojis.forEach { e ->
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (e == emoji) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant,
                        )
                        .clickable { emoji = e },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(e, style = MaterialTheme.typography.titleLarge)
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("Color", style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            habitColorKeys.forEach { key ->
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(habitColor(key))
                        .clickable { colorKey = key },
                    contentAlignment = Alignment.Center,
                ) {
                    if (key == colorKey) {
                        Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White)
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        EmberPrimaryButton(
            text = "Add habit",
            onClick = { onAdd(name, emoji, colorKey) },
            enabled = name.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))
    }
}
