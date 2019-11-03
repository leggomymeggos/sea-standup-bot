package com.leggomymeggos.standupselector.slack

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class SlackApiClient(
    @Value("\${slack.url.open_im}") private val openDmUrl: String,
    @Value("\${slack.url.post_message}") private val postMessageUrl: String,
    @Value("\${slack.api.token}") private val apiToken: String,
    @Value("\${slack.callback_token}") private val callbackToken: String,
    @Value("\${slack.url.user_info_by_email}") private val userInfoByEmailUrl: String,
    private val restTemplate: RestTemplate
) {
    fun writeToChannel(channelId: String, message: String) {
        val messageJson = SlackMessageJson(
            channel = channelId,
            text = message
        )

        postSlackMessageJson(messageJson)
    }

    fun getUserInfo(standupperEmail: String): SlackUserInfo {
        return restTemplate.postForEntity(
            userInfoByEmailUrl,
            HttpEntity(SlackUserInfoRequest(email = standupperEmail), createHeaders()),
            SlackUserInfo::class.java
        ).body!!
    }

    fun openIm(userId: String): SlackChannelInfo {
        return restTemplate.postForEntity(
            openDmUrl,
            SlackChannelInfoRequest(userId),
            SlackChannelInfo::class.java
        ).body!!
    }

    fun writeToChannelWithSelectionAttachment(channelId: String, message: String) {
        val acceptOption = SlackButtonConfirmOption(
            title = "Are you sure you want to confirm?",
            text = "You will need to reach out to an admin to change this (currently)",
            okText = "Yes",
            dismissText = "No"
        )
        val acceptAction = SlackAction(
            name = "confirmation_action",
            text = "I accept the bot's glorious offer",
            type = "button",
            value = "yes",
            confirm = acceptOption,
            style = ""
        )
        val rejectOption = SlackButtonConfirmOption(
            title = "Are you sure you wish to defy Standup Bot?",
            text = "Standup bot may forgive, but it will never forget",
            okText = "Yes",
            dismissText = "No"
        )
        val rejectAction = SlackAction(
            name = "confirmation_action",
            text = "I defiantly refuse the bot",
            type = "button",
            value = "no",
            confirm = rejectOption,
            style = "danger"
        )
        val attachment = SlackAttachment(
            text = "Glorious standup bot, bot of bots, selector of standuppers, has generously chosen YOU to run standup for the upcoming week.",
            fallback = "Interactive messages not supported via this client",
            callbackId = callbackToken,
            color = "#3AA3E3",
            attachmentType = "default",
            actions = listOf(acceptAction, rejectAction)
        )
        val selectionMessage = SelectionMessageJson(
            text = message,
            channel = channelId,
            attachments = listOf(attachment)
        )
        postSlackMessageJson(selectionMessage)
    }

    private fun postSlackMessageJson(message: SlackJson) {
        val request = HttpEntity(message, createHeaders())

        restTemplate.postForEntity(
            postMessageUrl,
            request,
            String::class.java
        )
    }

    private fun createHeaders(): HttpHeaders {
        val headers = HttpHeaders()
        headers["Authorization"] = listOf("Bearer $apiToken")
        headers.contentType = MediaType.APPLICATION_JSON
        return headers
    }
}

data class SlackUserInfo(
    val user: SlackUser
)

data class SlackUserInfoRequest(
    val email: String
)

data class SlackUser(
    val id: String
)

data class SlackChannelInfo(
    val channel: SlackChannel
)

data class SlackChannelInfoRequest(
    val user: String
)

data class SlackChannel(
    val id: String
)

interface SlackJson

data class SelectionMessageJson(
    val text: String,
    val channel: String,
    val attachments: List<SlackAttachment>
): SlackJson


data class SlackMessageJson(
    val text: String,
    val channel: String
) : SlackJson

data class SlackAttachment(
    val text: String,
    val fallback: String,
    val callbackId: String,
    val color: String,
    val attachmentType: String,
    val actions: List<SlackAction>
)

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
