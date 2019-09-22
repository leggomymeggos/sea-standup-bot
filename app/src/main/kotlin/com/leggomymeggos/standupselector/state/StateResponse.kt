package com.leggomymeggos.standupselector.state

import java.sql.Date

data class StateResponse(val stateValues: List<StateValue>)

data class StateValue(
    val id: Int,
    val weekOf: Date,
    val firstConfirmed: String,
    val secondConfirmed: String,
    val selected: String,
    val rejected: String,
    val issuanceId: Int
)