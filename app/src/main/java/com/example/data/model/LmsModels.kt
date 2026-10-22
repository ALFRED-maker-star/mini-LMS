package com.example.data.model

enum class LessonContentType {
    VIDEO,
    ARTICLE,
    CODE_LAB
}

data class Lesson(
    val id: String,
    val courseId: String,
    val order: Int,
    val title: String,
    val type: LessonContentType = LessonContentType.VIDEO,
    val durationMinutes: Int = 15,
    val videoUrl: String = "",
    val content: String = "",
    val codeSnippet: String? = null,
    val keyTakeaways: List<String> = emptyList()
)

data class QuizQuestion(
    val id: String,
    val questionText: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String = ""
)

data class Quiz(
    val id: String,
    val courseId: String,
    val title: String,
    val passThresholdPercentage: Int = 70,
    val questions: List<QuizQuestion> = emptyList()
)

data class Course(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val instructor: String,
    val instructorTitle: String = "Lead Instructor & Educator",
    val instructorBio: String = "Industry veteran with 10+ years specializing in building scalable software and educating thousands of students worldwide.",
    val durationHours: Double = 6.5,
    val rating: Float = 4.9f,
    val reviewCount: Int = 1240,
    val studentsEnrolled: Int = 3450,
    val coverGradientStart: Long = 0xFF7C3AED,
    val coverGradientEnd: Long = 0xFF4F46E5,
    val iconName: String = "code",
    val isPublished: Boolean = true,
    val lessons: List<Lesson> = emptyList(),
    val quiz: Quiz? = null
)

data class Enrollment(
    val courseId: String,
    val enrolledAtMillis: Long = System.currentTimeMillis(),
    val completedLessonIds: Set<String> = emptySet(),
    val quizScorePercent: Int? = null,
    val isCompleted: Boolean = false,
    val completedAtMillis: Long? = null
)

data class StudentRecord(
    val studentId: String,
    val studentName: String,
    val studentEmail: String,
    val courseId: String,
    val enrolledDate: String,
    val completedLessonsCount: Int,
    val totalLessonsCount: Int,
    val quizScorePercent: Int?,
    val isCompleted: Boolean,
    val lastActive: String
) {
    val completionPercentage: Int
        get() = if (totalLessonsCount > 0) ((completedLessonsCount.toFloat() / totalLessonsCount) * 100).toInt().coerceIn(0, 100) else 0
}

data class InstructorProfile(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val bio: String
)

enum class UserRole {
    STUDENT,
    INSTRUCTOR
}

sealed interface LmsScreen {
    // Student screens
    data object CourseCatalog : LmsScreen
    data class CourseDetail(val courseId: String) : LmsScreen
    data class LessonViewer(val courseId: String, val lessonId: String) : LmsScreen
    data class QuizScreen(val courseId: String) : LmsScreen
    data class QuizResultScreen(val courseId: String, val score: Int, val total: Int, val passed: Boolean) : LmsScreen
    data object MyProgress : LmsScreen
    data class Certificate(val courseId: String) : LmsScreen

    // Instructor screens
    data object InstructorLogin : LmsScreen
    data object InstructorDashboard : LmsScreen
    data object InstructorCourses : LmsScreen
    data class AddEditCourse(val courseId: String? = null) : LmsScreen
    data class AddLesson(val courseId: String) : LmsScreen
    data class AddQuiz(val courseId: String) : LmsScreen
    data class StudentProgressOverview(val courseId: String? = null) : LmsScreen
}
