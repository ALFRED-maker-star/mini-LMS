package com.example.ui.screens.instructor

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Course
import com.example.ui.theme.LearnAmber
import com.example.ui.theme.LearnAmberContainer
import com.example.ui.theme.LearnBackground
import com.example.ui.theme.LearnBorder
import com.example.ui.theme.LearnGreen
import com.example.ui.theme.LearnGreenContainer
import com.example.ui.theme.LearnOnGreenContainer
import com.example.ui.theme.LearnSurface
import com.example.ui.theme.LearnTextPrimary
import com.example.ui.theme.LearnTextSecondary
import com.example.ui.theme.LearnViolet
import com.example.ui.theme.LearnVioletContainer

@Composable
fun InstructorCoursesScreen(
    courses: List<Course>,
    onCreateCourseClick: () -> Unit,
    onEditCourseClick: (String) -> Unit,
    onTogglePublishClick: (String) -> Unit,
    onAddLessonClick: (String) -> Unit,
    onAddQuizClick: (String) -> Unit,
    onViewStudentsClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateCourseClick,
                containerColor = LearnGreen,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_create_course")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create New Course")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(LearnBackground)
                .padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Course Management",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${courses.size} total courses in your catalog",
                            style = MaterialTheme.typography.bodySmall.copy(color = LearnTextSecondary)
                        )
                    }

                    Button(
                        onClick = onCreateCourseClick,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LearnGreen),
                        modifier = Modifier.testTag("create_course_header_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Create Course")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Courses List
            items(courses, key = { it.id }) { course ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .testTag("instructor_course_item_${course.id}"),
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Published / Draft Badge
                                    Surface(
                                        color = if (course.isPublished) LearnGreenContainer else LearnAmberContainer,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = if (course.isPublished) "PUBLISHED" else "DRAFT",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (course.isPublished) LearnOnGreenContainer else Color(0xFF92400E),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }

                                    Text(
                                        text = course.category,
                                        style = MaterialTheme.typography.labelSmall.copy(color = LearnTextSecondary)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = course.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "${course.lessons.size} lessons • ${course.durationHours}h • ${course.studentsEnrolled} students enrolled",
                                    style = MaterialTheme.typography.bodySmall.copy(color = LearnTextSecondary)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Action Buttons Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Edit Course
                            OutlinedButton(
                                onClick = { onEditCourseClick(course.id) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("edit_course_btn_${course.id}"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = LearnViolet)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit", color = LearnViolet, fontSize = 12.sp)
                            }

                            // Add Lesson
                            OutlinedButton(
                                onClick = { onAddLessonClick(course.id) },
                                modifier = Modifier
                                    .weight(1.2f)
                                    .testTag("add_lesson_btn_${course.id}"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.PostAdd, contentDescription = null, modifier = Modifier.size(14.dp), tint = LearnViolet)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("+ Lesson", color = LearnViolet, fontSize = 12.sp)
                            }

                            // Add/Configure Quiz
                            OutlinedButton(
                                onClick = { onAddQuizClick(course.id) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("quiz_btn_${course.id}"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(14.dp), tint = LearnViolet)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Quiz", color = LearnViolet, fontSize = 12.sp)
                            }

                            // Publish / Unpublish Toggle
                            Button(
                                onClick = { onTogglePublishClick(course.id) },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .testTag("toggle_publish_btn_${course.id}"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (course.isPublished) LearnAmber else LearnGreen
                                )
                            ) {
                                Icon(
                                    imageVector = if (course.isPublished) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (course.isPublished) "Unpublish" else "Publish",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // View Student Grades Button
                        OutlinedButton(
                            onClick = { onViewStudentsClick(course.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("view_students_btn_${course.id}"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(16.dp), tint = LearnTextSecondary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("View Enrolled Students & Grades (${course.studentsEnrolled})", color = LearnTextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
