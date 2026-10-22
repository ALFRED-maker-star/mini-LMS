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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Course
import com.example.data.model.Enrollment
import com.example.ui.theme.LearnAmber
import com.example.ui.theme.LearnAmberContainer
import com.example.ui.theme.LearnBackground
import com.example.ui.theme.LearnBorder
import com.example.ui.theme.LearnGreen
import com.example.ui.theme.LearnGreenContainer
import com.example.ui.theme.LearnOnAmberContainer
import com.example.ui.theme.LearnOnGreenContainer
import com.example.ui.theme.LearnOnVioletContainer
import com.example.ui.theme.LearnSurface
import com.example.ui.theme.LearnTextPrimary
import com.example.ui.theme.LearnTextSecondary
import com.example.ui.theme.LearnViolet
import com.example.ui.theme.LearnVioletContainer

@Composable
fun MyProgressScreen(
    studentName: String,
    courses: List<Course>,
    enrollments: Map<String, Enrollment>,
    onResumeCourse: (String) -> Unit,
    onViewCertificate: (String) -> Unit,
    onExploreCatalog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val enrolledCourses = courses.filter { enrollments.containsKey(it.id) }
    val completedCourses = enrolledCourses.filter { enrollments[it.id]?.isCompleted == true }

    // Calculate approximate learned hours based on completed lessons
    val totalHoursLearned = enrolledCourses.sumOf { course ->
        val enrollment = enrollments[course.id]
        val completedCount = enrollment?.completedLessonIds?.size ?: 0
        val totalLessons = course.lessons.size
        if (totalLessons > 0) {
            (completedCount.toDouble() / totalLessons) * course.durationHours
        } else {
            0.0
        }
    }

    val quizzesPassed = enrolledCourses.count { course ->
        val enr = enrollments[course.id]
        val threshold = course.quiz?.passThresholdPercentage ?: 70
        (enr?.quizScorePercent ?: 0) >= threshold
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LearnBackground),
        contentPadding = PaddingValues(16.dp)
    ) {
        // Welcome & Streak Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LearnSurface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Welcome back, $studentName!",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Keep up the momentum! You're making steady progress.",
                            style = MaterialTheme.typography.bodySmall.copy(color = LearnTextSecondary)
                        )
                    }

                    // Streak Pill
                    Surface(
                        color = LearnAmberContainer,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.LocalFireDepartment, contentDescription = "Streak", tint = LearnAmber, modifier = Modifier.size(16.dp))
                            Text(
                                text = "5 Day Streak",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = LearnOnAmberContainer
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Stats Overview Grid (4 Stat Cards)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProgressStatCard(
                        title = "Enrolled",
                        value = "${enrolledCourses.size}",
                        icon = Icons.Default.MenuBook,
                        color = LearnViolet,
                        containerColor = LearnVioletContainer,
                        modifier = Modifier.weight(1f)
                    )
                    ProgressStatCard(
                        title = "Completed",
                        value = "${completedCourses.size}",
                        icon = Icons.Default.CheckCircle,
                        color = LearnGreen,
                        containerColor = LearnGreenContainer,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProgressStatCard(
                        title = "Hours Learned",
                        value = String.format("%.1fh", totalHoursLearned),
                        icon = Icons.Default.AccessTime,
                        color = LearnAmber,
                        containerColor = LearnAmberContainer,
                        modifier = Modifier.weight(1f)
                    )
                    ProgressStatCard(
                        title = "Certificates",
                        value = "$quizzesPassed",
                        icon = Icons.Default.EmojiEvents,
                        color = LearnGreen,
                        containerColor = LearnGreenContainer,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Enrolled Courses Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Enrolled Courses (${enrolledCourses.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (enrolledCourses.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = LearnSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.School, contentDescription = null, tint = LearnViolet, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("You haven't enrolled in any courses yet", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Explore our catalog and start learning today!", color = LearnTextSecondary, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onExploreCatalog,
                            colors = ButtonDefaults.buttonColors(containerColor = LearnViolet)
                        ) {
                            Text("Browse Course Catalog")
                        }
                    }
                }
            }
        } else {
            items(enrolledCourses, key = { it.id }) { course ->
                val enrollment = enrollments[course.id]
                val completedCount = enrollment?.completedLessonIds?.size ?: 0
                val totalLessons = course.lessons.size
                val percent = if (totalLessons > 0) ((completedCount.toFloat() / totalLessons) * 100).toInt() else 0
                val isCompleted = enrollment?.isCompleted == true

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onResumeCourse(course.id) }
                        .testTag("enrolled_course_item_${course.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = LearnSurface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    color = LearnVioletContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = course.category.uppercase(),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = LearnOnVioletContainer,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = course.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Instructor: ${course.instructor}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = LearnTextSecondary)
                                )
                            }

                            // Completion percentage circle/pill
                            Surface(
                                color = if (isCompleted) LearnGreenContainer else LearnVioletContainer,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    if (isCompleted) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = LearnGreen, modifier = Modifier.size(14.dp))
                                    }
                                    Text(
                                        text = "$percent%",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isCompleted) LearnOnGreenContainer else LearnOnVioletContainer
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Progress Indicator
                        LinearProgressIndicator(
                            progress = { (percent / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (isCompleted) LearnGreen else LearnViolet,
                            trackColor = LearnBorder
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$completedCount of $totalLessons lessons completed",
                                style = MaterialTheme.typography.labelSmall.copy(color = LearnTextSecondary)
                            )

                            if (enrollment?.quizScorePercent != null) {
                                Text(
                                    text = "Quiz: ${enrollment.quizScorePercent}%",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = LearnGreen
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Action Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { onResumeCourse(course.id) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("resume_course_button_${course.id}"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isCompleted) LearnGreen else LearnViolet
                                )
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isCompleted) "Review Course" else "Resume Course", fontWeight = FontWeight.Bold)
                            }

                            if (isCompleted) {
                                OutlinedButton(
                                    onClick = { onViewCertificate(course.id) },
                                    modifier = Modifier.testTag("progress_view_certificate_button_${course.id}"),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = LearnGreen, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Certificate", color = LearnGreen, fontWeight = FontWeight.Bold)
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
private fun ProgressStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = LearnSurface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(containerColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            }

            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = LearnTextPrimary
                    )
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(color = LearnTextSecondary)
                )
            }
        }
    }
}
