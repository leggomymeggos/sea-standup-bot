package com.leggomymeggos.standupselector.standuppers

import java.util.*

data class StandupperResponse(val standuppers: List<StandupperValue>)

data class StandupperValue(
    val slackName: String,
    val email: String,
    val lastStandupRun: Date?,
    val numTimesSelected: Int,
    val forceSelection: Boolean,
    val forceOmission: Boolean,
    val id: Int
)
