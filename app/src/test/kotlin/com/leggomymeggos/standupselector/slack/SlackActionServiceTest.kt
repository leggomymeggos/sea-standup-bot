package com.leggomymeggos.standupselector.slack

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class SlackActionServiceTest {
    private val subject = SlackActionService()

    @Test
    fun `createAcceptAction creates an action for accepting the selection`() {
        val result = subject.createAcceptAction()

        assertThat(result).isEqualTo(
            SlackAction(
                name = "confirmation_action",
                text = "I accept the bot's glorious offer",
                type = "button",
                value = "yes",
                confirm = SlackButtonConfirmOption(
                    title = "Are you sure you want to confirm?",
                    text = "You will need to reach out to an admin to change this (currently)",
                    okText = "Yes",
                    dismissText = "No"
                ),
                style = ""
            )
        )
    }

    @Test
    fun `createRejectAction creates an action for rejecting the selection`() {
        val result = subject.createRejectAction()

        assertThat(result).isEqualTo(
            SlackAction(
                name = "confirmation_action",
                text = "I defiantly refuse the bot",
                type = "button",
                value = "no",
                confirm = SlackButtonConfirmOption(
                    title = "Are you sure you wish to defy Standup Bot?",
                    text = "Standup bot may forgive, but it will never forget",
                    okText = "Yes",
                    dismissText = "No"
                ),
                style = "danger"
            )
        )
    }
}