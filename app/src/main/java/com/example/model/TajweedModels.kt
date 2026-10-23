package com.example.model

enum class TajweedCategory(val title: String, val arabicTitle: String) {
    NOON_AND_TANWEEN("Noon & Tanween", "النون الساكنة والتنوين"),
    MEEM_SAKINAH("Meem Sakinah", "الميم الساكنة"),
    MADD_ELONGATION("Madd (Elongation)", "أحكام المدود"),
    QALQALAH_ECHO("Qalqalah (Echo)", "أحكام القلقلة"),
    MAKHARIJ("Makharij (Articulation)", "مخارج الحروف"),
    HEAVY_LIGHT("Tafkheem & Tarqeeq", "التفخيم والترقيق")
}

data class TajweedExample(
    val arabicText: String,
    val highlightedRule: String,
    val surahReference: String,
    val phoneticTip: String
)

data class TajweedRule(
    val id: String,
    val title: String,
    val arabicTitle: String,
    val category: TajweedCategory,
    val colorHex: Long, // Color convention used in color-coded Mushaf
    val explanation: String,
    val letters: String,
    val countsOrDuration: String = "",
    val examples: List<TajweedExample>
)

data class TajweedQuizQuestion(
    val id: String,
    val question: String,
    val arabicQuestion: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)
