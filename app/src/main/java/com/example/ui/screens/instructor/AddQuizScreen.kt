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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.model.QuizQuestion
import com.example.ui.theme.LearnBackground
import com.example.ui.theme.LearnBorder
import com.example.ui.theme.LearnGreen
import com.example.ui.theme.LearnGreenContainer
import com.example.ui.theme.LearnOnGreenContainer
import com.example.ui.theme.LearnRed
import com.example.ui.theme.LearnSurface
import com.example.ui.theme.LearnSurfaceVariant
import com.example.ui.theme.LearnTextPrimary
import com.example.ui.theme.LearnTextSecondary
import com.example.ui.theme.LearnViolet
import com.example.ui.theme.LearnVioletContainer
import java.util.UUID

@Composable
fun AddQuizScreen(
    course: Course,
    onSaveQuiz: (Quiz) -> Unit,
    modifier: Modifier = Modifier
) {
    val existingQuiz = course.quiz
    var quizTitle by remember { mutableStateOf(existingQuiz?.title ?: "${course.title} Comprehensive Exam") }
    var passThreshold by remember { mutableFloatStateOf(existingQuiz?.passThresholdPercentage?.toFloat() ?: 75f) }

    val questionsList = remember {
        mutableStateListOf<QuizQuestion>().apply {
            if (existingQuiz != null) {
                addAll(existingQuiz.questions)
            }
        }
    }

    // New question draft fields
    var newQuestionText by remember { mutableStateOf("") }
    var opt0 by remember { mutableStateOf("") }
    var opt1 by remember { mutableStateOf("") }
    var opt2 by remember { mutableStateOf("") }
    var opt3 by remember { mutableStateOf("") }
    var correctIndex by remember { mutableIntStateOf(0) }
    var explanationText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LearnBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Quiz & Assessment Builder",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Course: ${course.title}",
            style = MaterialTheme.typography.bodySmall.copy(color = LearnViolet, fontWeight = FontWeight.SemiBold)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Quiz General Settings Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LearnSurface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = quizTitle,
                    onValueChange = { quizTitle = it },
                    label = { Text("Quiz Title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quiz_title_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LearnViolet,
                        unfocusedBorderColor = LearnBorder
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Passing Threshold",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Surface(
                        color = LearnGreenContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${passThreshold.toInt()}% Required to Pass",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = LearnOnGreenContainer
                            )
                        )
                    }
                }

                Slider(
                    value = passThreshold,
                    onValueChange = { passThreshold = it },
                    valueRange = 50f..100f,
                    steps = 9,
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = LearnGreen,
                        activeTrackColor = LearnGreen,
                        inactiveTrackColor = LearnBorder
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Existing Questions Preview List
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Quiz Questions (${questionsList.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        questionsList.forEachIndexed { index, q ->
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
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${index + 1}. ${q.questionText}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Correct Answer: Option ${('A' + q.correctOptionIndex)} (${q.options.getOrNull(q.correctOptionIndex)})",
                            style = MaterialTheme.typography.labelSmall.copy(color = LearnGreen, fontWeight = FontWeight.SemiBold)
                        )
                    }

                    IconButton(
                        onClick = { questionsList.removeAt(index) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete question", tint = LearnRed, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Question Builder Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LearnSurface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = LearnViolet, modifier = Modifier.size(18.dp))
                    Text(
                        text = "Add New Question",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = LearnViolet)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = newQuestionText,
                    onValueChange = { newQuestionText = it },
                    label = { Text("Question Prompt") },
                    placeholder = { Text("e.g. Which lifecycle method is used for cleanup?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("question_prompt_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LearnViolet,
                        unfocusedBorderColor = LearnBorder
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Multiple Choice Options (Select radio for correct answer):",
                    style = MaterialTheme.typography.labelSmall.copy(color = LearnTextSecondary)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Option inputs A, B, C, D
                val optionHolders = listOf(
                    Pair(opt0) { v: String -> opt0 = v },
                    Pair(opt1) { v: String -> opt1 = v },
                    Pair(opt2) { v: String -> opt2 = v },
                    Pair(opt3) { v: String -> opt3 = v }
                )

                optionHolders.forEachIndexed { i, holder ->
                    val letter = ('A' + i).toString()
                    val isCorrect = correctIndex == i

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { correctIndex = i },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isCorrect) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                contentDescription = "Mark as correct",
                                tint = if (isCorrect) LearnGreen else LearnBorder
                            )
                        }

                        OutlinedTextField(
                            value = holder.first,
                            onValueChange = holder.second,
                            placeholder = { Text("Option $letter") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("option_${i}_input"),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (isCorrect) LearnGreen else LearnViolet,
                                unfocusedBorderColor = if (isCorrect) LearnGreen else LearnBorder
                            ),
                            singleLine = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = explanationText,
                    onValueChange = { explanationText = it },
                    label = { Text("Explanation (Optional)") },
                    placeholder = { Text("Explain why this answer is correct...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LearnViolet,
                        unfocusedBorderColor = LearnBorder
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Button to add this question
                OutlinedButton(
                    onClick = {
                        val opts = listOf(
                            opt0.ifBlank { "Option A" },
                            opt1.ifBlank { "Option B" },
                            opt2.ifBlank { "Option C" },
                            opt3.ifBlank { "Option D" }
                        )
                        val qPrompt = newQuestionText.ifBlank { "Question ${questionsList.size + 1}" }
                        questionsList.add(
                            QuizQuestion(
                                id = "q-" + UUID.randomUUID().toString().take(6),
                                questionText = qPrompt,
                                options = opts,
                                correctOptionIndex = correctIndex,
                                explanation = explanationText.ifBlank { "Correctly selected option ${('A' + correctIndex)}." }
                            )
                        )

                        // Reset question inputs
                        newQuestionText = ""
                        opt0 = ""
                        opt1 = ""
                        opt2 = ""
                        opt3 = ""
                        correctIndex = 0
                        explanationText = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_question_to_list_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = LearnViolet, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Question to Quiz", color = LearnViolet, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Save Whole Quiz Button
        Button(
            onClick = {
                val finalQuiz = Quiz(
                    id = existingQuiz?.id ?: ("quiz-" + UUID.randomUUID().toString().take(6)),
                    courseId = course.id,
                    title = quizTitle.ifBlank { "${course.title} Quiz" },
                    passThresholdPercentage = passThreshold.toInt(),
                    questions = questionsList.toList()
                )
                onSaveQuiz(finalQuiz)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("save_quiz_button"),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LearnGreen)
        ) {
            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save Quiz (${questionsList.size} Questions)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}
