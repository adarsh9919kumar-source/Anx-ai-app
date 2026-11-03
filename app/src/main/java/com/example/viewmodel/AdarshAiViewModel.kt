package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiApiClient
import com.example.data.local.AdarshAiDatabase
import com.example.data.local.ChatMessageEntity
import com.example.data.local.ChatSessionEntity
import com.example.data.local.TaskEntity
import com.example.phone.SmartphoneAssistant
import com.example.voice.VoiceManager
import com.example.voice.VoiceProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class AdarshAiViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AdarshAiDatabase.getInstance(application)
    private val chatDao = db.chatDao()
    private val taskDao = db.taskDao()

    val voiceManager = VoiceManager(application, viewModelScope)

    // User name state
    private val _userName = MutableStateFlow("Adarsh")
    val userName: StateFlow<String> = _userName.asStateFlow()

    // Current chat session
    private val _currentSessionId = MutableStateFlow(createInitialSessionId())
    val currentSessionId: StateFlow<String> = _currentSessionId.asStateFlow()

    // Observe messages for current session
    val currentMessages: StateFlow<List<ChatMessageEntity>> = _currentSessionId
        .flatMapLatest { sessionId ->
            chatDao.getMessagesForSession(sessionId)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Observe all saved sessions
    val savedSessions: StateFlow<List<ChatSessionEntity>> = chatDao.getAllSessions()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Observe all tasks
    val allTasks: StateFlow<List<TaskEntity>> = taskDao.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // AI thinking state
    private val _isThinking = MutableStateFlow(false)
    val isThinking: StateFlow<Boolean> = _isThinking.asStateFlow()

    // Temporary info/error banner
    private val _bannerMessage = MutableStateFlow<String?>(null)
    val bannerMessage: StateFlow<String?> = _bannerMessage.asStateFlow()

    // Auto-speak responses toggle
    private val _autoSpeakEnabled = MutableStateFlow(true)
    val autoSpeakEnabled: StateFlow<Boolean> = _autoSpeakEnabled.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val sid = _currentSessionId.value
            chatDao.insertSession(
                ChatSessionEntity(
                    sessionId = sid,
                    title = "New Conversation",
                    createdAt = System.currentTimeMillis(),
                    lastUpdated = System.currentTimeMillis()
                )
            )
        }
    }

    private fun createInitialSessionId(): String = "session_${UUID.randomUUID()}"

    fun updateUserName(name: String) {
        val clean = name.trim()
        if (clean.isNotBlank()) {
            _userName.value = clean
        }
    }

    fun toggleAutoSpeak() {
        _autoSpeakEnabled.value = !_autoSpeakEnabled.value
    }

    fun dismissBanner() {
        _bannerMessage.value = null
    }

    fun selectVoiceProfile(profile: VoiceProfile) {
        voiceManager.setVoiceProfile(profile)
        _bannerMessage.value = "AI voice set to: ${profile.name} (${profile.description})"
    }

    fun setSpeechLanguage(langCode: String) {
        voiceManager.setSpeechInputLanguage(langCode)
        val name = when (langCode) {
            "hi-IN" -> "Hindi (हिंदी)"
            "en-IN" -> "English (India)"
            else -> "Auto (Hindi + English)"
        }
        _bannerMessage.value = "Voice recognition set to: $name"
    }

    fun startListening() {
        _bannerMessage.value = null
        voiceManager.startListening(
            onResult = { recognizedText ->
                sendMessage(recognizedText, isVoiceInitiated = true)
            },
            onError = { error ->
                _bannerMessage.value = error
            }
        )
    }

    fun stopListening() {
        voiceManager.stopListening()
    }

    fun stopSpeaking() {
        voiceManager.stopSpeaking()
    }

    fun speakText(text: String) {
        voiceManager.speak(text)
    }

    // Direct task management methods
    fun addTask(title: String, reminderTime: String? = null) {
        if (title.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.insertTask(
                TaskEntity(
                    title = title.trim(),
                    reminderTime = reminderTime?.trim()?.ifBlank { null }
                )
            )
        }
    }

    fun toggleTaskCompletion(task: TaskEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.setTaskCompleted(task.id, !task.isCompleted)
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.deleteTask(taskId)
        }
    }

    fun clearCompletedTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.clearCompletedTasks()
        }
    }

    fun sendMessage(userText: String, isVoiceInitiated: Boolean = false) {
        val trimmed = userText.trim()
        if (trimmed.isBlank()) return

        val sessionId = _currentSessionId.value
        val name = _userName.value

        viewModelScope.launch(Dispatchers.IO) {
            // Save user message to database
            val userMsg = ChatMessageEntity(
                sessionId = sessionId,
                sender = "user",
                text = trimmed,
                timestamp = System.currentTimeMillis()
            )
            chatDao.insertMessage(userMsg)

            // Update session title if first user message
            val sessionTitle = if (trimmed.length > 32) trimmed.take(30) + "…" else trimmed
            chatDao.updateSession(sessionId, sessionTitle, System.currentTimeMillis())

            // 1. Check if user prompt is a smartphone assistant intent
            val phoneAssistantResponse = handlePhoneAssistantCommand(trimmed)
            if (phoneAssistantResponse != null) {
                val aiMsg = ChatMessageEntity(
                    sessionId = sessionId,
                    sender = "anx_ai",
                    text = phoneAssistantResponse,
                    timestamp = System.currentTimeMillis()
                )
                chatDao.insertMessage(aiMsg)

                if (_autoSpeakEnabled.value || isVoiceInitiated) {
                    voiceManager.speak(phoneAssistantResponse)
                }
                return@launch
            }

            // 2. Check if user prompt is a task / to-do management command
            val taskCommandResponse = handleTaskCommand(trimmed)
            if (taskCommandResponse != null) {
                val aiMsg = ChatMessageEntity(
                    sessionId = sessionId,
                    sender = "anx_ai",
                    text = taskCommandResponse,
                    timestamp = System.currentTimeMillis()
                )
                chatDao.insertMessage(aiMsg)

                if (_autoSpeakEnabled.value || isVoiceInitiated) {
                    voiceManager.speak(taskCommandResponse)
                }
                return@launch
            }

            // 3. Process with Gemini AI Brain
            _isThinking.value = true
            val existing = currentMessages.value.map { it.sender to it.text }
            val result = GeminiApiClient.askAssistant(existing, trimmed, userName = name)
            _isThinking.value = false

            result.onSuccess { aiResponse ->
                val aiMsg = ChatMessageEntity(
                    sessionId = sessionId,
                    sender = "anx_ai",
                    text = aiResponse,
                    timestamp = System.currentTimeMillis()
                )
                chatDao.insertMessage(aiMsg)

                if (_autoSpeakEnabled.value || isVoiceInitiated) {
                    voiceManager.speak(aiResponse)
                }
            }.onFailure { error ->
                val fallback = "Namaste $name! Main ANX AI hoon, aapka personal assistant. Main aapki har query me help karne ke liye taiyaar hoon."
                val aiMsg = ChatMessageEntity(
                    sessionId = sessionId,
                    sender = "anx_ai",
                    text = fallback,
                    timestamp = System.currentTimeMillis()
                )
                chatDao.insertMessage(aiMsg)
                _bannerMessage.value = error.localizedMessage
                if (_autoSpeakEnabled.value || isVoiceInitiated) {
                    voiceManager.speak(fallback)
                }
            }
        }
    }

    private fun handlePhoneAssistantCommand(input: String): String? {
        val lower = input.lowercase().trim()
        val context = getApplication<Application>()
        val name = _userName.value

        // WhatsApp
        if (lower.contains("open whatsapp") || lower.contains("whatsapp kholo") || lower.contains("whatsapp open")) {
            SmartphoneAssistant.openWhatsApp(context)
            return "Opening WhatsApp for you, $name! 📱"
        }

        // YouTube
        if (lower.contains("open youtube") || lower.contains("youtube kholo") || lower.contains("youtube chalao")) {
            val queryMatch = Regex("""(?:play|search|chalao)\s+(.+?)(?:\s+on\s+youtube|$)""", RegexOption.IGNORE_CASE).find(lower)
            val query = queryMatch?.groupValues?.getOrNull(1)?.trim()
            SmartphoneAssistant.openYouTube(context, query)
            return if (!query.isNullOrBlank()) "Opening YouTube and searching for '$query', $name! 📺"
            else "Opening YouTube for you, $name! 📺"
        }

        // Google
        if (lower.contains("open google") || lower.contains("google kholo") || lower.contains("google search")) {
            SmartphoneAssistant.openGoogleSearch(context)
            return "Opening Google Search, $name! 🔍"
        }

        // Camera
        if (lower.contains("open camera") || lower.contains("camera kholo") || lower.contains("photo khincho") || lower.contains("take photo")) {
            SmartphoneAssistant.openCamera(context)
            return "Launching Camera for you, $name! 📷"
        }

        // Settings
        if (lower.contains("open settings") || lower.contains("phone settings") || lower.contains("settings kholo")) {
            SmartphoneAssistant.openPhoneSettings(context)
            return "Opening Android Settings, $name! ⚙️"
        }

        // Alarm
        val alarmRegex = Regex("""(?:set\s+(?:an\s+)?alarm\s+(?:for|at)\s+([0-9]{1,2})(?::([0-9]{2}))?\s*(am|pm)?)""", RegexOption.IGNORE_CASE)
        val alarmMatch = alarmRegex.find(lower)
        if (alarmMatch != null) {
            val hStr = alarmMatch.groupValues[1]
            val mStr = alarmMatch.groupValues.getOrNull(2)?.ifBlank { "0" } ?: "0"
            val amPm = alarmMatch.groupValues.getOrNull(3)?.lowercase()

            var hour = hStr.toIntOrNull() ?: 7
            val min = mStr.toIntOrNull() ?: 0
            if (amPm == "pm" && hour < 12) hour += 12
            if (amPm == "am" && hour == 12) hour = 0

            val success = SmartphoneAssistant.setAlarm(context, hour, min, "Alarm by ANX AI")
            return if (success) {
                "Alarm set for %02d:%02d in your Android clock, $name! ⏰".format(hour, min)
            } else {
                "Tried to set alarm for %02d:%02d, $name! Please check your Clock app.".format(hour, min)
            }
        }

        return null
    }

    private suspend fun handleTaskCommand(input: String): String? {
        val lower = input.lowercase().trim()
        val name = _userName.value
        val isHindi = lower.contains("mera") || lower.contains("karo") || lower.contains("dikhao") ||
                lower.contains("lagao") || lower.contains("karna") || input.any { it in '\u0900'..'\u097F' }

        val addRegex1 = Regex("""(?:add)\s+(.+?)\s+(?:to\s+(?:my\s+)?(?:to-do|todo|task)\s*(?:list)?)""", RegexOption.IGNORE_CASE)
        val addRegex2 = Regex("""(?:add\s+task|add\s+todo)\s*[:\-]?\s*(.+)""", RegexOption.IGNORE_CASE)
        val addRegexHindi = Regex("""(?:mera\s+task\s+add\s+karo|task\s+add\s+karo|to-do\s+me\s+add\s+karo)\s*[:\-]?\s*(.+)""", RegexOption.IGNORE_CASE)

        val addMatch = addRegex1.find(input) ?: addRegex2.find(input) ?: addRegexHindi.find(input)
        if (addMatch != null) {
            val taskName = addMatch.groupValues[1].trim().removeSurrounding("\"", "\"").removeSurrounding("'", "'")
            if (taskName.isNotBlank()) {
                taskDao.insertTask(TaskEntity(title = taskName))
                return if (isHindi) {
                    "Haan $name! Maine aapki to-do list me **\"$taskName\"** add kar diya hai. ✅"
                } else {
                    "Done $name! I have added **\"$taskName\"** to your to-do list. ✅"
                }
            }
        }

        val remindRegex1 = Regex("""remind\s+(?:me\s+)?(?:to\s+)?(.+?)\s+at\s+([0-9a-zA-Z:\s]+)""", RegexOption.IGNORE_CASE)
        val remindMatch = remindRegex1.find(input)
        if (remindMatch != null) {
            val task = remindMatch.groupValues[1].trim()
            val time = remindMatch.groupValues[2].trim()
            taskDao.insertTask(TaskEntity(title = task, reminderTime = time))
            return if (isHindi) {
                "Bilkul $name! Maine **$time** ke liye **\"$task\"** ka reminder save kar liya hai. ⏰"
            } else {
                "Reminder set, $name! ⏰ I have noted **\"$task\"** for **$time**."
            }
        }

        if (lower.contains("show my task") || lower.contains("show tasks") || lower.contains("show to-do") ||
            lower.contains("show todo") || lower.contains("what are my tasks") || lower.contains("meri to-do") ||
            lower.contains("mera task dikhao") || lower.contains("task list")
        ) {
            val pending = taskDao.getPendingTasks()
            if (pending.isEmpty()) {
                return if (isHindi) {
                    "$name, aapki to-do list me abhi koi pending task nahi hai! Sab clear hai. 🎉"
                } else {
                    "$name, you have no pending tasks right now! Everything is clear. 🎉"
                }
            }

            val builder = StringBuilder()
            builder.append(if (isHindi) "$name, ye rahi aapki pending to-do list:\n\n" else "Here are your active to-do tasks, $name:\n\n")
            pending.forEachIndexed { index, task ->
                val timeStr = if (!task.reminderTime.isNullOrBlank()) " ⏰ [${task.reminderTime}]" else ""
                builder.append("${index + 1}. ◻️ **${task.title}**$timeStr\n")
            }
            return builder.toString()
        }

        return null
    }

    fun startNewChat() {
        stopSpeaking()
        stopListening()
        val newId = createInitialSessionId()
        _currentSessionId.value = newId
        viewModelScope.launch(Dispatchers.IO) {
            chatDao.insertSession(
                ChatSessionEntity(
                    sessionId = newId,
                    title = "New Conversation",
                    createdAt = System.currentTimeMillis(),
                    lastUpdated = System.currentTimeMillis()
                )
            )
        }
    }

    fun selectSession(sessionId: String) {
        stopSpeaking()
        stopListening()
        _currentSessionId.value = sessionId
    }

    fun deleteSession(sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            chatDao.deleteMessagesForSession(sessionId)
            chatDao.deleteSession(sessionId)
            if (_currentSessionId.value == sessionId) {
                startNewChat()
            }
        }
    }

    fun clearCurrentMessages() {
        val currentId = _currentSessionId.value
        viewModelScope.launch(Dispatchers.IO) {
            chatDao.deleteMessagesForSession(currentId)
        }
    }

    fun clearAllChatHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            chatDao.clearAllMessages()
            chatDao.clearAllSessions()
            startNewChat()
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.onDestroy()
    }
}
