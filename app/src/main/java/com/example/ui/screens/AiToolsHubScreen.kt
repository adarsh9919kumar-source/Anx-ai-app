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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.GeminiApiClient
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
import kotlinx.coroutines.launch

@Composable
fun AiToolsHubScreen(
    onSpeakResult: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedToolIndex by remember { mutableIntStateOf(0) }
    val toolTabs = listOf(
        "🧮 Math & Calc",
        "🌐 Translator",
        "📝 Document Writer",
        "🌦️ Weather & News",
        "📑 PDF & Summarizer",
        "🎓 Learning & GK"
    )

    var inputQuery by remember { mutableStateOf("Solve: 2x^2 + 5x - 12 = 0") }
    var secondaryInput by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var toolResult by remember {
        mutableStateOf(
            """
            Quadratic Equation: 2x² + 5x - 12 = 0
            
            1. Using Quadratic Formula: x = [-b ± √(b² - 4ac)] / 2a
               Here: a = 2, b = 5, c = -12
            
            2. Discriminant: Δ = 5² - 4(2)(-12) = 25 + 96 = 121
               √Δ = √121 = 11
            
            3. Roots:
               x₁ = (-5 + 11) / 4 = 6 / 4 = 1.5 (or 3/2)
               x₂ = (-5 - 11) / 4 = -16 / 4 = -4
            
            ✅ Solution: x = 1.5, -4
            """.trimIndent()
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("ai_tools_hub_screen"),
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
                        .background(QuantumGold.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = QuantumGold,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "ANX AI Tools Suite",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            brush = Brush.horizontalGradient(listOf(QuantumGold, NeonCyan))
                        )
                    )
                    Text(
                        text = "Math solver, translator, documents, summarizer & weather",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }
            }
        }

        // Scrollable Tab Selector
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedToolIndex,
                containerColor = SpaceNavyCard,
                contentColor = NeonCyan,
                edgePadding = 8.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedToolIndex]),
                        color = NeonCyan
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, SpaceNavyBorder, RoundedCornerShape(14.dp))
            ) {
                toolTabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedToolIndex == index,
                        onClick = {
                            selectedToolIndex = index
                            when (index) {
                                0 -> inputQuery = "Solve: 2x^2 + 5x - 12 = 0"
                                1 -> {
                                    inputQuery = "Namaste! Main kal tumhare ghar aaunga aur hum milkar naya project banayenge."
                                    secondaryInput = "English"
                                }
                                2 -> {
                                    inputQuery = "Leave application for 2 days due to fever"
                                    secondaryInput = "Formal & Polite"
                                }
                                3 -> inputQuery = "Weather in New Delhi and current news"
                                4 -> inputQuery = "Artificial intelligence and machine learning are revolutionizing software development by generating code, designing interfaces, and providing natural voice assistants that adapt to human speech."
                                5 -> inputQuery = "Explain the Theory of Relativity in simple everyday words with Hindi-English analogy"
                            }
                        },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedToolIndex == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    )
                }
            }
        }

        // Tool Inputs
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(SpaceNavyCard)
                    .border(1.dp, SpaceNavyBorder, RoundedCornerShape(18.dp))
                    .padding(14.dp)
            ) {
                val inputLabel = when (selectedToolIndex) {
                    0 -> "Enter Mathematical Problem or Calculus Equation:"
                    1 -> "Enter Text to Translate (Hindi / English / Hinglish):"
                    2 -> "Document Topic & Purpose:"
                    3 -> "Enter City or News Topic:"
                    4 -> "Paste PDF / Article Text to Summarize:"
                    else -> "Ask any Learning or General Knowledge Question:"
                }

                Text(text = inputLabel, color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = inputQuery,
                    onValueChange = { inputQuery = it },
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
                    maxLines = if (selectedToolIndex == 4) 6 else 3
                )

                // Secondary options for Translator / Document writer
                if (selectedToolIndex == 1) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Target Language:", color = TextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    val targetLangs = listOf("English", "Hindi (हिंदी)", "Hinglish", "Spanish", "French", "Japanese")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(targetLangs) { lang ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (secondaryInput == lang) NeonCyan.copy(alpha = 0.2f) else Color(0xFF132242))
                                    .border(1.dp, if (secondaryInput == lang) NeonCyan else Color.Transparent, RoundedCornerShape(8.dp))
                                    .clickable { secondaryInput = lang }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(lang, color = if (secondaryInput == lang) NeonCyan else TextPrimary, fontSize = 11.sp)
                            }
                        }
                    }
                } else if (selectedToolIndex == 2) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Document Type / Tone:", color = TextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    val docTones = listOf("Formal Email", "Creative Story", "Hindi Poem / Shayari", "Resume Bio", "Polite WhatsApp")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(docTones) { tone ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (secondaryInput == tone) NeonPurple.copy(alpha = 0.2f) else Color(0xFF132242))
                                    .border(1.dp, if (secondaryInput == tone) NeonPurple else Color.Transparent, RoundedCornerShape(8.dp))
                                    .clickable { secondaryInput = tone }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(tone, color = if (secondaryInput == tone) NeonPurple else TextPrimary, fontSize = 11.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Button
                Button(
                    onClick = {
                        if (inputQuery.isNotBlank()) {
                            scope.launch {
                                isProcessing = true
                                when (selectedToolIndex) {
                                    0 -> {
                                        val res = GeminiApiClient.solveMath(inputQuery)
                                        toolResult = res.getOrNull() ?: "Solved: $inputQuery"
                                    }
                                    1 -> {
                                        val target = if (secondaryInput.isNotBlank()) secondaryInput else "English"
                                        val res = GeminiApiClient.translateText(inputQuery, "Auto", target)
                                        toolResult = res.getOrNull() ?: "Translation to $target"
                                    }
                                    2 -> {
                                        val tone = if (secondaryInput.isNotBlank()) secondaryInput else "Professional"
                                        val res = GeminiApiClient.draftDocument(tone, inputQuery, tone)
                                        toolResult = res.getOrNull() ?: "Drafted document"
                                    }
                                    3 -> {
                                        val prompt = "Provide current weather insights, temperature estimate and latest news headlines for: '$inputQuery'."
                                        val res = GeminiApiClient.askAssistant(emptyList(), prompt)
                                        toolResult = res.getOrNull() ?: "Weather report for $inputQuery"
                                    }
                                    4 -> {
                                        val res = GeminiApiClient.summarizeText(inputQuery)
                                        toolResult = res.getOrNull() ?: "Summary complete"
                                    }
                                    else -> {
                                        val res = GeminiApiClient.askAssistant(emptyList(), inputQuery)
                                        toolResult = res.getOrNull() ?: "Knowledge answer"
                                    }
                                }
                                isProcessing = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("run_ai_tool_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(color = SpaceNavyDark, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ANX AI Neural Engine Processing...", color = SpaceNavyDark, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(imageVector = Icons.Filled.AutoAwesome, contentDescription = null, tint = SpaceNavyDark)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Execute with ANX AI", color = SpaceNavyDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Result Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF070E20))
                    .border(1.dp, Brush.horizontalGradient(listOf(NeonCyan, NeonPurple)), RoundedCornerShape(18.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ANX AI INTELLIGENCE OUTPUT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NeonCyan,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )

                    Row {
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Result", toolResult))
                                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.ContentCopy, contentDescription = "Copy", tint = TextSecondary, modifier = Modifier.size(16.dp))
                        }
                        IconButton(
                            onClick = { onSpeakResult(toolResult) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.VolumeUp, contentDescription = "Speak", tint = NeonCyan, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF040712))
                        .padding(12.dp)
                ) {
                    Text(
                        text = toolResult.trim(),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextPrimary,
                            lineHeight = 22.sp
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
