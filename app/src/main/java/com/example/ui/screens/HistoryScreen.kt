package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.SavedPrompt
import com.example.ui.theme.*
import com.example.ui.viewmodel.PromptViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HistoryScreen(
    viewModel: PromptViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val clipboardManager = LocalClipboardManager.current

    val savedPrompts by viewModel.savedPrompts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showOnlyFavorites by remember { mutableStateOf(false) }

    val filteredPrompts = remember(savedPrompts, showOnlyFavorites) {
        if (showOnlyFavorites) {
            savedPrompts.filter { it.isFavorite }
        } else {
            savedPrompts
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Search and Stats area
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Interactive Search bar with Arabic Support
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = { Text("بحث في البرومبتات المحفوظة أو الأفكار...", color = SlateText.copy(alpha = 0.6f), fontSize = 13.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("prompt_search_bar"),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = SlateText)
                            }
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = SpaceCard,
                        unfocusedContainerColor = SpaceCard,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = NeonCyan,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = TextStyle(textDirection = TextDirection.ContentOrRtl, fontSize = 13.sp)
                )

                // Tab filters & Stats indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = !showOnlyFavorites,
                            onClick = { showOnlyFavorites = false },
                            label = { Text("الكل (${savedPrompts.size})") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonCyan.copy(alpha = 0.15f),
                                selectedLabelColor = NeonCyan
                            )
                        )

                        FilterChip(
                            selected = showOnlyFavorites,
                            onClick = { showOnlyFavorites = true },
                            label = { Text("المفضلة (${savedPrompts.count { it.isFavorite }})") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AmberGlow.copy(alpha = 0.15f),
                                selectedLabelColor = AmberGlow
                            )
                        )
                    }

                    if (filteredPrompts.isNotEmpty() && !showOnlyFavorites) {
                        TextButton(
                            onClick = {
                                filteredPrompts.forEach { viewModel.deleteSavedPrompt(it) }
                                Toast.makeText(context, "تم إفراغ مكتبة البرومبتات الشخصية.", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = null, tint = CosmicRed, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("مسح الكل", color = CosmicRed, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Empty states
        if (filteredPrompts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (showOnlyFavorites) Icons.Default.BookmarkBorder else Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = SlateText.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (showOnlyFavorites) "لا توجد برومبتات مفضلة بعد!" else "المكتبة فارغة حالياً!",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (showOnlyFavorites) "قُم بتمييز أي برومبت بالضغط على نجمة المفضلة لحفظه هنا والمطور مستقبلاً." else "ابدأ بتوليد برومبتات جديدة عبر شاشة المولد وقم بحفظها لتظهر في هذا الملف المنظم.",
                            fontSize = 12.sp,
                            color = SlateText,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        // List representation of saved prompts
        items(filteredPrompts, key = { it.id }) { prompt ->
            SavedPromptCard(
                prompt = prompt,
                onToggleFavorite = { viewModel.toggleFavorite(prompt) },
                onDelete = {
                    viewModel.deleteSavedPrompt(prompt)
                    Toast.makeText(context, "تم حذف البرومبت من المكتبة 🗑️", Toast.LENGTH_SHORT).show()
                },
                onCopy = {
                    clipboardManager.setText(AnnotatedString(prompt.promptContent))
                    Toast.makeText(context, "تم نسخ البرومبت بنجاح! 📋", Toast.LENGTH_SHORT).show()
                },
                onShare = {
                    val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(android.content.Intent.EXTRA_TEXT, prompt.promptContent)
                    }
                    context.startActivity(android.content.Intent.createChooser(shareIntent, "مشاركة البرومبت عبر"))
                }
            )
        }
    }
}

@Composable
fun SavedPromptCard(
    prompt: SavedPrompt,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val isImagePrompt = prompt.platform == "Midjourney" || prompt.platform == "Stable Diffusion"

    val sdf = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
    val formattedDate = remember(prompt.timestamp) { sdf.format(Date(prompt.timestamp)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("saved_prompt_card_${prompt.id}")
            .animateContentSize()
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = if (prompt.isFavorite) AmberGlow.copy(alpha = 0.3f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                ),
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SpaceCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Category, Platform and dates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = NeonCyan.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = prompt.platform,
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = prompt.category,
                            color = SlateText,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (prompt.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "المفضلة",
                            tint = if (prompt.isFavorite) AmberGlow else SlateText
                        )
                    }

                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "حذف",
                            tint = CosmicRed.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Title and description
            Text(
                text = prompt.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "الفكرة الأصلية: ${prompt.originalIdea}",
                fontSize = 11.sp,
                color = SlateText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Body prompt (Expandable text element)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SpaceBlack)
                    .clickable { isExpanded = !isExpanded }
                    .padding(12.dp)
            ) {
                Text(
                    text = prompt.promptContent,
                    fontSize = 12.sp,
                    color = OffWhite,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp,
                    style = TextStyle(
                        textDirection = if (isImagePrompt) TextDirection.Ltr else TextDirection.ContentOrRtl
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = formattedDate, color = SlateText.copy(alpha = 0.5f), fontSize = 10.sp)

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(onClick = { isExpanded = !isExpanded }) {
                        Text(
                            text = if (isExpanded) "عرض أقل" else "اقرأ البرومبت كاملاً",
                            fontSize = 11.sp,
                            color = NeonCyan
                        )
                    }

                    IconButton(onClick = onCopy) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "نسخ", tint = NeonCyan, modifier = Modifier.size(16.dp))
                    }

                    IconButton(onClick = onShare) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "مشاركة", tint = OffWhite, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
