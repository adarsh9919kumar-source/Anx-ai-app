package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.PurpleGlow
import com.example.ui.theme.SpaceNavyBorder
import com.example.ui.theme.SpaceNavyCard
import com.example.ui.theme.SpaceNavyDark
import com.example.ui.theme.StatusListening
import com.example.ui.theme.StatusSpeaking
import com.example.ui.theme.StatusThinking
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AnxRobotGirl3D(
    isListening: Boolean,
    isSpeaking: Boolean,
    isThinking: Boolean,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Robot3DTransition")

    // Smooth vertical breathing float
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatingOffset"
    )

    // Continuous 360-degree holographic cyber-ring rotation
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ringRotation"
    )

    // Pulsing aura radius
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    val activeGlowColor = when {
        isListening -> StatusListening
        isSpeaking -> StatusSpeaking
        isThinking -> StatusThinking
        else -> NeonCyan
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(210.dp)
                .offset(y = floatOffset.dp)
                .clickable { onAvatarClick() },
            contentAlignment = Alignment.Center
        ) {
            // Background Canvas for rotating 3D holographic orbit rings & quantum particles
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = (size.minDimension / 2) - 10f

                // Outer rotating cyber ring with dash gaps
                rotate(ringRotation, pivot = center) {
                    drawCircle(
                        brush = Brush.sweepGradient(
                            listOf(
                                NeonCyan,
                                Color.Transparent,
                                NeonPurple,
                                Color.Transparent,
                                ElectricBlue,
                                NeonCyan
                            )
                        ),
                        radius = radius * glowPulse,
                        center = center,
                        style = Stroke(width = 3.5f)
                    )
                }

                // Counter-rotating inner ring
                rotate(-ringRotation * 1.5f, pivot = center) {
                    drawCircle(
                        brush = Brush.sweepGradient(
                            listOf(
                                NeonPink,
                                Color.Transparent,
                                NeonCyan,
                                Color.Transparent,
                                NeonPink
                            )
                        ),
                        radius = (radius - 16f) * (2f - glowPulse),
                        center = center,
                        style = Stroke(width = 2f)
                    )
                }
            }

            // Glow backing aura
            Box(
                modifier = Modifier
                    .size(172.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                activeGlowColor.copy(alpha = 0.45f),
                                activeGlowColor.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // 3D Avatar Image
            Image(
                painter = painterResource(id = R.drawable.img_anx_robot_avatar_1793739987446),
                contentDescription = "ANX AI 3D Cyber Android Assistant",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .border(
                        width = 2.5.dp,
                        brush = Brush.sweepGradient(
                            listOf(
                                NeonCyan,
                                NeonPurple,
                                ElectricBlue,
                                NeonPink,
                                NeonCyan
                            )
                        ),
                        shape = CircleShape
                    )
                    .shadow(elevation = 16.dp, shape = CircleShape, spotColor = activeGlowColor)
            )

            // Live status badge overlay on avatar
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-12).dp, y = (-8).dp)
                    .clip(CircleShape)
                    .background(SpaceNavyDark)
                    .border(1.5.dp, activeGlowColor, CircleShape)
                    .padding(6.dp)
            ) {
                Icon(
                    imageVector = when {
                        isSpeaking -> Icons.Filled.RecordVoiceOver
                        else -> Icons.Filled.AutoAwesome
                    },
                    contentDescription = null,
                    tint = activeGlowColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Mood & Neural Status Pill
        val moodText = when {
            isListening -> "Listening to your voice…"
            isSpeaking -> "ANX AI Speaking (Ananya)"
            isThinking -> "Neural Quantum Processing…"
            else -> "ANX AI Ready • Tap to Interact"
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(SpaceNavyCard.copy(alpha = 0.9f))
                .border(1.dp, activeGlowColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .clickable { onAvatarClick() }
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(activeGlowColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = moodText,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                )
            )
        }
    }
}
