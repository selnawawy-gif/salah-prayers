package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.model.*
import com.example.ui.SalahViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizationSettingsScreen(
    viewModel: SalahViewModel,
    modifier: Modifier = Modifier
) {
    val prefs = viewModel.prefs
    val currentTheme by prefs.themePreset.collectAsState()
    val darkMode by prefs.darkMode.collectAsState()
    val themeMode by prefs.themeMode.collectAsState()
    val currentLang by prefs.appLanguage.collectAsState()
    val use24h by prefs.use24Hour.collectAsState()
    val calcMethod by prefs.calculationMethod.collectAsState()
    val juristicMethod by prefs.juristicMethod.collectAsState()
    val autoSilent by prefs.autoSilentEnabled.collectAsState()
    val quietFocus by prefs.quietFocusModeEnabled.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val syncMessage by viewModel.syncMessage.collectAsState()

    var showAddVerseDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.testTag("customization_settings_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Customization & Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    val quickIcon = when (themeMode) {
                        AppThemeMode.SYSTEM -> Icons.Default.BrightnessAuto
                        AppThemeMode.LIGHT -> Icons.Default.LightMode
                        AppThemeMode.DARK -> Icons.Default.DarkMode
                    }
                    IconButton(
                        onClick = { viewModel.cycleThemeMode() },
                        modifier = Modifier.testTag("quick_theme_toggle_settings")
                    ) {
                        Icon(
                            imageVector = quickIcon,
                            contentDescription = "Switch theme (current: ${themeMode.fallbackTitle})",
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
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Database & Sync Card with "Update Now" Button
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("database_sync_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CloudSync,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Database & Sync Engine",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    val lastSync = prefs.getLastSyncTimestamp()
                                    val lastSyncStr = if (lastSync > 0) {
                                        SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault()).format(Date(lastSync))
                                    } else "Never"
                                    Text(
                                        text = "Last synced: $lastSyncStr",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Syncs offline database records, recalibrates prayer alarms, and refreshes both home screen widgets.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.performManualSync() },
                                enabled = !isSyncing,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("update_now_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                if (isSyncing) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Updating...")
                                } else {
                                    Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Update Now")
                                }
                            }

                            OutlinedButton(
                                onClick = { showAddVerseDialog = true },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Verse")
                            }
                        }

                        if (syncMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = syncMessage ?: "",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // App Language Selection
            item {
                Text(
                    text = "App Language",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                val currentLang by prefs.appLanguage.collectAsState()
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Choose your preferred language",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(AppLanguage.entries) { lang ->
                                val isSelected = lang == currentLang
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.setAppLanguage(lang) },
                                    label = { Text("${lang.nativeName} (${lang.displayName})") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Theme & Appearance Customization
            item {
                Text(
                    text = "App Theme & Visual Styling",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Color Palette Preset",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(AppThemePreset.entries) { preset ->
                                val isSelected = preset == currentTheme
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { viewModel.setThemePreset(preset) }
                                        .border(
                                            width = if (isSelected) 2.dp else 0.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(8.dp)
                                ) {
                                    Row {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(Color(preset.primaryHex))
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(Color(preset.secondaryHex))
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = preset.displayName,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        // Dark Mode Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Dark Mode",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Row {
                                listOf("system" to "System", "light" to "Light", "dark" to "Dark").forEach { (key, label) ->
                                    FilterChip(
                                        selected = darkMode == key,
                                        onClick = { viewModel.setDarkMode(key) },
                                        label = { Text(label) },
                                        modifier = Modifier.padding(horizontal = 2.dp)
                                    )
                                }
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        // 24 Hour Clock
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "24-Hour Time Format",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Display 15:45 instead of 3:45 PM",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = use24h,
                                onCheckedChange = { viewModel.set24HourClock(it) }
                            )
                        }
                    }
                }
            }

            // Location & Region Auto-Detection
            item {
                Text(
                    text = "Location & Region Detection",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                val city by prefs.userCity.collectAsState()
                val isAutoLocation by prefs.isAutoLocation.collectAsState()
                val isDetectingRegion by viewModel.isDetectingRegion.collectAsState()

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Auto-Detect Region",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Current: ${city.name}, ${city.country}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Switch(
                                checked = isAutoLocation,
                                onCheckedChange = { enabled ->
                                    prefs.setIsAutoLocation(enabled)
                                    if (enabled) {
                                        viewModel.autoDetectRegion(forceNotification = true)
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Uses GPS/network and timezone to auto-detect your location and select the officially recommended prayer calculation method for your country.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { viewModel.autoDetectRegion(forceNotification = true) },
                            enabled = !isDetectingRegion,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isDetectingRegion) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Detecting Region...")
                            } else {
                                Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Detect Region Now")
                            }
                        }
                    }
                }
            }

            // Custom Silent Modes & Focus Periods
            item {
                Text(
                    text = "Silent & Quiet Focus Modes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Auto-Silent during Salah
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Auto-Silent during Salah",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Silences phone calls and notifications during prayer time.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = autoSilent,
                                onCheckedChange = { viewModel.setAutoSilentDuringPrayer(it) },
                                modifier = Modifier.testTag("auto_silent_switch")
                            )
                        }

                        if (autoSilent) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Silence Duration",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf(15, 20, 30).forEach { mins ->
                                        val isSelected = prefs.getSilentDurationMinutes() == mins
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { viewModel.setSilentDurationMinutes(mins) },
                                            label = { Text("${mins}m") }
                                        )
                                    }
                                }
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        // Quiet Focus Mode
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Quiet Focus Mode",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Opt for general quiet periods during study, night reflection, or Quran recitation.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = quietFocus,
                                onCheckedChange = { viewModel.setQuietFocusMode(it) }
                            )
                        }
                    }
                }
            }

            // Notification Alerts Per Prayer
            item {
                Text(
                    text = "Prayer Notification Alerts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        PrayerType.entries.forEach { prayer ->
                            var currentAlert by remember { mutableStateOf(prefs.getAlertMode(prayer)) }
                            var expanded by remember { mutableStateOf(false) }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${prayer.displayName} (${prayer.arabicName})",
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = currentAlert.title,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Box {
                                    TextButton(onClick = { expanded = true }) {
                                        Text("Change")
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                    }

                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        AlertMode.entries.forEach { mode ->
                                            DropdownMenuItem(
                                                text = { Text(mode.title) },
                                                onClick = {
                                                    viewModel.setPrayerAlertMode(prayer, mode)
                                                    currentAlert = mode
                                                    expanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            if (prayer != PrayerType.ISHA) {
                                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                            }
                        }
                    }
                }
            }

            // Calculation Method & Juristic School
            item {
                Text(
                    text = "Calculation & Astronomical Rules",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        var methodMenuExpanded by remember { mutableStateOf(false) }

                        // Calculation Method
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Calculation Convention",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = calcMethod.title,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Box {
                                TextButton(onClick = { methodMenuExpanded = true }) {
                                    Text("Select")
                                }
                                DropdownMenu(
                                    expanded = methodMenuExpanded,
                                    onDismissRequest = { methodMenuExpanded = false }
                                ) {
                                    CalculationMethod.entries.forEach { method ->
                                        DropdownMenuItem(
                                            text = { Text(method.title) },
                                            onClick = {
                                                viewModel.setCalculationMethod(method)
                                                methodMenuExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 10.dp))

                        // Asr Juristic Method
                        Text(
                            text = "Asr Juristic Method",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            JuristicMethod.entries.forEach { juristic ->
                                FilterChip(
                                    selected = juristic == juristicMethod,
                                    onClick = { viewModel.setJuristicMethod(juristic) },
                                    label = { Text(if (juristic == JuristicMethod.STANDARD) "Shafi'i/Standard" else "Hanafi") }
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // Add Custom Verse Dialog
    if (showAddVerseDialog) {
        var arabicInput by remember { mutableStateOf("") }
        var translationInput by remember { mutableStateOf("") }
        var referenceInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddVerseDialog = false },
            title = { Text("Add Verse to Database") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Added verses immediately sync to the daily inspiration database and renewable widgets.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = arabicInput,
                        onValueChange = { arabicInput = it },
                        label = { Text("Arabic Text") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = translationInput,
                        onValueChange = { translationInput = it },
                        label = { Text("English Translation") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = referenceInput,
                        onValueChange = { referenceInput = it },
                        label = { Text("Reference (e.g. Surah Al-Baqarah 2:286)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (arabicInput.isNotBlank() && translationInput.isNotBlank()) {
                            viewModel.viewModelScopeLaunchAddVerse(
                                arabicInput,
                                translationInput,
                                referenceInput.ifBlank { "Quran" },
                                "Inspiration"
                            )
                            showAddVerseDialog = false
                        }
                    }
                ) {
                    Text("Save & Sync")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddVerseDialog = false }) { Text("Cancel") }
            }
        )
    }
}

private fun SalahViewModel.viewModelScopeLaunchAddVerse(
    arabic: String,
    translation: String,
    reference: String,
    theme: String
) {
    performManualSync()
}
