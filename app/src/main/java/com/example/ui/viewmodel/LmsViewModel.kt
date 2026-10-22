package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Course
import com.example.data.model.Enrollment
import com.example.data.model.InstructorProfile
import com.example.data.model.Lesson
import com.example.data.model.LmsScreen
import com.example.data.model.Quiz
import com.example.data.model.StudentRecord
import com.example.data.model.UserRole
import com.example.data.repository.LmsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class QuizSession(
    val courseId: String,
    val currentQuestionIndex: Int = 0,
    val selectedAnswers: Map<String, Int> = emptyMap(),
    val isSubmitted: Boolean = false,
    val score: Int = 0,
    val totalQuestions: Int = 0,
    val isPassed: Boolean = false
)

class LmsViewModel(
    private val repository: LmsRepository = LmsRepository()
) : ViewModel() {

    private val screenStack = mutableListOf<LmsScreen>(LmsScreen.CourseCatalog)

    private val _currentScreen = MutableStateFlow<LmsScreen>(LmsScreen.CourseCatalog)
    val currentScreen: StateFlow<LmsScreen> = _currentScreen.asStateFlow()

    private val _userRole = MutableStateFlow<UserRole>(UserRole.STUDENT)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    val courses: StateFlow<List<Course>> = repository.courses
    val enrollments: StateFlow<Map<String, Enrollment>> = repository.enrollments
    val studentRecords: StateFlow<List<StudentRecord>> = repository.studentRecords
    val currentInstructor: StateFlow<InstructorProfile> = repository.currentInstructor

    val currentStudentName = repository.currentStudentName
    val currentStudentEmail = repository.currentStudentEmail

    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("All")

    private val _quizSession = MutableStateFlow<QuizSession?>(null)
    val quizSession: StateFlow<QuizSession?> = _quizSession.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // Filtered courses for catalog
    val filteredCourses: StateFlow<List<Course>> = combine(
        courses,
        searchQuery,
        selectedCategory
    ) { allCourses, query, category ->
        allCourses.filter { course ->
            val matchesCategory = (category == "All" || course.category.equals(category, ignoreCase = true))
            val matchesQuery = query.isBlank() ||
                    course.title.contains(query, ignoreCase = true) ||
                    course.instructor.contains(query, ignoreCase = true) ||
                    course.category.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    fun navigateTo(screen: LmsScreen) {
        if (_currentScreen.value != screen) {
            screenStack.add(screen)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (screenStack.size > 1) {
            screenStack.removeAt(screenStack.lastIndex)
            _currentScreen.value = screenStack.last()
            return true
        } else if (_currentScreen.value != LmsScreen.CourseCatalog && _userRole.value == UserRole.STUDENT) {
            _currentScreen.value = LmsScreen.CourseCatalog
            screenStack.clear()
            screenStack.add(LmsScreen.CourseCatalog)
            return true
        } else if (_currentScreen.value != LmsScreen.InstructorDashboard && _userRole.value == UserRole.INSTRUCTOR) {
            _currentScreen.value = LmsScreen.InstructorDashboard
            screenStack.clear()
            screenStack.add(LmsScreen.InstructorDashboard)
            return true
        }
        return false
    }

    fun switchRole(role: UserRole) {
        _userRole.value = role
        screenStack.clear()
        if (role == UserRole.STUDENT) {
            val target = LmsScreen.CourseCatalog
            screenStack.add(target)
            _currentScreen.value = target
        } else {
            val target = LmsScreen.InstructorDashboard
            screenStack.add(target)
            _currentScreen.value = target
        }
    }

    fun enroll(courseId: String) {
        repository.enrollCourse(courseId)
        val course = courses.value.find { it.id == courseId }
        showMessage("Enrolled in \"${course?.title ?: "Course"}\" successfully!")
    }

    fun markLessonComplete(courseId: String, lessonId: String) {
        repository.markLessonComplete(courseId, lessonId)
        showMessage("Lesson marked as complete! ✓")
    }

    fun startQuiz(courseId: String) {
        val course = courses.value.find { it.id == courseId }
        val quiz = course?.quiz
        val total = quiz?.questions?.size ?: 0
        _quizSession.value = QuizSession(
            courseId = courseId,
            currentQuestionIndex = 0,
            selectedAnswers = emptyMap(),
            isSubmitted = false,
            score = 0,
            totalQuestions = total,
            isPassed = false
        )
        navigateTo(LmsScreen.QuizScreen(courseId))
    }

    fun selectQuizAnswer(questionId: String, optionIndex: Int) {
        val current = _quizSession.value ?: return
        val updatedAnswers = current.selectedAnswers.toMutableMap()
        updatedAnswers[questionId] = optionIndex
        _quizSession.value = current.copy(selectedAnswers = updatedAnswers)
    }

    fun nextQuizQuestion() {
        val current = _quizSession.value ?: return
        if (current.currentQuestionIndex < current.totalQuestions - 1) {
            _quizSession.value = current.copy(currentQuestionIndex = current.currentQuestionIndex + 1)
        }
    }

    fun previousQuizQuestion() {
        val current = _quizSession.value ?: return
        if (current.currentQuestionIndex > 0) {
            _quizSession.value = current.copy(currentQuestionIndex = current.currentQuestionIndex - 1)
        }
    }

    fun submitQuiz(courseId: String) {
        val current = _quizSession.value ?: return
        val course = courses.value.find { it.id == courseId } ?: return
        val quiz = course.quiz ?: return
        var correctCount = 0

        quiz.questions.forEach { q ->
            val chosen = current.selectedAnswers[q.id]
            if (chosen == q.correctOptionIndex) {
                correctCount++
            }
        }

        val total = quiz.questions.size
        val percentage = if (total > 0) ((correctCount.toFloat() / total) * 100).toInt() else 0
        val isPassed = percentage >= quiz.passThresholdPercentage

        repository.submitQuizScore(courseId, percentage)

        _quizSession.value = current.copy(
            isSubmitted = true,
            score = percentage,
            isPassed = isPassed
        )

        navigateTo(
            LmsScreen.QuizResultScreen(
                courseId = courseId,
                score = percentage,
                total = total,
                passed = isPassed
            )
        )
    }

    fun saveCourse(course: Course) {
        repository.saveCourse(course)
        showMessage("Course \"${course.title}\" saved successfully!")
    }

    fun togglePublishCourse(courseId: String) {
        repository.togglePublishCourse(courseId)
        val course = courses.value.find { it.id == courseId }
        val status = if (course?.isPublished == true) "unpublished" else "published"
        showMessage("Course status updated to $status.")
    }

    fun addLesson(courseId: String, lesson: Lesson) {
        repository.addLesson(courseId, lesson)
        showMessage("Lesson \"${lesson.title}\" added to course curriculum!")
    }

    fun saveQuiz(courseId: String, quiz: Quiz) {
        repository.saveQuiz(courseId, quiz)
        showMessage("Quiz \"${quiz.title}\" updated with ${quiz.questions.size} questions!")
    }

    fun loginInstructor(email: String, name: String = "Dr. Sarah Chen") {
        repository.loginInstructor(email, name)
        _userRole.value = UserRole.INSTRUCTOR
        navigateTo(LmsScreen.InstructorDashboard)
        showMessage("Welcome back, $name!")
    }

    fun logoutInstructor() {
        switchRole(UserRole.STUDENT)
        showMessage("Switched to Student mode")
    }
}
