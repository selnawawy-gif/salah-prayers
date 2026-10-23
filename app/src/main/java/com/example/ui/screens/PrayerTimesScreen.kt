package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PresetCities
import com.example.model.*
import com.example.ui.SalahScreen
import com.example.ui.SalahViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerTimesScreen(
    viewModel: SalahViewModel,
    modifier: Modifier = Modifier
) {
    val schedule by viewModel.schedule.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val syncMessage by viewModel.syncMessage.collectAsState()
    val city by viewModel.prefs.userCity.collectAsState()
    val autoSilent by viewModel.prefs.autoSilentEnabled.collectAsState()
    val isAutoLocation by viewModel.prefs.isAutoLocation.collectAsState()
    val isDetectingRegion by viewModel.isDetectingRegion.collectAsState()

    var showCityDialog by remember { mutableStateOf(false) }
    var selectedPrayerForStatusDialog by remember { mutableStateOf<PrayerTimeItem?>(null) }

    Scaffold(
        modifier = modifier.testTag("prayer_times_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showCityDialog = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("city_selector_button")
                    ) {
                        Icon(
                            imageVector = if (isAutoLocation) Icons.Default.MyLocation else Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = city.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                if (isAutoLocation) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Surface(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "Auto",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            Text(
                                text = city.country,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select City"
                        )
                    }
                },
                actions = {
                    // Update Now / Sync Button
                    IconButton(
                        onClick = { viewModel.performManualSync() },
                        enabled = !isSyncing,
                        modifier = Modifier.testTag("sync_button")
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Update & Sync Now",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Quick Theme Mode Toggle
                    val currentThemeMode by viewModel.prefs.themeMode.collectAsState()
                    val themeToggleIcon = when (currentThemeMode) {
                        com.example.model.AppThemeMode.SYSTEM -> Icons.Default.BrightnessAuto
                        com.example.model.AppThemeMode.LIGHT -> Icons.Default.LightMode
                        com.example.model.AppThemeMode.DARK -> Icons.Default.DarkMode
                    }
                    IconButton(
                        onClick = { viewModel.cycleThemeMode() },
                        modifier = Modifier.testTag("quick_theme_toggle_button")
                    ) {
                        Icon(
                            imageVector = themeToggleIcon,
                            contentDescription = "Theme: ${currentThemeMode.fallbackTitle}. Tap to toggle.",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Qibla Shortcut
                    IconButton(
                        onClick = { viewModel.selectScreen(SalahScreen.QIBLA) },
                        modifier = Modifier.testTag("qibla_shortcut_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = "Qibla Compass",
                            tint = MaterialTheme.colorScheme.secondary
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
            // Sync message banner
            if (syncMessage != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sync_status_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = syncMessage ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            // Top Hero Card: Hijri Date & Next Prayer Spotlight
            item {
                schedule?.let { sched ->
                    HeroNextPrayerCard(
                        schedule = sched,
                        autoSilentActive = autoSilent,
                        onOpenSettings = { viewModel.selectScreen(SalahScreen.SETTINGS) }
                    )
                }
            }

            // Daily Progress Bar
            item {
                schedule?.let { sched ->
                    DailySalahProgressCard(progress = sched.progressToday)
                }
            }

            // Prayer Timeline Items Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's Prayers",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tap prayer to log status",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Prayer Timeline List
            schedule?.let { sched ->
                items(sched.items) { item ->
                    PrayerTimeCard(
                        item = item,
                        onClick = {
                            if (item.type != PrayerType.SUNRISE) {
                                selectedPrayerForStatusDialog = item
                            }
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // City Selection Dialog
    if (showCityDialog) {
        CitySelectionDialog(
            currentCity = city,
            isDetecting = isDetectingRegion,
            onAutoDetect = { viewModel.autoDetectRegion(forceNotification = true) },
            onCitySelected = { selected ->
                viewModel.selectCity(selected)
                showCityDialog = false
            },
            onDismiss = { showCityDialog = false }
        )
    }

    // Prayer Status Selection Dialog
    selectedPrayerForStatusDialog?.let { prayerItem ->
        PrayerStatusDialog(
            prayerItem = prayerItem,
            onStatusSelected = { status ->
                schedule?.let { sched ->
                    viewModel.logPrayerStatus(sched.dateString, prayerItem.type, status)
                }
                selectedPrayerForStatusDialog = null
            },
            onDismiss = { selectedPrayerForStatusDialog = null }
        )
    }
}

@Composable
fun HeroNextPrayerCard(
    schedule: PrayerSchedule,
    autoSilentActive: Boolean,
    onOpenSettings: () -> Unit
) {
    val nextPrayer = schedule.nextPrayer
    val remainingMillis = schedule.nextPrayerRemainingMillis

    val hours = (remainingMillis / (1000 * 60 * 60))
    val minutes = ((remainingMillis / (1000 * 60)) % 60)
    val seconds = ((remainingMillis / 1000) % 60)
    val countdownText = String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_next_prayer_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                // Header: Hijri Date and Silent Mode Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = schedule.hijriDate.split("•").firstOrNull()?.trim() ?: "",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )

                    if (autoSilentActive) {
                        Surface(
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeOff,
                                    contentDescription = "Auto-Silent On",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Auto-Silent",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Next Prayer Spotlight
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "NEXT PRAYER",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = nextPrayer?.type?.displayName ?: "Fajr",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = nextPrayer?.type?.arabicName ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f)
                        )
                    }

                    // Next prayer exact time pill
                    Surface(
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = nextPrayer?.formattedTime ?: "--:--",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = "Adhan Time",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Live Countdown Bar
                Surface(
                    color = Color.Black.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Time remaining",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                            )
                        }
                        Text(
                            text = countdownText,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DailySalahProgressCard(progress: Float) {
    val completedPrayers = (progress * 5f).toInt()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("daily_progress_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Salah Progress",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$completedPrayers/5 Completed",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            )
        }
    }
}

@Composable
fun PrayerTimeCard(
    item: PrayerTimeItem,
    onClick: () -> Unit
) {
    val isNext = item.isNext
    val isCurrent = item.isCurrent

    val cardColor = when {
        isNext -> MaterialTheme.colorScheme.primaryContainer
        isCurrent -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }

    val borderColor = if (isNext) MaterialTheme.colorScheme.primary else Color.Transparent

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(if (isNext) 2.dp else 0.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("prayer_card_${item.type.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Status icon or prayer icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (isNext) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val iconVector = when (item.type) {
                        PrayerType.FAJR -> Icons.Default.Brightness5
                        PrayerType.SUNRISE -> Icons.Default.WbSunny
                        PrayerType.DHUHR -> Icons.Default.LightMode
                        PrayerType.ASR -> Icons.Default.CloudQueue
                        PrayerType.MAGHRIB -> Icons.Default.WbTwilight
                        PrayerType.ISHA -> Icons.Default.Nightlight
                    }
                    Icon(
                        imageVector = iconVector,
                        contentDescription = item.type.displayName,
                        tint = if (isNext) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.type.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = if (isNext) FontWeight.Bold else FontWeight.SemiBold
                        )
                        if (isNext) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "NEXT",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Text(
                        text = item.type.arabicName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Time and Status Badge
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = item.formattedTime,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                if (item.type != PrayerType.SUNRISE) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = Color(item.status.colorHex).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = item.status.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(item.status.colorHex),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CitySelectionDialog(
    currentCity: UserCity,
    isDetecting: Boolean,
    onAutoDetect: () -> Unit,
    onCitySelected: (UserCity) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filtered = remember(searchQuery) {
        if (searchQuery.isBlank()) PresetCities.CITIES
        else PresetCities.CITIES.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.country.contains(searchQuery, ignoreCase = true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select City or Region") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Auto-Detect Region Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !isDetecting) {
                            onAutoDetect()
                            onDismiss()
                        }
                        .padding(bottom = 12.dp)
                        .testTag("auto_detect_gps_button")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isDetecting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Auto-Detect My Region (GPS)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Auto-configure location & optimal calculation",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search city or country...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_city_input"),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    items(filtered) { city ->
                        val isSelected = city.name == currentCity.name
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCitySelected(city) }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = city.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                                Text(
                                    text = city.country,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun PrayerStatusDialog(
    prayerItem: PrayerTimeItem,
    onStatusSelected: (PrayerStatus) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Log ${prayerItem.type.displayName} Prayer (${prayerItem.type.arabicName})")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PrayerStatus.entries.forEach { status ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onStatusSelected(status) }
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(status.colorHex).copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = status.label,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(status.colorHex)
                            )
                            Text(
                                text = status.arabicLabel,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(status.colorHex)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
