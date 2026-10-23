package com.example.data

import com.example.model.DailyVerse
import com.example.model.QuranAyah
import com.example.model.QuranSurah

object QuranData {
    val SURAHS: List<QuranSurah> = listOf(
        QuranSurah(
            number = 1,
            englishName = "Al-Fatihah",
            arabicName = "الفاتحة",
            englishTranslation = "The Opening",
            totalVerses = 7,
            revelationType = "Meccan",
            ayahs = listOf(
                QuranAyah(1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "In the name of Allah, the Entirely Merciful, the Especially Merciful.", "Bismillaahir-Rahmaanir-Raheem"),
                QuranAyah(2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "[All] praise is [due] to Allah, Lord of the worlds -", "Alhamdu lillaahi Rabbil 'aalameen"),
                QuranAyah(3, "الرَّحْمَٰنِ الرَّحِيمِ", "The Entirely Merciful, the Especially Merciful,", "Ar-Rahmaanir-Raheem"),
                QuranAyah(4, "مَالِكِ يَوْمِ الدِّينِ", "Sovereign of the Day of Recompense.", "Maaliki Yawmid-Deen"),
                QuranAyah(5, "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", "It is You we worship and You we ask for help.", "Iyyaaka na'budu wa lyyaaka nasta'een"),
                QuranAyah(6, "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", "Guide us to the straight path -", "Ihdinas-Siraatal-Mustaqeem"),
                QuranAyah(7, "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ", "The path of those upon whom You have bestowed favor, not of those who have evoked [Your] anger or of those who are astray.", "Siraatal-lazeena an'amta 'alayhim ghayril-maghdoobi 'alayhim wa lad-daalleen")
            )
        ),
        QuranSurah(
            number = 2,
            englishName = "Ayat Al-Kursi (Al-Baqarah 255)",
            arabicName = "آية الكرسي",
            englishTranslation = "The Throne Verse",
            totalVerses = 1,
            revelationType = "Medinan",
            ayahs = listOf(
                QuranAyah(
                    numberInSurah = 255,
                    arabicText = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ",
                    translation = "Allah - there is no deity except Him, the Ever-Living, the Sustainer of all existence. Neither drowsiness overtakes Him nor sleep. To Him belongs whatever is in the heavens and whatever is on the earth. Who is it that can intercede with Him except by His permission? He knows what is before them and what will be after them, and they encompass not a thing of His knowledge except for what He wills. His Kursi extends over the heavens and the earth, and their preservation tires Him not. And He is the Most High, the Most Great.",
                    transliteration = "Allahu laaa ilaaha illaa Huwal Hayyul Qayyoom; laa ta'khuzuhoo sinatunw wa laa nawm; lahoo maa fis samaawaati wa maa fil ard..."
                )
            )
        ),
        QuranSurah(
            number = 67,
            englishName = "Al-Mulk",
            arabicName = "الملك",
            englishTranslation = "The Sovereignty",
            totalVerses = 5,
            revelationType = "Meccan",
            ayahs = listOf(
                QuranAyah(1, "تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ", "Blessed is He in whose hand is dominion, and He is over all things competent -", "Tabaarakal-lazee biyadihil-mulku wa Huwa 'alaa kulli shay'in Qadeer"),
                QuranAyah(2, "الَّذِي خَلَقَ الْمَوْتَ وَالْحَيَاةَ لِيَبْلُوَكُمْ أَيُّكُمْ أَحْسَنُ عَمَلًا ۚ وَهُوَ الْعَزِيزُ الْغَفُورُ", "[He] who created death and life to test you [as to] which of you is best in deed - and He is the Exalted in Might, the Forgiving -", "Allazee khalaqal mawta walhayaata liyabluwakum ayyukum ahsanu 'amalaa..."),
                QuranAyah(3, "الَّذِي خَلَقَ سَبْعَ سَمَاوَاتٍ طِبَاقًا ۖ مَّا تَرَىٰ فِي خَلْقِ الرَّحْمَٰنِ مِن تَفَاوُتٍ", "[And] who created seven heavens in layers. You see not in the creation of the Most Merciful any inconsistency.", "Allazee khalaqa sab'a samaawaatin tibaaqan..."),
                QuranAyah(4, "فَارْجِعِ الْبَصَرَ هَلْ تَرَىٰ مِن فُطُورٍ", "So return [your] vision [to the sky]; do you see any breaks?", "Farji'il-basara hal taraa min futoor"),
                QuranAyah(5, "وَلَقَدْ زَيَّنَّا السَّمَاءَ الدُّنْيَا بِمَصَابِيحَ وَجَعَلْنَاهَا رُجُومًا لِّلشَّيَاطِينِ", "And We have certainly beautified the nearest heaven with lamps [stars]...", "Wa laqad zayyannas-samaaa'ad dunyaa bimasaabeeha...")
            )
        ),
        QuranSurah(
            number = 112,
            englishName = "Al-Ikhlas",
            arabicName = "الإخلاص",
            englishTranslation = "The Sincerity",
            totalVerses = 4,
            revelationType = "Meccan",
            ayahs = listOf(
                QuranAyah(1, "قُلْ هُوَ اللَّهُ أَحَدٌ", "Say, \"He is Allah, [who is] One,", "Qul Huwal-laahu Ahad"),
                QuranAyah(2, "اللَّهُ الصَّمَدُ", "Allah, the Eternal Refuge.", "Allahus-Samad"),
                QuranAyah(3, "لَمْ يَلِدْ وَلَمْ يُولَدْ", "He neither begets nor is born,", "Lam yalid wa lam yoolad"),
                QuranAyah(4, "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ", "Nor is there to Him any equivalent.\"", "Wa lam yakun lahoo kufuwan ahad")
            )
        ),
        QuranSurah(
            number = 113,
            englishName = "Al-Falaq",
            arabicName = "الفلق",
            englishTranslation = "The Daybreak",
            totalVerses = 5,
            revelationType = "Meccan",
            ayahs = listOf(
                QuranAyah(1, "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ", "Say, \"I seek refuge in the Lord of daybreak", "Qul a'oozu bi rabbil-falaq"),
                QuranAyah(2, "مِن شَرِّ مَا خَلَقَ", "From the evil of that which He created", "Min sharri maa khalaq"),
                QuranAyah(3, "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ", "And from the evil of darkness when it settles", "Wa min sharri ghaasiqin izaa waqab"),
                QuranAyah(4, "وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ", "And from the evil of the blowers in knots", "Wa min sharrin-naffaasaati fil 'uqad"),
                QuranAyah(5, "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ", "And from the evil of an envier when he envies.\"", "Wa min sharri haasidin izaa hasad")
            )
        ),
        QuranSurah(
            number = 114,
            englishName = "An-Nas",
            arabicName = "الناس",
            englishTranslation = "Mankind",
            totalVerses = 6,
            revelationType = "Meccan",
            ayahs = listOf(
                QuranAyah(1, "قُلْ أَعُوذُ بِرَبِّ النَّاسِ", "Say, \"I seek refuge in the Lord of mankind,", "Qul a'oozu bi rabbin-naas"),
                QuranAyah(2, "مَلِكِ النَّاسِ", "The Sovereign of mankind,", "Malikin-naas"),
                QuranAyah(3, "إِلَٰهِ النَّاسِ", "The God of mankind,", "Ilaahin-naas"),
                QuranAyah(4, "مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ", "From the evil of the retreating whisperer -", "Min sharril-waswaasil-khannaas"),
                QuranAyah(5, "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ", "Who whispers into the breasts of mankind -", "Allazee yuwaswisu fee sudoorin-naas"),
                QuranAyah(6, "مِنَ الْجِنَّةِ وَالنَّاسِ", "From among the jinn and mankind.\"", "Minal-jinnati wan-naas")
            )
        )
    )

    val INITIAL_DAILY_VERSES: List<DailyVerse> = listOf(
        DailyVerse(
            id = "v_rad_28",
            arabicText = "الَّذِينَ آمَنُوا وَتَطْمَئِنُّ قُلُوبُهُم بِذِكْرِ اللَّهِ ۗ أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ",
            translation = "Those who have believed and whose hearts are assured by the remembrance of Allah. Unquestionably, by the remembrance of Allah hearts are assured.",
            reference = "Surah Ar-Ra'd (13:28)",
            theme = "Peace of Heart"
        ),
        DailyVerse(
            id = "v_baqarah_153",
            arabicText = "يَا أَيُّهَا الَّذِينَ آمَنُوا اسْتَعِينُوا بِالصَّبْرِ وَالصَّلَاةِ ۚ إِنَّ اللَّهَ مَعَ الصَّابِرِينَ",
            translation = "O you who have believed, seek help through patience and prayer. Indeed, Allah is with the patient.",
            reference = "Surah Al-Baqarah (2:153)",
            theme = "Prayer & Patience"
        ),
        DailyVerse(
            id = "v_ankabut_45",
            arabicText = "وَأَقِمِ الصَّلَاةَ ۖ إِنَّ الصَّلَاةَ تَنْهَىٰ عَنِ الْفَحْشَاءِ وَالْمُنكَرِ ۗ وَلَذِكْرُ اللَّهِ أَكْبَرُ",
            translation = "And establish prayer. Indeed, prayer prohibits immorality and wrongdoing, and the remembrance of Allah is greater.",
            reference = "Surah Al-'Ankabut (29:45)",
            theme = "Virtue of Salah"
        ),
        DailyVerse(
            id = "v_sharh_5_6",
            arabicText = "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا • إِنَّ مَعَ الْعُسْرِ يُسْرًا",
            translation = "For indeed, with hardship [will be] ease. Indeed, with hardship [will be] ease.",
            reference = "Surah Ash-Sharh (94:5-6)",
            theme = "Hope & Relief"
        ),
        DailyVerse(
            id = "v_baqarah_186",
            arabicText = "وَإِذَا سَأَلَكَ عِبَادِي عَنِّي فَإِنِّي قَرِيبٌ ۖ أُجِيبُ دَعْوَةَ الدَّاعِ إِذَا دَعَانِ",
            translation = "And when My servants ask you concerning Me, indeed I am near. I respond to the invocation of the supplicant when he calls upon Me.",
            reference = "Surah Al-Baqarah (2:186)",
            theme = "Allah's Closeness"
        ),
        DailyVerse(
            id = "v_taha_14",
            arabicText = "إِنَّنِي أَنَا اللَّهُ لَا إِلَٰهَ إِلَّا أَنَا فَاعْبُدْنِي وَأَقِمِ الصَّلَاةَ لِذِكْرِي",
            translation = "Indeed, I am Allah. There is no deity except Me, so worship Me and establish prayer for My remembrance.",
            reference = "Surah Taha (20:14)",
            theme = "Salah for Remembrance"
        ),
        DailyVerse(
            id = "v_isra_78",
            arabicText = "أَقِمِ الصَّلَاةَ لِدُلُوكِ الشَّمْسِ إِلَىٰ غَسَقِ اللَّيْلِ وَقُرْآنَ الْفَجْرِ ۖ إِنَّ قُرْآنَ الْفَجْرِ كَانَ مَشْهُودًا",
            translation = "Establish prayer at the decline of the sun until the darkness of the night and [also] the Quran of dawn. Indeed, the recitation of dawn is ever witnessed.",
            reference = "Surah Al-Isra (17:78)",
            theme = "Times of Salah"
        )
    )
}
