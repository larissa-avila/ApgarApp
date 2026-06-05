package com.example.apgarapp.ui.screens

import android.content.Intent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.apgarapp.model.*
import com.example.apgarapp.ui.theme.*
import com.example.apgarapp.viewmodel.ApgarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    score: Int,
    viewModel: ApgarViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToCalculator: () -> Unit
) {
    val classification = getApgarClassification(score)
    val context = LocalContext.current

    // Show clinical alert automatically for severe scores
    var showClinicalAlert by remember { mutableStateOf(score < 4) }

    val (primaryColor, bgGradient) = when (classification) {
        ApgarClassification.NORMAL -> Pair(ScoreGood, listOf(ScoreGoodDark, ScoreGood))
        ApgarClassification.MODERATE -> Pair(ScoreModerate, listOf(ScoreModerateDark, ScoreModerate))
        ApgarClassification.SEVERE -> Pair(ScoreCritical, listOf(ScoreCriticalDark, ScoreCritical))
    }

    val animatedFloat by animateFloatAsState(
        targetValue = score / 10f,
        animationSpec = tween(1400, easing = FastOutSlowInEasing),
        label = "progress"
    )

    // Pulsing ring for severe
    val infiniteTransition = rememberInfiniteTransition(label = "alertPulse")
    val alertPulse by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = if (score < 4) 1.15f else 1f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "alertPulse"
    )

    val lastEval = viewModel.history.firstOrNull()
    val patientLabel = if (lastEval?.patientName?.isNotBlank() == true) "Baby: ${lastEval.patientName}\n" else ""
    val shareText = buildString {
        append(" Apgar Score Report\n\n")
        append(patientLabel)
        if (lastEval?.motherName?.isNotBlank() == true) append("Mother: ${lastEval.motherName}\n")
        if (lastEval?.fatherName?.isNotBlank() == true) append("Father: ${lastEval.fatherName}\n")
        if (lastEval?.attendingStaff?.isNotBlank() == true) append("Attending: ${lastEval.attendingStaff}\n")
        if (lastEval?.notes?.isNotBlank() == true) append("Notes: ${lastEval.notes}\n")
        append("Score: $score / 10  —  ${classification.label}\n")
        append("Moment: ${lastEval?.moment ?: ""}\n\n")
        append("Breakdown:\n")
        apgarCriteria.forEach { c ->
            append("  ${c.icon} ${c.name}: ${viewModel.currentScores[c.id] ?: 0}/2\n")
        }
        append("\n ${classification.description}\n")
        append(" ${classification.recommendation}")
    }

    // ── Clinical Alert Dialog ───────────────────────────────────────
    if (showClinicalAlert) {
        AlertDialog(
            onDismissRequest = { showClinicalAlert = false },
            containerColor = Color.White,
            icon = { Text("", fontSize = 40.sp) },
            title = {
                Text(
                    "Critical Score — Immediate Action Required",
                    fontWeight = FontWeight.ExtraBold,
                    color = ScoreCritical,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Apgar Score: $score / 10",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ScoreCritical
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        classification.recommendation,
                        textAlign = TextAlign.Center,
                        color = Color.DarkGray,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showClinicalAlert = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ScoreCritical),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Acknowledged", fontWeight = FontWeight.Bold) }
            }
        )
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(DeepNavy, MedicalBlue, MedicalTeal)))
            ) {
                TopAppBar(
                    title = { Text("Assessment Result", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateToHome) {
                            Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                putExtra(Intent.EXTRA_SUBJECT, "Apgar Score Report")
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Report"))
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Hero score card ─────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(DeepNavy, NavyMid)))
            ) {
                // Decorative blob
                Box(modifier = Modifier.size(200.dp).align(Alignment.TopEnd).offset(x = 40.dp, y = (-20).dp)
                    .background(primaryColor.copy(alpha = 0.08f), CircleShape))

                Column(
                    modifier = Modifier.fillMaxWidth().padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (lastEval?.patientName?.isNotBlank() == true) {
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = Color.White.copy(alpha = 0.12f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(lastEval.patientName, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Score ring
                    Box(
                        modifier = Modifier.size(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { animatedFloat },
                            modifier = Modifier.fillMaxSize(),
                            color = primaryColor,
                            trackColor = primaryColor.copy(alpha = 0.18f),
                            strokeWidth = 14.dp,
                            strokeCap = StrokeCap.Round
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(classification.emoji, fontSize = 28.sp)
                            Text("$score", fontSize = 56.sp, fontWeight = FontWeight.ExtraBold, color = primaryColor)
                            Text("out of 10", fontSize = 13.sp, color = Color.White.copy(alpha = 0.6f))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .background(Brush.horizontalGradient(bgGradient), RoundedCornerShape(50.dp))
                            .padding(horizontal = 28.dp, vertical = 10.dp)
                    ) {
                        Text(classification.label, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (lastEval?.moment?.isNotBlank() == true) {
                        Text("Assessment at ${lastEval.moment}", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Score breakdown ─────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Assessment, contentDescription = null, tint = MedicalBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Score Breakdown", fontWeight = FontWeight.ExtraBold, color = MedicalBlueDark, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    apgarCriteria.forEach { criterion ->
                        val criterionScore = viewModel.currentScores[criterion.id] ?: 0
                        if (criterion != apgarCriteria.first()) HorizontalDivider(color = BackgroundLight, thickness = 1.dp)
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(criterion.icon, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(criterion.name, fontSize = 14.sp, color = MedicalBlueDark, fontWeight = FontWeight.SemiBold)
                                    Text(criterion.description, fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                repeat(3) { i ->
                                    val dotColor = when {
                                        i < criterionScore -> primaryColor
                                        else -> primaryColor.copy(alpha = 0.15f)
                                    }
                                    Box(modifier = Modifier.size(16.dp).background(dotColor, CircleShape))
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("$criterionScore", fontWeight = FontWeight.ExtraBold, color = primaryColor,
                                    fontSize = 18.sp, modifier = Modifier.width(20.dp), textAlign = TextAlign.End)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── Birth Team & Family Card ──────────────────────────────────
            val hasBirthInfo = listOf(lastEval?.motherName, lastEval?.fatherName, lastEval?.attendingStaff)
                .any { it?.isNotBlank() == true }
            if (hasBirthInfo) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(34.dp).background(PurpleAccent.copy(alpha = 0.12f), CircleShape),
                                contentAlignment = Alignment.Center) {
                                Text("‍‍", fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Birth Team & Family", fontWeight = FontWeight.ExtraBold,
                                color = MedicalBlueDark, fontSize = 15.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        if (lastEval?.motherName?.isNotBlank() == true) {
                            BirthInfoRow("", "Mother", lastEval.motherName)
                        }
                        if (lastEval?.fatherName?.isNotBlank() == true) {
                            BirthInfoRow("", "Father", lastEval.fatherName)
                        }
                        if (lastEval?.attendingStaff?.isNotBlank() == true) {
                            BirthInfoRow("", "Attending", lastEval.attendingStaff)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // ── Clinical notes if present ─────────────────────────────────
            if (lastEval?.notes?.isNotBlank() == true) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Notes, contentDescription = null, tint = MedicalTeal, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Clinical Notes", fontWeight = FontWeight.Bold, color = MedicalBlueDark, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(lastEval.notes, color = Color.DarkGray, fontSize = 13.sp, lineHeight = 19.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // ── Interpretation ──────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = primaryColor.copy(alpha = 0.07f)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(34.dp).background(primaryColor, CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.MedicalServices, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Clinical Interpretation", fontWeight = FontWeight.ExtraBold, color = primaryColor, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(classification.description, color = MedicalBlueDark, fontSize = 14.sp, lineHeight = 22.sp)
                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = primaryColor.copy(alpha = 0.15f))
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Recommendation", fontWeight = FontWeight.ExtraBold, color = primaryColor, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(classification.recommendation, color = MedicalBlueDark, fontSize = 14.sp, lineHeight = 22.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── Score reference legend ──────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Score Reference", fontWeight = FontWeight.Bold, color = MedicalBlueDark, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(14.dp))
                    listOf(
                        Triple("7 – 10", "Normal", ScoreGood),
                        Triple("4 – 6", "Moderate", ScoreModerate),
                        Triple("0 – 3", "Critical", ScoreCritical)
                    ).forEach { (range, label, color) ->
                        val isCurrent = (score >= 7 && color == ScoreGood) ||
                                (score in 4..6 && color == ScoreModerate) ||
                                (score < 4 && color == ScoreCritical)
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .then(
                                    if (isCurrent) Modifier.background(color.copy(alpha = 0.07f), RoundedCornerShape(10.dp)).padding(6.dp)
                                    else Modifier
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(if (isCurrent) color else color.copy(alpha = 0.8f), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(range, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(label, fontSize = 14.sp, color = if (isCurrent) color else MedicalBlueDark,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal)
                            if (isCurrent) {
                                Spacer(modifier = Modifier.weight(1f))
                                Text("◀ Current", fontSize = 11.sp, color = color, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateToHome,
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MedicalBlue),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, MedicalBlue)
                ) {
                    Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Home", fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = {
                        viewModel.resetScores()
                        onNavigateToCalculator()
                    },
                    modifier = Modifier.weight(2f).height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MedicalBlue
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Assessment", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
private fun BirthInfoRow(emoji: String, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(emoji, fontSize = 16.sp, modifier = Modifier.width(26.dp))
        Text("$label: ", fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
        Text(value, fontSize = 13.sp, color = MedicalBlueDark, fontWeight = FontWeight.Medium)
    }
}
