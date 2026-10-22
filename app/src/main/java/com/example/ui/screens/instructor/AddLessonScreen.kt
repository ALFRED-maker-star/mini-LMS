package com.example.ui.screens.instructor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Course
import com.example.data.model.Lesson
import com.example.data.model.LessonContentType
import com.example.ui.theme.LearnBackground
import com.example.ui.theme.LearnBorder
import com.example.ui.theme.LearnGreen
import com.example.ui.theme.LearnSurface
import com.example.ui.theme.LearnTextPrimary
import com.example.ui.theme.LearnTextSecondary
import com.example.ui.theme.LearnViolet
import java.util.UUID

@Composable
fun AddLessonScreen(
    course: Course,
    onSaveLesson: (Lesson) -> Unit,
    modifier: Modifier = Modifier
) {
    val nextOrder = course.lessons.size + 1
    var title by remember { mutableStateOf("") }
    var contentType by remember { mutableStateOf(LessonContentType.VIDEO) }
    var durationText by remember { mutableStateOf("20") }
    var orderText by remember { mutableStateOf("$nextOrder") }
    var videoUrl by remember { mutableStateOf("https://example.com/videos/lesson-$nextOrder") }
    var textContent by remember { mutableStateOf("") }
    var codeSnippet by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LearnBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Add New Lesson",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Adding to: ${course.title}",
            style = MaterialTheme.typography.bodySmall.copy(color = LearnViolet, fontWeight = FontWeight.SemiBold)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LearnSurface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Lesson Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Lesson Title") },
                    placeholder = { Text("e.g. $nextOrder. Navigation & Back Handling") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("lesson_title_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LearnViolet,
                        unfocusedBorderColor = LearnBorder
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Content Type Selector
                Text(
                    text = "Content Type",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LessonContentType.entries.forEach { type ->
                        val isSelected = contentType == type
                        FilterChip(
                            selected = isSelected,
                            onClick = { contentType = type },
                            label = {
                                Text(
                                    when (type) {
                                        LessonContentType.VIDEO -> "Video"
                                        LessonContentType.ARTICLE -> "Article / Text"
                                        LessonContentType.CODE_LAB -> "Interactive Lab"
                                    }
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = when (type) {
                                        LessonContentType.VIDEO -> Icons.Default.Videocam
                                        LessonContentType.ARTICLE -> Icons.Default.Article
                                        LessonContentType.CODE_LAB -> Icons.Default.Code
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LearnViolet,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White,
                                containerColor = LearnSurface,
                                labelColor = LearnTextPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Duration & Order Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = durationText,
                        onValueChange = { durationText = it },
                        label = { Text("Duration (Minutes)") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("lesson_duration_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LearnViolet,
                            unfocusedBorderColor = LearnBorder
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = orderText,
                        onValueChange = { orderText = it },
                        label = { Text("Curriculum Order") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("lesson_order_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LearnViolet,
                            unfocusedBorderColor = LearnBorder
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Video URL if video type
                if (contentType == LessonContentType.VIDEO) {
                    OutlinedTextField(
                        value = videoUrl,
                        onValueChange = { videoUrl = it },
                        label = { Text("Video URL / Stream Link") },
                        placeholder = { Text("https://...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("lesson_video_url_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LearnViolet,
                            unfocusedBorderColor = LearnBorder
                        )
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Lesson Text Content / Guide
                OutlinedTextField(
                    value = textContent,
                    onValueChange = { textContent = it },
                    label = { Text("Lesson Content / Overview") },
                    placeholder = { Text("Explain core concepts, methodology, and learning objectives...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("lesson_content_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LearnViolet,
                        unfocusedBorderColor = LearnBorder
                    ),
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Optional Code Snippet
                OutlinedTextField(
                    value = codeSnippet,
                    onValueChange = { codeSnippet = it },
                    label = { Text("Sample Code Snippet (Optional)") },
                    placeholder = { Text("// Paste sample Kotlin or code here") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .testTag("lesson_code_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LearnViolet,
                        unfocusedBorderColor = LearnBorder
                    ),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Save Lesson Button
                Button(
                    onClick = {
                        val duration = durationText.toIntOrNull() ?: 20
                        val order = orderText.toIntOrNull() ?: nextOrder
                        val newLesson = Lesson(
                            id = "lesson-" + UUID.randomUUID().toString().take(6),
                            courseId = course.id,
                            order = order,
                            title = if (title.isNotBlank()) title else "$order. New Module",
                            type = contentType,
                            durationMinutes = duration,
                            videoUrl = videoUrl,
                            content = if (textContent.isNotBlank()) textContent else "In this lesson, we explore essential techniques and best practices.",
                            codeSnippet = codeSnippet.ifBlank { null },
                            keyTakeaways = listOf(
                                "Core architectural pattern understanding",
                                "Practical application in modern projects"
                            )
                        )
                        onSaveLesson(newLesson)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_lesson_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LearnGreen)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Lesson to Curriculum", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
