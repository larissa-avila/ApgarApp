package com.example.apgarapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.apgarapp.model.ApgarEvaluation
import com.example.apgarapp.ui.theme.*
import com.example.apgarapp.viewmodel.ApgarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: ApgarViewModel,
    onNavigateBack: () -> Unit
) {
    val history = viewModel.history

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
                            Text("History", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 19.sp)
                            Text("${history.size} evaluation${if (history.size != 1) "s" else ""} recorded",
                                color = Color.White.copy(alpha = 0.70f), fontSize = 11.sp)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    actions = {
                        if (history.isNotEmpty()) {
                            IconButton(onClick = { viewModel.clearHistory() }) {
                                Icon(Icons.Default.DeleteSweep, contentDescription = "Clear all", tint = Color.White)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        }
    ) { innerPadding ->
        if (history.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundLight)
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        "No evaluations yet",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MedicalBlueDark.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Complete an Apgar evaluation\nto see results here.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundLight)
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(history, key = { it.id }) { evaluation ->
                    HistoryCard(
                        evaluation = evaluation,
                        onDelete = { viewModel.deleteEvaluation(evaluation.id) }
                    )
                }
                item { Spacer(modifier = Modifier.height(12.dp)) }
            }
        }
    }
}

@Composable
private fun HistoryCard(
    evaluation: ApgarEvaluation,
    onDelete: () -> Unit
) {
    val (scoreColor, scoreLabel, scoreIcon) = when {
        evaluation.totalScore >= 7 -> Triple(ScoreGood, "Normal", Icons.Default.CheckCircle)
        evaluation.totalScore in 4..6 -> Triple(ScoreModerate, "Moderate", Icons.Default.Warning)
        else -> Triple(ScoreCritical, "Critical", Icons.Default.Error)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Top row: score badge + label + delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Big score circle
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .background(
                            Brush.linearGradient(listOf(scoreColor, scoreColor.copy(alpha = 0.7f))),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "${evaluation.totalScore}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(scoreIcon, contentDescription = null,
                            tint = scoreColor, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            scoreLabel,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = scoreColor
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    if (evaluation.patientName.isNotBlank()) {
                        Text(
                            "  ${evaluation.patientName}",
                            fontSize = 12.sp,
                            color = MedicalBlueDark,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    if (evaluation.motherName.isNotBlank()) {
                        Text("  Mother: ${evaluation.motherName}", fontSize = 11.sp, color = Color.Gray)
                    }
                    if (evaluation.fatherName.isNotBlank()) {
                        Text("  Father: ${evaluation.fatherName}", fontSize = 11.sp, color = Color.Gray)
                    }
                    if (evaluation.attendingStaff.isNotBlank()) {
                        Text("  Attending: ${evaluation.attendingStaff}", fontSize = 11.sp, color = Color.Gray)
                    }
                    Text(
                        "${evaluation.moment}  ·  ${evaluation.formattedDate}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.LightGray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Score breakdown per criterion
            val criteriaOrder = listOf(
                Triple("❤️", "Heart Rate", "pulse"),
                Triple("💨", "Breathing", "respiration"),
                Triple("😮", "Reflex", "grimace"),
                Triple("💪", "Muscle Tone", "activity"),
                Triple("🎨", "Color", "appearance")
            )
            val dotColors = listOf(MedicalBlue, MedicalTeal, GoldAccent, PurpleAccent, ScoreGood)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                criteriaOrder.forEachIndexed { i, (icon, name, id) ->
                    val v = evaluation.scores[id] ?: 0
                    val c = dotColors[i]
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(icon, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(c.copy(alpha = 0.12f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "$v",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = c
                            )
                        }
                        Text(name, fontSize = 8.sp, color = Color.Gray, textAlign = TextAlign.Center)
                    }
                }
            }

            // Notes if present
            if (evaluation.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = BackgroundLight)
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Notes, contentDescription = null,
                        tint = MedicalTeal, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        evaluation.notes,
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}



