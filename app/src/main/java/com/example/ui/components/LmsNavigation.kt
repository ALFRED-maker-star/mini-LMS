package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.LmsScreen
import com.example.data.model.UserRole
import com.example.ui.theme.LearnBorder
import com.example.ui.theme.LearnGreen
import com.example.ui.theme.LearnGreenContainer
import com.example.ui.theme.LearnOnGreenContainer
import com.example.ui.theme.LearnOnVioletContainer
import com.example.ui.theme.LearnTextSecondary
import com.example.ui.theme.LearnViolet
import com.example.ui.theme.LearnVioletContainer

@Composable
fun LmsBottomNavigationBar(
    currentScreen: LmsScreen,
    userRole: UserRole,
    onNavigate: (LmsScreen) -> Unit,
    onSwitchRole: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .testTag("lms_bottom_navigation_bar")
            .windowInsetsPadding(WindowInsets.navigationBars),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        if (userRole == UserRole.STUDENT) {
            NavigationBarItem(
                selected = currentScreen is LmsScreen.CourseCatalog || currentScreen is LmsScreen.CourseDetail || currentScreen is LmsScreen.LessonViewer,
                onClick = { onNavigate(LmsScreen.CourseCatalog) },
                icon = { Icon(Icons.Default.Explore, contentDescription = "Course Catalog") },
                label = { Text("Catalog") },
                colors = studentNavColors(),
                modifier = Modifier.testTag("nav_tab_catalog")
            )
            NavigationBarItem(
                selected = currentScreen is LmsScreen.MyProgress,
                onClick = { onNavigate(LmsScreen.MyProgress) },
                icon = { Icon(Icons.Default.TrendingUp, contentDescription = "My Progress") },
                label = { Text("My Progress") },
                colors = studentNavColors(),
                modifier = Modifier.testTag("nav_tab_progress")
            )
            NavigationBarItem(
                selected = currentScreen is LmsScreen.Certificate,
                onClick = { onNavigate(LmsScreen.Certificate("course-android")) },
                icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Certificates") },
                label = { Text("Certificates") },
                colors = studentNavColors(),
                modifier = Modifier.testTag("nav_tab_certificates")
            )
            NavigationBarItem(
                selected = false,
                onClick = { onSwitchRole(UserRole.INSTRUCTOR) },
                icon = { Icon(Icons.Default.Analytics, contentDescription = "Switch to Instructor") },
                label = { Text("Instructor") },
                colors = studentNavColors(),
                modifier = Modifier.testTag("nav_tab_switch_instructor")
            )
        } else {
            NavigationBarItem(
                selected = currentScreen is LmsScreen.InstructorDashboard,
                onClick = { onNavigate(LmsScreen.InstructorDashboard) },
                icon = { Icon(Icons.Default.Analytics, contentDescription = "Dashboard") },
                label = { Text("Dashboard") },
                colors = instructorNavColors(),
                modifier = Modifier.testTag("nav_tab_dashboard")
            )
            NavigationBarItem(
                selected = currentScreen is LmsScreen.InstructorCourses || currentScreen is LmsScreen.AddEditCourse || currentScreen is LmsScreen.AddLesson || currentScreen is LmsScreen.AddQuiz,
                onClick = { onNavigate(LmsScreen.InstructorCourses) },
                icon = { Icon(Icons.Default.MenuBook, contentDescription = "My Courses") },
                label = { Text("Courses") },
                colors = instructorNavColors(),
                modifier = Modifier.testTag("nav_tab_instructor_courses")
            )
            NavigationBarItem(
                selected = currentScreen is LmsScreen.StudentProgressOverview,
                onClick = { onNavigate(LmsScreen.StudentProgressOverview()) },
                icon = { Icon(Icons.Default.Group, contentDescription = "Student Progress") },
                label = { Text("Students") },
                colors = instructorNavColors(),
                modifier = Modifier.testTag("nav_tab_students")
            )
            NavigationBarItem(
                selected = false,
                onClick = { onSwitchRole(UserRole.STUDENT) },
                icon = { Icon(Icons.Default.School, contentDescription = "Switch to Student") },
                label = { Text("Student Mode") },
                colors = instructorNavColors(),
                modifier = Modifier.testTag("nav_tab_switch_student")
            )
        }
    }
}

@Composable
fun LmsNavigationRail(
    currentScreen: LmsScreen,
    userRole: UserRole,
    onNavigate: (LmsScreen) -> Unit,
    onSwitchRole: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        modifier = modifier.testTag("lms_navigation_rail"),
        containerColor = MaterialTheme.colorScheme.surface,
        header = {
            // Header spacing
        }
    ) {
        if (userRole == UserRole.STUDENT) {
            NavigationRailItem(
                selected = currentScreen is LmsScreen.CourseCatalog || currentScreen is LmsScreen.CourseDetail || currentScreen is LmsScreen.LessonViewer,
                onClick = { onNavigate(LmsScreen.CourseCatalog) },
                icon = { Icon(Icons.Default.Explore, contentDescription = "Catalog") },
                label = { Text("Catalog") },
                colors = studentRailColors()
            )
            NavigationRailItem(
                selected = currentScreen is LmsScreen.MyProgress,
                onClick = { onNavigate(LmsScreen.MyProgress) },
                icon = { Icon(Icons.Default.TrendingUp, contentDescription = "My Progress") },
                label = { Text("Progress") },
                colors = studentRailColors()
            )
            NavigationRailItem(
                selected = currentScreen is LmsScreen.Certificate,
                onClick = { onNavigate(LmsScreen.Certificate("course-android")) },
                icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Certificates") },
                label = { Text("Certificates") },
                colors = studentRailColors()
            )
            NavigationRailItem(
                selected = false,
                onClick = { onSwitchRole(UserRole.INSTRUCTOR) },
                icon = { Icon(Icons.Default.Analytics, contentDescription = "Instructor") },
                label = { Text("Instructor") },
                colors = studentRailColors()
            )
        } else {
            NavigationRailItem(
                selected = currentScreen is LmsScreen.InstructorDashboard,
                onClick = { onNavigate(LmsScreen.InstructorDashboard) },
                icon = { Icon(Icons.Default.Analytics, contentDescription = "Dashboard") },
                label = { Text("Dashboard") },
                colors = instructorRailColors()
            )
            NavigationRailItem(
                selected = currentScreen is LmsScreen.InstructorCourses,
                onClick = { onNavigate(LmsScreen.InstructorCourses) },
                icon = { Icon(Icons.Default.MenuBook, contentDescription = "Courses") },
                label = { Text("Courses") },
                colors = instructorRailColors()
            )
            NavigationRailItem(
                selected = currentScreen is LmsScreen.StudentProgressOverview,
                onClick = { onNavigate(LmsScreen.StudentProgressOverview()) },
                icon = { Icon(Icons.Default.Group, contentDescription = "Students") },
                label = { Text("Students") },
                colors = instructorRailColors()
            )
            NavigationRailItem(
                selected = false,
                onClick = { onSwitchRole(UserRole.STUDENT) },
                icon = { Icon(Icons.Default.School, contentDescription = "Student View") },
                label = { Text("Student") },
                colors = instructorRailColors()
            )
        }
    }
}

@Composable
private fun studentNavColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = LearnViolet,
    selectedTextColor = LearnViolet,
    indicatorColor = LearnVioletContainer,
    unselectedIconColor = LearnTextSecondary,
    unselectedTextColor = LearnTextSecondary
)

@Composable
private fun instructorNavColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = LearnGreen,
    selectedTextColor = LearnGreen,
    indicatorColor = LearnGreenContainer,
    unselectedIconColor = LearnTextSecondary,
    unselectedTextColor = LearnTextSecondary
)

@Composable
private fun studentRailColors() = NavigationRailItemDefaults.colors(
    selectedIconColor = LearnViolet,
    selectedTextColor = LearnViolet,
    indicatorColor = LearnVioletContainer,
    unselectedIconColor = LearnTextSecondary,
    unselectedTextColor = LearnTextSecondary
)

@Composable
private fun instructorRailColors() = NavigationRailItemDefaults.colors(
    selectedIconColor = LearnGreen,
    selectedTextColor = LearnGreen,
    indicatorColor = LearnGreenContainer,
    unselectedIconColor = LearnTextSecondary,
    unselectedTextColor = LearnTextSecondary
)
