package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.animation.*
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.PromptViewModel

data class PromptSystemTemplate(
    val id: String,
    val name: String,
    val description: String,
    val baseTemplate: String,
    val targetPlatform: String,
    val category: String,
    val variables: List<PromptVariable>
)

data class PromptVariable(
    val name: String,
    val label: String,
    val placeholder: String
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SystemsScreen(
    viewModel: PromptViewModel,
    onNavigateToGenerator: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // Set of pre-configured robust Mega Prompting systems
    val systems = remember {
        listOf(
            PromptSystemTemplate(
                id = "polymath",
                name = "نظام مجلس الخبراء الثلاثي (Polymath)",
                description = "موجه احترافي يجبر نموذج الذكاء الاصطناعي على تبني 3 شخصيات منفصلة لخبراء ينقدون فكرتك ويصلون إلى ميزان متسق وحل متراكم ومحكم.",
                targetPlatform = "ChatGPT",
                category = "إبداعي وحل مشاكل",
                variables = listOf(
                    PromptVariable("topic", "الموضوع أو فكرتك العريضة:", "مثال: مكافحة انتشار الشائعات الرقمية..."),
                    PromptVariable("outcome", "المخرج المطلوب صياغته:", "مثال: كتابة خطة عمل متكاملة وحملة ورقية...")
                ),
                baseTemplate = """
                    أنت الآن منسق لمجلس استشاري افتراضي يتكون من ثلاثة من كبار الصناع والخبراء المتمرسين ذوي مهارات متباينة وتكميلية لحل قضية: [topic].
                    
                    الخبراء المشاركون هم:
                    1. الخبير الاستراتيجي والمنطقي الفذ (صاحب نظرة شمولية وحلول واقعية واقعية).
                    2. الخبير الفني المتنور (يركز على عمق التنفيذ والتجند بالبراهين والأدوات والتقنيات).
                    3. الخبير الإبداعي والمثالي (يبحث عن حلول خارج الصندوق، ذو لمسة جمالية وإنسانية دقيقة).
                    
                    خطوات العمل التناوبي:
                    - أولاً: يقوم الخبير 1 بتحليل الموقف واقتراح الحلول الأولية.
                    - ثانياً: يراجع الخبير 2 المقترح ويبرز الصعوبات والجوانب التقنية لصالحه.
                    - ثالثاً: يقوم الخبير 3 بإعادة صياغة الأقوال بلمحة وعنصر إبداعي فريد.
                    - رابعاً: يقومون أخيراً بدمج رؤيتهم في إخراج متسق واحد يحقق الهدف وهو: [outcome].
                    
                    ابدأ بالترحيب بالمجلس وصياغة الرد فورياً وبشكل منظم.
                """.trimIndent()
            ),
            PromptSystemTemplate(
                id = "interactive_interviewer",
                name = "بروتوكول المقابلة التفاعلية (Interview)",
                description = "هياكل برومبت تمنع الذكاء الاصطناعي من الإجابة التلقائية المفتوحة بل تحثه على استجوابك خطوة بخطوة للحصول على متطلبات عمل مخصصة بنسبة 100%.",
                targetPlatform = "Gemini",
                category = "أعمال وتطوير",
                variables = listOf(
                    PromptVariable("role", "دور الذكاء الاصطناعي كخبير:", "مثال: مبرمج أندرويد محترف، خبير سيو..."),
                    PromptVariable("goal", "الهدف النهائي المطلوب تحقيقه:", "مثال: إطلاق موقع تجارة إلكترونية...")
                ),
                baseTemplate = """
                    أريدك أن تتصرف كخبير ومستشار متمكن في دور: [role].
                    هدفي في النهاية هو: [goal].
                    
                    لكي تساعدني في صياغة هذا الهدف بشكل لا غبار عليه وبأرقى جودة، لا تكتب لي الإجابة الآن أبداً!
                    
                    بدلاً من ذلك، أريد منك تفعيل بروتوكول المقابلة التفاعلية:
                    1. اطرح عليّ سؤالاً واحداً فقط في كل مرة.
                    2. انتظر إجابتي لتستوعب السياق تماماً وتجمع المعطيات.
                    3. بعد أن تجيبني بسؤالك التالي، كرر العملية حتى تمتلك كل الأوراق المطلوبة.
                    4. عندما ترى أنك جاهز ولديك معلومات كاملة، اكتب لي التقرير أو الحل الفعلي الشامل.
                    
                    الآن، رحّب بي واطرح عليّ السؤال الأول.
                """.trimIndent()
            ),
            PromptSystemTemplate(
                id = "red_team",
                name = "محلل الثغرات والتدقيق المنطقي (Red Team)",
                description = "يستخدم هذا البرومبت في إخضاع مقالاتك أو خططك للتدقيق المنطقي القاسي، ومكافحة الثغرات الإدراكية وطرق تحسين صلبة.",
                targetPlatform = "Claude",
                category = "أكاديمي وتدقيق",
                variables = listOf(
                    PromptVariable("text", "النص أو الفكرة المراد كشف ثغراتها:", "أدخل مسودة نصك أو مقالك هنا..."),
                    PromptVariable("rigor", "مدى شدة وقسوة التحليل والمراجعة:", "شديد للغاية وصارم منطقياً / معتدل وناصح...")
                ),
                baseTemplate = """
                    أنت تلعب الآن دور المدقق المحايد وعضو فريق النقد المعاكس (Red Team) لمراجعة الأفكار والنصوص بطباع: [rigor].
                    
                    أريدك أن تفحص النص التالي بذكاء وعمق شديد وتستخرج ثغراته بالتنفيذ:
                    1. الانحيازات الشخصية والافتراضات غير المقترنة بدليل صلب.
                    2. العثرات المنطقية أو الفجوات في سلاسة الطرح والاقناع.
                    3. النقاط الضعيفة التي يسهل على الخصوم أو القراء دحضها.
                    
                    هذا هو النص المستهدف للفحص:
                    \"\"\"
                    [text]
                    \"\"\"
                    
                    بعد استعراض الثغرات بالتفصيل في فئات ونقاط محددة، اقترح عليّ صياغة بديلة محسنة تخلق نصاً قوياً يرمم كل هذه الثغرات بإتقان.
                """.trimIndent()
            ),
            PromptSystemTemplate(
                id = "reverse_engineer",
                name = "مهندس البرومبتات العكسي (Reverse Prompt)",
                description = "إذا رأيت مخرجاً ساحراً من ذكاء اصطناعي وأردت معرفة البرومبت الذي كتبه الآخرون للحصول على تلك النتيجة الدقيقة، فهذا النظام هو سلاحك.",
                targetPlatform = "ChatGPT",
                category = "هندسة عكسية",
                variables = listOf(
                    PromptVariable("output", "ضع النص الجميل أو المخرج الممتاز كنموذج:", "ألصق النص الإبداعي النموذجي هنا...")
                ),
                baseTemplate = """
                    أنت مهندس موجهات ذكاء اصطناعي لغوي خبير (AI Reverse Prompt Architect).
                    أريد منك دراسة النص النموذجي التالي بدقة بالغة، وتحليل نبرته، وأسلوب التبسيط، وصيغة الكلام، والهيكلة الفريدة له:
                    
                    \"\"\"
                    [output]
                    \"\"\"
                    
                    بناءً على تحليلك التفصيلي، صِغ لي البرومبت النهائي الأكثر ملاءمة (System Prompt or User Prompt) والذي لو أدخلناه إلى نموذج لغوي (مثل ChatGPT أو Claude) سيقوم بإعادة إنتاج نص بنفس هذا الأسلوب والجودة الدقيقة.
                    
                    اجعل مخرجك النهائي يتضمن برومبت واضحاً وقابلاً للتطبيق الفوري.
                """.trimIndent()
            )
        )
    }

    var selectedTemplate by remember { mutableStateOf<PromptSystemTemplate?>(null) }
    val variableStates = remember { mutableStateMapOf<String, String>() }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Explanatory Banner of Systems module
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
                        Brush.horizontalGradient(listOf(ElectricViolet.copy(alpha = 0.5f), NeonCyan.copy(alpha = 0.5f))),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.Schema, contentDescription = null, tint = ElectricViolet, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "أنظمة وهياكل البرومبتات العملاقة 🏛️",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "لا تعتمد على البرومبتات البسيطة! استخدم أطر العمل والأنظمة الهيكلية المتقدمة لبناء برومبتات ذات جودة احترافية مبهرة في خطوتين.",
                        fontSize = 12.sp,
                        color = SlateText,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Templates Selection or filling
        if (selectedTemplate == null) {
            // Display the list of available prompt systems
            item {
                Text(
                    text = "اختر هيكلاً أو نظاماً برومبتياً للملء والتخصيص:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            items(systems) { system ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedTemplate = system
                            variableStates.clear()
                            system.variables.forEach { variableStates[it.name] = "" }
                        }
                        .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SpaceCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = system.name,
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = ElectricViolet.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = system.category,
                                    color = ElectricViolet,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = system.description,
                            color = SlateText,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Launch, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                            Text(
                                text = "اضغط لتعبئة هذا النظام وتجهيزه",
                                fontSize = 11.sp,
                                color = NeonCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else {
            // Filling view for the selected prompt system
            val system = selectedTemplate!!

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = { selectedTemplate = null },
                        colors = ButtonDefaults.textButtonColors(contentColor = SlateText)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "العودة للأنظمة", fontSize = 13.sp)
                    }

                    Text(
                        text = "تعبئة المتغيرات للنظام",
                        fontSize = 11.sp,
                        color = ElectricViolet,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, ElectricViolet.copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SpaceCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = system.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = system.description, fontSize = 12.sp, color = SlateText, lineHeight = 16.sp)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Render each variable placeholder field dynamically
                        system.variables.forEach { variable ->
                            Column(modifier = Modifier.padding(bottom = 12.dp)) {
                                Text(
                                    text = variable.label,
                                    fontSize = 12.sp,
                                    color = NeonCyan,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )

                                OutlinedTextField(
                                    value = variableStates[variable.name] ?: "",
                                    onValueChange = { variableStates[variable.name] = it },
                                    placeholder = { Text(variable.placeholder, fontSize = 12.sp, color = SlateText.copy(alpha = 0.5f)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = SpaceBlack,
                                        unfocusedContainerColor = SpaceBlack,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    textStyle = TextStyle(textDirection = TextDirection.ContentOrRtl, fontSize = 13.sp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Copy directly constructed prompt
                            OutlinedButton(
                                onClick = {
                                    var finalPrompt = system.baseTemplate
                                    system.variables.forEach { v ->
                                        val fillValue = variableStates[v.name] ?: ""
                                        finalPrompt = finalPrompt.replace("[${v.name}]", fillValue)
                                    }
                                    clipboardManager.setText(AnnotatedString(finalPrompt))
                                    Toast.makeText(context, "تم دمج ونسخ البرومبت النهائي للذاكرة! 📋", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f),
                                border = BorderStroke(1.dp, SlateText.copy(alpha = 0.3f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = OffWhite, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "دمج ونسخ", color = OffWhite, fontSize = 12.sp)
                            }

                            // Launch / transfer to optimizer
                            Button(
                                onClick = {
                                    var finalIdea = ""
                                    system.variables.forEach { v ->
                                        finalIdea += "${v.label} ${variableStates[v.name] ?: ""}\n"
                                    }
                                    
                                    var structuredConstraints = "اتبّع الهيكل والمحددات التالية حرفياً:\n" + system.baseTemplate
                                    system.variables.forEach { v ->
                                        val value = variableStates[v.name] ?: ""
                                        structuredConstraints = structuredConstraints.replace("[${v.name}]", value)
                                    }

                                    viewModel.loadTemplate(
                                        platform = system.targetPlatform,
                                        category = system.category,
                                        idea = finalIdea.trim(),
                                        constraints = structuredConstraints
                                    )

                                    onNavigateToGenerator()
                                    Toast.makeText(context, "تم تحويل المعماري بنجاح لمُحسّن الذكاء الاصطناعي!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1.2f),
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.AutoMode, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "نقل للمُحسّن ✨", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Direct Save to library local
                        TextButton(
                            onClick = {
                                var finalPrompt = system.baseTemplate
                                system.variables.forEach { v ->
                                    val fillValue = variableStates[v.name] ?: ""
                                    finalPrompt = finalPrompt.replace("[${v.name}]", fillValue)
                                }
                                viewModel.savePromptDirectly(
                                    title = system.name,
                                    content = finalPrompt,
                                    platform = system.targetPlatform,
                                    category = system.category
                                )
                                Toast.makeText(context, "تم حفظ البرومبت مباشرة في المكتبة الشخصية!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            colors = ButtonDefaults.textButtonColors(contentColor = NeonCyan)
                        ) {
                            Icon(imageVector = Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "حفظ فوري في المكتبة", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
