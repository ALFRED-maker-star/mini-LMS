package com.example.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Course
import com.example.data.model.Enrollment
import com.example.ui.theme.LearnAmber
import com.example.ui.theme.LearnBackground
import com.example.ui.theme.LearnBorder
import com.example.ui.theme.LearnGreen
import com.example.ui.theme.LearnGreenContainer
import com.example.ui.theme.LearnOnGreenContainer
import com.example.ui.theme.LearnSurface
import com.example.ui.theme.LearnSurfaceVariant
import com.example.ui.theme.LearnTextPrimary
import com.example.ui.theme.LearnTextSecondary
import com.example.ui.theme.LearnViolet
import com.example.ui.theme.LearnVioletContainer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CertificateScreen(
    course: Course,
    enrollment: Enrollment?,
    studentName: String,
    onDownloadCertificate: () -> Unit,
    onShareCertificate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val completionDate = if (enrollment?.completedAtMillis != null) {
        SimpleDateFormat("MMMM dd, yyyy", Locale.US).format(Date(enrollment.completedAtMillis))
    } else {
        SimpleDateFormat("MMMM dd, yyyy", Locale.US).format(Date())
    }

    val certId = "LP-" + course.id.takeLast(4).uppercase() + "-" + (enrollment?.completedAtMillis ?: 1729000000000L).toString().takeLast(6)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LearnBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Status Callout
        Surface(
            color = LearnGreenContainer,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.Verified, contentDescription = null, tint = LearnGreen, modifier = Modifier.size(16.dp))
                Text(
                    text = "Official Verified Certificate",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = LearnOnGreenContainer,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        // Printable Certificate Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("certificate_preview_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LearnSurface),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            // Elegant Ornamental Border
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .border(width = 2.dp, color = LearnViolet, shape = RoundedCornerShape(12.dp))
                    .padding(4.dp)
                    .border(width = 1.dp, color = LearnAmber.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Academy Emblem
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(LearnViolet),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                        Text(
                            text = "LearnPath Academy",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = LearnViolet,
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "CERTIFICATE OF COMPLETION",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = LearnTextPrimary,
                            letterSpacing = 2.sp,
                            fontSize = 18.sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "This certificate is proudly awarded to",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontStyle = FontStyle.Italic,
                            color = LearnTextSecondary
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Student Name
                    Text(
                        text = studentName,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = LearnViolet,
                            letterSpacing = 0.5.sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.width(180.dp).height(2.dp).background(LearnAmber))
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "for successfully demonstrating mastery and completing all requirements for",
                        style = MaterialTheme.typography.bodySmall.copy(color = LearnTextSecondary),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Course Title
                    Text(
                        text = course.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = LearnTextPrimary
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = LearnBorder)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Signatures & Seal Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Date column
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = completionDate,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Box(modifier = Modifier.width(100.dp).height(1.dp).background(Color.Gray))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Date of Issue",
                                style = MaterialTheme.typography.labelSmall.copy(color = LearnTextSecondary, fontSize = 10.sp)
                            )
                        }

                        // Gold Seal Emblem in Middle
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(LearnAmber, Color(0xFFB45309))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.EmojiEvents,
                                contentDescription = "Seal",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        // Instructor signature column
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = course.instructor,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontStyle = FontStyle.Italic
                                )
                            )
                            Box(modifier = Modifier.width(110.dp).height(1.dp).background(Color.Gray))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Lead Instructor",
                                style = MaterialTheme.typography.labelSmall.copy(color = LearnTextSecondary, fontSize = 10.sp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Certificate ID & Security Code
                    Surface(
                        color = LearnSurfaceVariant,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Credential ID: $certId • Verified by LearnPath",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = LearnTextSecondary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons: Download & Share
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onDownloadCertificate,
                modifier = Modifier
                    .weight(1f)
                    .testTag("download_certificate_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LearnViolet)
            ) {
                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Download", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = onShareCertificate,
                modifier = Modifier
                    .weight(1f)
                    .testTag("share_certificate_button"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, tint = LearnViolet, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Share", color = LearnViolet, fontWeight = FontWeight.Bold)
            }
        }
    }
}
