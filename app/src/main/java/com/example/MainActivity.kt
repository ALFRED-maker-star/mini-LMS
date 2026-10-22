package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.LmsScreen
import com.example.data.model.UserRole
import com.example.ui.components.LmsBottomNavigationBar
import com.example.ui.components.LmsNavigationRail
import com.example.ui.components.LmsTopBar
import com.example.ui.screens.instructor.AddEditCourseScreen
import com.example.ui.screens.instructor.AddLessonScreen
import com.example.ui.screens.instructor.AddQuizScreen
import com.example.ui.screens.instructor.InstructorCoursesScreen
import com.example.ui.screens.instructor.InstructorDashboardScreen
import com.example.ui.screens.instructor.InstructorLoginScreen
import com.example.ui.screens.instructor.StudentProgressOverviewScreen
import com.example.ui.screens.student.CertificateScreen
import com.example.ui.screens.student.CourseCatalogScreen
import com.example.ui.screens.student.CourseDetailScreen
import com.example.ui.screens.student.LessonViewerScreen
import com.example.ui.screens.student.MyProgressScreen
import com.example.ui.screens.student.QuizResultScreen
import com.example.ui.screens.student.QuizScreen
import com.example.ui.theme.LearnBackground
import com.example.ui.theme.LearnPathTheme
import com.example.ui.viewmodel.LmsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LearnPathTheme {
                LearnPathApp()
            }
        }
    }
}

@Composable
fun LearnPathApp(
    viewModel: LmsViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val userRole by viewModel.userRole.collectAsStateWithLifecycle()
    val courses by viewModel.courses.collectAsStateWithLifecycle()
    val filteredCourses by viewModel.filteredCourses.collectAsStateWithLifecycle()
    val enrollments by viewModel.enrollments.collectAsStateWithLifecycle()
    val studentRecords by viewModel.studentRecords.collectAsStateWithLifecycle()
    val currentInstructor by viewModel.currentInstructor.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val quizSession by viewModel.quizSession.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    val isRootScreen = when (userRole) {
        UserRole.STUDENT -> currentScreen is LmsScreen.CourseCatalog
        UserRole.INSTRUCTOR -> currentScreen is LmsScreen.InstructorDashboard
    }

    BackHandler(enabled = !isRootScreen) {
        viewModel.navigateBack()
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(LearnBackground)) {
        val isWide = maxWidth >= 700.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                LmsTopBar(
                    currentScreen = currentScreen,
                    userRole = userRole,
                    studentName = viewModel.currentStudentName,
                    instructorName = currentInstructor.name,
                    onBackClick = { viewModel.navigateBack() },
                    onRoleToggle = {
                        val newRole = if (userRole == UserRole.STUDENT) UserRole.INSTRUCTOR else UserRole.STUDENT
                        viewModel.switchRole(newRole)
                    },
                    canNavigateBack = !isRootScreen,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
                )
            },
            bottomBar = {
                if (!isWide && currentScreen !is LmsScreen.InstructorLogin) {
                    LmsBottomNavigationBar(
                        currentScreen = currentScreen,
                        userRole = userRole,
                        onNavigate = { viewModel.navigateTo(it) },
                        onSwitchRole = { viewModel.switchRole(it) }
                    )
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Adaptive Navigation Rail on wide screens / desktop
                if (isWide && currentScreen !is LmsScreen.InstructorLogin) {
                    LmsNavigationRail(
                        currentScreen = currentScreen,
                        userRole = userRole,
                        onNavigate = { viewModel.navigateTo(it) },
                        onSwitchRole = { viewModel.switchRole(it) },
                        modifier = Modifier.fillMaxHeight()
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    when (val screen = currentScreen) {
                        // 1. Course Catalog
                        is LmsScreen.CourseCatalog -> {
                            CourseCatalogScreen(
                                courses = filteredCourses,
                                enrollments = enrollments,
                                searchQuery = searchQuery,
                                onSearchQueryChange = { viewModel.searchQuery.value = it },
                                selectedCategory = selectedCategory,
                                onCategorySelect = { viewModel.selectedCategory.value = it },
                                onCourseClick = { viewModel.navigateTo(LmsScreen.CourseDetail(it)) },
                                onEnrollClick = { viewModel.enroll(it) }
                            )
                        }

                        // 2. Course Detail
                        is LmsScreen.CourseDetail -> {
                            val course = courses.find { it.id == screen.courseId } ?: courses.first()
                            CourseDetailScreen(
                                course = course,
                                enrollment = enrollments[course.id],
                                onEnrollClick = { viewModel.enroll(course.id) },
                                onStartLesson = { lessonId ->
                                    viewModel.navigateTo(LmsScreen.LessonViewer(course.id, lessonId))
                                },
                                onStartQuiz = { viewModel.startQuiz(course.id) },
                                onViewCertificate = { viewModel.navigateTo(LmsScreen.Certificate(course.id)) }
                            )
                        }

                        // 3. Lesson Viewer
                        is LmsScreen.LessonViewer -> {
                            val course = courses.find { it.id == screen.courseId } ?: courses.first()
                            LessonViewerScreen(
                                course = course,
                                currentLessonId = screen.lessonId,
                                enrollment = enrollments[course.id],
                                onSelectLesson = { newLessonId ->
                                    viewModel.navigateTo(LmsScreen.LessonViewer(course.id, newLessonId))
                                },
                                onMarkComplete = { lessonId ->
                                    viewModel.markLessonComplete(course.id, lessonId)
                                },
                                onTakeQuiz = { viewModel.startQuiz(course.id) }
                            )
                        }

                        // 4. Quiz Screen
                        is LmsScreen.QuizScreen -> {
                            val course = courses.find { it.id == screen.courseId } ?: courses.first()
                            val session = quizSession ?: com.example.ui.viewmodel.QuizSession(courseId = course.id)
                            QuizScreen(
                                course = course,
                                quizSession = session,
                                onSelectAnswer = { qId, optIdx ->
                                    viewModel.selectQuizAnswer(qId, optIdx)
                                },
                                onNextQuestion = { viewModel.nextQuizQuestion() },
                                onPreviousQuestion = { viewModel.previousQuizQuestion() },
                                onSubmitQuiz = { viewModel.submitQuiz(course.id) }
                            )
                        }

                        // 4b. Quiz Result Screen
                        is LmsScreen.QuizResultScreen -> {
                            val course = courses.find { it.id == screen.courseId } ?: courses.first()
                            QuizResultScreen(
                                course = course,
                                score = screen.score,
                                total = screen.total,
                                passed = screen.passed,
                                onRetakeQuiz = { viewModel.startQuiz(course.id) },
                                onViewCertificate = { viewModel.navigateTo(LmsScreen.Certificate(course.id)) },
                                onBackToCourse = { viewModel.navigateTo(LmsScreen.CourseDetail(course.id)) }
                            )
                        }

                        // 5. My Progress Screen
                        is LmsScreen.MyProgress -> {
                            MyProgressScreen(
                                studentName = viewModel.currentStudentName,
                                courses = courses,
                                enrollments = enrollments,
                                onResumeCourse = { courseId ->
                                    val course = courses.find { it.id == courseId }
                                    val enrollment = enrollments[courseId]
                                    val nextLesson = course?.lessons?.firstOrNull {
                                        enrollment?.completedLessonIds?.contains(it.id) != true
                                    } ?: course?.lessons?.firstOrNull()

                                    if (nextLesson != null) {
                                        viewModel.navigateTo(LmsScreen.LessonViewer(courseId, nextLesson.id))
                                    } else {
                                        viewModel.navigateTo(LmsScreen.CourseDetail(courseId))
                                    }
                                },
                                onViewCertificate = { courseId ->
                                    viewModel.navigateTo(LmsScreen.Certificate(courseId))
                                },
                                onExploreCatalog = {
                                    viewModel.navigateTo(LmsScreen.CourseCatalog)
                                }
                            )
                        }

                        // 6. Certificate Screen
                        is LmsScreen.Certificate -> {
                            val course = courses.find { it.id == screen.courseId } ?: courses.first()
                            CertificateScreen(
                                course = course,
                                enrollment = enrollments[course.id],
                                studentName = viewModel.currentStudentName,
                                onDownloadCertificate = {
                                    Toast.makeText(context, "Certificate PDF downloaded to device storage!", Toast.LENGTH_LONG).show()
                                    viewModel.showMessage("Certificate downloaded successfully! 🎓")
                                },
                                onShareCertificate = {
                                    Toast.makeText(context, "Certificate credential link copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    viewModel.showMessage("Share link copied! Show your achievement to the world!")
                                }
                            )
                        }

                        // 7. Instructor Login
                        is LmsScreen.InstructorLogin -> {
                            InstructorLoginScreen(
                                onLoginSuccess = { email, name ->
                                    viewModel.loginInstructor(email, name)
                                },
                                onBackToStudent = {
                                    viewModel.switchRole(UserRole.STUDENT)
                                }
                            )
                        }

                        // 8. Instructor Dashboard
                        is LmsScreen.InstructorDashboard -> {
                            InstructorDashboardScreen(
                                instructor = currentInstructor,
                                courses = courses,
                                studentRecords = studentRecords,
                                onCreateCourseClick = { viewModel.navigateTo(LmsScreen.AddEditCourse(null)) },
                                onManageCoursesClick = { viewModel.navigateTo(LmsScreen.InstructorCourses) },
                                onStudentProgressClick = { courseId ->
                                    viewModel.navigateTo(LmsScreen.StudentProgressOverview(courseId))
                                },
                                onEditCourseClick = { courseId ->
                                    viewModel.navigateTo(LmsScreen.AddEditCourse(courseId))
                                }
                            )
                        }

                        // 9. Instructor My Courses
                        is LmsScreen.InstructorCourses -> {
                            InstructorCoursesScreen(
                                courses = courses,
                                onCreateCourseClick = { viewModel.navigateTo(LmsScreen.AddEditCourse(null)) },
                                onEditCourseClick = { courseId -> viewModel.navigateTo(LmsScreen.AddEditCourse(courseId)) },
                                onTogglePublishClick = { courseId -> viewModel.togglePublishCourse(courseId) },
                                onAddLessonClick = { courseId -> viewModel.navigateTo(LmsScreen.AddLesson(courseId)) },
                                onAddQuizClick = { courseId -> viewModel.navigateTo(LmsScreen.AddQuiz(courseId)) },
                                onViewStudentsClick = { courseId -> viewModel.navigateTo(LmsScreen.StudentProgressOverview(courseId)) }
                            )
                        }

                        // 10. Add/Edit Course
                        is LmsScreen.AddEditCourse -> {
                            val existingCourse = courses.find { it.id == screen.courseId }
                            AddEditCourseScreen(
                                course = existingCourse,
                                onSaveCourse = { updatedCourse ->
                                    viewModel.saveCourse(updatedCourse)
                                    viewModel.navigateTo(LmsScreen.InstructorCourses)
                                }
                            )
                        }

                        // 11. Add Lesson
                        is LmsScreen.AddLesson -> {
                            val targetCourse = courses.find { it.id == screen.courseId } ?: courses.first()
                            AddLessonScreen(
                                course = targetCourse,
                                onSaveLesson = { lesson ->
                                    viewModel.addLesson(targetCourse.id, lesson)
                                    viewModel.navigateTo(LmsScreen.InstructorCourses)
                                }
                            )
                        }

                        // 12. Add Quiz
                        is LmsScreen.AddQuiz -> {
                            val targetCourse = courses.find { it.id == screen.courseId } ?: courses.first()
                            AddQuizScreen(
                                course = targetCourse,
                                onSaveQuiz = { quiz ->
                                    viewModel.saveQuiz(targetCourse.id, quiz)
                                    viewModel.navigateTo(LmsScreen.InstructorCourses)
                                }
                            )
                        }

                        // 13. Student Progress Overview
                        is LmsScreen.StudentProgressOverview -> {
                            StudentProgressOverviewScreen(
                                courses = courses,
                                studentRecords = studentRecords,
                                initialCourseId = screen.courseId
                            )
                        }
                    }
                }
            }
        }
    }
}
