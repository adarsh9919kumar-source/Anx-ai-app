package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.HologramTeal
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SpaceNavyBorder
import com.example.ui.theme.SpaceNavyCard
import com.example.ui.theme.SpaceNavyLightCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class QuickPrompt(
    val title: String,
    val prompt: String,
    val icon: ImageVector,
    val accentColor: Color
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WelcomeHeroCard(
    onPromptSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hero_breathing")
    val avatarScale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "avatar_scale"
    )

    val quickPrompts = listOf(
        QuickPrompt(
            title = "Hinglish Shayari",
            prompt = "Ek inspiring Hindi shayari sunao Hinglish script me.",
            icon = Icons.Filled.AutoAwesome,
            accentColor = NeonPurple
        ),
        QuickPrompt(
            title = "Write Kotlin Code",
            prompt = "Write a clean Kotlin Jetpack Compose function with animation example.",
            icon = Icons.Filled.Code,
            accentColor = ElectricBlue
        ),
        QuickPrompt(
            title = "Daily Routine",
            prompt = "Mera aaj ka productive daily routine plan karo Hindi me.",
            icon = Icons.Filled.Bolt,
            accentColor = NeonCyan
        ),
        QuickPrompt(
            title = "Solve Maths",
            prompt = "Solve step-by-step: 3x^2 - 12x + 9 = 0",
            icon = Icons.Filled.AutoAwesome,
            accentColor = Color(0xFFFFB300)
        ),
        QuickPrompt(
            title = "Explain AI",
            prompt = "Artificial Intelligence aur Neural Networks kaise kaam karte hain? Hindi me samjhao.",
            icon = Icons.Filled.Psychology,
            accentColor = NeonCyan
        ),
        QuickPrompt(
            title = "Voice Chat Guide",
            prompt = "Aap mujhse Hindi aur English me kaise baat kar sakte hain?",
            icon = Icons.Filled.RecordVoiceOver,
            accentColor = Color(0xFF00E676)
        )
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("welcome_hero_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SpaceNavyCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SpaceNavyBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Glowing Avatar
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .scale(avatarScale),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(NeonCyan.copy(alpha = 0.45f), Color.Transparent)
                            )
                        )
                )

                Image(
                    painter = painterResource(id = R.drawable.img_anx_robot_avatar_1793739987446),
                    contentDescription = "ANX AI 3D Cyber Assistant",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(2.dp, NeonCyan, CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Welcome to ANX AI",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    brush = Brush.horizontalGradient(
                        listOf(NeonCyan, Color.White, NeonPurple)
                    )
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Your ultra-intelligent personal 3D AI companion. Powered by real-time voice, advanced tools, and smart Android controls.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 21.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0A1832))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00E676))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Hindi • English • Hinglish • Ultra-HD 3D",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = HologramTeal,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Quick suggestions for you:",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = TextSecondary,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Suggestion chips in FlowRow
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickPrompts.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(SpaceNavyLightCard)
                            .border(1.dp, item.accentColor.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .clickable { onPromptSelected(item.prompt) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = item.accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }
    }
}
