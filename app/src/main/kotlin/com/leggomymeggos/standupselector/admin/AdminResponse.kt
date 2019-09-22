package com.leggomymeggos.standupselector.admin

data class AdminResponse(val admins: List<AdminValue>)

data class AdminValue(
    val slackName: String,
    val email: String
)
