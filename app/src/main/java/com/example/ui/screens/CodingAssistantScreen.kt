package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.GeminiApiClient
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.HologramTeal
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SpaceNavyBorder
import com.example.ui.theme.SpaceNavyCard
import com.example.ui.theme.SpaceNavyDark
import com.example.ui.theme.SpaceNavyLightCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun CodingAssistantScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0: Code Assistant, 1: Website Builder
    val languages = listOf("Kotlin", "Python", "JavaScript", "HTML / Tailwind", "Java", "C++", "SQL")
    var selectedLanguage by remember { mutableStateOf("Kotlin") }

    var codePrompt by remember { mutableStateOf("Build a Jetpack Compose glowing cybernetic button with ripple animation") }
    var websitePrompt by remember { mutableStateOf("Futuristic personal portfolio for an AI engineer with dark neon theme and project cards") }

    var isGenerating by remember { mutableStateOf(false) }

    var generatedCodeResult by remember {
        mutableStateOf(
            """
            // Kotlin Jetpack Compose - Cybernetic Glow Button
            @Composable
            fun CyberneticButton(
                text: String,
                onClick: () -> Unit
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF00F0FF), Color(0xFFB026FF))
                            )
                        )
                        .clickable { onClick() }
                        .padding(horizontal = 24.dp, vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = text,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                }
            }
            """.trimIndent()
        )
    }

    var generatedWebsiteHtml by remember {
        mutableStateOf(
            """
            <!DOCTYPE html>
            <html lang="en">
            <head>
              <meta charset="UTF-8">
              <script src="https://cdn.tailwindcss.com"></script>
              <title>ANX AI Personal Portfolio</title>
            </head>
            <body class="bg-gray-950 text-white min-h-screen p-8">
              <header class="max-w-4xl mx-auto flex justify-between items-center py-6 border-b border-cyan-500/30">
                <h1 class="text-2xl font-black bg-gradient-to-r from-cyan-400 to-purple-500 bg-clip-text text-transparent">ANX AI</h1>
                <nav class="space-x-6 text-sm text-cyan-300">
                  <a href="#">Projects</a>
                  <a href="#">Skills</a>
                  <a href="#">Contact</a>
                </nav>
              </header>
              <main class="max-w-4xl mx-auto mt-12 text-center">
                <h2 class="text-4xl font-extrabold text-cyan-400">Your Personal AI World</h2>
                <p class="mt-4 text-gray-400">Crafting the future of intelligent Android applications.</p>
              </main>
            </body>
            </html>
            """.trimIndent()
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("coding_assistant_screen"),
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
                        .background(NeonPurple.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (selectedTabIndex == 0) Icons.Filled.Code else Icons.Filled.Web,
                        contentDescription = null,
                        tint = NeonPurple,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (selectedTabIndex == 0) "AI Coding Assistant" else "AI Website Builder",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            brush = Brush.horizontalGradient(listOf(NeonPurple, NeonCyan))
                        )
                    )
                    Text(
                        text = if (selectedTabIndex == 0) "Generate, debug & explain code in real-time" else "Build responsive HTML & Tailwind web apps",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }
            }
        }

        // Tabs: Code Assistant vs Website Builder
        item {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = SpaceNavyCard,
                contentColor = NeonCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = NeonCyan
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, SpaceNavyBorder, RoundedCornerShape(14.dp))
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Text(
                            text = "Code Assistant",
                            fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Text(
                            text = "Website Builder",
                            fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // Language Pills (if Code Assistant)
        if (selectedTabIndex == 0) {
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(languages) { lang ->
                        val isSelected = lang == selectedLanguage
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) SpaceNavyLightCard else SpaceNavyCard)
                                .border(
                                    1.dp,
                                    if (isSelected) NeonCyan else SpaceNavyBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedLanguage = lang }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = lang,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) NeonCyan else TextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }
            }
        }

        // Prompt Input
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
                    text = if (selectedTabIndex == 0) "What would you like to build in $selectedLanguage?" else "Describe your website or landing page idea:",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = if (selectedTabIndex == 0) codePrompt else websitePrompt,
                    onValueChange = {
                        if (selectedTabIndex == 0) codePrompt = it else websitePrompt = it
                    },
                    modifier = Modifier.fillMaxWidth(),
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

                // Quick presets
                val samplePills = if (selectedTabIndex == 0) {
                    listOf("REST API Client", "Binary Search Algorithm", "Room Database Entity", "Coroutines Flow Worker")
                } else {
                    listOf("Portfolio Page", "Crypto Dashboard", "SaaS Landing Page", "Restaurant Menu")
                }

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(samplePills) { pill ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF132242))
                                .clickable {
                                    if (selectedTabIndex == 0) codePrompt = "Write $selectedLanguage code for $pill"
                                    else websitePrompt = "Build a modern single-page $pill with dark futuristic theme"
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = pill, style = MaterialTheme.typography.labelSmall.copy(color = HologramTeal, fontSize = 10.sp))
                        }
                    }
                }
            }
        }

        // Generate Action Button
        item {
            Button(
                onClick = {
                    val prompt = if (selectedTabIndex == 0) codePrompt else websitePrompt
                    if (prompt.isNotBlank()) {
                        scope.launch {
                            isGenerating = true
                            if (selectedTabIndex == 0) {
                                val result = GeminiApiClient.generateCodeAssistant(selectedLanguage, prompt)
                                result.onSuccess {
                                    generatedCodeResult = it
                                }
                            } else {
                                val result = GeminiApiClient.generateWebsiteMockup(prompt)
                                result.onSuccess {
                                    generatedWebsiteHtml = it
                                }
                            }
                            isGenerating = false
                            Toast.makeText(context, "Code generated successfully!", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("generate_code_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("ANX AI Generating Code...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (selectedTabIndex == 0) "Generate $selectedLanguage Code" else "Build Modern Website",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }

        // Code Viewer with Copy Action
        item {
            val contentToDisplay = if (selectedTabIndex == 0) generatedCodeResult else generatedWebsiteHtml
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF050813))
                    .border(1.dp, SpaceNavyBorder, RoundedCornerShape(18.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedTabIndex == 0) "GENERATED $selectedLanguage".uppercase() else "INDEX.HTML / TAILWIND",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NeonCyan,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )

                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Code", contentToDisplay))
                            Toast.makeText(context, "Code copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF132242)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.ContentCopy, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Code", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF03050C))
                        .border(1.dp, Color(0xFF15223C), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = contentToDisplay.trim(),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFBCE0FD),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
