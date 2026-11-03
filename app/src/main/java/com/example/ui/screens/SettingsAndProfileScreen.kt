package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.HologramTeal
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SpaceNavyBorder
import com.example.ui.theme.SpaceNavyCard
import com.example.ui.theme.SpaceNavyDark
import com.example.ui.theme.SpaceNavyLightCard
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusOnline
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.voice.VoiceGender
import com.example.voice.VoiceProfile

@Composable
fun SettingsAndProfileScreen(
    currentUserName: String,
    onUpdateUserName: (String) -> Unit,
    activeVoiceProfile: VoiceProfile,
    activeSpeechLang: String,
    autoSpeakEnabled: Boolean,
    onVoiceProfileSelected: (VoiceProfile) -> Unit,
    onSpeechLangSelected: (String) -> Unit,
    onToggleAutoSpeak: () -> Unit,
    onTestVoice: (VoiceProfile) -> Unit,
    onClearChatHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var userNameInput by remember { mutableStateOf(currentUserName) }
    var isEditingName by remember { mutableStateOf(false) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }

    var rememberNameEnabled by remember { mutableStateOf(true) }
    var highQualitySpeechEnabled by remember { mutableStateOf(true) }
    var selectedThemeName by remember { mutableStateOf("Ultra-HD Cyberpunk Dark") }

    // Clear History Dialog
    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text("Clear All Chat History?", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("This will permanently delete your stored conversation history from the local database.", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        showClearHistoryDialog = false
                        onClearChatHistory()
                        Toast.makeText(context, "Chat history cleared", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusError)
                ) {
                    Text("Delete All", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showClearHistoryDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SpaceNavyCard
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_and_profile_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(NeonCyan.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Settings & Profile",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            brush = Brush.horizontalGradient(listOf(NeonCyan, NeonPurple))
                        )
                    )
                    Text(
                        text = "Customize personality, voices, languages & privacy",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }
            }
        }

        // Profile Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(SpaceNavyCard)
                    .border(
                        1.5.dp,
                        Brush.horizontalGradient(listOf(NeonCyan, NeonPurple)),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile Avatar
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .border(2.dp, NeonCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_anx_launcher_1793739987451),
                            contentDescription = "User Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(60.dp).clip(CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentUserName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(StatusOnline.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("PRO USER", color = StatusOnline, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Text(
                            text = "ANX AI Master User",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                        )
                    }

                    OutlinedButton(
                        onClick = { isEditingName = !isEditingName },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (isEditingName) "Done" else "Edit", color = NeonCyan, fontSize = 12.sp)
                    }
                }

                if (isEditingName) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = userNameInput,
                            onValueChange = { userNameInput = it },
                            label = { Text("Your Name", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = SpaceNavyBorder,
                                focusedTextColor = TextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (userNameInput.isNotBlank()) {
                                    onUpdateUserName(userNameInput.trim())
                                    isEditingName = false
                                    Toast.makeText(context, "Name updated to ${userNameInput.trim()}", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                        ) {
                            Text("Save", color = SpaceNavyDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Voice Assistant Persona (Female & Male options)
        item {
            Text(
                text = "AI VOICE SELECTION",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(SpaceNavyCard)
                    .border(1.dp, SpaceNavyBorder, RoundedCornerShape(18.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                VoiceProfile.ALL_PROFILES.forEach { profile ->
                    val isSelected = profile.id == activeVoiceProfile.id
                    val genderColor = if (profile.gender == VoiceGender.FEMALE) NeonPurple else ElectricBlue
                    val genderIcon = if (profile.gender == VoiceGender.FEMALE) Icons.Filled.Female else Icons.Filled.Male

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) SpaceNavyLightCard else SpaceNavyDark)
                            .border(
                                1.dp,
                                if (isSelected) NeonCyan else SpaceNavyBorder,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { onVoiceProfileSelected(profile) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(genderColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = genderIcon, contentDescription = null, tint = genderColor, modifier = Modifier.size(20.dp))
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = profile.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (profile.gender == VoiceGender.FEMALE) "(Female)" else "(Male)",
                                        color = genderColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(text = profile.description, color = TextSecondary, fontSize = 11.sp)
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { onTestVoice(profile) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "Test Voice", tint = NeonCyan, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        // Auto Speak & Speech Language
        item {
            Text(
                text = "SPEECH & LANGUAGE PREFERENCES",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(SpaceNavyCard)
                    .border(1.dp, SpaceNavyBorder, RoundedCornerShape(18.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Auto-speak toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Auto-Speak Answers", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        Text("Voice response speaks automatically after reply", color = TextSecondary, fontSize = 11.sp)
                    }
                    Switch(
                        checked = autoSpeakEnabled,
                        onCheckedChange = { onToggleAutoSpeak() },
                        colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = ElectricBlue)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Speech Input Language
                Text("Voice Recognition Language:", color = TextSecondary, fontSize = 11.sp)
                val langOptions = listOf("auto" to "Auto (Hindi + English)", "hi-IN" to "Hindi (हिंदी)", "en-IN" to "English (Indian)")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    langOptions.forEach { (code, label) ->
                        val isSelected = activeSpeechLang == code
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) SpaceNavyLightCard else SpaceNavyDark)
                                .border(1.dp, if (isSelected) NeonCyan else SpaceNavyBorder, RoundedCornerShape(10.dp))
                                .clickable { onSpeechLangSelected(code) }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = label, color = if (isSelected) NeonCyan else TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Privacy & Permissions
        item {
            Text(
                text = "PRIVACY & LOCAL DATABASE",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(SpaceNavyCard)
                    .border(1.dp, SpaceNavyBorder, RoundedCornerShape(18.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Remember User Name & Context", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        Text("Personalizes ANX AI conversation memory", color = TextSecondary, fontSize = 11.sp)
                    }
                    Switch(
                        checked = rememberNameEnabled,
                        onCheckedChange = { rememberNameEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = ElectricBlue)
                    )
                }

                // Clear Chat History Action
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF26101A))
                        .border(1.dp, StatusError.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .clickable { showClearHistoryDialog = true }
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = null, tint = StatusError, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Clear All Chat History", color = StatusError, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Text("Delete", color = StatusError, fontSize = 12.sp)
                }
            }
        }

        // App Info
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ANX AI • ULTRA-HD 3D FUTURISTIC EDITION",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = HologramTeal,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontSize = 10.sp
                    )
                )
                Text(
                    text = "Version 2.0 • Powered by Google Gemini AI & Android Native",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
