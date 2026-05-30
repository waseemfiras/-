package com.example.ui.screens

import android.widget.Toast
import java.util.Locale
import androidx.compose.foundation.BorderStroke
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.PromptViewModel

@Composable
fun SettingsScreen(
    viewModel: PromptViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    var tempApiKey by remember { mutableStateOf(viewModel.customApiKey) }
    var isKeyVisible by remember { mutableStateOf(false) }

    val platforms = listOf("ChatGPT", "Gemini", "Claude", "Midjourney", "Stable Diffusion")
    val defaultTones = listOf("تلقائي", "احترافي", "إبداعي", "صديق ومقنع", "أكاديمي وعلمي")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Settings Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    )
                    .border(
                        1.dp,
                        Brush.horizontalGradient(listOf(NeonCyan.copy(alpha = 0.5f), ElectricViolet.copy(alpha = 0.5f))),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "التخصيص والإعدادات الذكية ⚙️",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "قم بضبط محرك التوليد، تخصيص مفاتيح الاتصال وإدارة السلوك العام لتطابق احتياجاتك.",
                        fontSize = 12.sp,
                        color = SlateText,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Section: API Key Configuration
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SpaceCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.VpnKey, contentDescription = null, tint = NeonCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "مفتاح اتصال Google Gemini API", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "إذا لم تقم بتهيئة مفتاح API ضمن 'أسرار المنصة' في AI Studio البناء، يمكنك إدخال مفتاح API الخاص بك يدوياً هنا محلياً للاستفادة من محرك التحسين السحابي في صياغة البرومبتات مجاناً.",
                        color = SlateText,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = tempApiKey,
                        onValueChange = { tempApiKey = it },
                        placeholder = { Text("أدخل مفتاح AI_KEY يبدأ بـ AIzaSy...", fontSize = 12.sp, color = SlateText.copy(alpha = 0.4f)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("api_key_input_field"),
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = if (isKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isKeyVisible = !isKeyVisible }) {
                                Icon(
                                    imageVector = if (isKeyVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = SlateText
                                )
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = SpaceBlack,
                            unfocusedContainerColor = SpaceBlack,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = NeonCyan
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                uriHandler.openUri("https://aistudio.google.com/")
                            }
                        ) {
                            Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "احصل على مفتاح مجاني من Google", fontSize = 11.sp, color = NeonCyan)
                        }

                        Button(
                            onClick = {
                                viewModel.updateApiKey(tempApiKey.trim())
                                Toast.makeText(context, "تم حفظ وتحديث مفتاح API بنجاح ✅", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "حفظ المفتاح", color = SpaceBlack, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Section: Generation Parameters / Engine Configs
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SpaceCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = ElectricViolet)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "محددات المحرك التوليدي (معايير الحرية البوقية)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Temperature slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "درجة الابتكار والعشوائية (Temperature):", color = OffWhite, fontSize = 12.sp)
                            Text(
                                text = String.format(Locale.US, "%.1f", viewModel.promptTemperature),
                                color = ElectricViolet,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Slider(
                            value = viewModel.promptTemperature,
                            onValueChange = { viewModel.updateTemperature(it) },
                            valueRange = 0.0f..1.0f,
                            steps = 9,
                            colors = SliderDefaults.colors(
                                thumbColor = ElectricViolet,
                                activeTrackColor = ElectricViolet,
                                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "أكثر تركيزاً ودقة لغوية", color = SlateText, fontSize = 10.sp)
                            Text(text = "أكثر حرية وإبداعية", color = SlateText, fontSize = 10.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Default target Platform selection drop
                    Column {
                        Text(text = "المنصة التلقائية عند إقلاع التطبيق:", color = OffWhite, fontSize = 12.sp, modifier = Modifier.padding(bottom = 6.dp))
                        var expandedPlatform by remember { mutableStateOf(false) }
                        Box {
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expandedPlatform = true },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = viewModel.defaultPlatform, fontSize = 13.sp, color = Color.White)
                                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = NeonCyan)
                                }
                            }
                            DropdownMenu(
                                expanded = expandedPlatform,
                                onDismissRequest = { expandedPlatform = false },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                platforms.forEach { platform ->
                                    DropdownMenuItem(
                                        text = { Text(platform, color = Color.White) },
                                        onClick = {
                                            viewModel.updateDefaultPlatform(platform)
                                            expandedPlatform = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: Theme Mode Toggles
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SpaceCard)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Palette, contentDescription = null, tint = AmberGlow)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "المظهر والنظام اللوني الكوني الداكن", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "ألغِ التحديد للانتقال للمظهر النهاري الساطع", color = SlateText, fontSize = 11.sp)
                        }
                    }

                    Switch(
                        checked = viewModel.isDarkTheme,
                        onCheckedChange = { viewModel.updateThemeMode(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AmberGlow,
                            checkedTrackColor = AmberGlow.copy(alpha = 0.4f)
                        )
                    )
                }
            }
        }

        // About the app
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SpaceCardLight),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "نسخة التطبيق: 1.0.0 (مستقر)", color = SlateText, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "تم التطوير بكل فخر لدعم صياغة الموجهات العربية 🇸🇦", color = SlateText, fontSize = 10.sp, textAlign = TextAlign.Center)
                }
            }
        }
    }
}
