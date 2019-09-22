package com.leggomymeggos.standupselector.standuppers

data class Standupper(
    val slackName: String,
    val email: String,
    val isForceSelected: Boolean,
    val isForceOmitted: Boolean,
    val selectionProbability: Double
)