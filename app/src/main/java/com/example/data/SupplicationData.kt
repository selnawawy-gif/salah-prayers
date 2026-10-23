package com.example.data

import com.example.model.SupplicationCategory
import com.example.model.SupplicationItem

object SupplicationData {
    val ITEMS: List<SupplicationItem> = listOf(
        // Post Prayer Adhkar
        SupplicationItem(
            id = "adhkar_post_istighfar",
            category = SupplicationCategory.AFTER_PRAYER,
            title = "Istighfar (Seeking Forgiveness)",
            arabicText = "أَسْتَغْفِرُ اللَّهَ ، أَسْتَغْفِرُ اللَّهَ ، أَسْتَغْفِرُ اللَّهَ",
            translation = "I seek the forgiveness of Allah (three times).",
            transliteration = "Astaghfirullah, Astaghfirullah, Astaghfirullah",
            reference = "Sahih Muslim",
            targetCount = 3,
            virtue = "Said immediately after finishing each obligatory prayer."
        ),
        SupplicationItem(
            id = "adhkar_post_salam",
            category = SupplicationCategory.AFTER_PRAYER,
            title = "Allahumma Antas-Salam",
            arabicText = "اللَّهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ ، تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ",
            translation = "O Allah, You are Peace and from You comes peace. Blessed are You, O Owner of majesty and honor.",
            transliteration = "Allahumma Antas-Salamu wa minkas-salam, tabarakta ya Dhal-Jalali wal-Ikram",
            reference = "Sahih Muslim 591",
            targetCount = 1
        ),
        SupplicationItem(
            id = "adhkar_tasbih_33",
            category = SupplicationCategory.AFTER_PRAYER,
            title = "Tasbih (SubhanAllah)",
            arabicText = "سُبْحَانَ اللَّهِ",
            translation = "Glory be to Allah.",
            transliteration = "SubhanAllah",
            reference = "Sahih Muslim",
            targetCount = 33,
            virtue = "Recited 33 times after each prayer."
        ),
        SupplicationItem(
            id = "adhkar_tahmid_33",
            category = SupplicationCategory.AFTER_PRAYER,
            title = "Tahmid (Alhamdulillah)",
            arabicText = "الْحَمْدُ لِلَّهِ",
            translation = "Praise be to Allah.",
            transliteration = "Alhamdulillah",
            reference = "Sahih Muslim",
            targetCount = 33,
            virtue = "Recited 33 times after each prayer."
        ),
        SupplicationItem(
            id = "adhkar_takbir_33",
            category = SupplicationCategory.AFTER_PRAYER,
            title = "Takbir (Allahu Akbar)",
            arabicText = "اللَّهُ أَكْبَرُ",
            translation = "Allah is the Greatest.",
            transliteration = "Allahu Akbar",
            reference = "Sahih Muslim",
            targetCount = 33,
            virtue = "Recited 33 times after each prayer."
        ),
        SupplicationItem(
            id = "adhkar_tahlil_100",
            category = SupplicationCategory.AFTER_PRAYER,
            title = "Completion of 100",
            arabicText = "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
            translation = "There is no deity except Allah alone, with no partner. His is the dominion and His is the praise, and He has power over all things.",
            transliteration = "La ilaha illallahu wahdahu la shareeka lah, lahul-mulku wa lahul-hamdu wa Huwa 'ala kulli shay'in qadeer",
            reference = "Sahih Muslim 597",
            targetCount = 1,
            virtue = "Completes the 100 post-prayer dhikr for forgiveness of sins even if like sea foam."
        ),

        // Morning Adhkar
        SupplicationItem(
            id = "adhkar_morn_asbahna",
            category = SupplicationCategory.MORNING,
            title = "Asbahna wa Asbahal-Mulk",
            arabicText = "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ",
            translation = "We have entered the morning and kingdom belongs to Allah, all praise is due to Allah. None has the right to be worshipped except Allah alone, without partner.",
            transliteration = "Asbahna wa asbahal-mulku lillah, wal-hamdu lillah, la ilaha illallahu wahdahu la shareeka lah",
            reference = "Sahih Muslim 2723",
            targetCount = 1
        ),
        SupplicationItem(
            id = "adhkar_sayyid_istighfar",
            category = SupplicationCategory.MORNING,
            title = "Sayyid al-Istighfar (Chief of Prayers for Forgiveness)",
            arabicText = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ لَكَ بِذَنْبِي، فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
            translation = "O Allah, You are my Lord, there is no deity worthy of worship except You. You created me and I am Your servant, and I abide by Your covenant and promise as best I can. I seek refuge in You from the evil of what I have done. I acknowledge Your favor upon me and I acknowledge my sin, so forgive me, for none forgives sins except You.",
            transliteration = "Allahumma Anta Rabbi la ilaha illa Ant, khalaqtani wa ana 'abduk...",
            reference = "Sahih al-Bukhari 6306",
            targetCount = 1,
            virtue = "Whoever recites it during the morning with conviction and dies that day will enter Paradise."
        ),
        SupplicationItem(
            id = "adhkar_bismillah_alladhi",
            category = SupplicationCategory.MORNING,
            title = "Protection in Earth and Heaven",
            arabicText = "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
            translation = "In the name of Allah, with whose name nothing on earth or in the heavens can cause harm, and He is the All-Hearing, All-Knowing.",
            transliteration = "Bismillahil-ladhi la yadurru ma'as-mihi shay'un fil-ardi wa la fis-samaa'i wa Huwas-Samee'ul-'Aleem",
            reference = "Abu Dawood & At-Tirmidhi",
            targetCount = 3,
            virtue = "Recited 3 times morning and evening for complete protection."
        ),

        // Evening Adhkar
        SupplicationItem(
            id = "adhkar_eve_amsayna",
            category = SupplicationCategory.EVENING,
            title = "Amsayna wa Amsal-Mulk",
            arabicText = "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ",
            translation = "We have reached the evening and dominion belongs to Allah, and all praise is for Allah. There is no deity except Allah alone.",
            transliteration = "Amsayna wa amsal-mulku lillah, wal-hamdu lillah...",
            reference = "Sahih Muslim",
            targetCount = 1
        ),
        SupplicationItem(
            id = "adhkar_aoodhu_bikalimatillah",
            category = SupplicationCategory.EVENING,
            title = "Refuge in the Perfect Words of Allah",
            arabicText = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ",
            translation = "I seek refuge in the perfect words of Allah from the evil of what He has created.",
            transliteration = "A'oodhu bikalimatil-lahit-tammati min sharri ma khalaq",
            reference = "Sahih Muslim 2709",
            targetCount = 3,
            virtue = "Guards against harm during the night."
        ),

        // Daily Duas
        SupplicationItem(
            id = "dua_waking_up",
            category = SupplicationCategory.DAILY_DUAS,
            title = "Upon Waking Up",
            arabicText = "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
            translation = "All praise is for Allah who gave us life after having taken it from us and unto Him is the resurrection.",
            transliteration = "Alhamdu lillahil-ladhi ahyana ba'da ma amatana wa ilayhin-nushoor",
            reference = "Sahih al-Bukhari",
            targetCount = 1
        ),
        SupplicationItem(
            id = "dua_leaving_home",
            category = SupplicationCategory.DAILY_DUAS,
            title = "When Leaving Home",
            arabicText = "بِسْمِ اللَّهِ تَوَكَّلْتُ عَلَى اللَّهِ ، لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
            translation = "In the name of Allah, I place my trust in Allah. There is no might nor power except by Allah.",
            transliteration = "Bismillahi tawakkaltu 'alallah, wa la hawla wa la quwwata illa billah",
            reference = "Abu Dawood & At-Tirmidhi",
            targetCount = 1
        )
    )
}
