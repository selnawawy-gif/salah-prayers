package com.example.model

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val isRtl: Boolean
) {
    ENGLISH("en", "English", "English", false),
    ARABIC("ar", "Arabic", "العربية", true),
    FRENCH("fr", "French", "Français", false),
    INDONESIAN("in", "Indonesian", "Bahasa Indonesia", false),
    TURKISH("tr", "Turkish", "Türkçe", false),
    URDU("ur", "Urdu", "اردو", true);

    companion object {
        fun fromCode(code: String): AppLanguage =
            values().firstOrNull { it.code.equals(code, ignoreCase = true) } ?: ENGLISH
    }
}
