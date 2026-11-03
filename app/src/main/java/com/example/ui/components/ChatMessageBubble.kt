package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.ChatMessageEntity
import com.example.ui.theme.AssistantBubble
import com.example.ui.theme.AssistantBubbleBorder
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SpaceNavyDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.UserBubble
import com.example.ui.theme.UserBubbleBorder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatMessageBubble(
    message: ChatMessageEntity,
    userName: String = "You",
    onSpeak: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isUser = message.sender == "user"
    val context = LocalContext.current
    val timeFormatted = remember(message.timestamp) {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(message.timestamp))
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag(if (isUser) "user_message_bubble" else "ai_message_bubble"),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            // ANX AI Avatar
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, Brush.linearGradient(listOf(NeonCyan, NeonPurple)), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_anx_robot_avatar_1793739987446),
                    contentDescription = "ANX AI",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(32.dp).clip(CircleShape)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 330.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            // Sender Label
            Text(
                text = if (isUser) userName else "ANX AI",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isUser) ElectricBlue else NeonCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                ),
                modifier = Modifier.padding(bottom = 2.dp, start = if (isUser) 0.dp else 4.dp, end = if (isUser) 4.dp else 0.dp)
            )

            // Message Bubble Container
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (isUser) 18.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 18.dp
                        )
                    )
                    .background(
                        if (isUser) {
                            Brush.linearGradient(
                                listOf(
                                    UserBubble,
                                    Color(0xFF1B3D73)
                                )
                            )
                        } else {
                            Brush.verticalGradient(
                                listOf(
                                    AssistantBubble,
                                    Color(0xFF070F20)
                                )
                            )
                        }
                    )
                    .border(
                        width = 1.dp,
                        brush = if (isUser) Brush.linearGradient(listOf(UserBubbleBorder, ElectricBlue))
                        else Brush.linearGradient(listOf(NeonPurple.copy(alpha = 0.5f), NeonCyan.copy(alpha = 0.5f))),
                        shape = RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (isUser) 18.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 18.dp
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Column {
                    FormattedMessageContent(text = message.text, isUser = isUser)

                    Spacer(modifier = Modifier.height(4.dp))

                    // Footer with timestamp & actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = timeFormatted,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextTertiary,
                                fontSize = 10.sp
                            )
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Copy button
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("ANX AI", message.text)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ContentCopy,
                                    contentDescription = "Copy text",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            if (!isUser) {
                                Spacer(modifier = Modifier.width(4.dp))
                                // Re-speak aloud button
                                IconButton(
                                    onClick = { onSpeak(message.text) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Speak aloud",
                                        tint = NeonCyan,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FormattedMessageContent(text: String, isUser: Boolean) {
    val context = LocalContext.current
    val codeBlockRegex = Regex("```([a-zA-Z]*)\n([\\s\\S]*?)```")
    val match = codeBlockRegex.find(text)

    if (match != null && !isUser) {
        val parts = text.split(codeBlockRegex)
        Column {
            if (parts.isNotEmpty() && parts[0].isNotBlank()) {
                Text(
                    text = parts[0].trim(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextPrimary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Code block
            val codeLang = match.groupValues.getOrNull(1)?.ifBlank { "Code" } ?: "Code"
            val codeContent = match.groupValues.getOrNull(2) ?: ""

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF040710))
                    .border(1.dp, Color(0xFF1F2F52), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = codeLang.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = NeonCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        FilledTonalButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Code", codeContent.trim())
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Code copied", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFF142442),
                                contentColor = NeonCyan
                            ),
                            modifier = Modifier.height(26.dp)
                        ) {
                            Text("Copy Code", fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = codeContent.trim(),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFB3E5FC),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    )
                }
            }

            if (parts.size > 1 && parts[1].isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = parts[1].trim(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextPrimary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                )
            }
        }
    } else {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextPrimary,
                fontSize = 14.sp,
                lineHeight = 21.sp
            )
        )
    }
}
