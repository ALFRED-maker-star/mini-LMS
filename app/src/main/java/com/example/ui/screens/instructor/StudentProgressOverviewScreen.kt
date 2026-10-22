package com.example.ui.screens.instructor

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Course
import com.example.data.model.StudentRecord
import com.example.ui.theme.LearnAmber
import com.example.ui.theme.LearnAmberContainer
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
fun StudentProgressOverviewScreen(
    courses: List<Course>,
    studentRecords: List<StudentRecord>,
    initialCourseId: String? = null,
    modifier: Modifier = Modifier
) {
    var selectedCourseId by remember { mutableStateOf(initialCourseId ?: "all") }
    var searchQuery by remember { mutableStateOf("") }

    val filteredRecords = studentRecords.filter { record ->
        val matchesCourse = selectedCourseId == "all" || record.courseId == selectedCourseId
        val matchesSearch = searchQuery.isBlank() ||
                record.studentName.contains(searchQuery, ignoreCase = true) ||
                record.studentEmail.contains(searchQuery, ignoreCase = true)
        matchesCourse && matchesSearch
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LearnBackground),
        contentPadding = PaddingValues(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Student Progress Overview",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Track completion rates, quiz scores, and certificate milestones per course.",
                    style = MaterialTheme.typography.bodySmall.copy(color = LearnTextSecondary)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by student name or email...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = LearnViolet) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("student_search_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LearnViolet,
                    unfocusedBorderColor = LearnBorder,
                    focusedContainerColor = LearnSurface,
                    unfocusedContainerColor = LearnSurface
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Course Filter Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCourseId == "all",
                    onClick = { selectedCourseId = "all" },
                    label = { Text("All Courses (${studentRecords.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LearnViolet,
                        selectedLabelColor = Color.White
                    )
                )

                courses.forEach { course ->
                    val isSelected = selectedCourseId == course.id
                    val count = studentRecords.count { it.courseId == course.id }
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCourseId = course.id },
                        label = { Text("${course.title.take(24)}... ($count)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LearnViolet,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Summary Bar
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = LearnSurface),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Enrolled Students (${filteredRecords.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${filteredRecords.count { it.isCompleted }} Certified",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = LearnGreen
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Student Progress Table / Cards
        if (filteredRecords.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = LearnSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = LearnTextSecondary, modifier = Modifier.size(44.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No student records found", fontWeight = FontWeight.Bold)
                        Text("Try changing the search filter or course selection.", color = LearnTextSecondary, fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(filteredRecords) { record ->
                val course = courses.find { it.id == record.courseId }
                val isDone = record.isCompleted

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .testTag("student_progress_record_${record.studentId}_${record.courseId}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = LearnSurface),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                // Avatar with initials
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(if (isDone) LearnGreen else LearnViolet),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val initials = record.studentName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
                                    Text(
                                        text = initials,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }

                                Column {
                                    Text(
                                        text = record.studentName,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = record.studentEmail,
                                        style = MaterialTheme.typography.bodySmall.copy(color = LearnTextSecondary)
                                    )
                                }
                            }

                            // Status Tag
                            Surface(
                                color = if (isDone) LearnGreenContainer else LearnVioletContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    if (isDone) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = LearnGreen, modifier = Modifier.size(12.dp))
                                        Text(
                                            text = "Certified",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = LearnOnGreenContainer,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            )
                                        )
                                    } else {
                                        Text(
                                            text = "In Progress",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = LearnOnVioletContainer,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Course Title Reference
                        Text(
                            text = "Course: ${course?.title ?: record.courseId}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = LearnViolet,
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Progress Bar & Percentage
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Progress: ${record.completedLessonsCount}/${record.totalLessonsCount} lessons",
                                style = MaterialTheme.typography.labelSmall.copy(color = LearnTextSecondary)
                            )
                            Text(
                                text = "${record.completionPercentage}%",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDone) LearnGreen else LearnViolet
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        LinearProgressIndicator(
                            progress = { (record.completionPercentage / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (isDone) LearnGreen else LearnViolet,
                            trackColor = LearnBorder
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = LearnBorder.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Metrics Row: Quiz Score & Last Active
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Quiz Score
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Quiz, contentDescription = null, tint = LearnTextSecondary, modifier = Modifier.size(14.dp))
                                Text(
                                    text = if (record.quizScorePercent != null)
                                        "Quiz Score: ${record.quizScorePercent}% (${if (record.quizScorePercent >= 70) "Passed" else "Failed"})"
                                    else
                                        "Quiz: Not Attempted",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (record.quizScorePercent != null && record.quizScorePercent >= 70) LearnGreen else LearnTextSecondary,
                                        fontWeight = if (record.quizScorePercent != null) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                )
                            }

                            // Last Active
                            Text(
                                text = "Active ${record.lastActive}",
                                style = MaterialTheme.typography.labelSmall.copy(color = LearnTextSecondary, fontSize = 11.sp)
                            )
                        }
                    }
                }
            }
        }
    }
}
