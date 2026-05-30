package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.PromptViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GeneratorScreen(
    viewModel: PromptViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val clipboardManager = LocalClipboardManager.current

    val platforms = listOf("ChatGPT", "Gemini", "Claude", "Midjourney", "Stable Diffusion")
    val categories = listOf("عام", "برمجة", "كتابة إعلانية", "رواية وقصص", "أعمال وتسويق", "تعليم")
    val tones = listOf("تلقائي", "احترافي", "إبداعي", "صديق ومقنع", "أكاديمي وعلمي", "ساخر وفكاهي")
    val languages = listOf("العربية", "English")

    val artStyles = listOf("واقعي (Cinematic)", "أنمي (Anime)", "رسم ثلاثي الأبعاد (3D Render)", "رسم زيتي (Oil Painting)", "فانتازيا خيالية", "بساطة (Minimalist)", "سايبربانك (Cyberpunk)")
    val cameraAngles = listOf("تلقائي (Default)", "لقطة واسعة (Wide Angle)", "عدسة ماكرو دقيقة (Macro)", "تصوير طائرة درون (Drone View)", "لقطة مقربة (Portrait / Close-up)")
    val lightings = listOf("إضاءة سينمائية", "الساعة الذهبية (Golden Hour)", "إضاءة نيون ملونة", "إضاءة استوديو ناعمة", "إضاءة خافتة غامضة")
    val aspectRatios = listOf("16:9", "1:1", "9:16", "4:3", "21:9")

    var showSaveDialog by remember { mutableStateOf(false) }
    var customSavedTitle by remember { mutableStateOf("") }
    var isConstraintsExpanded by remember { mutableStateOf(false) }

    val isImagePlatform = viewModel.selectedPlatform == "Midjourney" || viewModel.selectedPlatform == "Stable Diffusion"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome and Header Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(NeonCyan.copy(alpha = 0.4f), ElectricViolet.copy(alpha = 0.4f))
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val apiKeyAvailable = viewModel.isApiKeyAvailable()
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (apiKeyAvailable) CosmicGreen.copy(alpha = 0.15f) else AmberGlow.copy(alpha = 0.15f),
                            modifier = Modifier.padding(start = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(if (apiKeyAvailable) CosmicGreen else AmberGlow, RoundedCornerShape(50))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (apiKeyAvailable) "الذكاء الاصطناعي متصل" else "بانتظار مفتاح الـ API",
                                    color = if (apiKeyAvailable) CosmicGreen else AmberGlow,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "محترف صياغة الموجهات ✨",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "حوّل أي فكرة عادية إلى برومبت متكامل ومدهش ومحسّن لمختلف منصات الذكاء الاصطناعي.",
                        fontSize = 13.sp,
                        color = SlateText,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // Platform Selection Row
        item {
            Column {
                Text(
                    text = "1. اختر منصة الذكاء الاصطناعي المستهدفة:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(platforms) { platform ->
                        val isSelected = viewModel.selectedPlatform == platform
                        val borderBrush = if (isSelected) {
                            Brush.sweepGradient(listOf(NeonCyan, ElectricViolet))
                        } else {
                            Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    brush = if (isSelected) borderBrush else Brush.linearGradient(listOf(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    viewModel.selectedPlatform = platform
                                    if (platform == "Midjourney" || platform == "Stable Diffusion") {
                                        viewModel.selectedCategory = "صور ورسم"
                                    }
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .testTag("platform_chip_$platform")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val icon = when (platform) {
                                    "ChatGPT" -> Icons.Default.ChatBubbleOutline
                                    "Gemini" -> Icons.Default.AutoAwesome
                                    "Claude" -> Icons.Default.FilterFrames
                                    "Midjourney" -> Icons.Default.Palette
                                    else -> Icons.Default.Brush
                                }
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) NeonCyan else SlateText,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = platform,
                                    color = if (isSelected) Color.White else SlateText,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Shared Standard Configurations Row (Category, Tone, Language)
        if (!isImagePlatform) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "2. خصائص وخصائص الصياغة للنص الموجه:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )

                    // Target Tone
                    Column {
                        Text(text = "نبرة الصياغة المطلوبة:", color = SlateText, fontSize = 12.sp, modifier = Modifier.padding(bottom = 6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(tones) { tone ->
                                val isSelected = viewModel.selectedTone == tone
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.selectedTone = tone },
                                    label = { Text(tone, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ElectricViolet.copy(alpha = 0.3f),
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Category Layout
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "تصنيف الموضوع:", color = SlateText, fontSize = 12.sp, modifier = Modifier.padding(bottom = 6.dp))
                            var expandedCategory by remember { mutableStateOf(false) }
                            Box {
                                OutlinedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { expandedCategory = true },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = viewModel.selectedCategory, fontSize = 13.sp, color = Color.White, modifier = Modifier.weight(1f))
                                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = NeonCyan)
                                    }
                                }
                                DropdownMenu(
                                    expanded = expandedCategory,
                                    onDismissRequest = { expandedCategory = false },
                                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    categories.forEach { category ->
                                        DropdownMenuItem(
                                            text = { Text(category, color = Color.White) },
                                            onClick = {
                                                viewModel.selectedCategory = category
                                                expandedCategory = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Target Language
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "مخرجات البرومبت بـ:", color = SlateText, fontSize = 12.sp, modifier = Modifier.padding(bottom = 6.dp))
                            var expandedLang by remember { mutableStateOf(false) }
                            Box {
                                OutlinedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { expandedLang = true },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = viewModel.selectedLanguage, fontSize = 13.sp, color = Color.White, modifier = Modifier.weight(1f))
                                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = NeonCyan)
                                    }
                                }
                                DropdownMenu(
                                    expanded = expandedLang,
                                    onDismissRequest = { expandedLang = false },
                                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    languages.forEach { lang ->
                                        DropdownMenuItem(
                                            text = { Text(lang, color = Color.White) },
                                            onClick = {
                                                viewModel.selectedLanguage = lang
                                                expandedLang = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Image Generation Special Options (Art Style, Lighting, Camera, Aspect Ratio)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "2. خصائص البرومبت وفنون الصورة الفنية:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )

                    // Art Direction Choices
                    Column {
                        Text(text = "النمط والأسلوب الفني للرسم:", color = SlateText, fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(artStyles) { style ->
                                val isSelected = viewModel.artStyle == style
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.artStyle = style },
                                    label = { Text(style, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ElectricViolet.copy(alpha = 0.3f),
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    // Lighting Choices
                    Column {
                        Text(text = "تأثير وهيكل الإضاءة:", color = SlateText, fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(lightings) { light ->
                                val isSelected = viewModel.lightingCondition == light
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.lightingCondition = light },
                                    label = { Text(light, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NeonCyan.copy(alpha = 0.15f),
                                        selectedLabelColor = NeonCyan
                                    )
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Camera Angle Choice
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "نوع العدسة واللقطة:", color = SlateText, fontSize = 12.sp, modifier = Modifier.padding(bottom = 6.dp))
                            var expandedCam by remember { mutableStateOf(false) }
                            Box {
                                OutlinedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { expandedCam = true },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = viewModel.cameraUnit, fontSize = 12.sp, color = Color.White, modifier = Modifier.weight(1f))
                                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = NeonCyan)
                                    }
                                }
                                DropdownMenu(
                                    expanded = expandedCam,
                                    onDismissRequest = { expandedCam = false },
                                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    cameraAngles.forEach { cam ->
                                        DropdownMenuItem(
                                            text = { Text(cam, color = Color.White) },
                                            onClick = {
                                                viewModel.cameraUnit = cam
                                                expandedCam = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Aspect Ratio choice
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "مقاس شاشة الصورة (Aspect Ratio):", color = SlateText, fontSize = 11.sp, modifier = Modifier.padding(bottom = 6.dp))
                            var expandedRatio by remember { mutableStateOf(false) }
                            Box {
                                OutlinedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { expandedRatio = true },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = viewModel.aspectRatioChoice, fontSize = 12.sp, color = Color.White, modifier = Modifier.weight(1f))
                                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = NeonCyan)
                                    }
                                }
                                DropdownMenu(
                                    expanded = expandedRatio,
                                    onDismissRequest = { expandedRatio = false },
                                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    aspectRatios.forEach { ratio ->
                                        DropdownMenuItem(
                                            text = { Text(ratio, color = Color.White) },
                                            onClick = {
                                                viewModel.aspectRatioChoice = ratio
                                                expandedRatio = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Input user idea panel
        item {
            Column {
                Text(
                    text = "3. فكرتك باللغة الطبيعية (اكتب ما يدور في ذهنك):",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = viewModel.userIdea,
                    onValueChange = { viewModel.userIdea = it },
                    placeholder = {
                        Text(
                            text = if (isImagePlatform) "مثال: رائد فضاء يركب خيلاً كرتونياً فوق المجرّة..." else "اكتب فكرتك باختصار، مثلاً: حوّل هذا الجدول المبيّعات إلى شفرة بايثون... أو اكتب مقالة عن فوائد النوم...",
                            color = SlateText.copy(alpha = 0.6f),
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("user_idea_input"),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = NeonCyan,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    ),
                    textStyle = TextStyle(
                        textDirection = TextDirection.ContentOrRtl,
                        fontSize = 14.sp
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                )
            }
        }

        // Constraints and special exclusions (Expandable Area)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isConstraintsExpanded = !isConstraintsExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.SettingsInputComponent, contentDescription = null, tint = AmberGlow, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "قيود إضافية واشتراطات خاصة (اختياري)", fontSize = 13.sp, color = OffWhite, fontWeight = FontWeight.Bold)
                        }
                        Icon(
                            imageVector = if (isConstraintsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = SlateText
                        )
                    }

                    AnimatedVisibility(visible = isConstraintsExpanded) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            OutlinedTextField(
                                value = viewModel.additionalConstraints,
                                onValueChange = { viewModel.additionalConstraints = it },
                                placeholder = {
                                    Text(
                                        text = "مثال: لا تضع أي شروحات توضيحية، اجعل الحل كله في نقاط موجزة، لا تستخدم مصطلحات معقدة...",
                                        color = SlateText.copy(alpha = 0.5f),
                                        fontSize = 12.sp
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.background,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                textStyle = TextStyle(textDirection = TextDirection.ContentOrRtl, fontSize = 13.sp)
                            )
                        }
                    }
                }
            }
        }

        // Glowing Optimization Button
        item {
            val keyReady = viewModel.isApiKeyAvailable()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        if (!keyReady) {
                            Toast.makeText(context, "الرجاء توفير مفتاح Gemini API في شاشة الإعدادات أولاً.", Toast.LENGTH_LONG).show()
                        } else {
                            viewModel.generateAIRequest()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(16.dp),
                            ambientColor = if (keyReady) NeonCyan else Color.Transparent,
                            spotColor = if (keyReady) ElectricViolet else Color.Transparent
                        )
                        .testTag("generate_prompts_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (keyReady) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (keyReady) Color.White else SlateText
                    ),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .then(
                                if (keyReady) {
                                    Modifier.background(
                                        Brush.horizontalGradient(listOf(NeonCyan, ElectricViolet))
                                    )
                                } else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (viewModel.isGenerating) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "توليد البرومبت المحسّن بالذكاء الاصطناعي ✨",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Warnings / API Key Alert inside generator
        if (!viewModel.isApiKeyAvailable()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CosmicRed.copy(alpha = 0.08f)),
                    border = BorderStroke(1.dp, CosmicRed.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = CosmicRed)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "مفتاح API غير متوفر", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "يمكنك إضافة مفتاح Gemini API مجاني خاص بك بسرعة من شاشة 'الإعدادات' للبدء في استخدام محرك تحسين البرومبتات الفوري.",
                                color = OffWhite.copy(alpha = 0.8f),
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // Live generation Error Banner
        viewModel.errorMessage?.let { error ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CosmicRed.copy(alpha = 0.08f)),
                    border = BorderStroke(1.dp, CosmicRed.copy(alpha = 0.3f))
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.ErrorOutline, contentDescription = null, tint = CosmicRed)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = error, color = OffWhite, fontSize = 12.sp, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Optimisation results displayed elegantly
        if (viewModel.generatedPromptResult.isNotBlank()) {
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🚀 البرومبت الاحترافي المصاغ:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )

                        TextButton(
                            onClick = {
                                viewModel.clearInputs()
                            }
                        ) {
                            Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = null, tint = CosmicRed, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "مسح النتائج", color = CosmicRed, fontSize = 11.sp)
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SpaceCardLight)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Raw output Box with directional formatting
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = viewModel.generatedPromptResult,
                                    fontSize = 13.sp,
                                    color = Color.White,
                                    lineHeight = 22.sp,
                                    style = TextStyle(
                                        textDirection = if (isImagePlatform) TextDirection.Ltr else TextDirection.ContentOrRtl
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Output Actions Pane (Copy, Share, Save locally)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(viewModel.generatedPromptResult))
                                        Toast.makeText(context, "تم نسخ البرومبت بنجاح! 📋", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f))
                                ) {
                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "نسخ", color = NeonCyan, fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, viewModel.generatedPromptResult)
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, "مشاركة البرومبت عبر"))
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, SlateText.copy(alpha = 0.4f))
                                ) {
                                    Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = OffWhite, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "مشاركة", color = OffWhite, fontSize = 12.sp)
                                }

                                Button(
                                    onClick = {
                                        customSavedTitle = "برومبت ${viewModel.selectedPlatform} - ${viewModel.selectedCategory}"
                                        showSaveDialog = true
                                    },
                                    modifier = Modifier.weight(1.2f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet)
                                ) {
                                    Icon(imageVector = Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "حفظ ومزامنة", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Save prompt confirmation dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = {
                Text(
                    text = "حفظ البرومبت بالمكتبة 💾",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            },
            text = {
                Column {
                    Text(
                        text = "اكتب عنواناً معبراً لهذا البرومبت للعودة إليه وتطويره لاحقاً:",
                        fontSize = 13.sp,
                        color = SlateText,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = customSavedTitle,
                        onValueChange = { customSavedTitle = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        textStyle = TextStyle(textDirection = TextDirection.ContentOrRtl)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveActivePrompt(customSavedTitle)
                        showSaveDialog = false
                        Toast.makeText(context, "تم حفظ البرومبت في المكتبة بنجاح! 💾", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text(text = "حفظ", color = SpaceBlack, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text(text = "إلغاء", color = SlateText)
                }
            },
            containerColor = SpaceCard
        )
    }
}
