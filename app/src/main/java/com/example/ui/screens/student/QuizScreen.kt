package com.example.ui.screens.student

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Course
import com.example.data.model.Quiz
import com.example.ui.theme.LearnBackground
import com.example.ui.theme.LearnBorder
import com.example.ui.theme.LearnGreen
import com.example.ui.theme.LearnGreenContainer
import com.example.ui.theme.LearnOnGreenContainer
import com.example.ui.theme.LearnOnRedContainer
import com.example.ui.theme.LearnOnVioletContainer
import com.example.ui.theme.LearnRed
import com.example.ui.theme.LearnRedContainer
import com.example.ui.theme.LearnSurface
import com.example.ui.theme.LearnSurfaceVariant
import com.example.ui.theme.LearnTextPrimary
import com.example.ui.theme.LearnTextSecondary
import com.example.ui.theme.LearnViolet
import com.example.ui.theme.LearnVioletContainer
import com.example.ui.viewmodel.QuizSession

@Composable
fun QuizScreen(
    course: Course,
    quizSession: QuizSession,
    onSelectAnswer: (questionId: String, optionIndex: Int) -> Unit,
    onNextQuestion: () -> Unit,
    onPreviousQuestion: () -> Unit,
    onSubmitQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    val quiz = course.quiz ?: return
    val questions = quiz.questions
    val currentIndex = quizSession.currentQuestionIndex.coerceIn(0, (questions.size - 1).coerceAtLeast(0))
    val currentQuestion = questions.getOrNull(currentIndex)
    val total = questions.size

    val progress = if (total > 0) (currentIndex + 1).toFloat() / total else 0f
    val answeredCount = quizSession.selectedAnswers.size

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LearnBackground)
            .padding(16.dp)
    ) {
        // Quiz Header & Progress Indicator
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LearnSurface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = quiz.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Question ${currentIndex + 1} of $total",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = LearnViolet
                        )
                    )
                    Text(
                        text = "$answeredCount answered",
                        style = MaterialTheme.typography.labelSmall.copy(color = LearnTextSecondary)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = LearnViolet,
                    trackColor = LearnBorder
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Question Card
        if (currentQuestion != null) {
            val selectedOptionIndex = quizSession.selectedAnswers[currentQuestion.id]

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LearnSurface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    item {
                        Surface(
                            color = LearnVioletContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "MULTIPLE CHOICE",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = LearnOnVioletContainer,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = currentQuestion.questionText,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = LearnTextPrimary,
                                lineHeight = 28.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // Options List
                    itemsIndexed(currentQuestion.options) { optionIdx, optionText ->
                        val isSelected = selectedOptionIndex == optionIdx

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) LearnViolet else LearnBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onSelectAnswer(currentQuestion.id, optionIdx) }
                                .testTag("quiz_option_${currentQuestion.id}_$optionIdx"),
                            color = if (isSelected) LearnVioletContainer.copy(alpha = 0.5f) else LearnSurface,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) LearnViolet else LearnSurfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val letter = ('A' + optionIdx).toString()
                                    Text(
                                        text = letter,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else LearnTextSecondary
                                        )
                                    )
                                }

                                Text(
                                    text = optionText,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = if (isSelected) LearnOnVioletContainer else LearnTextPrimary,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                    ),
                                    modifier = Modifier.weight(1f)
                                )

                                Icon(
                                    imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (isSelected) LearnViolet else LearnBorder
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation & Submission Action Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onPreviousQuestion,
                enabled = currentIndex > 0,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("quiz_prev_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Previous")
            }

            if (currentIndex < total - 1) {
                Button(
                    onClick = onNextQuestion,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LearnViolet),
                    modifier = Modifier.testTag("quiz_next_button")
                ) {
                    Text("Next Question")
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            } else {
                Button(
                    onClick = onSubmitQuiz,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LearnGreen),
                    modifier = Modifier.testTag("submit_quiz_button")
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Submit Quiz", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun QuizResultScreen(
    course: Course,
    score: Int,
    total: Int,
    passed: Boolean,
    onRetakeQuiz: () -> Unit,
    onViewCertificate: () -> Unit,
    onBackToCourse: () -> Unit,
    modifier: Modifier = Modifier
) {
    val quiz = course.quiz ?: return
    val passThreshold = quiz.passThresholdPercentage

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LearnBackground)
            .padding(16.dp)
    ) {
        // Result Summary Hero
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = LearnSurface),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Status Badge Icon
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(if (passed) LearnGreenContainer else LearnRedContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (passed) Icons.Default.EmojiEvents else Icons.Default.Cancel,
                            contentDescription = if (passed) "Passed" else "Failed",
                            tint = if (passed) LearnGreen else LearnRed,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (passed) "Congratulations! You Passed!" else "Needs Improvement",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (passed) LearnGreen else LearnRed
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (passed)
                            "You demonstrated strong proficiency in ${course.title}."
                        else
                            "You didn't reach the passing threshold of $passThreshold%. Review the material and try again!",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = LearnTextSecondary
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Score Callout Box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = if (passed) LearnGreenContainer.copy(alpha = 0.5f) else LearnRedContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Your Score", style = MaterialTheme.typography.labelSmall.copy(color = LearnTextSecondary))
                                Text(
                                    text = "$score%",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (passed) LearnGreen else LearnRed
                                    )
                                )
                            }

                            Box(modifier = Modifier.width(1.dp).height(36.dp).background(LearnBorder))

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Pass Threshold", style = MaterialTheme.typography.labelSmall.copy(color = LearnTextSecondary))
                                Text(
                                    text = "$passThreshold%",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = LearnTextPrimary
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action Buttons
                    if (passed) {
                        Button(
                            onClick = onViewCertificate,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("claim_certificate_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LearnGreen)
                        ) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Claim Official Certificate", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onRetakeQuiz,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("retake_quiz_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Retake Quiz")
                        }

                        Button(
                            onClick = onBackToCourse,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("back_to_course_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LearnViolet)
                        ) {
                            Text("Back to Course")
                        }
                    }
                }
            }
        }

        // Question Answers Review Header
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Detailed Question Breakdown",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // List each question with explanation
        itemsIndexed(quiz.questions) { idx, q ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = LearnSurface),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "${idx + 1}. ${q.questionText}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Correct answer indicator
                    Surface(
                        color = LearnGreenContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = LearnGreen, modifier = Modifier.size(14.dp))
                            Text(
                                text = "Correct: ${q.options.getOrNull(q.correctOptionIndex) ?: ""}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = LearnOnGreenContainer
                                )
                            )
                        }
                    }

                    if (q.explanation.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = q.explanation,
                            style = MaterialTheme.typography.bodySmall.copy(color = LearnTextSecondary)
                        )
                    }
                }
            }
        }
    }
}
