package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.PurpleGlow
import com.example.ui.theme.SpaceNavyDark
import com.example.ui.theme.StatusSpeaking
import com.example.ui.theme.StatusThinking

@Composable
fun GlowingMicButton(
    isListening: Boolean,
    isSpeaking: Boolean,
    isThinking: Boolean,
    audioLevel: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mic_glow")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.90f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val active = isListening || isSpeaking || isThinking
    val dynamicScale = if (active) (1f + (audioLevel * 0.22f).coerceIn(0f, 0.3f)) else 1f

    Box(
        modifier = modifier
            .size(84.dp)
            .testTag("big_microphone_button"),
        contentAlignment = Alignment.Center
    ) {
        // Outer pulsing neon aura
        if (active) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(pulseScale * dynamicScale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                when {
                                    isSpeaking -> StatusSpeaking.copy(alpha = glowAlpha * 0.6f)
                                    isThinking -> StatusThinking.copy(alpha = glowAlpha * 0.6f)
                                    else -> NeonPurple.copy(alpha = glowAlpha * 0.7f)
                                },
                                CyanGlow.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        // Concentric Holographic Ring
        Box(
            modifier = Modifier
                .size(72.dp)
                .scale(if (active) dynamicScale else 1f)
                .clip(CircleShape)
                .background(
                    Brush.sweepGradient(
                        colors = if (isSpeaking) listOf(StatusSpeaking, ElectricBlue, StatusSpeaking)
                        else if (isThinking) listOf(StatusThinking, NeonPurple, StatusThinking)
                        else listOf(NeonCyan, NeonPurple, ElectricBlue, NeonCyan)
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.sweepGradient(listOf(NeonCyan, NeonPurple, NeonCyan)),
                    shape = CircleShape
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true, color = Color.White),
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            // Deep Space Center
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                if (isListening) Color(0xFF0F1E3D) else Color(0xFF090E1F),
                                SpaceNavyDark
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                val icon = when {
                    isListening -> Icons.Filled.Stop
                    isSpeaking -> Icons.Filled.VolumeUp
                    else -> Icons.Filled.Mic
                }

                val tint = when {
                    isSpeaking -> StatusSpeaking
                    isThinking -> StatusThinking
                    isListening -> NeonCyan
                    else -> NeonCyan
                }

                Icon(
                    imageVector = icon,
                    contentDescription = if (isListening) "Stop Listening" else if (isSpeaking) "Stop Speaking" else "Start Voice Chat",
                    tint = tint,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
