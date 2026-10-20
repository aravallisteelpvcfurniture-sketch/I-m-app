package com.example.util

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

data class AiPosterContent(
  val headerText: String,
  val festiveWish: String,
  val offerHighlight: String
)

object GeminiService {
  private const val TAG = "GeminiService"
  // Supports gemini-flash-latest as demonstrated in user curl request
  private const val MODEL_NAME = "gemini-flash-latest"
  private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

  // 60-second timeouts as mandated by Gemini guidelines
  private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

  /**
   * Resolve active API key from BuildConfig or environment.
   */
  fun getActiveApiKey(): String {
    val buildKey = BuildConfig.GEMINI_API_KEY
    if (!buildKey.isNullOrBlank() && buildKey != "MY_GEMINI_API_KEY") {
      return buildKey
    }
    val envKey = System.getenv("GEMINI_API_KEY")
    if (!envKey.isNullOrBlank()) {
      return envKey
    }
    return ""
  }

  fun isApiKeyConfigured(): Boolean {
    val key = getActiveApiKey()
    return key.isNotBlank() && key != "MY_GEMINI_API_KEY"
  }

  fun getMaskedApiKey(): String {
    val key = getActiveApiKey()
    if (key.isBlank() || key == "MY_GEMINI_API_KEY") return "Not Configured"
    return if (key.length > 8) "${key.take(4)}...${key.takeLast(4)}" else "••••••••"
  }

  /**
   * General text generation with Gemini (e.g., prompt responses).
   */
  suspend fun generateContent(prompt: String): Result<String> = withContext(Dispatchers.IO) {
    val apiKey = getActiveApiKey()
    if (apiKey.isBlank()) {
      return@withContext Result.failure(
        IllegalStateException("Gemini API key is not configured. Please configure GEMINI_API_KEY in the Secrets panel.")
      )
    }

    try {
      val requestJson = JSONObject().apply {
        val contentsArray = JSONArray().apply {
          val contentObj = JSONObject().apply {
            val partsArray = JSONArray().apply {
              put(JSONObject().put("text", prompt))
            }
            put("parts", partsArray)
          }
          put(contentObj)
        }
        put("contents", contentsArray)
      }

      val url = "$BASE_URL/$MODEL_NAME:generateContent"
      val request = Request.Builder()
        .url(url)
        .addHeader("Content-Type", "application/json")
        .addHeader("X-goog-api-key", apiKey)
        .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
        .build()

      val response = okHttpClient.newCall(request).execute()
      val responseString = response.body?.string() ?: ""

      if (!response.isSuccessful) {
        Log.e(TAG, "Gemini API error ${response.code}: $responseString")
        return@withContext Result.failure(Exception("Gemini HTTP ${response.code}: $responseString"))
      }

      val rootJson = JSONObject(responseString)
      val candidates = rootJson.optJSONArray("candidates")
      val firstCandidate = candidates?.optJSONObject(0)
      val content = firstCandidate?.optJSONObject("content")
      val parts = content?.optJSONArray("parts")
      val text = parts?.optJSONObject(0)?.optString("text")

      if (text.isNullOrBlank()) {
        Result.failure(Exception("Empty candidate returned by Gemini."))
      } else {
        Result.success(text.trim())
      }
    } catch (e: Exception) {
      Log.e(TAG, "Exception calling Gemini generateContent", e)
      Result.failure(e)
    }
  }

  /**
   * Generates tailored festive & marketing copy for Aravalli Steel & PVC Furniture.
   */
  suspend fun generateFestivalOrPromoPoster(
    occasion: String,
    recipientName: String,
    shopName: String = "Aravalli Steel PVC Furniture",
    customFocus: String = "Stainless steel railings, glass balcony, PVC modular kitchen & interior"
  ): Result<AiPosterContent> = withContext(Dispatchers.IO) {
    val apiKey = getActiveApiKey()
    if (apiKey.isBlank()) {
      return@withContext Result.failure(
        IllegalStateException("Gemini API key is not configured in build. Please add GEMINI_API_KEY in the AI Studio Secrets panel.")
      )
    }

    val prompt = """
      You are an expert marketing copywriter for '$shopName', a luxury architectural metal and interior business specializing in Stainless Steel Railings (SS 304 Jindal), Aluminium Toughened Glass Balconies, PVC Waterproof Modular Kitchens, Designer Wardrobes, and Fluted TV Units.
      
      Generate a premium festive greeting or marketing promotion for:
      - Occasion/Event: $occasion
      - Client Name to greet: $recipientName
      - Products/Focus: $customFocus
      
      Return ONLY a raw valid JSON object with the following three string keys (do NOT wrap in markdown quotes or codeblocks):
      {
        "headerText": "Catchy 2-4 word uppercase festive/promotional banner header (e.g. SHUBH DEEPAVALI / FESTIVE LUXURY OFFER / GRIHA PRAVESH MUBARAK)",
        "festiveWish": "Warm, respectful, luxury 2-3 sentence wish for the client and family celebrating their milestone or festival.",
        "offerHighlight": "Compelling 1-2 sentence business promotion or incentive (e.g., Free site 3D consultation & 10% off Jindal SS 304 railings)."
      }
    """.trimIndent()

    try {
      val requestJson = JSONObject().apply {
        val contentsArray = JSONArray().apply {
          val contentObj = JSONObject().apply {
            val partsArray = JSONArray().apply {
              put(JSONObject().put("text", prompt))
            }
            put("parts", partsArray)
          }
          put(contentObj)
        }
        put("contents", contentsArray)

        val genConfig = JSONObject().apply {
          put("temperature", 0.7)
          put("topP", 0.95)
          put("maxOutputTokens", 800)
          put("responseMimeType", "application/json")
        }
        put("generationConfig", genConfig)
      }

      val url = "$BASE_URL/$MODEL_NAME:generateContent"
      val request = Request.Builder()
        .url(url)
        .addHeader("Content-Type", "application/json")
        .addHeader("X-goog-api-key", apiKey)
        .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
        .build()

      val response = okHttpClient.newCall(request).execute()
      val responseString = response.body?.string() ?: ""

      if (!response.isSuccessful) {
        Log.e(TAG, "Gemini API error code: ${response.code}, body: $responseString")
        return@withContext Result.failure(Exception("Gemini API responded with HTTP ${response.code}: $responseString"))
      }

      val rootJson = JSONObject(responseString)
      val candidates = rootJson.optJSONArray("candidates")
      val firstCandidate = candidates?.optJSONObject(0)
      val content = firstCandidate?.optJSONObject("content")
      val parts = content?.optJSONArray("parts")
      val text = parts?.optJSONObject(0)?.optString("text")

      if (text.isNullOrBlank()) {
        return@withContext Result.failure(Exception("Gemini returned empty text response."))
      }

      // Clean markdown formatting if present
      val cleanJson = text.trim()
        .removePrefix("```json")
        .removePrefix("```")
        .removeSuffix("```")
        .trim()

      val parsed = JSONObject(cleanJson)
      val headerText = parsed.optString("headerText", "$occasion Greetings".uppercase())
      val festiveWish = parsed.optString("festiveWish", "Wishing you and your family immense prosperity, luxury, and warmth in your home.")
      val offerHighlight = parsed.optString("offerHighlight", "Special discount available on premium SS 304 Balcony Railings & PVC Wardrobes.")

      Result.success(
        AiPosterContent(
          headerText = headerText,
          festiveWish = festiveWish,
          offerHighlight = offerHighlight
        )
      )
    } catch (e: Exception) {
      Log.e(TAG, "Exception calling Gemini API", e)
      Result.failure(e)
    }
  }
}
