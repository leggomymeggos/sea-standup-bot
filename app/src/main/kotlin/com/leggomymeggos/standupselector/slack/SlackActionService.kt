package com.leggomymeggos.standupselector.slack

import org.springframework.stereotype.Service

@Service
class SlackActionService {

    fun createAcceptAction(): SlackAction {
        val acceptOption = SlackButtonConfirmOption(
            title = "Are you sure you want to confirm?",
            text = "You will need to reach out to an admin to change this (currently)",
            okText = "Yes",
            dismissText = "No"
        )
        return SlackAction(
            name = "confirmation_action",
            text = "I accept the bot's glorious offer",
            type = "button",
            value = "yes",
            confirm = acceptOption,
            style = ""
        )
    }

    fun createRejectAction(): SlackAction {
        val rejectOption = SlackButtonConfirmOption(
            title = "Are you sure you wish to defy Standup Bot?",
            text = "Standup bot may forgive, but it will never forget",
            okText = "Yes",
            dismissText = "No"
        )
        return SlackAction(
            name = "confirmation_action",
            text = "I defiantly refuse the bot",
            type = "button",
            value = "no",
            confirm = rejectOption,
            style = "danger"
        )
    }
}

data class SlackAction(
    val name: String,
    val text: String,
    val style: String?,
    val type: String,
    val value: String,
    val confirm: SlackButtonConfirmOption
)

data class SlackButtonConfirmOption(
    val title: String,
    val text: String,
    val okText: String,
    val dismissText: String
)
