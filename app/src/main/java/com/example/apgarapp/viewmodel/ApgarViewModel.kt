package com.example.apgarapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.apgarapp.model.ApgarEvaluation

class ApgarViewModel : ViewModel() {
    val history = mutableStateListOf<ApgarEvaluation>()
    val currentScores = mutableStateMapOf<String, Int>()

    // Birth record fields
    var patientName by mutableStateOf("")
    var motherName by mutableStateOf("")
    var fatherName by mutableStateOf("")
    var attendingStaff by mutableStateOf("")
    var notes by mutableStateOf("")

    fun setScore(criterionId: String, score: Int) {
        currentScores[criterionId] = score
    }

    fun getTotalScore(): Int = currentScores.values.sum()

    fun isComplete(): Boolean = currentScores.size == 5

    fun saveEvaluation(moment: String): ApgarEvaluation {
        val evaluation = ApgarEvaluation(
            moment = moment,
            patientName = patientName.trim(),
            motherName = motherName.trim(),
            fatherName = fatherName.trim(),
            attendingStaff = attendingStaff.trim(),
            notes = notes.trim(),
            scores = currentScores.toMap(),
            totalScore = getTotalScore()
        )
        history.add(0, evaluation)
        return evaluation
    }

    fun resetScores() {
        currentScores.clear()
        patientName = ""
        motherName = ""
        fatherName = ""
        attendingStaff = ""
        notes = ""
    }

    fun deleteEvaluation(id: Long) {
        history.removeIf { it.id == id }
    }

    fun clearHistory() {
        history.clear()
    }
}
