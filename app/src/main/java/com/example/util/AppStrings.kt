package com.example.util

import com.example.model.AppLanguage
import com.example.model.PrayerStatus
import com.example.model.PrayerType

object AppStrings {

    fun getPrayerName(type: PrayerType, lang: AppLanguage): String {
        return when (lang) {
            AppLanguage.ARABIC -> type.arabicName
            AppLanguage.FRENCH -> when (type) {
                PrayerType.FAJR -> "Fajr (Aube)"
                PrayerType.SUNRISE -> "Lever du soleil"
                PrayerType.DHUHR -> "Dhuhr (Midi)"
                PrayerType.ASR -> "Asr (Après-midi)"
                PrayerType.MAGHRIB -> "Maghrib (Coucher)"
                PrayerType.ISHA -> "Isha (Nuit)"
            }
            AppLanguage.INDONESIAN -> when (type) {
                PrayerType.FAJR -> "Subuh"
                PrayerType.SUNRISE -> "Terbit"
                PrayerType.DHUHR -> "Dzuhur"
                PrayerType.ASR -> "Ashar"
                PrayerType.MAGHRIB -> "Maghrib"
                PrayerType.ISHA -> "Isya"
            }
            AppLanguage.TURKISH -> when (type) {
                PrayerType.FAJR -> "İmsak"
                PrayerType.SUNRISE -> "Güneş"
                PrayerType.DHUHR -> "Öğle"
                PrayerType.ASR -> "İkindi"
                PrayerType.MAGHRIB -> "Akşam"
                PrayerType.ISHA -> "Yatsı"
            }
            AppLanguage.URDU -> when (type) {
                PrayerType.FAJR -> "فجر"
                PrayerType.SUNRISE -> "طلوع آفتاب"
                PrayerType.DHUHR -> "ظہر"
                PrayerType.ASR -> "عصر"
                PrayerType.MAGHRIB -> "مغرب"
                PrayerType.ISHA -> "عشاء"
            }
            AppLanguage.ENGLISH -> type.displayName
        }
    }

    fun getStatusLabel(status: PrayerStatus, lang: AppLanguage): String {
        return when (lang) {
            AppLanguage.ARABIC -> status.arabicLabel
            AppLanguage.FRENCH -> when (status) {
                PrayerStatus.NOT_LOGGED -> "Non enregistré"
                PrayerStatus.PRAYED_ON_TIME -> "À l'heure"
                PrayerStatus.PRAYED_JAMAAH -> "En groupe"
                PrayerStatus.PRAYED_LATE -> "Rattrapée"
                PrayerStatus.MISSED -> "Manquée"
                PrayerStatus.EXCUSED -> "Dispensé"
            }
            AppLanguage.INDONESIAN -> when (status) {
                PrayerStatus.NOT_LOGGED -> "Belum Dicatat"
                PrayerStatus.PRAYED_ON_TIME -> "Tepat Waktu"
                PrayerStatus.PRAYED_JAMAAH -> "Berjamaah"
                PrayerStatus.PRAYED_LATE -> "Qadha"
                PrayerStatus.MISSED -> "Terlewat"
                PrayerStatus.EXCUSED -> "Udzur"
            }
            AppLanguage.TURKISH -> when (status) {
                PrayerStatus.NOT_LOGGED -> "Kaydedilmedi"
                PrayerStatus.PRAYED_ON_TIME -> "Vaktinde"
                PrayerStatus.PRAYED_JAMAAH -> "Cemaatle"
                PrayerStatus.PRAYED_LATE -> "Kaza"
                PrayerStatus.MISSED -> "Kaçırıldı"
                PrayerStatus.EXCUSED -> "Mazeretli"
            }
            AppLanguage.URDU -> when (status) {
                PrayerStatus.NOT_LOGGED -> "درج نہیں"
                PrayerStatus.PRAYED_ON_TIME -> "وقت پر"
                PrayerStatus.PRAYED_JAMAAH -> "باجماعت"
                PrayerStatus.PRAYED_LATE -> "قضاء"
                PrayerStatus.MISSED -> "چھوٹ گئی"
                PrayerStatus.EXCUSED -> "عذر"
            }
            AppLanguage.ENGLISH -> status.label
        }
    }

    fun t(key: String, lang: AppLanguage): String {
        val map = when (lang) {
            AppLanguage.ARABIC -> ARABIC_STRINGS
            AppLanguage.FRENCH -> FRENCH_STRINGS
            AppLanguage.INDONESIAN -> INDONESIAN_STRINGS
            AppLanguage.TURKISH -> TURKISH_STRINGS
            AppLanguage.URDU -> URDU_STRINGS
            AppLanguage.ENGLISH -> ENGLISH_STRINGS
        }
        return map[key] ?: ENGLISH_STRINGS[key] ?: key
    }

    private val ENGLISH_STRINGS = mapOf(
        "nav_prayer" to "Prayer",
        "nav_qibla" to "Qibla",
        "nav_quran" to "Quran & Duas",
        "nav_tracker" to "Tracker",
        "nav_settings" to "Settings",
        "next_prayer" to "NEXT PRAYER",
        "time_remaining" to "Time remaining",
        "daily_progress" to "Daily Salah Progress",
        "completed" to "Completed",
        "todays_prayers" to "Today's Prayers",
        "tap_to_log" to "Tap prayer to log status",
        "update_now" to "Update Now",
        "updating" to "Updating...",
        "select_city" to "Select City or Region",
        "auto_detect_gps" to "Auto-Detect My Region (GPS)",
        "auto_detect_subtitle" to "Auto-configure location & optimal calculation",
        "search_city" to "Search city or country...",
        "qibla_compass" to "Qibla Compass",
        "aligned_kaaba" to "Aligned with Kaaba! Face this direction.",
        "rotate_prompt" to "Rotate until the golden Kaaba needle points straight UP",
        "current_heading" to "CURRENT HEADING",
        "qibla_bearing" to "QIBLA BEARING",
        "mecca_distance" to "MECCA DISTANCE",
        "quran_readings" to "Quran Readings",
        "daily_supplications" to "Daily Supplications",
        "tab_quran" to "📖 Holy Quran",
        "tab_adhkar" to "🤲 Daily Adhkar",
        "audio_recitation" to "Audio Recitation",
        "tap_tasbih" to "Tap Tasbih",
        "streak" to "Current Streak",
        "adherence" to "7-Day Adherence",
        "status_legend" to "Status Legend",
        "language" to "App Language",
        "theme" to "App Theme & Visual Styling",
        "theme_mode" to "Display Appearance Mode",
        "theme_mode_system" to "System Default",
        "theme_mode_light" to "Light Mode",
        "theme_mode_dark" to "Dark Mode",
        "theme_mode_desc_system" to "Matches your Android system appearance automatically",
        "theme_mode_desc_light" to "Clean, high-contrast bright daytime theme",
        "theme_mode_desc_dark" to "Deep gentle contrast for night and low-light prayers",
        "quick_theme_toggle" to "Switch Theme Mode",
        "dark_mode" to "Dark Mode",
        "time_format" to "24-Hour Time Format",
        "auto_silent" to "Auto-Silent during Salah",
        "quiet_focus" to "Quiet Focus Mode",
        "prayer_alerts" to "Prayer Notification Alerts",
        "calculation_rules" to "Calculation & Astronomical Rules",
        "location_region" to "Location & Region Detection"
    )

    private val ARABIC_STRINGS = mapOf(
        "nav_prayer" to "الصلاة",
        "nav_qibla" to "القبلة",
        "nav_quran" to "القرآن والأذكار",
        "nav_tracker" to "السجل",
        "nav_settings" to "الإعدادات",
        "next_prayer" to "الصلاة القادمة",
        "time_remaining" to "الوقت المتبقي",
        "daily_progress" to "تقدم صلوات اليوم",
        "completed" to "مكتمل",
        "todays_prayers" to "صلوات اليوم",
        "tap_to_log" to "اضغط لتسجيل حالة الصلاة",
        "update_now" to "تحديث الآن",
        "updating" to "جاري التحديث...",
        "select_city" to "اختر المدينة أو المنطقة",
        "auto_detect_gps" to "تحديد منطقتي تلقائياً (GPS)",
        "auto_detect_subtitle" to "ضبط الموقع وطريقة الحساب المناسبة لدولتك",
        "search_city" to "ابحث عن مدينة أو دولة...",
        "qibla_compass" to "بوصلة القبلة",
        "aligned_kaaba" to "باتجاه الكعبة المشرفة! صلّ في هذا الاتجاه.",
        "rotate_prompt" to "قم بتدوير الهاتف حتى تشير إبرة الكعبة الذهبية للأعلى تماماً",
        "current_heading" to "الاتجاه الحالي",
        "qibla_bearing" to "اتجاه القبلة",
        "mecca_distance" to "المسافة إلى مكة",
        "quran_readings" to "تلاوات القرآن الكريم",
        "daily_supplications" to "الأذكار والأدعية",
        "tab_quran" to "📖 القرآن الكريم",
        "tab_adhkar" to "🤲 أذكار المسلم",
        "audio_recitation" to "تلاوة صوتية",
        "tap_tasbih" to "اضغط للتسبيح",
        "streak" to "الأيام المتتالية",
        "adherence" to "الالتزام الأسبوعي",
        "status_legend" to "دليل الحالات",
        "language" to "لغة التطبيق",
        "theme" to "سمة التطبيق والمظهر",
        "theme_mode" to "وضع المظهر والعرض",
        "theme_mode_system" to "حسب النظام",
        "theme_mode_light" to "الوضع الفاتح",
        "theme_mode_dark" to "الوضع الداكن",
        "theme_mode_desc_system" to "مطابق لمظهر نظام أندرويد تلقائياً",
        "theme_mode_desc_light" to "مظهر نهاري ناصع ومريح في النهار",
        "theme_mode_desc_dark" to "مريح للعينين لصلوات الليل والقيام",
        "quick_theme_toggle" to "تبديل المظهر",
        "dark_mode" to "الوضع الداكن",
        "time_format" to "صيغة 24 ساعة",
        "auto_silent" to "الوضع الصامت أثناء الصلاة",
        "quiet_focus" to "وضع الهدوء والتركيز",
        "prayer_alerts" to "تنبيهات وأصوات الأذان",
        "calculation_rules" to "طريقة الحساب الفلكية",
        "location_region" to "تحديد الموقع والمنطقة"
    )

    private val FRENCH_STRINGS = mapOf(
        "nav_prayer" to "Prière",
        "nav_qibla" to "Qibla",
        "nav_quran" to "Coran & Douas",
        "nav_tracker" to "Suivi",
        "nav_settings" to "Paramètres",
        "next_prayer" to "PROCHAINE PRIÈRE",
        "time_remaining" to "Temps restant",
        "daily_progress" to "Progression des prières",
        "completed" to "Complété",
        "todays_prayers" to "Prières du jour",
        "tap_to_log" to "Appuyez pour enregistrer",
        "update_now" to "Actualiser",
        "updating" to "Mise à jour...",
        "select_city" to "Sélectionner la ville",
        "auto_detect_gps" to "Détection automatique (GPS)",
        "auto_detect_subtitle" to "Configurer l'emplacement et la méthode de calcul",
        "search_city" to "Rechercher une ville...",
        "qibla_compass" to "Boussole Qibla",
        "aligned_kaaba" to "Aligné avec la Kaaba !",
        "rotate_prompt" to "Tournez jusqu'à ce que l'aiguille pointe vers le haut",
        "current_heading" to "CAP ACTUEL",
        "qibla_bearing" to "DIRECTION QIBLA",
        "mecca_distance" to "DISTANCE DE LA MECQUE",
        "quran_readings" to "Lectures du Coran",
        "daily_supplications" to "Invocations quotidiennes",
        "tab_quran" to "📖 Saint Coran",
        "tab_adhkar" to "🤲 Adhkar quotidiens",
        "audio_recitation" to "Récitation audio",
        "tap_tasbih" to "Appuyez Tasbih",
        "streak" to "Série actuelle",
        "adherence" to "Assiduité 7 jours",
        "status_legend" to "Légende des statuts",
        "language" to "Langue de l'application",
        "theme" to "Thème et style visuel",
        "theme_mode" to "Mode d'affichage",
        "theme_mode_system" to "Système par défaut",
        "theme_mode_light" to "Mode clair",
        "theme_mode_dark" to "Mode sombre",
        "theme_mode_desc_system" to "S'adapte automatiquement au système Android",
        "theme_mode_desc_light" to "Thème clair et net pour la journée",
        "theme_mode_desc_dark" to "Contraste doux pour la nuit et les prières nocturnes",
        "quick_theme_toggle" to "Changer de thème",
        "dark_mode" to "Mode sombre",
        "time_format" to "Format 24 heures",
        "auto_silent" to "Silence auto pendant la prière",
        "quiet_focus" to "Mode de concentration calme",
        "prayer_alerts" to "Alertes de prière et Adhan",
        "calculation_rules" to "Méthodes de calcul",
        "location_region" to "Localisation et région"
    )

    private val INDONESIAN_STRINGS = mapOf(
        "nav_prayer" to "Sholat",
        "nav_qibla" to "Kiblat",
        "nav_quran" to "Al-Qur'an & Doa",
        "nav_tracker" to "Pelacak",
        "nav_settings" to "Pengaturan",
        "next_prayer" to "SHOLAT BERIKUTNYA",
        "time_remaining" to "Sisa waktu",
        "daily_progress" to "Progres Sholat Harian",
        "completed" to "Selesai",
        "todays_prayers" to "Jadwal Sholat Hari Ini",
        "tap_to_log" to "Ketuk untuk mencatat status",
        "update_now" to "Perbarui Sekarang",
        "updating" to "Memperbarui...",
        "select_city" to "Pilih Kota atau Wilayah",
        "auto_detect_gps" to "Deteksi Wilayah Otomatis (GPS)",
        "auto_detect_subtitle" to "Atur lokasi & metode hisab sholat secara otomatis",
        "search_city" to "Cari kota atau negara...",
        "qibla_compass" to "Kompas Kiblat",
        "aligned_kaaba" to "Menghadap Ka'bah! Luruskan sholat ke arah ini.",
        "rotate_prompt" to "Putar ponsel hingga jarum Ka'bah mengarah ke atas",
        "current_heading" to "ARAH SEKARANG",
        "qibla_bearing" to "ARAH KIBLAT",
        "mecca_distance" to "JARAK KE MAKKAH",
        "quran_readings" to "Bacaan Al-Qur'an",
        "daily_supplications" to "Doa & Dzikir Harian",
        "tab_quran" to "📖 Al-Qur'an",
        "tab_adhkar" to "🤲 Dzikir Harian",
        "audio_recitation" to "Murottal Audio",
        "tap_tasbih" to "Ketuk Tasbih",
        "streak" to "Runtunan Hari",
        "adherence" to "Kepatuhan 7 Hari",
        "status_legend" to "Keterangan Status",
        "language" to "Bahasa Aplikasi",
        "theme" to "Tema & Tampilan Visual",
        "theme_mode" to "Mode Tampilan Tema",
        "theme_mode_system" to "Sistem Default",
        "theme_mode_light" to "Mode Terang",
        "theme_mode_dark" to "Mode Gelap",
        "theme_mode_desc_system" to "Otomatis mengikuti tampilan sistem Android",
        "theme_mode_desc_light" to "Tema cerah dan bersih untuk siang hari",
        "theme_mode_desc_dark" to "Nyaman di mata untuk malam hari & ibadah malam",
        "quick_theme_toggle" to "Ganti Mode Tema",
        "dark_mode" to "Mode Gelap",
        "time_format" to "Format Waktu 24 Jam",
        "auto_silent" to "Hening Otomatis Saat Sholat",
        "quiet_focus" to "Mode Fokus Tenang",
        "prayer_alerts" to "Notifikasi Adzan & Pengingat",
        "calculation_rules" to "Metode Perhitungan Hisab",
        "location_region" to "Deteksi Lokasi & Wilayah"
    )

    private val TURKISH_STRINGS = mapOf(
        "nav_prayer" to "Namaz",
        "nav_qibla" to "Kıble",
        "nav_quran" to "Kuran & Dualar",
        "nav_tracker" to "Takip",
        "nav_settings" to "Ayarlar",
        "next_prayer" to "SONRAKİ VAKİT",
        "time_remaining" to "Kalan süre",
        "daily_progress" to "Günlük Namaz İlerlemesi",
        "completed" to "Tamamlandı",
        "todays_prayers" to "Bugünün Namaz Vakitleri",
        "tap_to_log" to "Kaydetmek için vakte dokunun",
        "update_now" to "Şimdi Güncelle",
        "updating" to "Güncelleniyor...",
        "select_city" to "Şehir veya Bölge Seç",
        "auto_detect_gps" to "Bölgeyi Otomatik Algıla (GPS)",
        "auto_detect_subtitle" to "Konum ve hesaplama yöntemini otomatik ayarla",
        "search_city" to "Şehir veya ülke ara...",
        "qibla_compass" to "Kıble Pusulası",
        "aligned_kaaba" to "Kâbe ile hizalandı! Bu yöne dönün.",
        "rotate_prompt" to "Altın Kâbe ibresi yukarıyı gösterene kadar çevirin",
        "current_heading" to "MEVCUT YÖN",
        "qibla_bearing" to "KIBLE AÇISI",
        "mecca_distance" to "MEKKE'YE UZAKLIK",
        "quran_readings" to "Kuran Okumaları",
        "daily_supplications" to "Günlük Dualar ve Zikirler",
        "tab_quran" to "📖 Kur'an-ı Kerim",
        "tab_adhkar" to "🤲 Günlük Zikirler",
        "audio_recitation" to "Sesli Tilavet",
        "tap_tasbih" to "Tesbihe Dokun",
        "streak" to "Günlük Seri",
        "adherence" to "7 Günlük Uyum",
        "status_legend" to "Durum Rehberi",
        "language" to "Uygulama Dili",
        "theme" to "Tema ve Görsel Stil",
        "theme_mode" to "Tema Görünüm Modu",
        "theme_mode_system" to "Sistem Varsayılanı",
        "theme_mode_light" to "Açık Mod",
        "theme_mode_dark" to "Karanlık Mod",
        "theme_mode_desc_system" to "Android sistem görünümüne otomatik uyum sağlar",
        "theme_mode_desc_light" to "Gündüz için aydınlık ve ferah görünüm",
        "theme_mode_desc_dark" to "Gece ibadeti ve loş ışık için göz yormaz",
        "quick_theme_toggle" to "Tema Değiştir",
        "dark_mode" to "Karanlık Mod",
        "time_format" to "24 Saat Formatı",
        "auto_silent" to "Namazda Otomatik Sessiz Mod",
        "quiet_focus" to "Sessiz Odaklanma Modu",
        "prayer_alerts" to "Ezan ve Vakit Bildirimleri",
        "calculation_rules" to "Hesaplama Yöntemleri",
        "location_region" to "Konum ve Bölge Algılama"
    )

    private val URDU_STRINGS = mapOf(
        "nav_prayer" to "نماز",
        "nav_qibla" to "قبلہ",
        "nav_quran" to "قرآن و دعائیں",
        "nav_tracker" to "ٹریکر",
        "nav_settings" to "ترتیبات",
        "next_prayer" to "اگلی نماز",
        "time_remaining" to "باقی وقت",
        "daily_progress" to "روزانہ نماز کی پیشرفت",
        "completed" to "مکمل",
        "todays_prayers" to "آج کے اوقات نماز",
        "tap_to_log" to "نماز کی حیثیت درج کرنے کے لیے ٹیپ کریں",
        "update_now" to "ابھی اپ ڈیٹ کریں",
        "updating" to "اپ ڈیٹ ہو رہا ہے...",
        "select_city" to "شہر یا خطہ منتخب کریں",
        "auto_detect_gps" to "میرا خطہ خودکار تلاش کریں (GPS)",
        "auto_detect_subtitle" to "مقام اور درست طریقہ حساب خودکار ترتیب دیں",
        "search_city" to "شہر یا ملک تلاش کریں...",
        "qibla_compass" to "قبلہ کمپاس",
        "aligned_kaaba" to "قبلہ رخ ہو گیا! اسی سمت نماز ادا کریں۔",
        "rotate_prompt" to "فون گھمائیں جب تک سنہری خانہ کعبہ کی سوئی اوپر نہ ہو جائے",
        "current_heading" to "موجودہ سمت",
        "qibla_bearing" to "قبلہ کا رخ",
        "mecca_distance" to "مکہ مکرمہ کا فاصلہ",
        "quran_readings" to "تلاوت قرآن",
        "daily_supplications" to "روزمرہ دعائیں و اذکار",
        "tab_quran" to "📖 قرآن مجید",
        "tab_adhkar" to "🤲 روزانہ کے اذکار",
        "audio_recitation" to "صوتی تلاوت",
        "tap_tasbih" to "تسبیح گنیں",
        "streak" to "مسلسل دن",
        "adherence" to "ہفتہ وار پابندی",
        "status_legend" to "حیثیت کی رہنمائی",
        "language" to "ایپ کی زبان",
        "theme" to "تھیم اور ڈیزائن",
        "theme_mode" to "تھیم موڈ منتخب کریں",
        "theme_mode_system" to "سسٹم کے مطابق",
        "theme_mode_light" to "لائٹ موڈ",
        "theme_mode_dark" to "ڈارک موڈ",
        "theme_mode_desc_system" to "خودکار طور پر اینڈرائڈ سسٹم کی سیٹنگز کے مطابق",
        "theme_mode_desc_light" to "دن کے وقت کے لیے روشن اور واضح تھیم",
        "theme_mode_desc_dark" to "رات اور تہجد کے وقت آنکھوں کے لیے پرسکون",
        "quick_theme_toggle" to "تھیم تبدیل کریں",
        "dark_mode" to "ڈارک موڈ",
        "time_format" to "24 گھنٹے کی گھڑی",
        "auto_silent" to "نماز کے دوران خودکار خاموش",
        "quiet_focus" to "پرسکون فوکس موڈ",
        "prayer_alerts" to "اذان اور نماز کے نوٹیفکیشن",
        "calculation_rules" to "اوقات نماز کے اصول",
        "location_region" to "مقام اور خطہ کی شناخت"
    )
}
