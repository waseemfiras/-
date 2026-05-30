package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.BuildConfig
import com.example.data.db.PromptDatabase
import com.example.data.db.SavedPrompt
import com.example.data.repository.PromptRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PromptViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPrefs: SharedPreferences =
        application.getSharedPreferences("promptify_prefs", Context.MODE_PRIVATE)

    // Initialize Room Database carefully
    private val db = Room.databaseBuilder(
        application,
        PromptDatabase::class.java,
        "promptify_db"
    ).fallbackToDestructiveMigration().build()

    private val repository = PromptRepository(db.savedPromptDao())

    // Search Query State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Saved prompts stream based on search query
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val savedPrompts: StateFlow<List<SavedPrompt>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.allPrompts
            } else {
                repository.searchPrompts(query)
            }
        }
        .catch { emit(emptyList()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Settings States
    var customApiKey by mutableStateOf(sharedPrefs.getString("pref_api_key", "") ?: "")
        private set

    var defaultPlatform by mutableStateOf(sharedPrefs.getString("pref_default_platform", "ChatGPT") ?: "ChatGPT")
        private set

    var defaultTone by mutableStateOf(sharedPrefs.getString("pref_default_tone", "احترافية") ?: "احترافية")
        private set

    var promptTemperature by mutableStateOf(sharedPrefs.getFloat("pref_default_temp", 0.7f))
        private set

    var isDarkTheme by mutableStateOf(sharedPrefs.getBoolean("pref_dark_theme", true))
        private set

    // Input States
    var userIdea by mutableStateOf("")
    var selectedPlatform by mutableStateOf(defaultPlatform)
    var selectedCategory by mutableStateOf("عام")
    var selectedTone by mutableStateOf(defaultTone)
    var selectedLanguage by mutableStateOf("العربية")
    var additionalConstraints by mutableStateOf("")

    // Image-specific Inputs
    var artStyle by mutableStateOf("واقعي (Cinematic)")
    var cameraUnit by mutableStateOf("تلقائي (Default)")
    var lightingCondition by mutableStateOf("إضاءة سينمائية")
    var aspectRatioChoice by mutableStateOf("16:9")

    // Async states
    var isGenerating by mutableStateOf(false)
        private set

    var generatedPromptResult by mutableStateOf("")
        private set

    var errorMessage by mutableStateOf<String?>(null)

    init {
        // Apply default configurations
        selectedPlatform = defaultPlatform
        selectedTone = defaultTone
    }

    /**
     * Gets the effective API key (checked first from build secrets, then custom settings key)
     */
    fun getEffectiveApiKey(): String {
        val buildKey = BuildConfig.GEMINI_API_KEY
        if (buildKey.isNotBlank() && buildKey != "MY_GEMINI_API_KEY" && buildKey != "PLACEHOLDER") {
            return buildKey
        }
        return customApiKey
    }

    fun isApiKeyAvailable(): Boolean {
        return getEffectiveApiKey().isNotBlank()
    }

    /**
     * Updates and persists custom API key
     */
    fun updateApiKey(key: String) {
        customApiKey = key
        sharedPrefs.edit().putString("pref_api_key", key).apply()
    }

    /**
     * Updates and persists default platform
     */
    fun updateDefaultPlatform(platform: String) {
        defaultPlatform = platform
        selectedPlatform = platform
        sharedPrefs.edit().putString("pref_default_platform", platform).apply()
    }

    /**
     * Updates default tone
     */
    fun updateDefaultTone(tone: String) {
        defaultTone = tone
        selectedTone = tone
        sharedPrefs.edit().putString("pref_default_tone", tone).apply()
    }

    /**
     * Updates generation temperature
     */
    fun updateTemperature(temp: Float) {
        promptTemperature = temp
        sharedPrefs.edit().putFloat("pref_default_temp", temp).apply()
    }

    /**
     * Updates dark/light theme setting
     */
    fun updateThemeMode(isDark: Boolean) {
        isDarkTheme = isDark
        sharedPrefs.edit().putBoolean("pref_dark_theme", isDark).apply()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * Clear active generation inputs
     */
    fun clearInputs() {
        userIdea = ""
        additionalConstraints = ""
        generatedPromptResult = ""
        errorMessage = null
    }

    /**
     * Set generation inputs with a pre-configured structure or template
     */
    fun loadTemplate(platform: String, category: String, idea: String, constraints: String) {
        selectedPlatform = platform
        selectedCategory = category
        userIdea = idea
        additionalConstraints = constraints
    }

    /**
     * Optimize prompting using Gemini API
     */
    fun generateAIRequest() {
        if (userIdea.trim().isBlank()) {
            errorMessage = "يرجى كتابة فكرتك أولاً لتوليد برومبت احترافي."
            return
        }

        errorMessage = null
        isGenerating = true

        viewModelScope.launch {
            val key = getEffectiveApiKey()
            val result = repository.optimizePrompt(
                apiKey = key,
                platform = selectedPlatform,
                category = selectedCategory,
                tone = selectedTone,
                language = selectedLanguage,
                userIdea = userIdea,
                additionalConstraints = additionalConstraints,
                artStyle = artStyle,
                camera = cameraUnit,
                lighting = lightingCondition,
                aspectRatio = aspectRatioChoice,
                temperature = promptTemperature
            )

            result.onSuccess { optimizedText ->
                generatedPromptResult = optimizedText
                isGenerating = false
            }.onFailure { exception ->
                errorMessage = exception.message ?: "حدث خطأ غير معروف أثناء الاتصال الخادم."
                isGenerating = false
                Log.e("PromptViewModel", "Error optimizing prompt", exception)
            }
        }
    }

    /**
     * Save the active generated prompt to Room Database
     */
    fun saveActivePrompt(customTitle: String) {
        if (generatedPromptResult.isBlank()) return

        val titleText = if (customTitle.isNotBlank()) customTitle else "برومبت $selectedPlatform - ${selectedCategory}"
        val prompt = SavedPrompt(
            title = titleText,
            promptContent = generatedPromptResult,
            originalIdea = userIdea,
            platform = selectedPlatform,
            category = selectedCategory,
            isFavorite = false
        )

        viewModelScope.launch {
            repository.insertPrompt(prompt)
        }
    }

    /**
     * Saves user's custom arbitrary prompts
     */
    fun savePromptDirectly(title: String, content: String, platform: String, category: String) {
        val prompt = SavedPrompt(
            title = title,
            promptContent = content,
            originalIdea = "برومبت مخصص وحر",
            platform = platform,
            category = category,
            isFavorite = false
        )
        viewModelScope.launch {
            repository.insertPrompt(prompt)
        }
    }

    /**
     * Toggles favorite status of saved prompt
     */
    fun toggleFavorite(prompt: SavedPrompt) {
        viewModelScope.launch {
            repository.updatePrompt(prompt.copy(isFavorite = !prompt.isFavorite))
        }
    }

    /**
     * Deletes a saved prompt
     */
    fun deleteSavedPrompt(prompt: SavedPrompt) {
        viewModelScope.launch {
            repository.deletePrompt(prompt)
        }
    }
}
