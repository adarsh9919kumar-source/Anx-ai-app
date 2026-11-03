package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.components.AnxLogo3D
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.components.ChatHistorySheet
import com.example.ui.components.ChatMessageBubble
import com.example.ui.components.GlowingMicButton
import com.example.ui.components.TasksSheet
import com.example.ui.components.VoiceSelectionSheet
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.HologramTeal
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SpaceNavyBorder
import com.example.ui.theme.SpaceNavyCard
import com.example.ui.theme.SpaceNavyDark
import com.example.ui.theme.SpaceNavyLightCard
import com.example.ui.theme.StatusListening
import com.example.ui.theme.StatusSpeaking
import com.example.ui.theme.StatusThinking
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AdarshAiViewModel

enum class MainNavDestination {
    HOME,
    CHAT,
    IMAGE_GEN,
    CODING,
    PHONE,
    TOOLS,
    SETTINGS
}

@Composable
fun AdarshAiMainScreen(
    viewModel: AdarshAiViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val chatListState = rememberLazyListState()

    val userName by viewModel.userName.collectAsState()
    val messages by viewModel.currentMessages.collectAsState()
    val savedSessions by viewModel.savedSessions.collectAsState()
    val allTasks by viewModel.allTasks.collectAsState()
    val activeSessionId by viewModel.currentSessionId.collectAsState()
    val isThinking by viewModel.isThinking.collectAsState()
    val bannerMessage by viewModel.bannerMessage.collectAsState()
    val autoSpeakEnabled by viewModel.autoSpeakEnabled.collectAsState()

    val isListening by viewModel.voiceManager.isListening.collectAsState()
    val isSpeaking by viewModel.voiceManager.isSpeaking.collectAsState()
    val audioLevel by viewModel.voiceManager.audioRmsLevel.collectAsState()
    val activeVoiceProfile by viewModel.voiceManager.activeProfile.collectAsState()
    val speechInputLang by viewModel.voiceManager.speechInputLanguage.collectAsState()

    var currentScreen by remember { mutableStateOf(MainNavDestination.HOME) }

    var chatInputText by remember { mutableStateOf("") }
    var showVoiceSheet by remember { mutableStateOf(false) }
    var showHistorySheet by remember { mutableStateOf(false) }
    var showTasksSheet by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    val pendingTasksCount = remember(allTasks) { allTasks.count { !it.isCompleted } }

    // Audio Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startListening()
        } else {
            Toast.makeText(context, "Microphone permission is needed for voice chat", Toast.LENGTH_LONG).show()
        }
    }

    // Handle Back Press to return to HOME when on sub-screen
    BackHandler(enabled = currentScreen != MainNavDestination.HOME) {
        currentScreen = MainNavDestination.HOME
    }

    // Auto-scroll to newest message in chat
    LaunchedEffect(messages.size, isThinking) {
        if (messages.isNotEmpty() && currentScreen == MainNavDestination.CHAT) {
            chatListState.animateScrollToItem(messages.size - 1)
        }
    }

    val triggerMicAction: () -> Unit = {
        if (isSpeaking) {
            viewModel.stopSpeaking()
        } else if (isListening) {
            viewModel.stopListening()
        } else {
            val hasAudioPerm = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED

            if (hasAudioPerm) {
                viewModel.startListening()
            } else {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(SpaceNavyDark)
            .windowInsetsPadding(WindowInsets.statusBars),
        containerColor = SpaceNavyDark,
        topBar = {
            // Futuristic Responsive Top Bar with 3D Glowing Logo & Quick Controls
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 680.dp)
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                // 3D Glowing Logo
                AnxLogo3D(
                    onClick = {
                        currentScreen = MainNavDestination.HOME
                    }
                )

                // Top Actions
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Tasks Button with Badge
                    IconButton(
                        onClick = { showTasksSheet = true },
                        modifier = Modifier.size(38.dp)
                    ) {
                        BadgedBox(
                            badge = {
                                if (pendingTasksCount > 0) {
                                    Badge(
                                        containerColor = NeonCyan,
                                        contentColor = Color(0xFF031424)
                                    ) {
                                        Text("$pendingTasksCount", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Checklist,
                                contentDescription = "To-Do Tasks",
                                tint = if (pendingTasksCount > 0) NeonCyan else TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Voice & Language Settings Quick Action
                    IconButton(
                        onClick = { showVoiceSheet = true },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.RecordVoiceOver,
                            contentDescription = "Voice Settings",
                            tint = if (isSpeaking) StatusSpeaking else NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Chat History
                    IconButton(
                        onClick = { showHistorySheet = true },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.History,
                            contentDescription = "Chat History",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Dropdown Menu
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "More",
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(SpaceNavyCard)
                        ) {
                            DropdownMenuItem(
                                text = { Text("New Conversation", color = TextPrimary) },
                                leadingIcon = {
                                    Icon(Icons.Filled.Add, contentDescription = null, tint = NeonCyan)
                                },
                                onClick = {
                                    showMenu = false
                                    viewModel.startNewChat()
                                    currentScreen = MainNavDestination.CHAT
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Settings & Profile", color = TextPrimary) },
                                leadingIcon = {
                                    Icon(Icons.Filled.Settings, contentDescription = null, tint = NeonPurple)
                                },
                                onClick = {
                                    showMenu = false
                                    currentScreen = MainNavDestination.SETTINGS
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Clear Current Chat", color = TextPrimary) },
                                leadingIcon = {
                                    Icon(Icons.Filled.Clear, contentDescription = null, tint = TextSecondary)
                                },
                                onClick = {
                                    showMenu = false
                                    viewModel.clearCurrentMessages()
                                }
                            )
                        }
                    }
                }
            }
        }
    },
    bottomBar = {
            // Futuristic M3 Navigation Bar with responsive max width & window insets
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars),
                color = SpaceNavyCard
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    NavigationBar(
                        modifier = Modifier.widthIn(max = 680.dp),
                        containerColor = SpaceNavyCard,
                        contentColor = NeonCyan,
                        tonalElevation = 8.dp
                    ) {
                    NavigationBarItem(
                        selected = currentScreen == MainNavDestination.HOME,
                        onClick = { currentScreen = MainNavDestination.HOME },
                        icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                        label = { Text("Home", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonCyan,
                            selectedTextColor = NeonCyan,
                            indicatorColor = Color(0xFF14244B),
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )
                    NavigationBarItem(
                        selected = currentScreen == MainNavDestination.CHAT,
                        onClick = { currentScreen = MainNavDestination.CHAT },
                        icon = { Icon(Icons.Filled.Chat, contentDescription = "Chat") },
                        label = { Text("AI Chat", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonCyan,
                            selectedTextColor = NeonCyan,
                            indicatorColor = Color(0xFF14244B),
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )
                    NavigationBarItem(
                        selected = currentScreen == MainNavDestination.IMAGE_GEN,
                        onClick = { currentScreen = MainNavDestination.IMAGE_GEN },
                        icon = { Icon(Icons.Filled.Image, contentDescription = "Images") },
                        label = { Text("Images", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonPink,
                            selectedTextColor = NeonPink,
                            indicatorColor = Color(0xFF3B1534),
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )
                    NavigationBarItem(
                        selected = currentScreen == MainNavDestination.CODING,
                        onClick = { currentScreen = MainNavDestination.CODING },
                        icon = { Icon(Icons.Filled.Code, contentDescription = "Code") },
                        label = { Text("Code & Web", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonPurple,
                            selectedTextColor = NeonPurple,
                            indicatorColor = Color(0xFF2C164D),
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )
                    NavigationBarItem(
                        selected = currentScreen == MainNavDestination.PHONE,
                        onClick = { currentScreen = MainNavDestination.PHONE },
                        icon = { Icon(Icons.Filled.PhoneAndroid, contentDescription = "Phone") },
                        label = { Text("Phone", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ElectricBlue,
                            selectedTextColor = ElectricBlue,
                            indicatorColor = Color(0xFF102852),
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )
                    NavigationBarItem(
                        selected = currentScreen == MainNavDestination.TOOLS,
                        onClick = { currentScreen = MainNavDestination.TOOLS },
                        icon = { Icon(Icons.Filled.Build, contentDescription = "Tools") },
                        label = { Text("Tools", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = HologramTeal,
                            selectedTextColor = HologramTeal,
                            indicatorColor = Color(0xFF0F323E),
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )
                }
            }
        }
    }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
        ) {
            // Notification / Banner Message
            AnimatedVisibility(
                visible = bannerMessage != null,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                bannerMessage?.let { msg ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F2648))
                            .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodySmall.copy(color = NeonCyan, fontSize = 12.sp),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { viewModel.dismissBanner() },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Dismiss", tint = TextSecondary, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            // Screen Content Routing (Responsive layout with mobile & tablet optimization)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 680.dp)
                ) {
                    when (currentScreen) {
                    MainNavDestination.HOME -> {
                        HomeScreen(
                            userName = userName,
                            isListening = isListening,
                            isSpeaking = isSpeaking,
                            isThinking = isThinking,
                            audioLevel = audioLevel,
                            onMicClick = triggerMicAction,
                            onNavigateToChat = { currentScreen = MainNavDestination.CHAT },
                            onNavigateToImages = { currentScreen = MainNavDestination.IMAGE_GEN },
                            onNavigateToCoding = { currentScreen = MainNavDestination.CODING },
                            onNavigateToPhone = { currentScreen = MainNavDestination.PHONE },
                            onNavigateToTools = { currentScreen = MainNavDestination.TOOLS },
                            onNavigateToSettings = { currentScreen = MainNavDestination.SETTINGS },
                            onNavigateToHistory = { showHistorySheet = true },
                            onPromptSelected = { prompt ->
                                viewModel.sendMessage(prompt)
                                currentScreen = MainNavDestination.CHAT
                            },
                            onSpeakGreeting = {
                                viewModel.speakText("Namaste $userName! I am ANX AI, your personal AI world. How can I help you today?")
                            }
                        )
                    }

                    MainNavDestination.CHAT -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Messages List
                            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                                if (messages.isEmpty()) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Chat,
                                            contentDescription = null,
                                            tint = NeonCyan,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Text(
                                            text = "Start chatting with ANX AI",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Text(
                                            text = "Real-time responses in Hindi, English, and Hinglish",
                                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                } else {
                                    LazyColumn(
                                        state = chatListState,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        items(messages, key = { it.id }) { message ->
                                            ChatMessageBubble(
                                                message = message,
                                                userName = userName,
                                                onSpeak = { text -> viewModel.speakText(text) }
                                            )
                                        }

                                        if (isThinking) {
                                            item {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 16.dp, vertical = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(14.dp))
                                                            .background(SpaceNavyCard)
                                                            .border(1.dp, SpaceNavyBorder, RoundedCornerShape(14.dp))
                                                            .padding(horizontal = 14.dp, vertical = 8.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        AudioWaveformVisualizer(
                                                            isActive = true,
                                                            audioLevel = 0.6f,
                                                            barCount = 7
                                                        )
                                                        Spacer(modifier = Modifier.width(10.dp))
                                                        Text(
                                                            text = "ANX AI is processing…",
                                                            style = MaterialTheme.typography.bodySmall.copy(
                                                                color = NeonCyan,
                                                                fontSize = 12.sp,
                                                                fontWeight = FontWeight.Medium
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Active voice indicator banner
                            AnimatedVisibility(
                                visible = isListening || isSpeaking,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 4.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF0C1935))
                                        .border(1.dp, if (isListening) NeonCyan else StatusSpeaking, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        AudioWaveformVisualizer(isActive = true, audioLevel = audioLevel, barCount = 7)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isListening) "Listening to you…" else "ANX AI speaking…",
                                            color = if (isListening) NeonCyan else StatusSpeaking,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.sp
                                        )
                                    }
                                    Text("Tap Mic to Stop", color = TextSecondary, fontSize = 11.sp)
                                }
                            }

                            // Chat Input Bar with Glowing Mic and Text Field
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = SpaceNavyDark
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    GlowingMicButton(
                                        isListening = isListening,
                                        isSpeaking = isSpeaking,
                                        isThinking = isThinking,
                                        audioLevel = audioLevel,
                                        onClick = triggerMicAction
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    OutlinedTextField(
                                        value = chatInputText,
                                        onValueChange = { chatInputText = it },
                                        placeholder = {
                                            Text("Ask ANX AI (Hindi / English / Hinglish)…", color = TextSecondary, fontSize = 13.sp)
                                        },
                                        trailingIcon = {
                                            if (chatInputText.isNotBlank()) {
                                                IconButton(
                                                    onClick = {
                                                        val text = chatInputText
                                                        chatInputText = ""
                                                        viewModel.sendMessage(text)
                                                    },
                                                    modifier = Modifier.testTag("send_button")
                                                ) {
                                                    Icon(Icons.Filled.Send, contentDescription = "Send", tint = NeonCyan)
                                                }
                                            }
                                        },
                                        singleLine = true,
                                        shape = RoundedCornerShape(24.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = NeonCyan,
                                            unfocusedBorderColor = SpaceNavyBorder,
                                            focusedContainerColor = SpaceNavyCard,
                                            unfocusedContainerColor = SpaceNavyCard,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        ),
                                        modifier = Modifier.weight(1f).testTag("chat_text_input")
                                    )
                                }
                            }
                        }
                    }

                    MainNavDestination.IMAGE_GEN -> {
                        ImageGeneratorScreen()
                    }

                    MainNavDestination.CODING -> {
                        CodingAssistantScreen()
                    }

                    MainNavDestination.PHONE -> {
                        SmartphoneAssistantScreen()
                    }

                    MainNavDestination.TOOLS -> {
                        AiToolsHubScreen(
                            onSpeakResult = { text -> viewModel.speakText(text) }
                        )
                    }

                    MainNavDestination.SETTINGS -> {
                        SettingsAndProfileScreen(
                            currentUserName = userName,
                            onUpdateUserName = { newName -> viewModel.updateUserName(newName) },
                            activeVoiceProfile = activeVoiceProfile,
                            activeSpeechLang = speechInputLang,
                            autoSpeakEnabled = autoSpeakEnabled,
                            onVoiceProfileSelected = { profile -> viewModel.selectVoiceProfile(profile) },
                            onSpeechLangSelected = { lang -> viewModel.setSpeechLanguage(lang) },
                            onToggleAutoSpeak = { viewModel.toggleAutoSpeak() },
                            onTestVoice = { profile ->
                                viewModel.selectVoiceProfile(profile)
                                viewModel.speakText("Namaste $userName! Main ${profile.name} hoon, aapka personal AI assistant.")
                            },
                            onClearChatHistory = { viewModel.clearAllChatHistory() }
                        )
                    }
                }
            }
        }
    }
}

    // Modal Bottom Sheets
    if (showVoiceSheet) {
        VoiceSelectionSheet(
            activeProfile = activeVoiceProfile,
            activeSpeechLang = speechInputLang,
            autoSpeakEnabled = autoSpeakEnabled,
            onProfileSelected = { profile -> viewModel.selectVoiceProfile(profile) },
            onSpeechLangSelected = { lang -> viewModel.setSpeechLanguage(lang) },
            onToggleAutoSpeak = { viewModel.toggleAutoSpeak() },
            onTestVoice = { profile ->
                viewModel.selectVoiceProfile(profile)
                viewModel.speakText("Namaste $userName! Main ${profile.name} hoon, aapka personal AI assistant.")
            },
            onDismiss = { showVoiceSheet = false }
        )
    }

    if (showHistorySheet) {
        ChatHistorySheet(
            sessions = savedSessions,
            activeSessionId = activeSessionId,
            onSessionSelected = { sessionId ->
                viewModel.selectSession(sessionId)
                currentScreen = MainNavDestination.CHAT
            },
            onSessionDeleted = { sessionId -> viewModel.deleteSession(sessionId) },
            onNewChat = {
                viewModel.startNewChat()
                currentScreen = MainNavDestination.CHAT
            },
            onDismiss = { showHistorySheet = false }
        )
    }

    if (showTasksSheet) {
        TasksSheet(
            tasks = allTasks,
            onAddTask = { title, reminder -> viewModel.addTask(title, reminder) },
            onToggleTask = { task -> viewModel.toggleTaskCompletion(task) },
            onDeleteTask = { id -> viewModel.deleteTask(id) },
            onClearCompleted = { viewModel.clearCompletedTasks() },
            onDismiss = { showTasksSheet = false }
        )
    }
}
