package com.example.data.repository

import com.example.data.api.Content
import com.example.data.api.GenerateContentRequest
import com.example.data.api.GeminiApiService
import com.example.data.api.GenerationConfig
import com.example.data.api.Part
import com.example.data.api.RetrofitClient
import com.example.data.db.SavedPrompt
import com.example.data.db.SavedPromptDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PromptRepository(
    private val savedPromptDao: SavedPromptDao,
    private val apiService: GeminiApiService = RetrofitClient.service
) {
    val allPrompts: Flow<List<SavedPrompt>> = savedPromptDao.getAllPrompts()
    val favoritePrompts: Flow<List<SavedPrompt>> = savedPromptDao.getFavoritePrompts()

    fun searchPrompts(query: String): Flow<List<SavedPrompt>> {
        return savedPromptDao.searchPrompts("%$query%")
    }

    suspend fun insertPrompt(prompt: SavedPrompt) = withContext(Dispatchers.IO) {
        savedPromptDao.insertPrompt(prompt)
    }

    suspend fun updatePrompt(prompt: SavedPrompt) = withContext(Dispatchers.IO) {
        savedPromptDao.updatePrompt(prompt)
    }

    suspend fun deletePrompt(prompt: SavedPrompt) = withContext(Dispatchers.IO) {
        savedPromptDao.deletePrompt(prompt)
    }

    suspend fun deletePromptById(id: Int) = withContext(Dispatchers.IO) {
        savedPromptDao.deletePromptById(id)
    }

    /**
     * Generates or optimizes a prompt using Gemini.
     */
    suspend fun optimizePrompt(
        apiKey: String,
        platform: String,
        category: String,
        tone: String,
        language: String,
        userIdea: String,
        additionalConstraints: String = "",
        artStyle: String = "",
        camera: String = "",
        lighting: String = "",
        aspectRatio: String = "",
        temperature: Float = 0.7f
    ): Result<String> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.failure(Exception("مفتاح API الخاص بـ Gemini غير مهيأ. يرجى إضافته من صفحة الإعدادات."))
        }

        // Construct the instructions for Gemini depending on whether it's text-to-text or text-to-image
        val isImageModel = platform == "Midjourney" || platform == "Stable Diffusion"
        
        val systemMessage = if (isImageModel) {
            """
            أنت مهندس موجهات صور (Image Prompt Architect) محترف للغاية.
            مهمتك هي تحويل فكرة المستخدم البسيطة إلى برومبت صور إنجليزي فائق الدقة والتفصيل والجمال (جميع برومبتات Midjourney و Stable Diffusion يجب أن تُكتب باللغة الإنجليزية لتفهمها النماذج بشكل صحيح).
            
            القواعد الذهبية:
            1. اكتب البرومبت النهائي باللغة الإنجليزية حصراً.
            2. ابدأ بالوصف الرئيسي بدقة.
            3. أضف النمط الفني ($artStyle)، إعدادات الكاميرا والعدسة ($camera)، تفاصيل الإضاءة ($lighting)، ونسبة العرض إلى الارتفاع ($aspectRatio) إذا كانت متوفرة.
            4. أضف كلمات دلالية احترافية مثل: ultra-detailed, photorealistic, cinematic lighting, 8k resolution, volumetric rendering.
            5. تجنب الكلمات المكررة غير المفيدة.
            6. اجعل الإخراج النهائي في شكل نص برومبت صافي جاهز للنسخ، دون أي مقدمات أو شروحات إضافية باللغة العربية، فقط البرومبت الإنجليزي النهائي.
            """.trimIndent()
        } else {
            """
            أنت خبير هندسة موجهات نصية ومتخصص ذكاء اصطناعي (Prompt Engineer).
            مهمتك هي تحويل فكرة المستخدم البسيطة أو طلبه إلى برومبت هيكلي متكامل واحترافي يحفز النماذج اللغوية (مثل ChatGPT, Claude, Gemini) على تقديم أفضل إجابة دقيقة وصحيحة وخالية من الهلوسة.
            
            يُفضل صياغة البرومبت النهائي بناءً على صيغة الهيكلة الاحترافية:
            - الدور (Role): من يجب أن يكون الذكاء الاصطناعي (مثلاً: خبير برمجيات، روائي عالمي، كاتب إعلانات محترف).
            - السياق (Context): معلومات الخلفية والتفاصيل اللازمة.
            - المهمة (Task): تحديد المطلوب بدقة شديدة خطوة بخطوة.
            - القيود (Constraints): ما الذي يجب تجنبه وما هي الشروط والحدود الصارمة.
            - أسلوب الإخراج (Output Style): الهيكل المطلوب (نقاط، جدول، شفرة برمجية، نبرة معينة).
            
            الخيارات المطلوبة المحددة:
            - المنصة المستهدفة: $platform
            - النبرة المطلوبة: $tone
            - لغة الإجابة المستهدفة للبرومبت: $language
            
            القيود الإضافية المحددة من المستخدم: $additionalConstraints
            
            اكتب البرومبت بذكاء وحرفية عالية، واجعل المخرج النهائي هو نص البرومبت الفعلي المقترح فقط، دون شروحات أو تزيين خارجي، لكي يتمكن المستخدم من نسخه واستخدامه مباشرة.
            """.trimIndent()
        }

        val userPrompt = """
            الفكرة الأساسية من المستخدم:
            "$userIdea"
            
            من فضلك حسن هذه الفكرة واصنع منها برومبت متكامل ومبهر ومحسّن خصيصاً لمنصة $platform.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = userPrompt)))
            ),
            generationConfig = GenerationConfig(
                temperature = temperature,
                maxOutputTokens = 2048
            ),
            systemInstruction = Content(parts = listOf(Part(text = systemMessage)))
        )

        try {
            val response = apiService.generateContent(apiKey, request)
            val textResult = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (textResult != null) {
                Result.success(textResult.trim())
            } else {
                Result.failure(Exception("لم يستجب نموذج الذكاء الاصطناعي بنص صحيح."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
