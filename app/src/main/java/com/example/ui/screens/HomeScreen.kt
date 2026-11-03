package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.AnxRobotGirl3D
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.components.GlowingMicButton
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.HologramTeal
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.QuantumGold
import com.example.ui.theme.SpaceNavyBorder
import com.example.ui.theme.SpaceNavyCard
import com.example.ui.theme.SpaceNavyDark
import com.example.ui.theme.SpaceNavyLightCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun HomeScreen(
    userName: String,
    isListening: Boolean,
    isSpeaking: Boolean,
    isThinking: Boolean,
    audioLevel: Float,
    onMicClick: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToImages: () -> Unit,
    onNavigateToCoding: () -> Unit,
    onNavigateToPhone: () -> Unit,
    onNavigateToTools: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onPromptSelected: (String) -> Unit,
    onSpeakGreeting: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Futuristic 3D Metaverse Hero Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .border(
                        1.5.dp,
                        Brush.horizontalGradient(listOf(NeonCyan, NeonPurple, ElectricBlue)),
                        RoundedCornerShape(22.dp)
                    )
                    .shadow(elevation = 12.dp, shape = RoundedCornerShape(22.dp), spotColor = NeonCyan),
                colors = CardDefaults.cardColors(containerColor = SpaceNavyCard)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(145.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_anx_hero_world_1793739987714),
                        contentDescription = "ANX AI Metaverse World",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0x22060913),
                                        Color(0xCC060913),
                                        Color(0xF0060913)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(NeonCyan)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "QUANTUM AI ONLINE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = NeonCyan,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.2.sp,
                                        fontSize = 10.sp
                                    )
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0x80101935))
                                    .border(1.dp, NeonPurple.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "3D Ultra HD",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = NeonPurple,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "Welcome, $userName",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    brush = Brush.horizontalGradient(listOf(TextPrimary, HologramTeal))
                                )
                            )
                            Text(
                                text = "ANX AI • YOUR PERSONAL AI WORLD",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = NeonCyan,
                                    letterSpacing = 1.2.sp,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }
        }

        // 2. 3D Animated AI Robot Girl & Voice Orb
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                AnxRobotGirl3D(
                    isListening = isListening,
                    isSpeaking = isSpeaking,
                    isThinking = isThinking,
                    onAvatarClick = { onSpeakGreeting() }
                )
            }
        }

        // 3. Central Voice Activation & Audio Waveform Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(SpaceNavyCard)
                    .border(1.dp, SpaceNavyBorder, RoundedCornerShape(20.dp))
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isListening) "Listening to you…" else if (isSpeaking) "ANX AI Speaking…" else "Tap to Speak (Hindi / English / Hinglish)",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (isListening) NeonCyan else if (isSpeaking) HologramTeal else TextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    AudioWaveformVisualizer(
                        isActive = isListening || isSpeaking || isThinking,
                        audioLevel = if (isThinking) 0.5f else audioLevel,
                        barCount = 7
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    GlowingMicButton(
                        isListening = isListening,
                        isSpeaking = isSpeaking,
                        isThinking = isThinking,
                        audioLevel = audioLevel,
                        onClick = onMicClick
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    AudioWaveformVisualizer(
                        isActive = isListening || isSpeaking || isThinking,
                        audioLevel = if (isThinking) 0.5f else audioLevel,
                        barCount = 7
                    )
                }
            }
        }

        // 4. Core Features Grid (9 Requested Items)
        item {
            Text(
                text = "COMMAND MODULES",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    fontSize = 11.sp
                ),
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FeatureTile(
                    title = "AI Chat",
                    subtitle = "Real-time trilingual chat",
                    icon = Icons.Filled.Chat,
                    accentColor = NeonCyan,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToChat
                )
                FeatureTile(
                    title = "AI Image Generator",
                    subtitle = "3D & Cyberpunk art",
                    icon = Icons.Filled.Image,
                    accentColor = NeonPink,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToImages
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FeatureTile(
                    title = "Coding & Web",
                    subtitle = "Kotlin, Python, HTML",
                    icon = Icons.Filled.Code,
                    accentColor = NeonPurple,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToCoding
                )
                FeatureTile(
                    title = "Phone Assistant",
                    subtitle = "WhatsApp, Alarms, Calls",
                    icon = Icons.Filled.PhoneAndroid,
                    accentColor = ElectricBlue,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToPhone
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FeatureTile(
                    title = "AI Tools Suite",
                    subtitle = "Math, Docs, Translate",
                    icon = Icons.Filled.Build,
                    accentColor = QuantumGold,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToTools
                )
                FeatureTile(
                    title = "Chat History",
                    subtitle = "Past AI conversations",
                    icon = Icons.Filled.History,
                    accentColor = HologramTeal,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToHistory
                )
            }
        }

        // 5. Quick AI Suggestion Chips
        item {
            Text(
                text = "SUGGESTED FOR YOU",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    fontSize = 11.sp
                ),
                modifier = Modifier.padding(start = 4.dp, top = 6.dp)
            )
        }

        item {
            val suggestions = listOf(
                "Write a cool shayari in Hinglish",
                "Explain Quantum Computing simply",
                "Solve math: 3x^2 - 12x + 9 = 0",
                "Create a responsive Portfolio HTML",
                "Open WhatsApp and message",
                "Write an email for leave",
                "Translate 'Main kal aaunga' to English"
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(suggestions) { prompt ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(SpaceNavyLightCard)
                            .border(1.dp, SpaceNavyBorder, RoundedCornerShape(14.dp))
                            .clickable {
                                onPromptSelected(prompt)
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = prompt,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextPrimary,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun FeatureTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(SpaceNavyCard)
            .border(1.dp, SpaceNavyBorder, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontSize = 11.sp
                ),
                maxLines = 1
            )
        }
    }
}
