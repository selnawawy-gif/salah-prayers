package com.example.model

enum class AppThemeMode(
    val key: String,
    val titleKey: String,
    val descKey: String,
    val fallbackTitle: String,
    val fallbackDesc: String
) {
    SYSTEM(
        key = "system",
        titleKey = "theme_mode_system",
        descKey = "theme_mode_desc_system",
        fallbackTitle = "System Default",
        fallbackDesc = "Follows device appearance"
    ),
    LIGHT(
        key = "light",
        titleKey = "theme_mode_light",
        descKey = "theme_mode_desc_light",
        fallbackTitle = "Light Mode",
        fallbackDesc = "Clean and bright daylight theme"
    ),
    DARK(
        key = "dark",
        titleKey = "theme_mode_dark",
        descKey = "theme_mode_desc_dark",
        fallbackTitle = "Dark Mode",
        fallbackDesc = "Deep contrast, gentle on eyes at night"
    );

    fun next(): AppThemeMode = when (this) {
        SYSTEM -> LIGHT
        LIGHT -> DARK
        DARK -> SYSTEM
    }

    companion object {
        fun fromKey(key: String?): AppThemeMode =
            entries.firstOrNull { it.key.equals(key, ignoreCase = true) } ?: SYSTEM
    }
}
