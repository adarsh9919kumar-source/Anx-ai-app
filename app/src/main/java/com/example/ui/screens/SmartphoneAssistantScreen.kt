package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phone.SmartphoneAssistant
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
import com.example.ui.theme.StatusOnline
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SmartphoneAssistantScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var callPhoneNumber by remember { mutableStateOf("") }
    var showCallConfirmationDialog by remember { mutableStateOf(false) }

    var alarmHour by remember { mutableIntStateOf(7) }
    var alarmMinute by remember { mutableIntStateOf(0) }
    var alarmLabel by remember { mutableStateOf("Wake Up with ANX AI") }

    var isTorchOn by remember { mutableStateOf(false) }

    var appSearchQuery by remember { mutableStateOf("") }
    var youtubeSearchQuery by remember { mutableStateOf("") }

    // Security Confirmation Dialog for Phone Call
    if (showCallConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showCallConfirmationDialog = false },
            title = {
                Text(
                    text = "Confirm Phone Call",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text(
                    text = "ANX AI requests permission to open the phone dialer for number: $callPhoneNumber. Would you like to proceed?",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCallConfirmationDialog = false
                        SmartphoneAssistant.openDialer(context, callPhoneNumber)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("Call Now", color = SpaceNavyDark, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showCallConfirmationDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SpaceNavyCard
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("smartphone_assistant_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ElectricBlue.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PhoneAndroid,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Smartphone Assistant",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            brush = Brush.horizontalGradient(listOf(ElectricBlue, NeonCyan))
                        )
                    )
                    Text(
                        text = "Control your Android phone with AI & native intents",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }
            }
        }

        // Quick Launch App Grid
        item {
            Text(
                text = "1-TAP APP LAUNCHERS",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AppLauncherTile(
                    label = "WhatsApp",
                    desc = "Open chats",
                    color = Color(0xFF25D366),
                    modifier = Modifier.weight(1f),
                    onClick = { SmartphoneAssistant.openWhatsApp(context) }
                )
                AppLauncherTile(
                    label = "YouTube",
                    desc = "Videos & Music",
                    color = Color(0xFFFF0000),
                    modifier = Modifier.weight(1f),
                    onClick = { SmartphoneAssistant.openYouTube(context) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AppLauncherTile(
                    label = "Google",
                    desc = "Web Search",
                    color = Color(0xFF4285F4),
                    modifier = Modifier.weight(1f),
                    onClick = { SmartphoneAssistant.openGoogleSearch(context) }
                )
                AppLauncherTile(
                    label = "Camera",
                    desc = "Photos & Video",
                    color = NeonPink,
                    modifier = Modifier.weight(1f),
                    onClick = { SmartphoneAssistant.openCamera(context) }
                )
            }
        }

        // Make Phone Call Card with Safety Confirmation
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(SpaceNavyCard)
                    .border(1.dp, SpaceNavyBorder, RoundedCornerShape(18.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.Phone, contentDescription = null, tint = StatusOnline, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Make Phone Call (With Confirmation)",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = callPhoneNumber,
                        onValueChange = { callPhoneNumber = it },
                        placeholder = { Text("Enter phone number...", color = TextSecondary, fontSize = 13.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = SpaceNavyBorder,
                            focusedContainerColor = SpaceNavyDark,
                            unfocusedContainerColor = SpaceNavyDark,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            if (callPhoneNumber.isNotBlank()) {
                                showCallConfirmationDialog = true
                            } else {
                                Toast.makeText(context, "Please enter a phone number", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusOnline),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(50.dp)
                    ) {
                        Text("Call", color = SpaceNavyDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Set Alarm Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(SpaceNavyCard)
                    .border(1.dp, SpaceNavyBorder, RoundedCornerShape(18.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.Alarm, contentDescription = null, tint = QuantumGold, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Set Android Alarm",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = alarmHour.toString(),
                        onValueChange = {
                            val v = it.toIntOrNull() ?: 0
                            alarmHour = v.coerceIn(0, 23)
                        },
                        label = { Text("Hour (0-23)", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = SpaceNavyBorder,
                            focusedContainerColor = SpaceNavyDark,
                            unfocusedContainerColor = SpaceNavyDark,
                            focusedTextColor = TextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = alarmMinute.toString(),
                        onValueChange = {
                            val v = it.toIntOrNull() ?: 0
                            alarmMinute = v.coerceIn(0, 59)
                        },
                        label = { Text("Minute (0-59)", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = SpaceNavyBorder,
                            focusedContainerColor = SpaceNavyDark,
                            unfocusedContainerColor = SpaceNavyDark,
                            focusedTextColor = TextPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = alarmLabel,
                    onValueChange = { alarmLabel = it },
                    label = { Text("Alarm Label", fontSize = 10.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = SpaceNavyBorder,
                        focusedContainerColor = SpaceNavyDark,
                        unfocusedContainerColor = SpaceNavyDark,
                        focusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        val success = SmartphoneAssistant.setAlarm(context, alarmHour, alarmMinute, alarmLabel)
                        if (success) {
                            Toast.makeText(context, "Alarm set for %02d:%02d".format(alarmHour, alarmMinute), Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = QuantumGold),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Set Alarm in Android", color = SpaceNavyDark, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Phone System Controls (WiFi, Bluetooth, Settings, Flashlight)
        item {
            Text(
                text = "PHONE CONTROLS & HARDWARE",
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
                // Flashlight toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.FlashlightOn, contentDescription = null, tint = QuantumGold, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Flashlight / Torch", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    }
                    Switch(
                        checked = isTorchOn,
                        onCheckedChange = {
                            isTorchOn = it
                            SmartphoneAssistant.toggleTorch(context, it)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = QuantumGold, checkedTrackColor = Color(0xFF6B5300))
                    )
                }

                // WiFi Settings
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { SmartphoneAssistant.openWifiSettings(context) }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.Wifi, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Wi-Fi Settings", color = TextPrimary)
                    }
                    Text("Open →", color = TextSecondary, fontSize = 12.sp)
                }

                // Bluetooth Settings
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { SmartphoneAssistant.openBluetoothSettings(context) }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.Bluetooth, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Bluetooth Settings", color = TextPrimary)
                    }
                    Text("Open →", color = TextSecondary, fontSize = 12.sp)
                }

                // Sound Settings
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { SmartphoneAssistant.openSoundSettings(context) }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.VolumeUp, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Sound & Volume Settings", color = TextPrimary)
                    }
                    Text("Open →", color = TextSecondary, fontSize = 12.sp)
                }

                // Device Settings
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { SmartphoneAssistant.openPhoneSettings(context) }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.Settings, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("All Phone Settings", color = TextPrimary)
                    }
                    Text("Open →", color = TextSecondary, fontSize = 12.sp)
                }

                // Notification Access Guide
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { SmartphoneAssistant.openNotificationSettings(context) }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.Notifications, contentDescription = null, tint = HologramTeal, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Notification Access Settings", color = TextPrimary)
                    }
                    Text("Open →", color = TextSecondary, fontSize = 12.sp)
                }
            }
        }

        // Voice Commands Reference
        item {
            Text(
                text = "VOICE COMMAND CHEATSHEET",
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
                    .clip(RoundedCornerShape(16.dp))
                    .background(SpaceNavyLightCard)
                    .border(1.dp, SpaceNavyBorder, RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("You can speak these commands anytime via the Mic button:", color = TextSecondary, fontSize = 12.sp)
                val voiceCommands = listOf(
                    "\"Open WhatsApp\"" to "Launches WhatsApp",
                    "\"Open YouTube and play music\"" to "Opens YouTube search",
                    "\"Set alarm for 6:30 AM\"" to "Triggers Android alarm intent",
                    "\"Open phone settings\"" to "Navigates to device settings",
                    "\"Call [Phone Number]\"" to "Opens phone dialer with number"
                )
                voiceCommands.forEach { (cmd, action) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = cmd, color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(text = action, color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun AppLauncherTile(
    label: String,
    desc: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SpaceNavyCard)
            .border(1.dp, SpaceNavyBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = label, style = MaterialTheme.typography.titleSmall.copy(color = TextPrimary, fontWeight = FontWeight.Bold))
            Text(text = desc, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp))
        }
    }
}
