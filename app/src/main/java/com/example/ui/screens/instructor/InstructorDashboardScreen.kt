package com.example.ui.screens.instructor

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
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
import com.example.data.model.InstructorProfile
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
fun InstructorDashboardScreen(
    instructor: InstructorProfile,
    courses: List<Course>,
    studentRecords: List<StudentRecord>,
    onCreateCourseClick: () -> Unit,
    onManageCoursesClick: () -> Unit,
    onStudentProgressClick: (String?) -> Unit,
    onEditCourseClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalStudents = courses.sumOf { it.studentsEnrolled }
    val avgCompletionRate = if (studentRecords.isNotEmpty()) {
        (studentRecords.map { it.completionPercentage }.average()).toInt()
    } else {
        76
    }
    val avgRating = if (courses.isNotEmpty()) {
        courses.map { it.rating.toDouble() }.average()
    } else {
        4.9
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LearnBackground),
        contentPadding = PaddingValues(16.dp)
    ) {
        // Welcome Header Banner
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
                            text = "Welcome, ${instructor.name}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = instructor.role,
                            style = MaterialTheme.typography.bodySmall.copy(color = LearnTextSecondary)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(LearnGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = instructor.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Stat Cards (My Courses, Total Students, Avg Completion Rate, Rating)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    InstructorStatCard(
                        title = "My Courses",
                        value = "${courses.size}",
                        subtitle = "${courses.count { it.isPublished }} published",
                        icon = Icons.Default.MenuBook,
                        color = LearnViolet,
                        containerColor = LearnVioletContainer,
                        modifier = Modifier.weight(1f)
                    )
                    InstructorStatCard(
                        title = "Total Students",
                        value = "$totalStudents",
                        subtitle = "across all courses",
                        icon = Icons.Default.Group,
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
                    InstructorStatCard(
                        title = "Avg Completion Rate",
                        value = "$avgCompletionRate%",
                        subtitle = "passing grade target",
                        icon = Icons.Default.TrendingUp,
                        color = LearnGreen,
                        containerColor = LearnGreenContainer,
                        modifier = Modifier.weight(1f)
                    )
                    InstructorStatCard(
                        title = "Instructor Rating",
                        value = String.format("%.1f ★", avgRating),
                        subtitle = "top 5% of mentors",
                        icon = Icons.Default.Star,
                        color = LearnAmber,
                        containerColor = LearnAmberContainer,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Quick Actions Row
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LearnSurface),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Quick Studio Actions",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onCreateCourseClick,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("dashboard_create_course_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LearnGreen)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Course", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { onStudentProgressClick(null) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("dashboard_student_overview_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Group, contentDescription = null, tint = LearnViolet, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Student Grades", color = LearnViolet, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Course Management List Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Authored Courses",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Manage All",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = LearnViolet,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.clickable { onManageCoursesClick() }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        // Authored Courses Preview List
        items(courses.take(3), key = { it.id }) { course ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = LearnSurface),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                color = if (course.isPublished) LearnGreenContainer else LearnAmberContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (course.isPublished) "PUBLISHED" else "DRAFT",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (course.isPublished) LearnOnGreenContainer else Color(0xFF92400E),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                )
                            }
                            Text(
                                text = "${course.lessons.size} lessons",
                                style = MaterialTheme.typography.labelSmall.copy(color = LearnTextSecondary)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = course.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${course.studentsEnrolled} enrolled • Rating: ${course.rating} ★",
                            style = MaterialTheme.typography.bodySmall.copy(color = LearnTextSecondary)
                        )
                    }

                    OutlinedButton(
                        onClick = { onEditCourseClick(course.id) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("dashboard_edit_course_${course.id}")
                    ) {
                        Text("Edit", color = LearnViolet)
                    }
                }
            }
        }
    }
}

@Composable
private fun InstructorStatCard(
    title: String,
    value: String,
    subtitle: String,
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
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(color = LearnTextSecondary)
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(containerColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = LearnTextPrimary
                )
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(color = LearnTextSecondary, fontSize = 10.sp)
            )
        }
    }
}
