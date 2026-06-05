package com.example.apgarapp.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.apgarapp.ui.theme.*
import com.example.apgarapp.viewmodel.ApgarViewModel

@Composable
fun HomeScreen(
    viewModel: ApgarViewModel,
    onNavigateToCalculator: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    val heroAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(800),
        label = "heroAlpha"
    )
    val heroSlide by animateFloatAsState(
        targetValue = if (visible) 0f else 40f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "heroSlide"
    )
    LaunchedEffect(Unit) { visible = true }

    // Count how many birth record fields are filled
    val filledFields = listOf(
        viewModel.patientName, viewModel.motherName,
        viewModel.fatherName, viewModel.attendingStaff
    ).count { it.isNotBlank() }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundLight)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Hero Header ──────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(310.dp)
                    .background(Brush.verticalGradient(listOf(DeepNavy, NavyMid, MedicalBlue)))
            ) {
                Box(modifier = Modifier.size(280.dp).alpha(0.07f).background(MedicalTeal, CircleShape)
                    .align(Alignment.TopEnd).offset(x = 80.dp, y = (-60).dp))
                Box(modifier = Modifier.size(200.dp).alpha(0.05f).background(CyanBright, CircleShape)
                    .align(Alignment.BottomStart).offset(x = (-50).dp, y = 50.dp))
                Box(modifier = Modifier.size(120.dp).alpha(0.04f).background(GoldAccent, CircleShape)
                    .align(Alignment.BottomEnd).offset(x = 30.dp, y = 20.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(top = 52.dp, bottom = 36.dp)
                        .alpha(heroAlpha).offset(y = heroSlide.dp)
                ) {
                    Box(modifier = Modifier.size(100.dp).background(Color.White.copy(alpha = 0.08f), CircleShape),
                        contentAlignment = Alignment.Center) {
                        Box(modifier = Modifier.size(76.dp).background(Color.White.copy(alpha = 0.14f), CircleShape),
                            contentAlignment = Alignment.Center) { Text("", fontSize = 40.sp) }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    Text("Apgar Score", fontSize = 34.sp, fontWeight = FontWeight.ExtraBold,
                        color = Color.White, letterSpacing = 0.3.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier.background(
                            Brush.horizontalGradient(listOf(MedicalTeal.copy(alpha = 0.9f), CyanBright.copy(alpha = 0.8f))),
                            RoundedCornerShape(50.dp)
                        ).padding(horizontal = 18.dp, vertical = 6.dp)
                    ) {
                        Text("NEONATAL ASSESSMENT TOOL", fontSize = 10.sp, color = Color.White,
                            fontWeight = FontWeight.Bold, letterSpacing = 1.8.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(modifier = Modifier.padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {

                // ── Birth Information Card ─────────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth().shadow(18.dp, RoundedCornerShape(28.dp)),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column {
                        // Gradient header bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(listOf(DeepNavy, MedicalBlue, MedicalTeal)),
                                    RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                                )
                                .padding(horizontal = 22.dp, vertical = 16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .background(Color.White.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Assignment, contentDescription = null,
                                        tint = Color.White, modifier = Modifier.size(22.dp))
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Birth Record Information",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Text(
                                        "Fill in before starting the evaluation",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.75f)
                                    )
                                }
                                // Completion pill
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (filledFields == 4) ScoreGood.copy(alpha = 0.85f)
                                            else Color.White.copy(alpha = 0.18f),
                                            RoundedCornerShape(50.dp)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        "$filledFields / 4",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        // Input fields
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            BirthInfoField(
                                value = viewModel.patientName,
                                onValueChange = { viewModel.patientName = it },
                                label = "Baby Name or ID",
                                placeholder = "e.g. Baby Doe, Patient #001",
                                emoji = "",
                                accentColor = MedicalTeal,
                                imeAction = ImeAction.Next
                            )

                            BirthInfoField(
                                value = viewModel.motherName,
                                onValueChange = { viewModel.motherName = it },
                                label = "Mother's Name",
                                placeholder = "e.g. Maria Silva",
                                emoji = "",
                                accentColor = PurpleAccent,
                                imeAction = ImeAction.Next
                            )

                            BirthInfoField(
                                value = viewModel.fatherName,
                                onValueChange = { viewModel.fatherName = it },
                                label = "Father's Name",
                                placeholder = "e.g. João Silva",
                                emoji = "",
                                accentColor = MedicalBlue,
                                imeAction = ImeAction.Next
                            )

                            BirthInfoField(
                                value = viewModel.attendingStaff,
                                onValueChange = { viewModel.attendingStaff = it },
                                label = "Attending Nurse / Doctor",
                                placeholder = "e.g. Nurse Ana Costa",
                                emoji = "",
                                accentColor = GoldAccent,
                                imeAction = ImeAction.Done
                            )
                        }
                    }
                }

                // ── CTA Card ─────────────────────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth().shadow(14.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().background(
                        Brush.horizontalGradient(listOf(MedicalBlue.copy(alpha = 0.04f), MedicalTeal.copy(alpha = 0.02f))),
                        RoundedCornerShape(24.dp)
                    )) {
                        Column(modifier = Modifier.padding(22.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                Box(modifier = Modifier.size(56.dp).background(
                                    Brush.radialGradient(listOf(MedicalTeal.copy(alpha = 0.15f), Color.Transparent)),
                                    CircleShape), contentAlignment = Alignment.Center) { Text("", fontSize = 28.sp) }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Neonatal Evaluation", fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold, color = MedicalBlueDark)
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text("Evaluate at 1, 5 & 10 min after birth",
                                        fontSize = 12.sp, color = Color.Gray, lineHeight = 17.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))

                            // Quick score range chips
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(Triple("7–10", "Normal", ScoreGood),
                                    Triple("4–6", "Moderate", ScoreModerate),
                                    Triple("0–3", "Critical", ScoreCritical)).forEach { (range, label, color) ->
                                    Box(modifier = Modifier.weight(1f).background(color.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                                        .padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(range, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = color)
                                            Text(label, fontSize = 10.sp, color = color.copy(alpha = 0.85f), fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))
                            Button(
                                onClick = onNavigateToCalculator,
                                modifier = Modifier.fillMaxWidth().height(58.dp),
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("New Evaluation", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.3.sp)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedButton(
                                onClick = onNavigateToHistory,
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MedicalBlue),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, MedicalBlue)
                            ) {
                                Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                val count = viewModel.history.size
                                Text(
                                    if (count > 0) "History  ($count)" else "History",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // ── Criteria Reference Card ───────────────────────────────────
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(modifier = Modifier.padding(22.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(40.dp).background(
                                Brush.linearGradient(listOf(MedicalBlue, MedicalTeal)), CircleShape),
                                contentAlignment = Alignment.Center) {
                                Text("A", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("5 Criteria · 0 to 2 pts each", fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold, color = MedicalBlueDark)
                                Text("Total score: 0 – 10", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                        Spacer(modifier = Modifier.height(18.dp))

                        val criteria = listOf(
                            Triple("❤️", "Heart Rate", "Pulse / bpm"),
                            Triple("", "Breathing", "Respiratory effort"),
                            Triple("", "Reflex", "Reflex irritability"),
                            Triple("", "Muscle Tone", "Activity / tone"),
                            Triple("", "Color", "Skin color")
                        )
                        val accentColors = listOf(MedicalBlue, MedicalTeal, GoldAccent, PurpleAccent, ScoreGood)

                        criteria.forEachIndexed { index, (icon, name, desc) ->
                            if (index > 0) HorizontalDivider(color = BackgroundLight, thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 9.dp)) {
                                Box(modifier = Modifier.size(40.dp).background(
                                    accentColors[index].copy(alpha = 0.10f), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center) { Text(icon, fontSize = 20.sp) }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MedicalBlueDark)
                                    Text(desc, fontSize = 11.sp, color = Color.Gray)
                                }
                                Box(modifier = Modifier.background(accentColors[index], RoundedCornerShape(10.dp))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)) {
                                    Text("0 – 2", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                // ── Info footer card ───────────────────────────────────────────
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MedicalBlue.copy(alpha = 0.05f)),
                    elevation = CardDefaults.cardElevation(0.dp)) {
                    Row(modifier = Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(42.dp).background(MedicalBlue.copy(alpha = 0.12f), CircleShape),
                            contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = MedicalBlue, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text("Virginia Apgar (1952)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MedicalBlueDark)
                            Text("Score each criterion from 0–2. Evaluated at 1, 5, and 10 minutes after delivery.",
                                fontSize = 12.sp, color = Color.Gray, lineHeight = 17.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

/**
 * Reusable input field for birth record entries.
 * Colored emoji badge on the left, accent-colored focus ring, green checkmark when filled.
 */
@Composable
private fun BirthInfoField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    emoji: String,
    accentColor: Color,
    imeAction: ImeAction = ImeAction.Next
) {
    val isFilled = value.isNotBlank()

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        label = { Text(label, fontSize = 13.sp) },
        placeholder = { Text(placeholder, fontSize = 13.sp, color = Color.LightGray) },
        leadingIcon = {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(accentColor.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) { Text(emoji, fontSize = 18.sp) }
        },
        trailingIcon = {
            if (isFilled) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Filled",
                    tint = ScoreGood,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            keyboardType = KeyboardType.Text,
            imeAction = imeAction
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = accentColor,
            focusedLabelColor = accentColor,
            cursorColor = accentColor
        )
    )
}

