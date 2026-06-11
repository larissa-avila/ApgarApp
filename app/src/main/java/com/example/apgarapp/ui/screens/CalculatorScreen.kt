package com.example.apgarapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.apgarapp.model.ApgarCriterion
import com.example.apgarapp.model.apgarCriteria
import com.example.apgarapp.ui.theme.*
import com.example.apgarapp.viewmodel.ApgarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: ApgarViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToResult: (Int) -> Unit
) {
    val moments = listOf("1 Minute", "5 Minutes", "10 Minutes")
    var selectedMoment by remember { mutableStateOf(moments[0]) }

    val scores = viewModel.currentScores
    val totalScore = viewModel.getTotalScore()
    val filled = scores.size
    val isComplete = viewModel.isComplete()

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(DeepNavy, MedicalBlue, MedicalTeal)))
            ) {
                TopAppBar(
                    title = {
                        Column {
                            Text("New Evaluation", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 19.sp)
                            Text("Score each criterion 0 – 2", color = Color.White.copy(alpha = 0.70f), fontSize = 11.sp)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
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
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── Live score banner ──────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .shadow(12.dp, RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(
                            if (isComplete) listOf(DeepNavy, MedicalBlue, MedicalTeal)
                            else listOf(Color(0xFF455A64), Color(0xFF607D8B))
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Total Score", color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp)
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                "$totalScore",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                lineHeight = 52.sp
                            )
                            Text(
                                " / 10",
                                fontSize = 20.sp,
                                color = Color.White.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                        if (isComplete) {
                            val label = when {
                                totalScore >= 7 -> "✅ Normal"
                                totalScore >= 4 -> "⚠️ Moderate"
                                else -> "🚨 Critical"
                            }
                            Text(label, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        } else {
                            Text("$filled of 5 rated", color = Color.White.copy(alpha = 0.65f), fontSize = 13.sp)
                        }
                    }

                    // Progress dots
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            apgarCriteria.forEachIndexed { i, criterion ->
                                val done = scores.containsKey(criterion.id)
                                Box(
                                    modifier = Modifier.size(12.dp)
                                        .background(
                                            if (done) Color.White else Color.White.copy(alpha = 0.25f),
                                            CircleShape
                                        )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        if (isComplete) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null,
                                tint = Color.White, modifier = Modifier.size(32.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── Assessment Moment selector ─────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccessTime, contentDescription = null,
                            tint = PurpleAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(7.dp))
                        Text("Assessment Moment", fontWeight = FontWeight.Bold,
                            color = MedicalBlueDark, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        moments.forEachIndexed { index, moment ->
                            val isSelected = moment == selectedMoment
                            val momentColors = listOf(MedicalBlue, PurpleAccent, MedicalTeal)
                            val c = momentColors[index]
                            Box(
                                modifier = Modifier.weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) c else c.copy(alpha = 0.07f))
                                    .border(if (isSelected) 0.dp else 1.dp, c.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                                    .clickable { selectedMoment = moment }
                                    .padding(vertical = 11.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(moment,
                                    color = if (isSelected) Color.White else c,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── Birth Record ───────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null,
                            tint = PurpleAccent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Birth Record", fontWeight = FontWeight.Bold,
                            color = MedicalBlueDark, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = viewModel.patientName,
                        onValueChange = { viewModel.patientName = it },
                        label = { Text("Baby's Name") },
                        leadingIcon = { Icon(Icons.Default.Face, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = viewModel.motherName,
                        onValueChange = { viewModel.motherName = it },
                        label = { Text("Mother's Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = viewModel.fatherName,
                        onValueChange = { viewModel.fatherName = it },
                        label = { Text("Father's Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = viewModel.attendingStaff,
                        onValueChange = { viewModel.attendingStaff = it },
                        label = { Text("Attending Staff") },
                        leadingIcon = { Icon(Icons.Default.MedicalServices, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = viewModel.notes,
                        onValueChange = { viewModel.notes = it },
                        label = { Text("Clinical Notes") },
                        leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── Criteria cards ─────────────────────────────────────────────
            apgarCriteria.forEachIndexed { index, criterion ->
                ScoreCriterionCard(
                    criterion = criterion,
                    selectedScore = scores[criterion.id],
                    onScoreSelected = { score -> viewModel.setScore(criterion.id, score) },
                    index = index
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ── Action buttons ─────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.resetScores() },
                    modifier = Modifier.weight(1f).height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MedicalBlue),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, MedicalBlue)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(17.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reset", fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = {
                        if (isComplete) {
                            viewModel.saveEvaluation(selectedMoment)
                            onNavigateToResult(totalScore)
                        }
                    },
                    modifier = Modifier.weight(2f).height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    enabled = isComplete,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MedicalBlue,
                        disabledContainerColor = Color(0xFFB0BEC5)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Icon(Icons.Default.Assessment, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("See Results", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
fun ScoreCriterionCard(
    criterion: ApgarCriterion,
    selectedScore: Int?,
    onScoreSelected: (Int) -> Unit,
    index: Int
) {
    val accentColors = listOf(MedicalBlue, MedicalTeal, GoldAccent, PurpleAccent, ScoreGood)
    val accentColor = accentColors[index % accentColors.size]

    val scoreLabels = mapOf(
        0 to criterion.options.find { it.score == 0 }?.label,
        1 to criterion.options.find { it.score == 1 }?.label,
        2 to criterion.options.find { it.score == 2 }?.label
    )
    val scoreDescriptions = mapOf(
        0 to criterion.options.find { it.score == 0 }?.description,
        1 to criterion.options.find { it.score == 1 }?.description,
        2 to criterion.options.find { it.score == 2 }?.description
    )

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(50.dp)
                        .background(accentColor.copy(alpha = 0.10f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) { Text(criterion.icon, fontSize = 24.sp) }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        criterion.name,
                        fontWeight = FontWeight.ExtraBold,
                        color = MedicalBlueDark,
                        fontSize = 17.sp
                    )
                    Text(criterion.description, color = Color.Gray, fontSize = 12.sp)
                }
                // Selected score badge
                AnimatedVisibility(
                    visible = selectedScore != null,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Box(
                        modifier = Modifier.size(40.dp)
                            .background(Brush.linearGradient(listOf(accentColor, accentColor.copy(alpha = 0.75f))), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "${selectedScore ?: 0}",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Score buttons: 0 | 1 | 2 ──────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (score in 0..2) {
                    val isSelected = selectedScore == score
                    val scoreColor = when (score) {
                        0 -> ScoreCritical
                        1 -> ScoreModerate
                        else -> ScoreGood
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) Brush.verticalGradient(listOf(accentColor, accentColor.copy(alpha = 0.75f)))
                                else Brush.verticalGradient(listOf(BackgroundLight, BackgroundLight))
                            )
                            .border(
                                width = if (isSelected) 0.dp else 1.5.dp,
                                color = if (isSelected) Color.Transparent else accentColor.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { onScoreSelected(score) }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "$score",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSelected) Color.White else accentColor
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isSelected) Color.White.copy(alpha = 0.20f)
                                        else scoreColor.copy(alpha = 0.10f),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    scoreLabels[score] ?: "",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else scoreColor,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                            }
                        }
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(6.dp)
                                    .size(16.dp)
                                    .background(Color.White.copy(alpha = 0.25f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null,
                                    tint = Color.White, modifier = Modifier.size(10.dp))
                            }
                        }
                    }
                }
            }

            // Description of selected score
            AnimatedVisibility(
                visible = selectedScore != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                if (selectedScore != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .background(accentColor.copy(alpha = 0.06f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null,
                            tint = accentColor, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            scoreDescriptions[selectedScore] ?: "",
                            fontSize = 12.sp,
                            color = accentColor,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

// Backward compat alias
@Composable
fun PremiumCriterionCard(
    criterion: ApgarCriterion,
    selectedScore: Int?,
    onScoreSelected: (Int) -> Unit,
    index: Int
) = ScoreCriterionCard(criterion, selectedScore, onScoreSelected, index)

@Composable
fun CriterionCard(
    criterion: ApgarCriterion,
    selectedScore: Int?,
    onScoreSelected: (Int) -> Unit,
    index: Int
) = ScoreCriterionCard(criterion, selectedScore, onScoreSelected, index)
