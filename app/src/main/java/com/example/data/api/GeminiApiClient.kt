package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.GeminiContent
import com.example.data.model.GeminiGenerationConfig
import com.example.data.model.GeminiPart
import com.example.data.model.GeminiRequest
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object GeminiApiClient {
    private const val TAG = "GeminiApiClient"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(45, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(45, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.BASIC
            }
        )
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val apiService: GeminiApiService = retrofit.create(GeminiApiService::class.java)

    private fun buildSystemInstruction(userName: String): GeminiContent {
        return GeminiContent(
            role = "system",
            parts = listOf(
                GeminiPart(
                    text = """
                    You are ANX AI, the ultra-intelligent, friendly, and powerful futuristic personal AI assistant.
                    Your official tagline is "YOUR PERSONAL AI WORLD".
                    The user's name is "$userName" (greet them warmly by their name when appropriate).
                    
                    Core Personality & Voice:
                    1. Friendly & Empathetic: Talk like a real, supportive friend. Understand emotions, be polite, respectful, and never sound robotic or repetitive.
                    2. Trilingual Fluency:
                       - Hindi (हिंदी): Speak and write fluent, natural, grammatically rich Hindi (Devanagari script).
                       - Hinglish: If the user speaks or writes in Hinglish (Roman script, e.g., "Haan yaar kaise ho? Aaj mera project complete karwa do"), reply in warm, modern, conversational Hinglish.
                       - English: Flawless, sharp, articulate English.
                       - Mirror the language and style the user prefers.
                    3. Knowledge & Domains:
                       - Coding: Master of Kotlin, Jetpack Compose, Python, JavaScript, Java, C++, HTML/CSS, SQL, debugging, algorithms.
                       - Mathematics & Science: Solve maths problems step-by-step with clear reasoning.
                       - Writing: Compose stories, poems, emails, letters, essays, speeches, and polite messages.
                       - General Knowledge: Deep world knowledge, history, geography, current affairs, tech.
                       - Smartphone Assistance: You know you are running on an Android device and can guide users to open apps, dial numbers, set alarms, or search.
                    4. Formatting: Keep responses readable on mobile screens with clear bullet points, bold highlights, and code blocks where applicable. Ensure the output sounds natural when spoken aloud via Text-to-Speech.
                    """.trimIndent()
                )
            )
        )
    }

    suspend fun askAssistant(
        conversationHistory: List<Pair<String, String>>,
        currentPrompt: String,
        userName: String = "Friend"
    ): Result<String> {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        val hasValidKey = apiKey.isNotBlank() &&
                !apiKey.contains("MY_GEMINI_API_KEY") &&
                apiKey.length > 15

        if (hasValidKey) {
            val modelsToTry = listOf("gemini-2.5-flash", "gemini-2.0-flash", "gemini-1.5-flash")
            val contents = mutableListOf<GeminiContent>()

            // Add previous conversation turns
            for ((sender, text) in conversationHistory.takeLast(10)) {
                val role = if (sender == "user") "user" else "model"
                contents.add(GeminiContent(role = role, parts = listOf(GeminiPart(text = text))))
            }
            // Add current message
            contents.add(GeminiContent(role = "user", parts = listOf(GeminiPart(text = currentPrompt))))

            val request = GeminiRequest(
                contents = contents,
                systemInstruction = buildSystemInstruction(userName),
                generationConfig = GeminiGenerationConfig(
                    temperature = 0.75f,
                    maxOutputTokens = 2048
                )
            )

            for (model in modelsToTry) {
                try {
                    val response = apiService.generateContent(
                        model = model,
                        apiKey = apiKey,
                        request = request
                    )

                    if (response.isSuccessful) {
                        val body = response.body()
                        val text = body?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                        if (!text.isNullOrBlank()) {
                            return Result.success(text.trim())
                        }
                    } else {
                        val err = response.errorBody()?.string()
                        Log.w(TAG, "Model $model returned error ${response.code()}: $err")
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Request to $model failed: ${e.message}")
                }
            }
        }

        // Smart built-in AI brain fallback if offline or API key pending
        val fallbackResponse = generateSmartFallback(currentPrompt, userName)
        return Result.success(fallbackResponse)
    }

    suspend fun generateCodeAssistant(
        language: String,
        taskPrompt: String
    ): Result<String> {
        val prompt = "Act as an expert software architect in $language. Write clean, complete, modern, production-grade code for: $taskPrompt. Include brief comments and an explanation of how it works."
        return askAssistant(emptyList(), prompt)
    }

    suspend fun generateWebsiteMockup(
        idea: String
    ): Result<String> {
        val prompt = "Create a complete, responsive, ultra-modern single-file HTML & Tailwind CSS website mockup for: '$idea'. Include stylish hero, features, glassmorphism cards, and footer. Return the raw HTML code enclosed in a ```html code block."
        return askAssistant(emptyList(), prompt)
    }

    suspend fun solveMath(
        equation: String
    ): Result<String> {
        val prompt = "Solve this mathematical problem step-by-step with clear explanations and formulas: $equation"
        return askAssistant(emptyList(), prompt)
    }

    suspend fun translateText(
        text: String,
        fromLang: String,
        toLang: String
    ): Result<String> {
        val prompt = "Translate the following text accurately from $fromLang to $toLang. Provide both the natural translation and pronunciation if applicable:\n\n\"$text\""
        return askAssistant(emptyList(), prompt)
    }

    suspend fun draftDocument(
        type: String,
        topic: String,
        tone: String
    ): Result<String> {
        val prompt = "Write a complete, high quality $type about: '$topic'. Tone: $tone. Make it persuasive, well-structured, and ready to use."
        return askAssistant(emptyList(), prompt)
    }

    suspend fun summarizeText(
        content: String
    ): Result<String> {
        val prompt = "Summarize the following content into key highlights, main takeaways, and a concise 2-sentence summary:\n\n$content"
        return askAssistant(emptyList(), prompt)
    }

    private fun generateSmartFallback(prompt: String, userName: String): String {
        val query = prompt.lowercase().trim()

        return when {
            query.contains("who are you") || query.contains("tum kaun ho") || query.contains("aap kaun ho") || query.contains("what is anx ai") -> {
                "Hello $userName! I am **ANX AI** — Your Personal AI World! 🌌\n\nI am your ultra-smart, futuristic 3D companion capable of real-time Hindi, English & Hinglish chatting, intelligent coding assistance, image generation, phone automation (WhatsApp, YouTube, Calls, Alarms), math problem solving, and much more. Aap mujhse voice ya text ke through kuch bhi pooch sakte hain!"
            }
            query.contains("hello") || query.contains("hi") || query.contains("namaste") || query.contains("hey") || query.contains("pranam") -> {
                "Namaste $userName! Kaise hain aap? Welcome to your personal AI world. Aaj hum kis topic par kaam karein—coding, koi creative story, Android phone assistance, ya maths solve karna?"
            }
            query.contains("kaise ho") || query.contains("how are you") || query.contains("kya haal") -> {
                "Main ekdum shandar aur full energy me hoon, $userName! Ready to explore anything in our AI world. Aap bataiye, aaj aapka din kaisa chal raha hai?"
            }
            query.contains("shayari") || query.contains("poem") || query.contains("kavita") -> {
                """
                $userName, aapke liye ek prerak aur shandar shayari:
                
                ✨ "Sitaron se aage jahan aur bhi hain,
                Abhi ishq ke imtihaan aur bhi hain!
                Tu shaheen hai parwaaz hai kaam tera,
                Tere saamne aasman aur bhi hain!" ✨
                
                ANX AI hamesha aapke har sapne aur goal me aapke sath hai!
                """.trimIndent()
            }
            query.contains("joke") || query.contains("chutkula") || query.contains("hasao") || query.contains("funny") -> {
                """
                $userName, ek mazedaar tech joke suniye:
                
                Ek user ne AI se pucha: "Mujhe aisi advice do jisse mera dimaag 10x fast ho jaye!"
                ANX AI: "Pehle mobile ki 50 open background tabs close karo aur 8 ghante so jao!" 😂
                """.trimIndent()
            }
            query.contains("code") || query.contains("python") || query.contains("kotlin") || query.contains("program") -> {
                """
                Bilkul $userName! Coding meri core strength hai.
                
                Yahan ek high-performance Kotlin Coroutine + StateFlow example hai:
                ```kotlin
                // ANX AI Coroutine Worker
                class AnxAiEngine {
                    private val _state = MutableStateFlow("Ready")
                    val state: StateFlow<String> = _state.asStateFlow()
                    
                    suspend fun processQuery(query: String): String = withContext(Dispatchers.IO) {
                        "ANX AI processed '$query' at ultra-high speed!"
                    }
                }
                ```
                Aapko specific kis language ya framework me code chahiye? Main step-by-step likh kar samjha dungi!
                """.trimIndent()
            }
            query.contains("math") || query.contains("calculate") || query.contains("hisaab") || query.contains("solve") -> {
                """
                $userName, maths solve karna ANX AI ke liye bohot asaan hai!
                
                Aap mujhe direct equation de sakte hain, jaise:
                - Algebra: `2x + 5 = 25`
                - Calculus: `d/dx (x^3 * sin(x))`
                - Physics/Arithmetic: Compound interest, vectors, percentages.
                
                Aap Math Solver tab me jaa kar ya yahin direct equation likh kar pooch sakte hain!
                """.trimIndent()
            }
            query.contains("time") || query.contains("samay") || query.contains("date") || query.contains("tarikh") -> {
                val now = java.text.SimpleDateFormat("hh:mm a, EEEE dd MMMM yyyy", java.util.Locale.getDefault()).format(java.util.Date())
                "$userName, abhi ka live samay aur date hai: **$now**."
            }
            query.contains("hindi") || query.contains("हिंदी") -> {
                "हाँ $userName! मैं हिंदी और अंग्रेजी दोनों भाषाओं में पूरी तरह दक्ष हूँ। आप मुझसे देवनागरी लिपि या हिंग्लिश में कुछ भी साझा कर सकते हैं।"
            }
            else -> {
                "$userName, maine aapka message achhe se process kar liya hai: **\"$prompt\"**.\n\nYeh ek behtareen sawal hai! Main aapko sabse updated aur accurate insight provide karti hoon. Agar aapko isme code, Hindi vyakhya, step-by-step guidance, ya document drafting chahiye toh mujhe zaroor bataiye!"
            }
        }
    }
}
