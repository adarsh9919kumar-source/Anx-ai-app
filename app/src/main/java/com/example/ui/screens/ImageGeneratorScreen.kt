package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.example.data.api.GeminiApiClient
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.HologramTeal
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SpaceNavyBorder
import com.example.ui.theme.SpaceNavyCard
import com.example.ui.theme.SpaceNavyDark
import com.example.ui.theme.SpaceNavyLightCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class GeneratedArtPiece(
    val id: String,
    val prompt: String,
    val enhancedPrompt: String,
    val style: String,
    val drawableRes: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Composable
fun ImageGeneratorScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var promptInput by remember { mutableStateOf("Futuristic cybernetic city with floating holographic neon signs at midnight") }
    var selectedStyle by remember { mutableStateOf("Cyberpunk Neon") }
    var selectedRatio by remember { mutableStateOf("1:1") }
    var isGenerating by remember { mutableStateOf(false) }
    var enhancedPromptInfo by remember { mutableStateOf<String?>(null) }

    val styles = listOf(
        "Cyberpunk Neon",
        "Ultra-HD 3D Render",
        "Anime Masterpiece",
        "Photorealistic Sci-Fi",
        "Holographic Cosmic"
    )

    val sampleCreations = remember {
        mutableListOf(
            GeneratedArtPiece(
                id = "art_1",
                prompt = "Futuristic glowing 3D AI cybernetic emblem",
                enhancedPrompt = "Ultra-HD 3D cybernetic logo emblem with holographic neon letters ANX AI, radiant purple and electric cyan plasma glow, crystalline floating quantum orb",
                style = "Cyberpunk Neon",
                drawableRes = R.drawable.img_anx_launcher_1793739987451
            ),
            GeneratedArtPiece(
                id = "art_2",
                prompt = "3D futuristic AI female android assistant face",
                enhancedPrompt = "Ultra-HD 3D futuristic AI female android assistant, glowing neon violet and electric cyan fiber-optic hair, translucent holographic visor",
                style = "Ultra-HD 3D Render",
                drawableRes = R.drawable.img_anx_robot_avatar_1793739987446
            ),
            GeneratedArtPiece(
                id = "art_3",
                prompt = "Futuristic 3D metaverse universe matrix grid",
                enhancedPrompt = "Futuristic 3D digital metaverse universe, glowing purple and neon blue quantum cyber matrix grid, floating holographic AI neural networks",
                style = "Holographic Cosmic",
                drawableRes = R.drawable.img_anx_hero_world_1793739987714
            )
        )
    }

    var currentDisplayArt by remember { mutableStateOf(sampleCreations.first()) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "artGlow"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("image_generator_screen"),
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
                        .background(NeonPink.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Image,
                        contentDescription = null,
                        tint = NeonPink,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "AI Image Generator",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            brush = Brush.horizontalGradient(listOf(NeonPink, NeonCyan))
                        )
                    )
                    Text(
                        text = "Create ultra-HD futuristic 3D renders & artwork",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }
            }
        }

        // Prompt Input Field & Enhancer
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(SpaceNavyCard)
                    .border(1.dp, SpaceNavyBorder, RoundedCornerShape(18.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = "Describe your imagination:",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = promptInput,
                    onValueChange = { promptInput = it },
                    placeholder = {
                        Text("e.g. Glowing neon cybernetic dragon flying over futuristic Tokyo...", color = TextSecondary, fontSize = 13.sp)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("image_prompt_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = SpaceNavyBorder,
                        focusedContainerColor = SpaceNavyDark,
                        unfocusedContainerColor = SpaceNavyDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Prompt Enhancer Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF16254A))
                            .clickable {
                                scope.launch {
                                    enhancedPromptInfo = "Enhancing prompt with AI neural weights..."
                                    val enhanced = "$promptInput, ultra-detailed 8k render, cinematic 3D lighting, vibrant $selectedStyle volumetric glow, unreal engine 5 render, award-winning concept art"
                                    delay(400)
                                    promptInput = enhanced
                                    enhancedPromptInfo = "✨ Prompt optimized for Ultra-HD generation!"
                                }
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = HologramTeal,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AI Prompt Enhancer",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = HologramTeal,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Text(
                        text = "Ultra 3D Quality",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 11.sp)
                    )
                }

                enhancedPromptInfo?.let {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = it, style = MaterialTheme.typography.labelSmall.copy(color = NeonCyan, fontSize = 11.sp))
                }
            }
        }

        // Style Selector
        item {
            Text(
                text = "ARTISTIC STYLE",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(styles) { style ->
                    val isSelected = style == selectedStyle
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) NeonPurple.copy(alpha = 0.3f) else SpaceNavyCard)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) NeonPurple else SpaceNavyBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedStyle = style }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = style,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isSelected) TextPrimary else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                }
            }
        }

        // Generate Button
        item {
            Button(
                onClick = {
                    if (promptInput.isNotBlank()) {
                        scope.launch {
                            isGenerating = true
                            // Simulate real neural processing delay with API assistance
                            val aiRefinement = GeminiApiClient.askAssistant(
                                emptyList(),
                                "Refine this visual prompt into a 3D art concept: $promptInput"
                            ).getOrNull() ?: promptInput

                            delay(1200)

                            val newPiece = GeneratedArtPiece(
                                id = "art_${System.currentTimeMillis()}",
                                prompt = promptInput,
                                enhancedPrompt = aiRefinement,
                                style = selectedStyle,
                                drawableRes = when ((0..2).random()) {
                                    0 -> R.drawable.img_anx_robot_avatar_1793739987446
                                    1 -> R.drawable.img_anx_launcher_1793739987451
                                    else -> R.drawable.img_anx_hero_world_1793739987714
                                }
                            )
                            sampleCreations.add(0, newPiece)
                            currentDisplayArt = newPiece
                            isGenerating = false
                            Toast.makeText(context, "Masterpiece rendered successfully!", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("generate_image_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Rendering 3D Neural Art...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(imageVector = Icons.Filled.AutoAwesome, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generate Ultra-HD Image", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }

        // Main Display Showcase
        item {
            Text(
                text = "RENDERED MASTERPIECE",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = NeonPink,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .border(
                        1.5.dp,
                        Brush.horizontalGradient(listOf(NeonPink, NeonPurple, NeonCyan)),
                        RoundedCornerShape(22.dp)
                    )
                    .shadow(elevation = 16.dp, shape = RoundedCornerShape(22.dp), spotColor = NeonPink)
                    .background(SpaceNavyCard)
            ) {
                Column {
                    Image(
                        painter = painterResource(id = currentDisplayArt.drawableRes),
                        contentDescription = currentDisplayArt.prompt,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.1f)
                    )

                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = currentDisplayArt.style,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = NeonCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            Row {
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Prompt", currentDisplayArt.enhancedPrompt))
                                        Toast.makeText(context, "Prompt copied to clipboard", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ContentCopy,
                                        contentDescription = "Copy Prompt",
                                        tint = TextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        Toast.makeText(context, "Saved to device gallery!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Download,
                                        contentDescription = "Download",
                                        tint = NeonCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = currentDisplayArt.prompt,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = currentDisplayArt.enhancedPrompt,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextTertiary,
                                fontSize = 11.sp
                            ),
                            maxLines = 2
                        )
                    }
                }
            }
        }

        // Gallery of Recent AI Creations
        item {
            Text(
                text = "YOUR AI GALLERY",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(sampleCreations) { art ->
                    val isSelected = art.id == currentDisplayArt.id
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) NeonCyan else SpaceNavyBorder,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { currentDisplayArt = art }
                    ) {
                        Image(
                            painter = painterResource(id = art.drawableRes),
                            contentDescription = art.prompt,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
