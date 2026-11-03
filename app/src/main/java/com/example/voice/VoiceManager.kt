package com.example.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.sin

class VoiceManager(
    private val context: Context,
    private val scope: CoroutineScope
) : TextToSpeech.OnInitListener, RecognitionListener {

    private val tag = "VoiceManager"

    // State flows
    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _audioRmsLevel = MutableStateFlow(0f)
    val audioRmsLevel: StateFlow<Float> = _audioRmsLevel.asStateFlow()

    private val _activeProfile = MutableStateFlow(VoiceProfile.DEFAULT)
    val activeProfile: StateFlow<VoiceProfile> = _activeProfile.asStateFlow()

    private val _speechInputLanguage = MutableStateFlow("auto") // "auto", "hi-IN", "en-IN"
    val speechInputLanguage: StateFlow<String> = _speechInputLanguage.asStateFlow()

    // Engines
    private var textToSpeech: TextToSpeech? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private var isTtsReady = false

    private var onSpeechResultCallback: ((String) -> Unit)? = null
    private var onSpeechErrorCallback: ((String) -> Unit)? = null

    private var waveSimulationJob: Job? = null

    init {
        try {
            textToSpeech = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e(tag, "Failed to initialize TTS", e)
        }

        initializeSpeechRecognizer()
    }

    private fun initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            try {
                speechRecognizer?.destroy()
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(this@VoiceManager)
                }
            } catch (e: Exception) {
                Log.e(tag, "Error creating SpeechRecognizer", e)
            }
        } else {
            Log.w(tag, "SpeechRecognizer is NOT available on this device")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsReady = true
            textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                    startSpeechWaveAnimation()
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                    stopSpeechWaveAnimation()
                }

                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                    stopSpeechWaveAnimation()
                }
            })
            applyVoiceProfile(_activeProfile.value)
        } else {
            Log.e(tag, "TTS init failed with status: $status")
        }
    }

    fun setVoiceProfile(profile: VoiceProfile) {
        _activeProfile.value = profile
        applyVoiceProfile(profile)
    }

    fun setSpeechInputLanguage(langCode: String) {
        _speechInputLanguage.value = langCode
    }

    private fun applyVoiceProfile(profile: VoiceProfile) {
        val tts = textToSpeech ?: return
        if (!isTtsReady) return

        try {
            tts.setPitch(profile.pitch)
            tts.setSpeechRate(profile.speechRate)

            // Try to set specific voice matching gender if supported by engine
            val availableVoices = try {
                tts.voices
            } catch (_: Exception) {
                null
            }

            if (!availableVoices.isNullOrEmpty()) {
                val isFemale = profile.gender == VoiceGender.FEMALE
                val matchingVoice = availableVoices.firstOrNull { voice: Voice ->
                    val name = voice.name.lowercase()
                    if (isFemale) {
                        (name.contains("female") || name.contains("woman") || name.contains("-f-") || name.contains("_f_")) &&
                                (voice.locale.language == profile.language.locale.language)
                    } else {
                        (name.contains("male") || name.contains("man") || name.contains("-m-") || name.contains("_m_")) &&
                                (voice.locale.language == profile.language.locale.language)
                    }
                }
                if (matchingVoice != null) {
                    tts.voice = matchingVoice
                } else {
                    tts.language = profile.language.locale
                }
            } else {
                tts.language = profile.language.locale
            }
        } catch (e: Exception) {
            Log.w(tag, "Failed to apply full voice profile: ${e.message}")
        }
    }

    fun speak(text: String) {
        if (!isTtsReady || textToSpeech == null) {
            Log.w(tag, "TTS not ready yet")
            return
        }

        // Clean markdown symbols for natural speech
        val cleaned = cleanForSpeech(text)
        if (cleaned.isBlank()) return

        stopSpeaking()

        val tts = textToSpeech ?: return
        val currentProfile = _activeProfile.value

        // Check if text has Devanagari Hindi characters
        val hasHindiCharacters = cleaned.any { it in '\u0900'..'\u097F' }
        if (hasHindiCharacters) {
            tts.language = Locale.forLanguageTag("hi-IN")
        } else {
            tts.language = currentProfile.language.locale
        }

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "adarsh_utterance_${System.currentTimeMillis()}")
        }

        tts.speak(cleaned, TextToSpeech.QUEUE_FLUSH, params, "adarsh_utterance")
    }

    fun stopSpeaking() {
        stopSpeechWaveAnimation()
        try {
            textToSpeech?.stop()
        } catch (e: Exception) {
            Log.w(tag, "Error stopping TTS: ${e.message}")
        }
        _isSpeaking.value = false
    }

    private fun cleanForSpeech(raw: String): String {
        return raw
            .replace(Regex("```[\\s\\S]*?```"), "Code snippet.")
            .replace(Regex("`[^`]*`"), "")
            .replace(Regex("\\[([^\\]]+)\\]\\([^\\)]+\\)"), "$1")
            .replace(Regex("[*#_~]"), " ")
            .replace(Regex("https?://\\S+"), "")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    fun startListening(
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        stopSpeaking()
        onSpeechResultCallback = onResult
        onSpeechErrorCallback = onError

        if (speechRecognizer == null) {
            initializeSpeechRecognizer()
        }

        val recognizer = speechRecognizer
        if (recognizer == null) {
            onError("Speech recognition is not available on this device.")
            return
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)

            // Language config
            val lang = _speechInputLanguage.value
            when (lang) {
                "hi-IN" -> {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "hi-IN")
                }
                "en-IN" -> {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-IN")
                }
                else -> {
                    // Auto / Bilingual supports English and Hindi
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")
                    putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", arrayOf("hi-IN", "en-US"))
                }
            }
        }

        try {
            recognizer.startListening(intent)
            _isListening.value = true
        } catch (e: Exception) {
            Log.e(tag, "Error starting SpeechRecognizer", e)
            _isListening.value = false
            onError("Unable to start listening: ${e.localizedMessage}")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            Log.w(tag, "Error stopping SpeechRecognizer: ${e.message}")
        }
        _isListening.value = false
        _audioRmsLevel.value = 0f
    }

    // SpeechRecognizer listener overrides
    override fun onReadyForSpeech(params: Bundle?) {
        _isListening.value = true
    }

    override fun onBeginningOfSpeech() {
        _isListening.value = true
    }

    override fun onRmsChanged(rmsdB: Float) {
        if (_isListening.value) {
            // Normalize -2dB to +10dB into 0f..1f range
            val normalized = ((rmsdB + 2f) / 12f).coerceIn(0.05f, 1.0f)
            _audioRmsLevel.value = normalized
        }
    }

    override fun onBufferReceived(buffer: ByteArray?) {}

    override fun onEndOfSpeech() {
        _isListening.value = false
        _audioRmsLevel.value = 0f
    }

    override fun onError(error: Int) {
        _isListening.value = false
        _audioRmsLevel.value = 0f

        val errorMessage = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error. Please check microphone."
            SpeechRecognizer.ERROR_CLIENT -> "Client speech error."
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required."
            SpeechRecognizer.ERROR_NETWORK -> "Network error during speech recognition."
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout."
            SpeechRecognizer.ERROR_NO_MATCH -> "Couldn't catch that. Please speak again clearly."
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Voice recognizer is busy."
            SpeechRecognizer.ERROR_SERVER -> "Server error from voice recognizer."
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected."
            else -> "Speech recognition error ($error)"
        }
        onSpeechErrorCallback?.invoke(errorMessage)
    }

    override fun onResults(results: Bundle?) {
        _isListening.value = false
        _audioRmsLevel.value = 0f

        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val text = matches?.firstOrNull()
        if (!text.isNullOrBlank()) {
            onSpeechResultCallback?.invoke(text.trim())
        } else {
            onSpeechErrorCallback?.invoke("No words recognized.")
        }
    }

    override fun onPartialResults(partialResults: Bundle?) {
        // Can be used for live transcription if desired
    }

    override fun onEvent(eventType: Int, params: Bundle?) {}

    private fun startSpeechWaveAnimation() {
        waveSimulationJob?.cancel()
        waveSimulationJob = scope.launch(Dispatchers.Default) {
            var step = 0.0
            while (isActive && _isSpeaking.value) {
                // Generate a lively dynamic wave level between 0.2f and 0.9f
                val dynamicLevel = (0.55f + 0.35f * sin(step)).toFloat()
                _audioRmsLevel.value = dynamicLevel
                step += 0.35
                delay(60)
            }
            _audioRmsLevel.value = 0f
        }
    }

    private fun stopSpeechWaveAnimation() {
        waveSimulationJob?.cancel()
        waveSimulationJob = null
        if (!_isListening.value) {
            _audioRmsLevel.value = 0f
        }
    }

    fun onDestroy() {
        stopSpeaking()
        stopListening()
        try {
            speechRecognizer?.destroy()
            textToSpeech?.shutdown()
        } catch (e: Exception) {
            Log.w(tag, "Cleanup error: ${e.message}")
        }
    }
}
