package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TajweedData
import com.example.model.TajweedCategory
import com.example.model.TajweedQuizQuestion
import com.example.model.TajweedRule

@Composable
fun TajweedScreen(
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf<TajweedCategory?>(null) }
    var showQuizDialog by remember { mutableStateOf(false) }

    val filteredRules = remember(selectedCategory) {
        if (selectedCategory == null) TajweedData.RULES
        else TajweedData.RULES.filter { it.category == selectedCategory }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("tajweed_screen")
    ) {
        // Quick Action Row: Color Legend & Quiz launcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🎨 Color Guide: ", style = MaterialTheme.typography.labelSmall)
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFDC2626)))
                    Text(text = " Madd ", style = MaterialTheme.typography.labelSmall)
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF10B981)))
                    Text(text = " Ghunnah ", style = MaterialTheme.typography.labelSmall)
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF0284C7)))
                    Text(text = " Qalqalah", style = MaterialTheme.typography.labelSmall)
                }
            }

            Button(
                onClick = { showQuizDialog = true },
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.testTag("tajweed_quiz_button")
            ) {
                Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Quiz", fontSize = 12.sp)
            }
        }

        // Category Filter Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    label = { Text("All Rules (${TajweedData.RULES.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
            items(TajweedCategory.entries) { cat ->
                val isSelected = cat == selectedCategory
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat.title) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        // Rules List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(filteredRules) { rule ->
                TajweedRuleCard(rule = rule)
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showQuizDialog) {
        TajweedQuizDialog(
            onDismiss = { showQuizDialog = false }
        )
    }
}

@Composable
fun TajweedRuleCard(rule: TajweedRule) {
    var isExpanded by remember { mutableStateOf(false) }
    val ruleColor = Color(rule.colorHex)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ruleColor.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
            .clickable { isExpanded = !isExpanded }
            .testTag("tajweed_rule_${rule.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Color bar, Title, Arabic title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .width(5.dp)
                            .height(24.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(ruleColor)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = rule.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = rule.category.title,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = rule.arabicTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ruleColor
                    )
                    if (rule.countsOrDuration.isNotBlank()) {
                        Surface(
                            color = ruleColor.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = rule.countsOrDuration,
                                style = MaterialTheme.typography.labelSmall,
                                color = ruleColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Explanation
            Text(
                text = rule.explanation,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Letters
            if (rule.letters.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Letters: ",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = rule.letters,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = ruleColor
                        )
                    }
                }
            }

            // Expandable Examples
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${rule.examples.size} Quranic Examples",
                    style = MaterialTheme.typography.labelSmall,
                    color = ruleColor,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = ruleColor
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rule.examples.forEach { example ->
                        Surface(
                            color = MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = ruleColor.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = example.highlightedRule,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = ruleColor,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Text(
                                        text = example.arabicText,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = example.phoneticTip,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = example.surahReference,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TajweedQuizDialog(
    onDismiss: () -> Unit
) {
    val questions = TajweedData.QUIZ_QUESTIONS
    var currentIndex by remember { mutableStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var score by remember { mutableStateOf(0) }
    var isAnswered by remember { mutableStateOf(false) }

    val currentQ = questions[currentIndex]

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tajweed Quiz (${currentIndex + 1}/${questions.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Score: $score",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = currentQ.question,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currentQ.arabicQuestion,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                currentQ.options.forEachIndexed { idx, opt ->
                    val isCorrect = idx == currentQ.correctIndex
                    val isUserSelection = selectedOption == idx

                    val containerColor = when {
                        !isAnswered -> MaterialTheme.colorScheme.surfaceVariant
                        isCorrect -> Color(0xFF10B981).copy(alpha = 0.25f)
                        isUserSelection -> Color(0xFFEF4444).copy(alpha = 0.25f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }

                    val borderColor = when {
                        !isAnswered -> Color.Transparent
                        isCorrect -> Color(0xFF10B981)
                        isUserSelection -> Color(0xFFEF4444)
                        else -> Color.Transparent
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
                            .clickable(enabled = !isAnswered) {
                                selectedOption = idx
                                isAnswered = true
                                if (idx == currentQ.correctIndex) {
                                    score++
                                }
                            },
                        color = containerColor
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = opt,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (isAnswered) {
                                if (isCorrect) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Correct", tint = Color(0xFF10B981))
                                } else if (isUserSelection) {
                                    Icon(Icons.Default.Cancel, contentDescription = "Incorrect", tint = Color(0xFFEF4444))
                                }
                            }
                        }
                    }
                }

                if (isAnswered) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "💡 ${currentQ.explanation}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (isAnswered) {
                if (currentIndex < questions.size - 1) {
                    Button(
                        onClick = {
                            currentIndex++
                            selectedOption = null
                            isAnswered = false
                        }
                    ) {
                        Text("Next Question")
                    }
                } else {
                    Button(onClick = onDismiss) {
                        Text("Finish (Score: $score/${questions.size})")
                    }
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
        }
    )
}
