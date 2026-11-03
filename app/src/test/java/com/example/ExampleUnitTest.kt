package com.example

import com.example.voice.VoiceGender
import com.example.voice.VoiceProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun voiceProfiles_containMaleAndFemaleOptions() {
        val profiles = VoiceProfile.ALL_PROFILES
        assertTrue(profiles.any { it.gender == VoiceGender.FEMALE })
        assertTrue(profiles.any { it.gender == VoiceGender.MALE })
        assertEquals("Ananya", VoiceProfile.DEFAULT.name)
    }

    @Test
    fun taskRegex_matchesAddTaskCommand() {
        val input = "add buy groceries to my to-do list"
        val regex = Regex("""(?:add)\s+(.+?)\s+(?:to\s+(?:my\s+)?(?:to-do|todo|task)\s*(?:list)?)""", RegexOption.IGNORE_CASE)
        val match = regex.find(input)
        assertNotNull(match)
        assertEquals("buy groceries", match?.groupValues?.get(1)?.trim())
    }

    @Test
    fun taskRegex_matchesHindiAddTaskCommand() {
        val input = "mera task add karo: study Kotlin Coroutines"
        val regex = Regex("""(?:mera\s+task\s+add\s+karo|task\s+add\s+karo|to-do\s+me\s+add\s+karo)\s*[:\-]?\s*(.+)""", RegexOption.IGNORE_CASE)
        val match = regex.find(input)
        assertNotNull(match)
        assertEquals("study Kotlin Coroutines", match?.groupValues?.get(1)?.trim())
    }

    @Test
    fun taskRegex_matchesReminderCommand() {
        val input = "remind me to call Rahul at 5 PM"
        val regex = Regex("""remind\s+(?:me\s+)?(?:to\s+)?(.+?)\s+at\s+([0-9a-zA-Z:\s]+)""", RegexOption.IGNORE_CASE)
        val match = regex.find(input)
        assertNotNull(match)
        assertEquals("call Rahul", match?.groupValues?.get(1)?.trim())
        assertEquals("5 PM", match?.groupValues?.get(2)?.trim())
    }
}
