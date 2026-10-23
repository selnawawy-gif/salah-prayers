package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PrayerRecordEntity
import com.example.model.PrayerStatus
import com.example.model.PrayerType
import com.example.ui.SalahViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerHistoryScreen(
    viewModel: SalahViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.prayerStats.collectAsState()
    val records by viewModel.recentRecords.collectAsState()
    var selectedRecordForEdit by remember { mutableStateOf<PrayerRecordEntity?>(null) }

    LaunchedEffect(Unit) {
        viewModel.refreshStats()
    }

    Scaffold(
        modifier = modifier.testTag("prayer_history_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Prayer History & Tracker",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.refreshStats() },
                        modifier = Modifier.testTag("refresh_stats_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Stats",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats Row: Streak & Weekly Adherence
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Streak Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("streak_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🔥",
                                fontSize = 32.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${stats.currentStreakDays} Days",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Current Streak",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }

                    // Weekly Adherence Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("adherence_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "📈",
                                fontSize = 32.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${stats.weeklyAdherencePercent.toInt()}%",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = "7-Day Adherence",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            // Legend of Statuses
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Status Legend",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            LegendItem(label = "On Time", color = Color(PrayerStatus.PRAYED_ON_TIME.colorHex))
                            LegendItem(label = "Jama'ah", color = Color(PrayerStatus.PRAYED_JAMAAH.colorHex))
                            LegendItem(label = "Late/Qada", color = Color(PrayerStatus.PRAYED_LATE.colorHex))
                            LegendItem(label = "Missed", color = Color(PrayerStatus.MISSED.colorHex))
                            LegendItem(label = "Excused", color = Color(PrayerStatus.EXCUSED.colorHex))
                        }
                    }
                }
            }

            // History Logs Header
            item {
                Text(
                    text = "Prayer Records (${records.size} days logged)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (records.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No prayers logged yet. Log today's prayers from the Prayer screen!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            items(records) { record ->
                HistoryDayCard(
                    record = record,
                    onClick = { selectedRecordForEdit = record }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Edit Past Prayer Record Dialog
    selectedRecordForEdit?.let { record ->
        EditRecordDialog(
            record = record,
            onUpdatePrayer = { prayerType, newStatus ->
                viewModel.logPrayerStatus(record.date, prayerType, newStatus)
            },
            onDismiss = {
                selectedRecordForEdit = null
                viewModel.refreshStats()
            }
        )
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun HistoryDayCard(
    record: PrayerRecordEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("history_record_${record.date}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = record.date,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Day",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 5 Prayers Status Dots / Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PrayerStatusChip(name = "Fajr", statusName = record.fajrStatus)
                PrayerStatusChip(name = "Dhuhr", statusName = record.dhuhrStatus)
                PrayerStatusChip(name = "Asr", statusName = record.asrStatus)
                PrayerStatusChip(name = "Maghrib", statusName = record.maghribStatus)
                PrayerStatusChip(name = "Isha", statusName = record.ishaStatus)
            }
        }
    }
}

@Composable
fun PrayerStatusChip(name: String, statusName: String) {
    val status = try {
        PrayerStatus.valueOf(statusName)
    } catch (e: Exception) {
        PrayerStatus.NOT_LOGGED
    }
    val color = Color(status.colorHex)

    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = status.label,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = color,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun EditRecordDialog(
    record: PrayerRecordEntity,
    onUpdatePrayer: (PrayerType, PrayerStatus) -> Unit,
    onDismiss: () -> Unit
) {
    var currentPrayerToEdit by remember { mutableStateOf<PrayerType?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Records for ${record.date}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (currentPrayerToEdit == null) {
                    Text(
                        text = "Select a prayer to update its status:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    listOf(
                        PrayerType.FAJR to record.fajrStatus,
                        PrayerType.DHUHR to record.dhuhrStatus,
                        PrayerType.ASR to record.asrStatus,
                        PrayerType.MAGHRIB to record.maghribStatus,
                        PrayerType.ISHA to record.ishaStatus
                    ).forEach { (prayer, status) ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { currentPrayerToEdit = prayer },
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${prayer.displayName} (${prayer.arabicName})",
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = status.replace("_", " "),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                } else {
                    val p = currentPrayerToEdit!!
                    Text(
                        text = "Set status for ${p.displayName}:",
                        fontWeight = FontWeight.Bold
                    )
                    PrayerStatus.entries.forEach { st ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onUpdatePrayer(p, st)
                                    currentPrayerToEdit = null
                                }
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(st.colorHex).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${st.label} (${st.arabicLabel})",
                                color = Color(st.colorHex),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Done") }
        }
    )
}
