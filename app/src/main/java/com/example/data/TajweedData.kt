package com.example.data

import com.example.model.TajweedCategory
import com.example.model.TajweedExample
import com.example.model.TajweedQuizQuestion
import com.example.model.TajweedRule

object TajweedData {

    val RULES: List<TajweedRule> = listOf(
        // Noon & Tanween Rules
        TajweedRule(
            id = "rule_izhar",
            title = "Izhar Halqi (Clear Pronunciation)",
            arabicTitle = "الإظهار الحلقي",
            category = TajweedCategory.NOON_AND_TANWEEN,
            colorHex = 0xFF2563EB, // Blue
            explanation = "Pronouncing the Noon Sakinah (نْ) or Tanween clearly from its articulation point without extra nasalization (ghunnah) when followed by one of the six throat letters.",
            letters = "ء ، هـ ، ع ، ح ، غ ، خ",
            countsOrDuration = "No prolongation (1 count normal)",
            examples = listOf(
                TajweedExample("مَنْ آمَنَ", "نْ + ء", "Surah Al-Baqarah", "Pronounce 'man' cleanly before 'aamana'"),
                TajweedExample("مِنْ غِلٍّ", "نْ + غ", "Surah Al-A'raf", "Clear noon before 'ghil'"),
                TajweedExample("عَلِيمٌ حَكِيمٌ", "ـٌ + ح", "Surah An-Nisa", "Clear tanween before 'Hakeem'")
            )
        ),
        TajweedRule(
            id = "rule_idgham_ghunnah",
            title = "Idgham with Ghunnah (Merging with Nasal Sound)",
            arabicTitle = "الإدغام بغنة",
            category = TajweedCategory.NOON_AND_TANWEEN,
            colorHex = 0xFF10B981, // Emerald Green
            explanation = "Merging the Noon Sakinah or Tanween completely or partially into the next letter while holding a beautiful 2-count nasal echo (ghunnah).",
            letters = "ي ، ن ، م ، و (يَنْمُو)",
            countsOrDuration = "2 Harakat (Ghunnah)",
            examples = listOf(
                TajweedExample("مَن يَقُولُ", "نْ + ي", "Surah Al-Baqarah", "Pronounced as 'may-yaqool' with 2-count nasal tone"),
                TajweedExample("مِّن وَالٍ", "نْ + و", "Surah Ar-Ra'd", "Pronounced as 'miw-waal' with nasal resonance"),
                TajweedExample("حَبْلٌ مِّن مَّسَدٍ", "ـٌ + م", "Surah Al-Masad", "Merge tanween into meem with nasalization")
            )
        ),
        TajweedRule(
            id = "rule_idgham_no_ghunnah",
            title = "Idgham without Ghunnah (Complete Merge)",
            arabicTitle = "الإدغام بغير غنة",
            category = TajweedCategory.NOON_AND_TANWEEN,
            colorHex = 0xFF6B7280, // Gray
            explanation = "Completely merging the Noon Sakinah or Tanween into Lam (ل) or Ra (ر) without any nasal sound (ghunnah). The Noon disappears into the following letter.",
            letters = "ل ، ر",
            countsOrDuration = "Direct transition, no hold",
            examples = listOf(
                TajweedExample("مِن رَّبِّهِمْ", "نْ + ر", "Surah Al-Baqarah", "Pronounced 'mir-rabbihim' with zero nasal sound"),
                TajweedExample("هُدًى لِّلْمُتَّقِينَ", "ـً + ل", "Surah Al-Baqarah", "Pronounced 'hudal-lil-muttaqeen'")
            )
        ),
        TajweedRule(
            id = "rule_iqlab",
            title = "Iqlab (Conversion to Meem)",
            arabicTitle = "الإقلاب",
            category = TajweedCategory.NOON_AND_TANWEEN,
            colorHex = 0xFF059669, // Green with small Meem marker
            explanation = "Converting the Noon Sakinah or Tanween into a hidden Meem (م) with Ghunnah when followed by the letter Baa (ب). In the Quran, marked with a tiny Meem (ـۢ).",
            letters = "ب (Baa)",
            countsOrDuration = "2 Harakat (Ghunnah)",
            examples = listOf(
                TajweedExample("مِنۢ بَعْدِ", "نْ + ب", "Surah Al-Baqarah", "Pronounced as 'mim-ba'di' with lips gently touching"),
                TajweedExample("سَمِيعٌۢ بَصِيرٌ", "ـٌ + ب", "Surah Al-Hajj", "Pronounced 'samee'um-baseer'")
            )
        ),
        TajweedRule(
            id = "rule_ikhfa",
            title = "Ikhfa Haqiqi (Hiding / Concealment)",
            arabicTitle = "الإخفاء الحقيقي",
            category = TajweedCategory.NOON_AND_TANWEEN,
            colorHex = 0xFF14B8A6, // Teal
            explanation = "Pronouncing the Noon Sakinah or Tanween in a state between Izhar and Idgham, without shaddah, maintaining a 2-count nasal sound (Ghunnah) at the point of the next letter.",
            letters = "ص، ذ، ث، ك، ج، ش، ق، س، د، ط، ز، ف، ت، ض، ظ (15 letters)",
            countsOrDuration = "2 Harakat",
            examples = listOf(
                TajweedExample("مِن قَبْلُ", "نْ + ق", "Surah Al-Baqarah", "Heavy ghunnah prepared at the articulation point of Qaf"),
                TajweedExample("إِنسَان", "نْ + س", "Surah Al-Insaan", "Light ghunnah prepared at the letter Seen"),
                TajweedExample("مِن دُونِ", "نْ + د", "Surah Al-Baqarah", "Subtle light nasal hiding before Dal")
            )
        ),

        // Qalqalah Rules
        TajweedRule(
            id = "rule_qalqalah",
            title = "Qalqalah (Echo / Bouncing Sound)",
            arabicTitle = "أحكام القلقلة",
            category = TajweedCategory.QALQALAH_ECHO,
            colorHex = 0xFF0284C7, // Sky Blue
            explanation = "Producing an echo or bouncing resonance when articulating one of the five Qalqalah letters when they carry a sukoon (ْ) or upon stopping on them at the end of an ayah.",
            letters = "ق ، ط ، ب ، ج ، د (قُطْبُ جَدٍّ)",
            countsOrDuration = "Vibrant quick release (bounce)",
            examples = listOf(
                TajweedExample("قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ", "قْ at stop", "Surah Al-Falaq", "Major Qalqalah (Kubra) with a clear, sharp release"),
                TajweedExample("قُلْ هُوَ اللَّهُ أَحَدٌ", "دْ at stop", "Surah Al-Ikhlas", "Echo the final Dal sharply when stopping"),
                TajweedExample("يَجْعَلُونَ", "جْ in middle", "Surah Al-Baqarah", "Minor Qalqalah (Sughra) bounce within the word")
            )
        ),

        // Madd (Elongation) Rules
        TajweedRule(
            id = "rule_madd_muttasil",
            title = "Madd Muttasil (Connected Prolongation)",
            arabicTitle = "المد الواجب المتصل",
            category = TajweedCategory.MADD_ELONGATION,
            colorHex = 0xFFDC2626, // Red
            explanation = "When a Madd letter (ا، و، ي) is followed by a Hamzah (ء) in the SAME word. Marked with a wavy tilde (~) in the Mushaf.",
            letters = "Madd letter + Hamzah in single word",
            countsOrDuration = "4 or 5 Harakat (Compulsory)",
            examples = listOf(
                TajweedExample("إِذَا جَاءَ نَصْرُ اللَّهِ", "جَـآءَ", "Surah An-Nasr", "Hold the 'aa' for 4 to 5 counts"),
                TajweedExample("وَالسَّمَاءِ وَالطَّارِقِ", "السَّمَـآءِ", "Surah At-Tariq", "Elongate smoothly for 4 to 5 counts")
            )
        ),
        TajweedRule(
            id = "rule_madd_munfasil",
            title = "Madd Munfasil (Separated Prolongation)",
            arabicTitle = "المد الجائز المنفصل",
            category = TajweedCategory.MADD_ELONGATION,
            colorHex = 0xFFF97316, // Orange
            explanation = "When a Madd letter is at the end of one word and the Hamzah (ء) is at the beginning of the FOLLOWING word.",
            letters = "Madd letter at end + Hamzah at start of next word",
            countsOrDuration = "4 or 5 Harakat (Permissible: 2, 4, or 5)",
            examples = listOf(
                TajweedExample("إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ", "إِنَّـآ أَعْطَيْنَاكَ", "Surah Al-Kawthar", "Elongate 4 to 5 counts before transition"),
                TajweedExample("يَا أَيُّهَا الَّذِينَ آمَنُوا", "يَـآ أَيُّهَا", "Surah Al-Baqarah", "Elongate across the word boundary")
            )
        ),
        TajweedRule(
            id = "rule_madd_lazim",
            title = "Madd Lazim (Compulsory 6-Count Long Elongation)",
            arabicTitle = "المد اللازم",
            category = TajweedCategory.MADD_ELONGATION,
            colorHex = 0xFF991B1B, // Deep Crimson
            explanation = "When a Madd letter is followed by an original Sukoon or Shaddah (ّ) in the same word. It is mandatory for all Qira'at readers to hold for 6 full counts.",
            letters = "Madd letter + Shaddah / Sukoon",
            countsOrDuration = "6 Harakat (Mandatory)",
            examples = listOf(
                TajweedExample("غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ", "الضَّـآلِّينَ", "Surah Al-Fatihah", "Must hold for a full 6 beats before pressing the Lam with shaddah"),
                TajweedExample("الْحَاقَّةُ", "الْحَـآقَّةُ", "Surah Al-Haqqah", "6 full counts with heavy resonance")
            )
        ),

        // Meem Sakinah Rules
        TajweedRule(
            id = "rule_meem_sakinah",
            title = "Meem Sakinah (Rules of Silent Meem)",
            arabicTitle = "أحكام الميم الساكنة",
            category = TajweedCategory.MEEM_SAKINAH,
            colorHex = 0xFF8B5CF6, // Purple
            explanation = "When a silent Meem (مْ) appears: 1. Ikhfa Shafawi before Baa (ب) with Ghunnah, 2. Idgham Mithlayn before another Meem (م) with Ghunnah, 3. Izhar Shafawi before all remaining letters.",
            letters = "ب (Ikhfa) | م (Idgham) | Remaining 26 (Izhar)",
            countsOrDuration = "2 Harakat for Ikhfa & Idgham",
            examples = listOf(
                TajweedExample("تَرْمِيهِم بِحِجَارَةٍ", "مْ + ب", "Surah Al-Fil", "Ikhfa Shafawi: soften lips with 2 counts of Ghunnah"),
                TajweedExample("لَهُم مَّا يَشَاءُونَ", "مْ + م", "Surah Az-Zumar", "Idgham Mithlayn: merge into a single stressed Meem with Ghunnah"),
                TajweedExample("أَلَمْ تَرَ", "مْ + ت", "Surah Al-Fil", "Izhar Shafawi: pronounce Meem clearly without pause")
            )
        ),

        // Makharij al-Huroof
        TajweedRule(
            id = "rule_makharij",
            title = "Makharij al-Huroof (The 5 Articulation Points)",
            arabicTitle = "مخارج الحروف الرئيسية",
            category = TajweedCategory.MAKHARIJ,
            colorHex = 0xFFD97706, // Amber
            explanation = "Where Arabic letters originate in the vocal tract: 1. Al-Jawf (Empty space in mouth/throat for long vowels), 2. Al-Halq (Throat), 3. Al-Lisan (Tongue - 18 letters), 4. Ash-Shafatayn (Lips - ف، ب، م، و), 5. Al-Khayshoom (Nasal cavity for Ghunnah).",
            letters = "Jawf, Halq, Lisan, Shafatayn, Khayshoom",
            countsOrDuration = "Foundational Articulation",
            examples = listOf(
                TajweedExample("Al-Halq (Throat)", "ء، هـ (Deep) | ع، ح (Middle) | غ، خ (Upper)", "Throat Letters", "Clear pronunciation from proper depth"),
                TajweedExample("Al-Lisan (Tongue)", "ق، ك (Back) | ج، ش، ي (Center) | ض، ل، ن، ر (Sides & Tip)", "Tongue Letters", "Precise tongue positioning")
            )
        )
    )

    val QUIZ_QUESTIONS: List<TajweedQuizQuestion> = listOf(
        TajweedQuizQuestion(
            id = "q1",
            question = "What Tajweed rule applies when a Noon Sakinah is followed by the letter Baa (ب)?",
            arabicQuestion = "ما هو الحكم التجويدي عند التقاء النون الساكنة بحرف الباء؟",
            options = listOf("Izhar", "Idgham", "Iqlab", "Ikhfa"),
            correctIndex = 2,
            explanation = "Iqlab (الإقلاب) occurs when Noon Sakinah or Tanween is followed by Baa, turning it into a hidden Meem with Ghunnah."
        ),
        TajweedQuizQuestion(
            id = "q2",
            question = "How many counts (harakat) must Madd Lazim (e.g. in 'ولا الضالين') be held?",
            arabicQuestion = "كم عدد حركات المد اللازم (مثل 'ولا الضالين')؟",
            options = listOf("2 counts", "4 counts", "6 counts", "8 counts"),
            correctIndex = 2,
            explanation = "Madd Lazim (المد اللازم) is compulsory and must be held for 6 full harakat (counts)."
        ),
        TajweedQuizQuestion(
            id = "q3",
            question = "Which letters are the letters of Qalqalah (the bouncing echo letters)?",
            arabicQuestion = "ما هي حروف القلقلة؟",
            options = listOf("ي ، ن ، م ، و", "ق ، ط ، ب ، ج ، د", "ء ، هـ ، ع ، ح", "ل ، ر"),
            correctIndex = 1,
            explanation = "The five Qalqalah letters are combined in the mnemonic 'قطب جد' (Qaf, Taa, Baa, Jeem, Dal)."
        ),
        TajweedQuizQuestion(
            id = "q4",
            question = "Which of the following is an example of Idgham without Ghunnah?",
            arabicQuestion = "أي مما يلي يُعد مثالاً على الإدغام بغير غنة؟",
            options = listOf("مَن يَقُولُ", "مِن رَّبِّهِمْ", "مِن بَعْدِ", "مَنْ آمَنَ"),
            correctIndex = 1,
            explanation = "'مِن رَّبِّهِمْ' is Idgham without Ghunnah because the Noon merges into the Ra (ر) completely without nasal sound."
        )
    )
}
