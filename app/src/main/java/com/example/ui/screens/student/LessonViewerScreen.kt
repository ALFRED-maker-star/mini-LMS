package com.example.ui.screens.student

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Course
import com.example.data.model.Enrollment
import com.example.data.model.Lesson
import com.example.data.model.LessonContentType
import com.example.ui.theme.LearnBackground
import com.example.ui.theme.LearnBorder
import com.example.ui.theme.LearnGreen
import com.example.ui.theme.LearnGreenContainer
import com.example.ui.theme.LearnOnGreenContainer
import com.example.ui.theme.LearnOnVioletContainer
import com.example.ui.theme.LearnSurface
import com.example.ui.theme.LearnSurfaceVariant
import com.example.ui.theme.LearnTextPrimary
import com.example.ui.theme.LearnTextSecondary
import com.example.ui.theme.LearnViolet
import com.example.ui.theme.LearnVioletContainer

@Composable
fun LessonViewerScreen(
    course: Course,
    currentLessonId: String,
    enrollment: Enrollment?,
    onSelectLesson: (String) -> Unit,
    onMarkComplete: (String) -> Unit,
    onTakeQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentLessonIndex = course.lessons.indexOfFirst { it.id == currentLessonId }.coerceAtLeast(0)
    val currentLesson = course.lessons.getOrNull(currentLessonIndex) ?: course.lessons.firstOrNull()
    val isCompleted = enrollment?.completedLessonIds?.contains(currentLesson?.id) == true

    var showMobileSidebar by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(LearnBackground)
    ) {
        val isWide = maxWidth > 800.dp

        if (isWide) {
            // Tablet / Desktop Responsive Split: Content on Left (65%), Sidebar on Right (35%)
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(0.65f)
                        .fillMaxHeight()
                ) {
                    if (currentLesson != null) {
                        LessonContentArea(
                            course = course,
                            lesson = currentLesson,
                            isCompleted = isCompleted,
                            hasPrevious = currentLessonIndex > 0,
                            hasNext = currentLessonIndex < course.lessons.size - 1,
                            onPrevious = { onSelectLesson(course.lessons[currentLessonIndex - 1].id) },
                            onNext = { onSelectLesson(course.lessons[currentLessonIndex + 1].id) },
                            onMarkComplete = { onMarkComplete(currentLesson.id) },
                            onTakeQuiz = onTakeQuiz
                        )
                    }
                }

                // Vertical Divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(LearnBorder)
                )

                // Sidebar on right
                Box(
                    modifier = Modifier
                        .weight(0.35f)
                        .fillMaxHeight()
                        .background(LearnSurface)
                ) {
                    LessonSidebarList(
                        course = course,
                        currentLessonId = currentLesson?.id ?: "",
                        enrollment = enrollment,
                        onSelectLesson = onSelectLesson,
                        onTakeQuiz = onTakeQuiz
                    )
                }
            }
        } else {
            // Mobile: Video & Content stacked vertically, with quick toggle button to view sidebar drawer
            Column(modifier = Modifier.fillMaxSize()) {
                // Mobile Sub-header with Curriculum Toggle
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = LearnSurface,
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Lesson ${currentLessonIndex + 1} of ${course.lessons.size}",
                                style = MaterialTheme.typography.labelSmall.copy(color = LearnTextSecondary)
                            )
                            Text(
                                text = currentLesson?.title ?: "Lesson",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1
                            )
                        }

                        OutlinedButton(
                            onClick = { showMobileSidebar = !showMobileSidebar },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("toggle_curriculum_sidebar_button")
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (showMobileSidebar) "Hide List" else "Curriculum")
                        }
                    }
                }

                AnimatedVisibility(visible = showMobileSidebar) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .background(LearnSurface)
                    ) {
                        LessonSidebarList(
                            course = course,
                            currentLessonId = currentLesson?.id ?: "",
                            enrollment = enrollment,
                            onSelectLesson = {
                                onSelectLesson(it)
                                showMobileSidebar = false
                            },
                            onTakeQuiz = {
                                onTakeQuiz()
                                showMobileSidebar = false
                            }
                        )
                    }
                }

                if (currentLesson != null) {
                    LessonContentArea(
                        course = course,
                        lesson = currentLesson,
                        isCompleted = isCompleted,
                        hasPrevious = currentLessonIndex > 0,
                        hasNext = currentLessonIndex < course.lessons.size - 1,
                        onPrevious = { onSelectLesson(course.lessons[currentLessonIndex - 1].id) },
                        onNext = { onSelectLesson(course.lessons[currentLessonIndex + 1].id) },
                        onMarkComplete = { onMarkComplete(currentLesson.id) },
                        onTakeQuiz = onTakeQuiz
                    )
                }
            }
        }
    }
}

@Composable
private fun LessonContentArea(
    course: Course,
    lesson: Lesson,
    isCompleted: Boolean,
    hasPrevious: Boolean,
    hasNext: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onMarkComplete: () -> Unit,
    onTakeQuiz: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(false) }
    var playbackProgress by remember { mutableFloatStateOf(0.35f) }
    var playbackSpeed by remember { mutableStateOf("1.0x") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
    ) {
        // Video Player Box / Media Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .background(Color(0xFF0F172A))
        ) {
            // Player Visual Background
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Play / Pause Circle Button
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(LearnViolet)
                        .clickable { isPlaying = !isPlaying }
                        .testTag("video_player_play_pause_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause video" else "Play video",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isPlaying) "Streaming: ${lesson.title}" else "Tap to Play Lesson Video",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            // Bottom Player Control Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.65f))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                // Scrub Slider
                Slider(
                    value = playbackProgress,
                    onValueChange = { playbackProgress = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = LearnGreen,
                        activeTrackColor = LearnGreen,
                        inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${(playbackProgress * lesson.durationMinutes).toInt()}:15 / ${lesson.durationMinutes}:00",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.White)
                        )

                        // Speed Pill
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.clickable {
                                playbackSpeed = when (playbackSpeed) {
                                    "1.0x" -> "1.25x"
                                    "1.25x" -> "1.5x"
                                    "1.5x" -> "2.0x"
                                    else -> "1.0x"
                                }
                            }
                        ) {
                            Text(
                                text = playbackSpeed,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Volume", tint = Color.White, modifier = Modifier.size(18.dp))
                        Icon(Icons.Default.Fullscreen, contentDescription = "Fullscreen", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        // Lesson Title & Action Row
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = lesson.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = LearnTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${course.title} • ${course.instructor}",
                        style = MaterialTheme.typography.bodySmall.copy(color = LearnTextSecondary)
                    )
                }

                // "Mark Complete" Button
                Button(
                    onClick = onMarkComplete,
                    modifier = Modifier.testTag("mark_complete_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCompleted) LearnGreenContainer else LearnGreen,
                        contentColor = if (isCompleted) LearnOnGreenContainer else Color.White
                    )
                ) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isCompleted) "Completed ✓" else "Mark Complete",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = LearnBorder)
            Spacer(modifier = Modifier.height(16.dp))

            // Lesson Overview Text
            Text(
                text = "Lesson Overview",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = lesson.content,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = LearnTextPrimary,
                    lineHeight = 22.sp
                )
            )

            // Code Snippet Card (if provided)
            if (!lesson.codeSnippet.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Code, contentDescription = null, tint = LearnGreen, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "Kotlin Sample Code",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Icon(
                                Icons.Default.ContentCopy,
                                contentDescription = "Copy code",
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = lesson.codeSnippet,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFE2E8F0),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }
            }

            // Key Takeaways
            if (lesson.keyTakeaways.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = LearnVioletContainer.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = LearnViolet, modifier = Modifier.size(18.dp))
                            Text(
                                text = "Key Takeaways",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = LearnOnVioletContainer
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        lesson.keyTakeaways.forEach { takeaway ->
                            Row(
                                modifier = Modifier.padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text("•", color = LearnViolet, fontWeight = FontWeight.Bold)
                                Text(
                                    text = takeaway,
                                    style = MaterialTheme.typography.bodySmall.copy(color = LearnTextPrimary)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Navigation Controls: Previous / Next / Take Quiz
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onPrevious,
                    enabled = hasPrevious,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("previous_lesson_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Previous")
                }

                if (hasNext) {
                    Button(
                        onClick = onNext,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LearnViolet),
                        modifier = Modifier.testTag("next_lesson_button")
                    ) {
                        Text("Next Lesson")
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                } else if (course.quiz != null) {
                    Button(
                        onClick = onTakeQuiz,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LearnGreen),
                        modifier = Modifier.testTag("finish_course_quiz_button")
                    ) {
                        Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Take Course Quiz", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonSidebarList(
    course: Course,
    currentLessonId: String,
    enrollment: Enrollment?,
    onSelectLesson: (String) -> Unit,
    onTakeQuiz: () -> Unit
) {
    val completedCount = enrollment?.completedLessonIds?.size ?: 0
    val totalLessons = course.lessons.size
    val progressPercent = if (totalLessons > 0) ((completedCount.toFloat() / totalLessons) * 100).toInt() else 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        item {
            Text(
                text = "Course Curriculum",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$completedCount of $totalLessons completed ($progressPercent%)",
                style = MaterialTheme.typography.bodySmall.copy(color = LearnTextSecondary)
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (progressPercent / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = LearnGreen,
                trackColor = LearnBorder
            )
            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = LearnBorder)
            Spacer(modifier = Modifier.height(10.dp))
        }

        itemsIndexed(course.lessons) { index, lesson ->
            val isCurrent = lesson.id == currentLessonId
            val isCompleted = enrollment?.completedLessonIds?.contains(lesson.id) == true

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onSelectLesson(lesson.id) }
                    .testTag("sidebar_lesson_item_${lesson.id}"),
                color = if (isCurrent) LearnVioletContainer else LearnSurfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                if (isCompleted) LearnGreen else if (isCurrent) LearnViolet else Color.LightGray
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(Icons.Default.Check, contentDescription = "Completed", tint = Color.White, modifier = Modifier.size(14.dp))
                        } else {
                            Text(
                                text = "${index + 1}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = lesson.title,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                color = if (isCurrent) LearnOnVioletContainer else LearnTextPrimary
                            ),
                            maxLines = 1
                        )
                        Text(
                            text = "${lesson.durationMinutes} min",
                            style = MaterialTheme.typography.labelSmall.copy(color = LearnTextSecondary, fontSize = 10.sp)
                        )
                    }
                }
            }
        }

        if (course.quiz != null) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onTakeQuiz() }
                        .testTag("sidebar_quiz_button"),
                    color = LearnGreenContainer,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.Quiz, contentDescription = null, tint = LearnGreen, modifier = Modifier.size(20.dp))
                        Column {
                            Text(
                                text = course.quiz.title,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = LearnOnGreenContainer
                                )
                            )
                            Text(
                                text = "Take certification quiz",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = LearnOnGreenContainer,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
