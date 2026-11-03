package com.example.voice

import java.util.Locale

enum class VoiceGender {
    FEMALE,
    MALE
}

enum class VoiceLanguage(val label: String, val locale: Locale) {
    HINDI("Hindi (हिंदी)", Locale.forLanguageTag("hi-IN")),
    ENGLISH("English (US/UK)", Locale.US),
    BILINGUAL("Hinglish / Auto", Locale.forLanguageTag("hi-IN"))
}

data class VoiceProfile(
    val id: String,
    val name: String,
    val gender: VoiceGender,
    val language: VoiceLanguage,
    val pitch: Float,
    val speechRate: Float,
    val description: String
) {
    companion object {
        // Hindi Female AI Voice (Default for ANX AI as requested)
        val ANANYA = VoiceProfile(
            id = "ananya_hindi_female",
            name = "Ananya",
            gender = VoiceGender.FEMALE,
            language = VoiceLanguage.HINDI,
            pitch = 1.15f,
            speechRate = 0.98f,
            description = "Natural Hindi female voice — warm, polite & friendly"
        )

        // English Futuristic Female AI Voice
        val NOVA = VoiceProfile(
            id = "nova_english_female",
            name = "Nova",
            gender = VoiceGender.FEMALE,
            language = VoiceLanguage.ENGLISH,
            pitch = 1.25f,
            speechRate = 1.02f,
            description = "Futuristic 3D English female voice — articulate & energetic"
        )

        // Hindi Male Voice
        val AARAV = VoiceProfile(
            id = "aarav_hindi_male",
            name = "Aarav",
            gender = VoiceGender.MALE,
            language = VoiceLanguage.HINDI,
            pitch = 0.90f,
            speechRate = 0.96f,
            description = "Intelligent Hindi & Hinglish male voice — calm & deep"
        )

        // English Male Voice
        val ZEPHYR = VoiceProfile(
            id = "zephyr_english_male",
            name = "Zephyr",
            gender = VoiceGender.MALE,
            language = VoiceLanguage.ENGLISH,
            pitch = 0.86f,
            speechRate = 1.00f,
            description = "Confident English tech assistant male voice"
        )

        val ALL_PROFILES = listOf(ANANYA, NOVA, AARAV, ZEPHYR)
        val DEFAULT = ANANYA
    }
}
