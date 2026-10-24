package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class AiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private val systemPrompt =
        "Kamu adalah RUZIE, asisten AI yang cerdas, ramah, dan helpful. " +
        "Kamu berbicara dalam bahasa Indonesia yang baik dan sopan. " +
        "Bantu pengguna dengan berbagai pertanyaan dan topik dengan format teks yang rapi dan mudah dibaca."

    suspend fun generateAiResponse(
        prompt: String,
        conversationHistory: List<Pair<String, String>> = emptyList()
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        // If a real Gemini API Key is provided, attempt Gemini first
        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val geminiResult = callGeminiApi(prompt, conversationHistory, apiKey)
                if (geminiResult.isSuccess) {
                    return@withContext geminiResult
                } else {
                    Log.w("AiService", "Gemini call failed, switching to Pollinations AI fallback: ${geminiResult.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                Log.w("AiService", "Gemini error: ${e.message}, falling back to Pollinations")
            }
        }

        // Fallback to Pollinations AI (works without any API key)
        return@withContext callPollinationsAi(prompt, conversationHistory)
    }

    private fun callGeminiApi(
        prompt: String,
        history: List<Pair<String, String>>,
        apiKey: String
    ): Result<String> {
        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"

            val contentsArray = JSONArray()

            // Add previous recent messages
            val recentHistory = history.takeLast(6)
            for ((role, text) in recentHistory) {
                val item = JSONObject()
                item.put("role", if (role == "user") "user" else "model")
                val parts = JSONArray()
                parts.put(JSONObject().put("text", text))
                item.put("parts", parts)
                contentsArray.put(item)
            }

            // Current prompt
            val currentItem = JSONObject()
            currentItem.put("role", "user")
            val currentParts = JSONArray()
            currentParts.put(JSONObject().put("text", prompt))
            currentItem.put("parts", currentParts)
            contentsArray.put(currentItem)

            val payload = JSONObject().apply {
                val sysPart = JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().put("text", systemPrompt)))
                }
                put("system_instruction", sysPart)
                put("contents", contentsArray)

                val genConfig = JSONObject().apply {
                    put("temperature", 0.7)
                    put("maxOutputTokens", 2048)
                }
                put("generationConfig", genConfig)
            }

            val request = Request.Builder()
                .url(url)
                .post(payload.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return Result.failure(Exception("Gemini HTTP ${response.code}: $responseBody"))
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.getJSONObject("content")
                val parts = content.getJSONArray("parts")
                if (parts.length() > 0) {
                    val text = parts.getJSONObject(0).optString("text", "")
                    if (text.isNotBlank()) {
                        return Result.success(text.trim())
                    }
                }
            }
            return Result.failure(Exception("Empty response from Gemini"))
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    private fun callPollinationsAi(
        prompt: String,
        history: List<Pair<String, String>>
    ): Result<String> {
        try {
            val url = "https://text.pollinations.ai/"

            val messagesArray = JSONArray()
            messagesArray.put(JSONObject().apply {
                put("role", "system")
                put("content", systemPrompt)
            })

            val recentHistory = history.takeLast(6)
            for ((role, text) in recentHistory) {
                messagesArray.put(JSONObject().apply {
                    put("role", if (role == "user") "user" else "assistant")
                    put("content", text)
                })
            }

            messagesArray.put(JSONObject().apply {
                put("role", "user")
                put("content", prompt)
            })

            val payload = JSONObject().apply {
                put("messages", messagesArray)
                put("model", "openai")
                put("temperature", 0.7)
                put("max_tokens", 2048)
                put("jsonMode", false)
            }

            val request = Request.Builder()
                .url(url)
                .post(payload.toString().toRequestBody(jsonMediaType))
                .header("User-Agent", "RuzieAI-Android/1.0")
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return Result.failure(Exception("Pollinations HTTP ${response.code}: $responseBody"))
            }

            val replyText = responseBody.trim()
            if (replyText.isNotEmpty()) {
                return Result.success(replyText)
            } else {
                return Result.failure(Exception("Empty response from AI"))
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}
