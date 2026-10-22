package com.example.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Course
import com.example.data.model.Enrollment
import com.example.data.model.Lesson
import com.example.data.model.LessonContentType
import com.example.ui.theme.LearnAmber
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
fun CourseDetailScreen(
    course: Course,
    enrollment: Enrollment?,
    onEnrollClick: () -> Unit,
    onStartLesson: (String) -> Unit,
    onStartQuiz: () -> Unit,
    onViewCertificate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEnrolled = enrollment != null
    val totalLessons = course.lessons.size
    val completedCount = enrollment?.completedLessonIds?.size ?: 0
    val progressPercent = if (totalLessons > 0) ((completedCount.toFloat() / totalLessons) * 100).toInt() else 0
    val isCompleted = enrollment?.isCompleted == true

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LearnBackground),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Cover Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(course.coverGradientStart),
                                Color(course.coverGradientEnd)
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.align(Alignment.BottomStart)
                ) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = course.category.uppercase(),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = course.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }

        // Meta Bar (Rating, Enrolled Students, Total Duration)
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = LearnSurface,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rating
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = LearnAmber, modifier = Modifier.size(18.dp))
                        Text(text = "${course.rating}", fontWeight = FontWeight.Bold)
                        Text(text = "(${course.reviewCount} reviews)", color = LearnTextSecondary, fontSize = 12.sp)
                    }

                    // Enrolled
                    Text(
                        text = "${course.studentsEnrolled} students",
                        style = MaterialTheme.typography.bodySmall.copy(color = LearnTextSecondary)
                    )

                    // Hours
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = LearnTextSecondary, modifier = Modifier.size(14.dp))
                        Text(text = "${course.durationHours}h on-demand", color = LearnTextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }

        // Enrollment Action Card & Progress
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LearnSurface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (isEnrolled) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isCompleted) "Course Completed! 🎉" else "Course Progress",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCompleted) LearnGreen else LearnViolet
                                )
                            )
                            Text(
                                text = "$progressPercent%",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isCompleted) LearnGreen else LearnViolet
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { (progressPercent / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (isCompleted) LearnGreen else LearnViolet,
                            trackColor = LearnBorder
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Find next incomplete lesson
                            val nextLesson = course.lessons.firstOrNull { !enrollment.completedLessonIds.contains(it.id) }
                                ?: course.lessons.firstOrNull()

                            Button(
                                onClick = {
                                    if (nextLesson != null) onStartLesson(nextLesson.id)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("continue_learning_button"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = LearnViolet)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Continue Learning", fontWeight = FontWeight.Bold)
                            }

                            if (course.quiz != null) {
                                OutlinedButton(
                                    onClick = onStartQuiz,
                                    modifier = Modifier.testTag("take_quiz_button"),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(18.dp), tint = LearnViolet)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Quiz", color = LearnViolet, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        if (isCompleted) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = onViewCertificate,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("view_certificate_button"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = LearnGreen)
                            ) {
                                Icon(Icons.Default.EmojiEvents, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("View Official Certificate", fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Text(
                            text = "Access all lessons, interactive quizzes, and earn an official verified certificate upon completion.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = LearnTextSecondary)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onEnrollClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("enroll_now_detail_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LearnViolet)
                        ) {
                            Text("Enroll Now • Free", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }

        // About Course Description
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LearnSurface),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "About this Course",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = course.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = LearnTextSecondary,
                            lineHeight = 22.sp
                        )
                    )
                }
            }
        }

        // Instructor Profile Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LearnSurface),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Instructor",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(LearnViolet),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = course.instructor.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = course.instructor,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Icon(Icons.Default.Verified, contentDescription = "Verified Instructor", tint = LearnViolet, modifier = Modifier.size(16.dp))
                            }
                            Text(
                                text = course.instructorTitle,
                                style = MaterialTheme.typography.bodySmall.copy(color = LearnTextSecondary)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = course.instructorBio,
                        style = MaterialTheme.typography.bodySmall.copy(color = LearnTextSecondary, lineHeight = 18.sp)
                    )
                }
            }
        }

        // Syllabus / Lessons List Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Course Curriculum",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = LearnTextPrimary
                    )
                )
                Text(
                    text = "${course.lessons.size} Lessons",
                    style = MaterialTheme.typography.bodySmall.copy(color = LearnTextSecondary)
                )
            }
        }

        // Lesson Items
        itemsIndexed(course.lessons) { index, lesson ->
            val isCompletedLesson = enrollment?.completedLessonIds?.contains(lesson.id) == true
            // Sequential lock rule: lesson 0 is unlocked; lesson N is unlocked if lesson N-1 is completed, or if enrolled
            val isUnlocked = !isEnrolled || index == 0 || enrollment?.completedLessonIds?.contains(course.lessons[index - 1].id) == true || isCompletedLesson

            LessonListItem(
                lesson = lesson,
                index = index + 1,
                isCompleted = isCompletedLesson,
                isUnlocked = isUnlocked,
                onClick = {
                    if (isUnlocked) {
                        onStartLesson(lesson.id)
                    }
                }
            )
        }

        // Course Quiz Item (if course has quiz)
        if (course.quiz != null) {
            item {
                val isAllLessonsDone = (enrollment?.completedLessonIds?.size ?: 0) >= course.lessons.size && course.lessons.isNotEmpty()
                val quizPassed = (enrollment?.quizScorePercent ?: 0) >= course.quiz.passThresholdPercentage

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(enabled = isEnrolled) { onStartQuiz() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (quizPassed) LearnGreenContainer else LearnVioletContainer
                    ),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (quizPassed) LearnGreen else LearnViolet),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (quizPassed) Icons.Default.CheckCircle else Icons.Default.Quiz,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = course.quiz.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = if (quizPassed) "Passed with ${enrollment?.quizScorePercent}% score! ✓" else "${course.quiz.questions.size} Questions • Pass mark: ${course.quiz.passThresholdPercentage}%",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (quizPassed) LearnOnGreenContainer else LearnOnVioletContainer
                                    )
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Take Quiz",
                            tint = if (quizPassed) LearnGreen else LearnViolet
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonListItem(
    lesson: Lesson,
    index: Int,
    isCompleted: Boolean,
    isUnlocked: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = isUnlocked) { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!isUnlocked) LearnSurfaceVariant.copy(alpha = 0.6f) else LearnSurface
        ),
        elevation = CardDefaults.cardElevation(if (isUnlocked) 1.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Status Icon: Green check if done, lock if locked, play icon if available
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isCompleted -> LearnGreenContainer
                                !isUnlocked -> LearnSurfaceVariant
                                else -> LearnVioletContainer
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isCompleted -> Icon(Icons.Default.CheckCircle, contentDescription = "Completed", tint = LearnGreen, modifier = Modifier.size(18.dp))
                        !isUnlocked -> Icon(Icons.Default.Lock, contentDescription = "Locked", tint = LearnTextSecondary, modifier = Modifier.size(16.dp))
                        else -> Icon(Icons.Default.PlayCircle, contentDescription = "Play", tint = LearnViolet, modifier = Modifier.size(18.dp))
                    }
                }

                Column {
                    Text(
                        text = lesson.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isUnlocked) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isUnlocked) LearnTextPrimary else LearnTextSecondary
                        )
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = when (lesson.type) {
                                LessonContentType.VIDEO -> "Video"
                                LessonContentType.ARTICLE -> "Article"
                                LessonContentType.CODE_LAB -> "Interactive Lab"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(color = LearnTextSecondary)
                        )
                        Text(text = "•", color = LearnTextSecondary, fontSize = 10.sp)
                        Text(
                            text = "${lesson.durationMinutes} min",
                            style = MaterialTheme.typography.labelSmall.copy(color = LearnTextSecondary)
                        )
                    }
                }
            }

            if (isUnlocked) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Start lesson",
                    tint = if (isCompleted) LearnGreen else LearnViolet,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
