package com.example.ui.screens.instructor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.ui.theme.LearnBackground
import com.example.ui.theme.LearnBorder
import com.example.ui.theme.LearnGreen
import com.example.ui.theme.LearnSurface
import com.example.ui.theme.LearnTextPrimary
import com.example.ui.theme.LearnTextSecondary
import com.example.ui.theme.LearnViolet
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCourseScreen(
    course: Course?,
    onSaveCourse: (Course) -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf(course?.title ?: "") }
    var category by remember { mutableStateOf(course?.category ?: "Development") }
    var description by remember { mutableStateOf(course?.description ?: "") }
    var durationText by remember { mutableStateOf(course?.durationHours?.toString() ?: "5.0") }
    var isPublished by remember { mutableStateOf(course?.isPublished ?: true) }
    var selectedGradientStart by remember { mutableLongStateOf(course?.coverGradientStart ?: 0xFF7C3AED) }
    var selectedGradientEnd by remember { mutableLongStateOf(course?.coverGradientEnd ?: 0xFF4F46E5) }

    var categoryExpanded by remember { mutableStateOf(false) }
    val categories = listOf("Development", "Design", "Cloud & AI", "Data Science", "Business")

    val gradientPresets = listOf(
        Pair(0xFF7C3AED, 0xFF4F46E5), // Violet / Indigo
        Pair(0xFFEC4899, 0xFF8B5CF6), // Pink / Violet
        Pair(0xFF10B981, 0xFF059669), // Emerald / Teal
        Pair(0xFF3B82F6, 0xFF1D4ED8), // Blue / Cobalt
        Pair(0xFFF59E0B, 0xFFD97706)  // Amber / Orange
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LearnBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = if (course == null) "Create New Course" else "Edit Course",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Configure curriculum details, category metadata, and publishing status.",
            style = MaterialTheme.typography.bodySmall.copy(color = LearnTextSecondary)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LearnSurface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Course Title") },
                    placeholder = { Text("e.g. Master Jetpack Compose & M3") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("course_title_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LearnViolet,
                        unfocusedBorderColor = LearnBorder
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .testTag("course_category_dropdown"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LearnViolet,
                            unfocusedBorderColor = LearnBorder
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Course Description") },
                    placeholder = { Text("Provide an overview of learning goals and prerequisites...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .testTag("course_description_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LearnViolet,
                        unfocusedBorderColor = LearnBorder
                    ),
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Estimated Duration
                OutlinedTextField(
                    value = durationText,
                    onValueChange = { durationText = it },
                    label = { Text("Estimated Duration (Hours)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("course_duration_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LearnViolet,
                        unfocusedBorderColor = LearnBorder
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Cover Palette Preset Selection
                Text(
                    text = "Cover Banner Theme",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    gradientPresets.forEach { preset ->
                        val isSelected = selectedGradientStart == preset.first && selectedGradientEnd == preset.second
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(preset.first), Color(preset.second))
                                    )
                                )
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) LearnViolet else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable {
                                    selectedGradientStart = preset.first
                                    selectedGradientEnd = preset.second
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = "Selected", tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Published Status Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Publish Immediately",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = if (isPublished) "Visible to all students in catalog" else "Draft mode (hidden from catalog)",
                            style = MaterialTheme.typography.labelSmall.copy(color = LearnTextSecondary)
                        )
                    }

                    Switch(
                        checked = isPublished,
                        onCheckedChange = { isPublished = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = LearnGreen
                        ),
                        modifier = Modifier.testTag("publish_status_switch")
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Save Button
                Button(
                    onClick = {
                        val hours = durationText.toDoubleOrNull() ?: 5.0
                        val courseId = course?.id ?: ("course-" + UUID.randomUUID().toString().take(6))
                        val updatedCourse = Course(
                            id = courseId,
                            title = title.ifBlank { "New Online Course" },
                            category = category,
                            description = description.ifBlank { "Comprehensive course designed to build job-ready skills." },
                            instructor = course?.instructor ?: "Dr. Sarah Chen",
                            instructorTitle = course?.instructorTitle ?: "Lead Instructor",
                            instructorBio = course?.instructorBio ?: "Senior Educator at LearnPath",
                            durationHours = hours,
                            rating = course?.rating ?: 5.0f,
                            reviewCount = course?.reviewCount ?: 0,
                            studentsEnrolled = course?.studentsEnrolled ?: 0,
                            coverGradientStart = selectedGradientStart,
                            coverGradientEnd = selectedGradientEnd,
                            iconName = when (category) {
                                "Development" -> "android"
                                "Design" -> "design"
                                "Cloud & AI" -> "psychology"
                                "Data Science" -> "bar_chart"
                                else -> "business_center"
                            },
                            isPublished = isPublished,
                            lessons = course?.lessons ?: emptyList(),
                            quiz = course?.quiz
                        )
                        onSaveCourse(updatedCourse)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_course_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LearnGreen)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Course", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
