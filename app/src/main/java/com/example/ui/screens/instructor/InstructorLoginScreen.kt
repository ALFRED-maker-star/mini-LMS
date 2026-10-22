package com.example.ui.screens.instructor

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CastForEducation
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun InstructorLoginScreen(
    onLoginSuccess: (email: String, name: String) -> Unit,
    onBackToStudent: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("sarah.chen@learnpath.edu") }
    var password by remember { mutableStateOf("••••••••") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LearnBackground)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("instructor_login_card"),
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
                // Header Emblem
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(LearnGreenContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CastForEducation,
                        contentDescription = "Instructor Portal",
                        tint = LearnGreen,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Instructor Studio",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Manage your courses, curriculum, quizzes, and track student outcomes.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = LearnTextSecondary,
                        fontSize = 13.sp
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Email field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Instructor Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = LearnViolet) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("instructor_email_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LearnViolet,
                        unfocusedBorderColor = LearnBorder
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = LearnViolet) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("instructor_password_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LearnViolet,
                        unfocusedBorderColor = LearnBorder
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Primary Login Button
                Button(
                    onClick = {
                        val name = if (email.contains("sarah", ignoreCase = true)) "Dr. Sarah Chen" else "Instructor"
                        onLoginSuccess(email, name)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("instructor_sign_in_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LearnGreen)
                ) {
                    Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign In to Studio", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = LearnBorder)
                Spacer(modifier = Modifier.height(16.dp))

                // Quick Demo Login Shortcut
                Text(
                    text = "Quick Demo Access:",
                    style = MaterialTheme.typography.labelSmall.copy(color = LearnTextSecondary)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        onLoginSuccess("sarah.chen@learnpath.edu", "Dr. Sarah Chen")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quick_instructor_demo_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Continue as Dr. Sarah Chen (Lead Instructor)", color = LearnViolet, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Return to student view
                OutlinedButton(
                    onClick = onBackToStudent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("switch_to_student_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.School, contentDescription = null, tint = LearnTextSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Return to Student Mode", color = LearnTextSecondary)
                }
            }
        }
    }
}
