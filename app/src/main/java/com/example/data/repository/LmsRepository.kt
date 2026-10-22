package com.example.data.repository

import com.example.data.model.Course
import com.example.data.model.Enrollment
import com.example.data.model.InstructorProfile
import com.example.data.model.Lesson
import com.example.data.model.LessonContentType
import com.example.data.model.Quiz
import com.example.data.model.QuizQuestion
import com.example.data.model.StudentRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LmsRepository {

    private val _courses = MutableStateFlow<List<Course>>(createSeedCourses())
    val courses: StateFlow<List<Course>> = _courses.asStateFlow()

    private val _enrollments = MutableStateFlow<Map<String, Enrollment>>(createSeedEnrollments())
    val enrollments: StateFlow<Map<String, Enrollment>> = _enrollments.asStateFlow()

    private val _studentRecords = MutableStateFlow<List<StudentRecord>>(createSeedStudentRecords())
    val studentRecords: StateFlow<List<StudentRecord>> = _studentRecords.asStateFlow()

    private val _currentInstructor = MutableStateFlow(
        InstructorProfile(
            id = "inst-01",
            name = "Dr. Sarah Chen",
            email = "sarah.chen@learnpath.edu",
            role = "Senior Android Architect & Instructor",
            bio = "Ex-Staff Engineer at TechCorp, educator to over 25,000 developers worldwide."
        )
    )
    val currentInstructor: StateFlow<InstructorProfile> = _currentInstructor.asStateFlow()

    val currentStudentName = "Alex Morgan"
    val currentStudentEmail = "alex.morgan@learnpath.io"

    fun enrollCourse(courseId: String) {
        val current = _enrollments.value.toMutableMap()
        if (!current.containsKey(courseId)) {
            current[courseId] = Enrollment(
                courseId = courseId,
                enrolledAtMillis = System.currentTimeMillis()
            )
            _enrollments.value = current

            // Increment students count on course
            _courses.value = _courses.value.map {
                if (it.id == courseId) it.copy(studentsEnrolled = it.studentsEnrolled + 1) else it
            }

            // Add student record
            val currentRecords = _studentRecords.value.toMutableList()
            val course = _courses.value.find { it.id == courseId }
            val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date())
            currentRecords.add(
                StudentRecord(
                    studentId = "stu-current",
                    studentName = currentStudentName,
                    studentEmail = currentStudentEmail,
                    courseId = courseId,
                    enrolledDate = dateStr,
                    completedLessonsCount = 0,
                    totalLessonsCount = course?.lessons?.size ?: 0,
                    quizScorePercent = null,
                    isCompleted = false,
                    lastActive = "Just now"
                )
            )
            _studentRecords.value = currentRecords
        }
    }

    fun markLessonComplete(courseId: String, lessonId: String) {
        val current = _enrollments.value.toMutableMap()
        val enrollment = current[courseId] ?: Enrollment(courseId = courseId)
        val updatedCompleted = enrollment.completedLessonIds + lessonId
        val course = _courses.value.find { it.id == courseId }
        val totalLessons = course?.lessons?.size ?: 0
        val isAllLessonsDone = updatedCompleted.size >= totalLessons && totalLessons > 0
        val hasQuiz = course?.quiz != null
        val isQuizPassed = (enrollment.quizScorePercent ?: 0) >= (course?.quiz?.passThresholdPercentage ?: 70)
        val isFullyComplete = if (hasQuiz) isAllLessonsDone && isQuizPassed else isAllLessonsDone

        val updatedEnrollment = enrollment.copy(
            completedLessonIds = updatedCompleted,
            isCompleted = isFullyComplete,
            completedAtMillis = if (isFullyComplete && enrollment.completedAtMillis == null) System.currentTimeMillis() else enrollment.completedAtMillis
        )
        current[courseId] = updatedEnrollment
        _enrollments.value = current

        // Update student record for current user
        updateStudentRecordForCurrentUser(courseId, updatedEnrollment)
    }

    fun submitQuizScore(courseId: String, scorePercent: Int) {
        val current = _enrollments.value.toMutableMap()
        val enrollment = current[courseId] ?: Enrollment(courseId = courseId)
        val course = _courses.value.find { it.id == courseId }
        val passThreshold = course?.quiz?.passThresholdPercentage ?: 70
        val isPassed = scorePercent >= passThreshold
        val totalLessons = course?.lessons?.size ?: 0
        val isAllLessonsDone = enrollment.completedLessonIds.size >= totalLessons && totalLessons > 0
        val isCourseCompleted = isPassed && isAllLessonsDone

        val updatedEnrollment = enrollment.copy(
            quizScorePercent = scorePercent,
            isCompleted = isCourseCompleted,
            completedAtMillis = if (isCourseCompleted && enrollment.completedAtMillis == null) System.currentTimeMillis() else enrollment.completedAtMillis
        )
        current[courseId] = updatedEnrollment
        _enrollments.value = current

        updateStudentRecordForCurrentUser(courseId, updatedEnrollment)
    }

    private fun updateStudentRecordForCurrentUser(courseId: String, enrollment: Enrollment) {
        val course = _courses.value.find { it.id == courseId }
        val list = _studentRecords.value.toMutableList()
        val idx = list.indexOfFirst { it.studentId == "stu-current" && it.courseId == courseId }
        val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date())
        val record = StudentRecord(
            studentId = "stu-current",
            studentName = currentStudentName,
            studentEmail = currentStudentEmail,
            courseId = courseId,
            enrolledDate = dateStr,
            completedLessonsCount = enrollment.completedLessonIds.size,
            totalLessonsCount = course?.lessons?.size ?: 0,
            quizScorePercent = enrollment.quizScorePercent,
            isCompleted = enrollment.isCompleted,
            lastActive = "Just now"
        )
        if (idx >= 0) {
            list[idx] = record
        } else {
            list.add(record)
        }
        _studentRecords.value = list
    }

    fun saveCourse(course: Course) {
        val list = _courses.value.toMutableList()
        val idx = list.indexOfFirst { it.id == course.id }
        if (idx >= 0) {
            list[idx] = course
        } else {
            list.add(0, course)
        }
        _courses.value = list
    }

    fun togglePublishCourse(courseId: String) {
        val list = _courses.value.map {
            if (it.id == courseId) it.copy(isPublished = !it.isPublished) else it
        }
        _courses.value = list
    }

    fun addLesson(courseId: String, lesson: Lesson) {
        val list = _courses.value.map { course ->
            if (course.id == courseId) {
                val updatedLessons = (course.lessons + lesson).sortedBy { it.order }
                course.copy(lessons = updatedLessons)
            } else {
                course
            }
        }
        _courses.value = list
    }

    fun saveQuiz(courseId: String, quiz: Quiz) {
        val list = _courses.value.map { course ->
            if (course.id == courseId) {
                course.copy(quiz = quiz)
            } else {
                course
            }
        }
        _courses.value = list
    }

    fun loginInstructor(email: String, name: String = "Dr. Sarah Chen") {
        _currentInstructor.value = InstructorProfile(
            id = "inst-01",
            name = name,
            email = email,
            role = "Lead Instructor",
            bio = "Experienced instructor crafting high-impact career courses on LearnPath."
        )
    }

    private fun createSeedCourses(): List<Course> {
        return listOf(
            Course(
                id = "course-android",
                title = "Modern Android with Jetpack Compose & Kotlin",
                category = "Development",
                description = "Master declarative UI with Jetpack Compose, Kotlin Coroutines, StateFlow, Clean Architecture, and Material 3 design patterns from fundamentals to advanced app deployment.",
                instructor = "Dr. Sarah Chen",
                instructorTitle = "Lead Mobile Architect",
                instructorBio = "Author of Modern Android Engineering, Google Developer Expert in Android, with 12+ years experience building apps loved by millions.",
                durationHours = 8.5,
                rating = 4.9f,
                reviewCount = 2840,
                studentsEnrolled = 6420,
                coverGradientStart = 0xFF7C3AED,
                coverGradientEnd = 0xFF4F46E5,
                iconName = "android",
                isPublished = true,
                lessons = listOf(
                    Lesson(
                        id = "lesson-and-1",
                        courseId = "course-android",
                        order = 1,
                        title = "1. Introduction to Compose & Declarative UI",
                        type = LessonContentType.VIDEO,
                        durationMinutes = 18,
                        videoUrl = "https://example.com/videos/compose-intro",
                        content = "Explore why Jetpack Compose revolutionizes Android UI development. We break down the mental shift from imperative XML Views to unidirectional data flow (UDF) in Compose.",
                        codeSnippet = "@Composable\nfun GreetingCard(name: String) {\n    Card(\n        modifier = Modifier.padding(16.dp),\n        elevation = CardDefaults.cardElevation(4.dp)\n    ) {\n        Text(\n            text = \"Welcome to LearnPath, \${'$'}name!\",\n            style = MaterialTheme.typography.titleMedium\n        )\n    }\n}",
                        keyTakeaways = listOf(
                            "Compose uses Kotlin functions annotated with @Composable",
                            "Data flows down via parameters; events flow up via lambda callbacks",
                            "Recomposition only updates parts of the UI whose inputs change"
                        )
                    ),
                    Lesson(
                        id = "lesson-and-2",
                        courseId = "course-android",
                        order = 2,
                        title = "2. Layouts, Modifiers, and Box/Row/Column",
                        type = LessonContentType.VIDEO,
                        durationMinutes = 24,
                        videoUrl = "https://example.com/videos/layouts",
                        content = "Learn how to orchestrate Box, Row, Column, and Spacer components with precision. Understand modifier chaining order and how constraints propagate through the layout tree.",
                        codeSnippet = "Row(\n    modifier = Modifier\n        .fillMaxWidth()\n        .padding(16.dp),\n    horizontalArrangement = Arrangement.SpaceBetween,\n    verticalAlignment = Alignment.CenterVertically\n) {\n    Text(\"Course Progress\", style = MaterialTheme.typography.bodyLarge)\n    Badge { Text(\"85%\") }\n}",
                        keyTakeaways = listOf(
                            "Modifier order matters: padding before background changes the click target",
                            "Box allows overlapping layers with Alignment.TopEnd / Center",
                            "LazyColumn is used for high-performance scrollable lists"
                        )
                    ),
                    Lesson(
                        id = "lesson-and-3",
                        courseId = "course-android",
                        order = 3,
                        title = "3. State Management with ViewModel & StateFlow",
                        type = LessonContentType.ARTICLE,
                        durationMinutes = 20,
                        videoUrl = "",
                        content = "State is the heart of every modern app. In this module, discover how to preserve state across configuration changes using ViewModel and expose immutable UI State via StateFlow and collectAsStateWithLifecycle.",
                        codeSnippet = "class CourseViewModel : ViewModel() {\n    private val _uiState = MutableStateFlow<CourseUiState>(CourseUiState.Loading)\n    val uiState: StateFlow<CourseUiState> = _uiState.asStateFlow()\n}",
                        keyTakeaways = listOf(
                            "Always expose StateFlow instead of MutableStateFlow to the UI",
                            "Use collectAsStateWithLifecycle to conserve resources when the app is backgrounded",
                            "RememberSaveable survives process death for light input states"
                        )
                    ),
                    Lesson(
                        id = "lesson-and-4",
                        courseId = "course-android",
                        order = 4,
                        title = "4. Material Design 3 Theming & Dynamic Colors",
                        type = LessonContentType.VIDEO,
                        durationMinutes = 22,
                        videoUrl = "https://example.com/videos/m3-theming",
                        content = "Deep dive into Material 3 color schemes, typography scales, shape tokens, and surface elevation to create an app aesthetic that feels native, modern, and delightful.",
                        codeSnippet = "MaterialTheme(\n    colorScheme = LightColorScheme,\n    typography = AppTypography,\n    shapes = AppShapes,\n    content = content\n)",
                        keyTakeaways = listOf(
                            "Use semantic color tokens like primary, surface, and outline",
                            "Always respect minimum touch targets of 48.dp",
                            "Handle edge-to-edge window insets cleanly"
                        )
                    ),
                    Lesson(
                        id = "lesson-and-5",
                        courseId = "course-android",
                        order = 5,
                        title = "5. Asynchronous Coroutines & Flow Pipelines",
                        type = LessonContentType.CODE_LAB,
                        durationMinutes = 28,
                        videoUrl = "",
                        content = "Handle network queries, background computations, and debounced search fields cleanly using CoroutineScope and StateFlow operators like debounce, map, and filter.",
                        codeSnippet = "val searchResults = searchQuery\n    .debounce(300)\n    .distinctUntilChanged()\n    .mapLatest { query -> repository.searchCourses(query) }\n    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())",
                        keyTakeaways = listOf(
                            "Dispatchers.IO for networking/disk operations",
                            "Dispatchers.Main for UI thread interaction",
                            "Structured concurrency ensures jobs cancel when screens detach"
                        )
                    ),
                    Lesson(
                        id = "lesson-and-6",
                        courseId = "course-android",
                        order = 6,
                        title = "6. Building Responsive & Adaptive Multiplatform Layouts",
                        type = LessonContentType.VIDEO,
                        durationMinutes = 25,
                        videoUrl = "https://example.com/videos/adaptive-ui",
                        content = "Design adaptive layouts that automatically transition from single-column mobile viewports to split list-detail panes and navigation rails on tablets and desktop screens.",
                        codeSnippet = "BoxWithConstraints {\n    if (maxWidth < 600.dp) {\n        CompactMobileLayout()\n    } else {\n        ExpandedTabletSplitLayout()\n    }\n}",
                        keyTakeaways = listOf(
                            "Design fluid layouts using BoxWithConstraints and WindowSizeClass",
                            "Use NavigationRail for wide horizontal aspect ratios",
                            "List-Detail layout shows content side-by-side on large screens"
                        )
                    )
                ),
                quiz = Quiz(
                    id = "quiz-and-1",
                    courseId = "course-android",
                    title = "Android Jetpack Compose Certification Exam",
                    passThresholdPercentage = 75,
                    questions = listOf(
                        QuizQuestion(
                            id = "q1",
                            questionText = "Which annotation designates a Kotlin function as part of the Jetpack Compose UI tree?",
                            options = listOf("@Composable", "@ComposeView", "@Layout", "@UIComponent"),
                            correctOptionIndex = 0,
                            explanation = "@Composable informs the Compose compiler that this function transforms data into UI nodes."
                        ),
                        QuizQuestion(
                            id = "q2",
                            questionText = "What is the recommended way to collect StateFlow safely in Compose according to official Android guidelines?",
                            options = listOf(
                                "collectAsStateWithLifecycle()",
                                "Flow.collect() inside runBlocking",
                                "GlobalScope.launch()",
                                "LiveData.observeForever()"
                            ),
                            correctOptionIndex = 0,
                            explanation = "collectAsStateWithLifecycle automatically halts collection when the Android Lifecycle falls below Started."
                        ),
                        QuizQuestion(
                            id = "q3",
                            questionText = "Which component is best suited for rendering thousands of scrollable course cards efficiently?",
                            options = listOf("Column with verticalScroll", "LazyColumn", "ScrollView", "ListView"),
                            correctOptionIndex = 1,
                            explanation = "LazyColumn recycles and only composes visible items, maintaining high 60/120 FPS performance."
                        ),
                        QuizQuestion(
                            id = "q4",
                            questionText = "What is the primary architectural benefit of Unidirectional Data Flow (UDF)?",
                            options = listOf(
                                "State flows down and events flow up, eliminating synchronization bugs",
                                "It forces all code into a single file",
                                "It replaces the need for any ViewModel",
                                "It turns all UI elements into static bitmaps"
                            ),
                            correctOptionIndex = 0,
                            explanation = "UDF decouples state producers from consumers, making testing and debugging predictable."
                        ),
                        QuizQuestion(
                            id = "q5",
                            questionText = "What minimum touch target size is recommended by Material 3 for accessible interactive components?",
                            options = listOf("24dp x 24dp", "36dp x 36dp", "48dp x 48dp", "64dp x 64dp"),
                            correctOptionIndex = 2,
                            explanation = "48dp x 48dp satisfies standard mobile accessibility guidelines for thumb and finger touch targets."
                        )
                    )
                )
            ),
            Course(
                id = "course-design",
                title = "UI/UX Design Systems & Figma Mastery",
                category = "Design",
                description = "Build scalable enterprise design systems from scratch. Master design tokens, auto-layout, atomic component libraries, responsive breakpoints, and WCAG accessibility standards.",
                instructor = "Marcus Vance",
                instructorTitle = "Principal Design Director",
                instructorBio = "Former lead designer at Stripe and Figma community advocate. Passionate about bridging design and code.",
                durationHours = 6.0,
                rating = 4.8f,
                reviewCount = 1890,
                studentsEnrolled = 4800,
                coverGradientStart = 0xFFEC4899,
                coverGradientEnd = 0xFF8B5CF6,
                iconName = "design",
                isPublished = true,
                lessons = listOf(
                    Lesson(
                        id = "lesson-des-1",
                        courseId = "course-design",
                        order = 1,
                        title = "1. Design Tokens: Colors, Typography & Elevation",
                        type = LessonContentType.VIDEO,
                        durationMinutes = 20,
                        content = "Discover how to structure semantic tokens that translate seamlessly between Figma styles and Jetpack Compose/CSS themes."
                    ),
                    Lesson(
                        id = "lesson-des-2",
                        courseId = "course-design",
                        order = 2,
                        title = "2. Auto-Layout Mastery & Responsive Constraints",
                        type = LessonContentType.VIDEO,
                        durationMinutes = 25,
                        content = "Harness fluid resizing, Hug and Fill constraints, and nested flex containers to design screens that adapt to any screen size."
                    ),
                    Lesson(
                        id = "lesson-des-3",
                        courseId = "course-design",
                        order = 3,
                        title = "3. Micro-Interactions & Prototyping",
                        type = LessonContentType.ARTICLE,
                        durationMinutes = 18,
                        content = "Create smooth state transitions, hover effects, and animated loading indicators that delight end users."
                    )
                ),
                quiz = Quiz(
                    id = "quiz-des-1",
                    courseId = "course-design",
                    title = "Design Systems Certification Quiz",
                    passThresholdPercentage = 70,
                    questions = listOf(
                        QuizQuestion(
                            id = "qd1",
                            questionText = "What is the primary role of a semantic design token like 'color.primary'?",
                            options = listOf(
                                "To assign meaning to a raw value, enabling systematic theme changes",
                                "To lock colors permanently without change",
                                "To compress images automatically",
                                "To generate CSS files directly"
                            ),
                            correctOptionIndex = 0,
                            explanation = "Semantic tokens decouple visual intent from specific hex codes."
                        ),
                        QuizQuestion(
                            id = "qd2",
                            questionText = "What contrast ratio is required by WCAG AA for normal body text against its background?",
                            options = listOf("3:1", "4.5:1", "7:1", "10:1"),
                            correctOptionIndex = 1,
                            explanation = "WCAG 2.1 AA mandates at least 4.5:1 contrast for regular text."
                        )
                    )
                )
            ),
            Course(
                id = "course-ai",
                title = "Full-Stack AI Engineering with Python & APIs",
                category = "Cloud & AI",
                description = "Learn to build production-grade AI applications using modern LLMs, function calling, vector embeddings, retrieval augmented generation (RAG), and FastAPI backends.",
                instructor = "Elena Rostova",
                instructorTitle = "Chief AI Research Scientist",
                instructorBio = "AI researcher with publications in NeurIPS and builder of multi-agent distributed systems.",
                durationHours = 9.0,
                rating = 4.95f,
                reviewCount = 3120,
                studentsEnrolled = 7890,
                coverGradientStart = 0xFF10B981,
                coverGradientEnd = 0xFF059669,
                iconName = "psychology",
                isPublished = true,
                lessons = listOf(
                    Lesson(
                        id = "lesson-ai-1",
                        courseId = "course-ai",
                        order = 1,
                        title = "1. LLM Fundamentals & Prompt Architecture",
                        type = LessonContentType.VIDEO,
                        durationMinutes = 22,
                        content = "Understand tokenization, context windows, few-shot prompting, and structured JSON output generation."
                    ),
                    Lesson(
                        id = "lesson-ai-2",
                        courseId = "course-ai",
                        order = 2,
                        title = "2. Tool Use & Function Calling Workflows",
                        type = LessonContentType.CODE_LAB,
                        durationMinutes = 30,
                        content = "Connect language models to real-world APIs and databases using function calling declarations and schema validation."
                    )
                ),
                quiz = Quiz(
                    id = "quiz-ai-1",
                    courseId = "course-ai",
                    title = "AI Engineering Principles Assessment",
                    passThresholdPercentage = 80,
                    questions = listOf(
                        QuizQuestion(
                            id = "qa1",
                            questionText = "What is the primary function of Tool Calling in modern LLMs?",
                            options = listOf(
                                "To empower the model to invoke external APIs and retrieve deterministic data",
                                "To make the model run 10x faster on CPU",
                                "To replace neural network weights",
                                "To convert code into assembly"
                            ),
                            correctOptionIndex = 0,
                            explanation = "Tool calling bridges creative reasoning with real-world databases and services."
                        )
                    )
                )
            ),
            Course(
                id = "course-data",
                title = "Data Science & Predictive Analytics Bootcamp",
                category = "Data Science",
                description = "Transform complex datasets into actionable business intelligence using Pandas, NumPy, Scikit-learn, and interactive data visualization dashboards.",
                instructor = "Prof. David Kim",
                instructorTitle = "Associate Professor of Computer Science",
                instructorBio = "Data Science author and consultant for Fortune 500 analytics teams.",
                durationHours = 7.5,
                rating = 4.7f,
                reviewCount = 940,
                studentsEnrolled = 2100,
                coverGradientStart = 0xFF3B82F6,
                coverGradientEnd = 0xFF1D4ED8,
                iconName = "bar_chart",
                isPublished = true,
                lessons = listOf(
                    Lesson(
                        id = "lesson-dat-1",
                        courseId = "course-data",
                        order = 1,
                        title = "1. Data Cleaning & Feature Engineering with Pandas",
                        type = LessonContentType.VIDEO,
                        durationMinutes = 26,
                        content = "Master handling missing data, outlier detection, category encoding, and date-time aggregations."
                    )
                )
            ),
            Course(
                id = "course-biz",
                title = "Product Management & Agile Product Leadership",
                category = "Business",
                description = "Master customer journey mapping, backlog prioritization, OKRs, unit economics, and leading cross-functional engineering teams to ship high-impact products.",
                instructor = "Maya Patel",
                instructorTitle = "VP of Product Strategy",
                instructorBio = "15 years shaping consumer and enterprise tech products from 0 to 1 and scaling to IPO.",
                durationHours = 5.0,
                rating = 4.85f,
                reviewCount = 1420,
                studentsEnrolled = 3300,
                coverGradientStart = 0xFFF59E0B,
                coverGradientEnd = 0xFFD97706,
                iconName = "business_center",
                isPublished = true,
                lessons = listOf(
                    Lesson(
                        id = "lesson-biz-1",
                        courseId = "course-biz",
                        order = 1,
                        title = "1. Crafting Vision & OKRs that Drive Outcomes",
                        type = LessonContentType.VIDEO,
                        durationMinutes = 20,
                        content = "Learn how to define objective key results that align engineering, design, and executive stakeholders."
                    )
                )
            )
        )
    }

    private fun createSeedEnrollments(): Map<String, Enrollment> {
        val now = System.currentTimeMillis()
        val threeDaysAgo = now - 3 * 24 * 60 * 60 * 1000L
        val oneWeekAgo = now - 7 * 24 * 60 * 60 * 1000L

        return mapOf(
            // Modern Android course: 6/6 lessons completed, quiz passed 100%, completed course!
            "course-android" to Enrollment(
                courseId = "course-android",
                enrolledAtMillis = oneWeekAgo,
                completedLessonIds = setOf(
                    "lesson-and-1",
                    "lesson-and-2",
                    "lesson-and-3",
                    "lesson-and-4",
                    "lesson-and-5",
                    "lesson-and-6"
                ),
                quizScorePercent = 100,
                isCompleted = true,
                completedAtMillis = now - 1 * 24 * 60 * 60 * 1000L
            ),
            // UI/UX Design course: in progress (2 of 3 lessons done)
            "course-design" to Enrollment(
                courseId = "course-design",
                enrolledAtMillis = threeDaysAgo,
                completedLessonIds = setOf("lesson-des-1", "lesson-des-2"),
                quizScorePercent = null,
                isCompleted = false,
                completedAtMillis = null
            )
        )
    }

    private fun createSeedStudentRecords(): List<StudentRecord> {
        return listOf(
            StudentRecord(
                studentId = "stu-current",
                studentName = "Alex Morgan",
                studentEmail = "alex.morgan@learnpath.io",
                courseId = "course-android",
                enrolledDate = "Oct 15, 2026",
                completedLessonsCount = 6,
                totalLessonsCount = 6,
                quizScorePercent = 100,
                isCompleted = true,
                lastActive = "1 hour ago"
            ),
            StudentRecord(
                studentId = "stu-current",
                studentName = "Alex Morgan",
                studentEmail = "alex.morgan@learnpath.io",
                courseId = "course-design",
                enrolledDate = "Oct 19, 2026",
                completedLessonsCount = 2,
                totalLessonsCount = 3,
                quizScorePercent = null,
                isCompleted = false,
                lastActive = "Yesterday"
            ),
            StudentRecord(
                studentId = "stu-002",
                studentName = "Sophia Chen",
                studentEmail = "sophia.chen@example.com",
                courseId = "course-android",
                enrolledDate = "Oct 10, 2026",
                completedLessonsCount = 6,
                totalLessonsCount = 6,
                quizScorePercent = 95,
                isCompleted = true,
                lastActive = "3 hours ago"
            ),
            StudentRecord(
                studentId = "stu-003",
                studentName = "Liam Walker",
                studentEmail = "liam.w@company.com",
                courseId = "course-android",
                enrolledDate = "Oct 12, 2026",
                completedLessonsCount = 4,
                totalLessonsCount = 6,
                quizScorePercent = null,
                isCompleted = false,
                lastActive = "Today"
            ),
            StudentRecord(
                studentId = "stu-004",
                studentName = "Olivia Martinez",
                studentEmail = "olivia.m@tech.org",
                courseId = "course-android",
                enrolledDate = "Oct 08, 2026",
                completedLessonsCount = 5,
                totalLessonsCount = 6,
                quizScorePercent = 80,
                isCompleted = false,
                lastActive = "4 days ago"
            ),
            StudentRecord(
                studentId = "stu-005",
                studentName = "Noah Jackson",
                studentEmail = "noah.j@codebase.io",
                courseId = "course-android",
                enrolledDate = "Oct 14, 2026",
                completedLessonsCount = 2,
                totalLessonsCount = 6,
                quizScorePercent = null,
                isCompleted = false,
                lastActive = "Yesterday"
            ),
            StudentRecord(
                studentId = "stu-006",
                studentName = "Emma Watson",
                studentEmail = "emma.w@designlab.com",
                courseId = "course-design",
                enrolledDate = "Oct 11, 2026",
                completedLessonsCount = 3,
                totalLessonsCount = 3,
                quizScorePercent = 90,
                isCompleted = true,
                lastActive = "2 days ago"
            )
        )
    }
}
