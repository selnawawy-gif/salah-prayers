package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.SalahScreen
import com.example.ui.SalahViewModel
import com.example.ui.screens.*
import com.example.ui.theme.SalahTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: SalahViewModel = viewModel()
            val themePreset by viewModel.prefs.themePreset.collectAsState()
            val themeMode by viewModel.prefs.themeMode.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()
            val context = LocalContext.current

            // Check intent for shortcut navigation (e.g. from Widget or Notification)
            LaunchedEffect(intent) {
                handleIntent(intent, viewModel)
            }

            // Permissions launcher
            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                // Permission handled
            }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            val currentLang by viewModel.prefs.appLanguage.collectAsState()

            // Back handler: return to Prayer Times if on a secondary tab
            if (currentScreen != SalahScreen.PRAYER_TIMES) {
                BackHandler {
                    viewModel.selectScreen(SalahScreen.PRAYER_TIMES)
                }
            }

            SalahTheme(
                preset = themePreset,
                themeMode = themeMode
            ) {
                androidx.compose.runtime.CompositionLocalProvider(
                    androidx.compose.ui.platform.LocalLayoutDirection provides (
                        if (currentLang.isRtl) androidx.compose.ui.unit.LayoutDirection.Rtl
                        else androidx.compose.ui.unit.LayoutDirection.Ltr
                    )
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        contentWindowInsets = WindowInsets(0, 0, 0, 0),
                        bottomBar = {
                            SalahBottomNavigation(
                                currentScreen = currentScreen,
                                currentLang = currentLang,
                                onScreenSelected = { viewModel.selectScreen(it) }
                            )
                        }
                    ) { innerPadding ->
                        Crossfade(
                            targetState = currentScreen,
                            modifier = Modifier.padding(innerPadding),
                            label = "screen_crossfade"
                        ) { screen ->
                            when (screen) {
                                SalahScreen.PRAYER_TIMES -> PrayerTimesScreen(viewModel = viewModel)
                                SalahScreen.QIBLA -> QiblaScreen(viewModel = viewModel)
                                SalahScreen.QURAN_DUAS -> QuranDuasScreen(viewModel = viewModel)
                                SalahScreen.HISTORY -> PrayerHistoryScreen(viewModel = viewModel)
                                SalahScreen.SETTINGS -> CustomizationSettingsScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun handleIntent(intent: Intent?, viewModel: SalahViewModel) {
        if (intent == null) return
        val openTab = intent.getStringExtra("OPEN_TAB")
        if (openTab == "quran") {
            viewModel.selectScreen(SalahScreen.QURAN_DUAS)
        }
    }
}

@Composable
fun SalahBottomNavigation(
    currentScreen: SalahScreen,
    currentLang: com.example.model.AppLanguage,
    onScreenSelected: (SalahScreen) -> Unit
) {
    NavigationBar(
        modifier = Modifier.testTag("salah_bottom_navigation"),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        SalahScreen.entries.forEach { screen ->
            val isSelected = screen == currentScreen
            val icon = when (screen) {
                SalahScreen.PRAYER_TIMES -> Icons.Default.AccessTime
                SalahScreen.QIBLA -> Icons.Default.Explore
                SalahScreen.QURAN_DUAS -> Icons.Default.MenuBook
                SalahScreen.HISTORY -> Icons.Default.BarChart
                SalahScreen.SETTINGS -> Icons.Default.Settings
            }

            val titleKey = when (screen) {
                SalahScreen.PRAYER_TIMES -> "nav_prayer"
                SalahScreen.QIBLA -> "nav_qibla"
                SalahScreen.QURAN_DUAS -> "nav_quran"
                SalahScreen.HISTORY -> "nav_tracker"
                SalahScreen.SETTINGS -> "nav_settings"
            }
            val title = com.example.util.AppStrings.t(titleKey, currentLang)

            NavigationBarItem(
                selected = isSelected,
                onClick = { onScreenSelected(screen) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = title
                    )
                },
                label = {
                    Text(
                        text = title,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}")
            )
        }
    }
}
