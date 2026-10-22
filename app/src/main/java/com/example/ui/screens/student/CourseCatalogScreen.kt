package com.example.ui.screens.student

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Course
import com.example.data.model.Enrollment
import com.example.ui.theme.LearnAmber
import com.example.ui.theme.LearnBackground
import com.example.ui.theme.LearnBorder
import com.example.ui.theme.LearnGreen
import com.example.ui.theme.LearnGreenContainer
import com.example.ui.theme.LearnOnGreenContainer
import com.example.ui.theme.LearnOnVioletContainer
import com.example.ui.theme.LearnSurface
import com.example.ui.theme.LearnTextPrimary
import com.example.ui.theme.LearnTextSecondary
import com.example.ui.theme.LearnViolet
import com.example.ui.theme.LearnVioletContainer

@Composable
fun CourseCatalogScreen(
    courses: List<Course>,
    enrollments: Map<String, Enrollment>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    onCourseClick: (String) -> Unit,
    onEnrollClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf("All", "Development", "Design", "Cloud & AI", "Data Science", "Business")

    BoxWithConstraints(modifier = modifier.fillMaxSize().background(LearnBackground)) {
        val isWide = maxWidth > 700.dp

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Hero Banner Header
            item {
                CatalogHeroBanner(onExploreClick = { onCategorySelect("All") })
            }

            // Search Bar & Filter Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("course_search_input"),
                        placeholder = { Text("Search courses, instructors, topics...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = LearnViolet)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchQueryChange("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear search")
                                }
                            }
                        },
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

                    // Category Filter Chips Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { cat ->
                            val isSelected = selectedCategory.equals(cat, ignoreCase = true)
                            FilterChip(
                                selected = isSelected,
                                onClick = { onCategorySelect(cat) },
                                label = { Text(cat, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = LearnViolet,
                                    selectedLabelColor = Color.White,
                                    containerColor = LearnSurface,
                                    labelColor = LearnTextPrimary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) LearnViolet else LearnBorder
                                ),
                                modifier = Modifier.testTag("filter_chip_$cat")
                            )
                        }
                    }
                }
            }

            // Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedCategory == "All") "Popular & Featured Courses" else "$selectedCategory Courses",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = LearnTextPrimary
                        )
                    )
                    Text(
                        text = "${courses.size} available",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = LearnTextSecondary
                        )
                    )
                }
            }

            // Course Cards List or Grid
            if (isWide) {
                // Chunk into pairs for tablet / desktop wide screens
                val chunked = courses.chunked(2)
                items(chunked) { pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        pair.forEach { course ->
                            Box(modifier = Modifier.weight(1f)) {
                                CourseCard(
                                    course = course,
                                    enrollment = enrollments[course.id],
                                    onCourseClick = { onCourseClick(course.id) },
                                    onEnrollClick = { onEnrollClick(course.id) }
                                )
                            }
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            } else {
                items(courses, key = { it.id }) { course ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        CourseCard(
                            course = course,
                            enrollment = enrollments[course.id],
                            onCourseClick = { onCourseClick(course.id) },
                            onEnrollClick = { onEnrollClick(course.id) }
                        )
                    }
                }
            }

            if (courses.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "No courses found",
                            tint = LearnTextSecondary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No matching courses found",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Try adjusting your search or category filter.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = LearnTextSecondary)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CatalogHeroBanner(
    onExploreClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = LearnSurface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Background Image
            Image(
                painter = painterResource(id = R.drawable.learnpath_hero_1792668135936),
                contentDescription = "Online Learning Illustration",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.25f),
                                Color.Black.copy(alpha = 0.75f)
                            )
                        )
                    )
            )

            // Content on top
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomStart)
            ) {
                Surface(
                    color = LearnGreen,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "CAREER-READY SKILLS",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Learn Without Limits",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                )
                Text(
                    text = "Master in-demand skills from industry experts with structured lessons & accredited certificates.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.9f)
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun CourseCard(
    course: Course,
    enrollment: Enrollment?,
    onCourseClick: () -> Unit,
    onEnrollClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEnrolled = enrollment != null
    val totalLessons = course.lessons.size
    val completedCount = enrollment?.completedLessonIds?.size ?: 0
    val progressPercent = if (totalLessons > 0) ((completedCount.toFloat() / totalLessons) * 100).toInt() else 0
    val isCompleted = enrollment?.isCompleted == true

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("course_card_${course.id}")
            .clip(RoundedCornerShape(16.dp))
            .clickable { onCourseClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LearnSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Course Header / Cover Art
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(course.coverGradientStart),
                                Color(course.coverGradientEnd)
                            )
                        )
                    )
                    .padding(12.dp)
            ) {
                // Category Chip Badge
                Surface(
                    color = Color.Black.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = course.category.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    )
                }

                // Center Icon Emblem
                Icon(
                    imageVector = getCategoryIcon(course.iconName),
                    contentDescription = course.category,
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier
                        .size(44.dp)
                        .align(Alignment.Center)
                )

                // Enrolled / Status Badge
                if (isEnrolled) {
                    Surface(
                        color = if (isCompleted) LearnGreen else LearnVioletContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (isCompleted) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "Completed",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Completed",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 11.sp
                                    )
                                )
                            } else {
                                Text(
                                    text = "$progressPercent% Done",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = LearnOnVioletContainer,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Card Body
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = course.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = LearnTextPrimary
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Instructor
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Instructor",
                        tint = LearnTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = course.instructor,
                        style = MaterialTheme.typography.bodySmall.copy(color = LearnTextSecondary)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Metadata: Rating, Duration, Lessons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Rating
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = LearnAmber,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "${course.rating}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = LearnTextPrimary
                            )
                        )
                        Text(
                            text = "(${course.reviewCount})",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = LearnTextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }

                    // Duration
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            Icons.Default.AccessTime,
                            contentDescription = "Duration",
                            tint = LearnTextSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "${course.durationHours}h",
                            style = MaterialTheme.typography.bodySmall.copy(color = LearnTextSecondary)
                        )
                    }

                    // Lessons count
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            Icons.Default.MenuBook,
                            contentDescription = "Lessons",
                            tint = LearnTextSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "$totalLessons lessons",
                            style = MaterialTheme.typography.bodySmall.copy(color = LearnTextSecondary)
                        )
                    }
                }

                // Progress Bar if enrolled
                if (isEnrolled) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Progress",
                                style = MaterialTheme.typography.labelSmall.copy(color = LearnTextSecondary)
                            )
                            Text(
                                text = "$completedCount/$totalLessons completed",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCompleted) LearnGreen else LearnViolet
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { (progressPercent / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (isCompleted) LearnGreen else LearnViolet,
                            trackColor = LearnBorder
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Button
                if (isEnrolled) {
                    Button(
                        onClick = onCourseClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("continue_course_button_${course.id}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCompleted) LearnGreen else LearnViolet
                        )
                    ) {
                        Icon(
                            imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isCompleted) "Review Course & Certificate" else "Continue Learning",
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Button(
                        onClick = onEnrollClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("enroll_course_button_${course.id}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LearnViolet)
                    ) {
                        Text(text = "Enroll Now", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

fun getCategoryIcon(iconName: String): ImageVector {
    return when (iconName) {
        "android" -> Icons.Default.Android
        "design" -> Icons.Default.ColorLens
        "psychology" -> Icons.Default.Psychology
        "bar_chart" -> Icons.Default.BarChart
        "business_center" -> Icons.Default.BusinessCenter
        else -> Icons.Default.MenuBook
    }
}
