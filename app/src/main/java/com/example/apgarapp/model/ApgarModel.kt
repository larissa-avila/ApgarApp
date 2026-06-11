package com.example.apgarapp.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ApgarCriterion(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val options: List<ApgarOption>
)

data class ApgarOption(
    val score: Int,
    val label: String,
    val description: String
)

data class ApgarEvaluation(
    val id: Long = System.currentTimeMillis(),
    val moment: String,
    val patientName: String = "",
    val motherName: String = "",
    val fatherName: String = "",
    val attendingStaff: String = "",
    val notes: String = "",
    val scores: Map<String, Int>,
    val totalScore: Int,
    val timestamp: Long = System.currentTimeMillis()
) {
    val formattedDate: String
        get() = SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US).format(Date(timestamp))
}

fun getApgarClassification(score: Int): ApgarClassification {
    return when {
        score >= 7 -> ApgarClassification.NORMAL
        score in 4..6 -> ApgarClassification.MODERATE
        else -> ApgarClassification.SEVERE
    }
}

enum class ApgarClassification(
    val label: String,
    val description: String,
    val recommendation: String,
    val emoji: String
) {
    NORMAL(
        "Normal",
        "The newborn shows good adaptation to the extrauterine environment. Vital signs are stable and reflexes are responsive.",
        "Routine monitoring. Maintain warmth and encourage skin-to-skin contact with the mother.",
        "✅"
    ),
    MODERATE(
        "Moderate",
        "The newborn requires additional stimulation and close monitoring. Some signs of adaptation difficulty are present.",
        "Provide tactile stimulation, administer supplemental oxygen, and monitor continuously for improvement.",
        "⚠️"
    ),
    SEVERE(
        "Critical",
        "The newborn shows significant signs of distress and requires immediate medical intervention.",
        "Initiate immediate neonatal resuscitation. Alert the neonatology team urgently. Do not delay.",
        "🚨"
    )
}

val apgarCriteria = listOf(
    ApgarCriterion(
        id = "pulse",
        name = "Heart Rate",
        description = "Heart Rate (bpm)",
        icon = "❤️",
        options = listOf(
            ApgarOption(0, "Absent", "No heartbeat detected"),
            ApgarOption(1, "< 100 bpm", "Heart rate below 100 bpm"),
            ApgarOption(2, "≥ 100 bpm", "Heart rate at or above 100 bpm")
        )
    ),
    ApgarCriterion(
        id = "respiration",
        name = "Breathing",
        description = "Breathing Effort",
        icon = "💨",
        options = listOf(
            ApgarOption(0, "Absent", "No respiratory effort"),
            ApgarOption(1, "Weak / Irregular", "Slow, irregular breathing or weak cry"),
            ApgarOption(2, "Strong & Regular", "Regular breathing with vigorous cry")
        )
    ),
    ApgarCriterion(
        id = "grimace",
        name = "Reflex",
        description = "Reflex Irritability",
        icon = "😮",
        options = listOf(
            ApgarOption(0, "No Response", "No response to stimulation"),
            ApgarOption(1, "Grimace", "Facial grimace or minimal response"),
            ApgarOption(2, "Cry / Cough", "Vigorous cry, cough, or sneeze")
        )
    ),
    ApgarCriterion(
        id = "activity",
        name = "Muscle Tone",
        description = "Muscle Tone",
        icon = "💪",
        options = listOf(
            ApgarOption(0, "Limp", "No muscle tone, completely limp"),
            ApgarOption(1, "Some Flexion", "Some flexion of limbs"),
            ApgarOption(2, "Active Motion", "Active motion, good resistance")
        )
    ),
    ApgarCriterion(
        id = "appearance",
        name = "Color",
        description = "Skin Color",
        icon = "🎨",
        options = listOf(
            ApgarOption(0, "Blue / Pale", "Entire body is blue or pale"),
            ApgarOption(1, "Acrocyanosis", "Body pink, extremities blue"),
            ApgarOption(2, "Completely Pink", "Entire body completely pink")
        )
    )
)