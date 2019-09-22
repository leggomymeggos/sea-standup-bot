package com.leggomymeggos.standupselector.slack

import com.leggomymeggos.standupselector.admin.Admin
import com.leggomymeggos.standupselector.standuppers.Standupper
import com.nhaarman.mockito_kotlin.*
import org.junit.Test
import java.sql.Date
import java.time.LocalDate

class MessagingServiceTest {
    private val slackApiClient = mock<SlackApiClient> {
        on { getUserInfo(any()) } doReturn SlackUserInfo(SlackUser(""))
        on { openIm(any()) } doReturn SlackChannelInfo(SlackChannel(""))
    }

    private val subject = MessagingService(slackApiClient)

    // region notifyStanduppersOfSelection
    @Test
    fun `notifyStanduppersOfSelection gets the user info for each standupper`() {
        val mrPeanutButter = Standupper(
            slackName = "peanutbutterman",
            email = "mister@pblivin.com",
            isForceOmitted = false,
            isForceSelected = false,
            selectionProbability = 0.0
        )
        val todd = Standupper(
            slackName = "todd",
            email = "tchavez@whattimeisitrightnow.com",
            isForceOmitted = false,
            isForceSelected = false,
            selectionProbability = 0.0
        )

        subject.notifyStanduppersOfSelection(listOf(mrPeanutButter, todd), Date.valueOf(LocalDate.now()))

        verify(slackApiClient).getUserInfo("mister@pblivin.com")
        verify(slackApiClient).getUserInfo("tchavez@whattimeisitrightnow.com")
    }

    @Test
    fun `notifyStandupperOfSelection opens IM with each standupper`() {
        whenever(slackApiClient.getUserInfo("mister@pblivin.com")).thenReturn(SlackUserInfo(SlackUser("1234")))
        whenever(slackApiClient.getUserInfo("tchavez@whattimeisitrightnow.com")).thenReturn(SlackUserInfo(SlackUser("5678")))

        val mrPeanutButter = Standupper(
            slackName = "peanutbutterman",
            email = "mister@pblivin.com",
            isForceOmitted = false,
            isForceSelected = false,
            selectionProbability = 0.0
        )
        val todd = Standupper(
            slackName = "todd",
            email = "tchavez@whattimeisitrightnow.com",
            isForceOmitted = false,
            isForceSelected = false,
            selectionProbability = 0.0
        )

        subject.notifyStanduppersOfSelection(listOf(mrPeanutButter, todd), Date.valueOf(LocalDate.now()))

        verify(slackApiClient).openIm("1234")
        verify(slackApiClient).openIm("5678")
    }

    @Test
    fun `notifyStanduppersOfSelection messages each standupper`() {
        whenever(slackApiClient.getUserInfo("mister@pblivin.com")).thenReturn(SlackUserInfo(SlackUser("1234")))
        whenever(slackApiClient.openIm("1234")).thenReturn(SlackChannelInfo(SlackChannel("channel-id-1")))

        whenever(slackApiClient.getUserInfo("tchavez@whattimeisitrightnow.com")).thenReturn(SlackUserInfo(SlackUser("5678")))
        whenever(slackApiClient.openIm("5678")).thenReturn(SlackChannelInfo(SlackChannel("channel-id-2")))

        val mrPeanutButter = Standupper(
            slackName = "peanutbutterman",
            email = "mister@pblivin.com",
            isForceOmitted = false,
            isForceSelected = false,
            selectionProbability = 0.0
        )
        val todd = Standupper(
            slackName = "todd",
            email = "tchavez@whattimeisitrightnow.com",
            isForceOmitted = false,
            isForceSelected = false,
            selectionProbability = 0.0
        )

        subject.notifyStanduppersOfSelection(listOf(mrPeanutButter, todd), Date.valueOf("2019-02-01"))

        val message = "You have been selected to run standup for the week of February 1"

        verify(slackApiClient).writeToChannelWithSelectionAttachment("channel-id-1", message)
        verify(slackApiClient).writeToChannelWithSelectionAttachment("channel-id-2", message)
    }
    // endregion

    // region notifyAdminsOfSelection
    @Test
    fun `notifyAdminsOfSelection gets admin info`() {
        val admins = listOf(
            Admin("princess-c", "pcarolyn@vim.com"),
            Admin("diane", "sadgirl@diane.com")
        )

        val currentWeek = Date.valueOf("2017-10-29")
        subject.notifyAdminsOfSelection(admins, emptyList(), currentWeek)

        verify(slackApiClient).getUserInfo("pcarolyn@vim.com")
        verify(slackApiClient).getUserInfo("sadgirl@diane.com")
    }

    @Test
    fun `notifyAdminsOfSelection opens IM with each admin`() {
        whenever(slackApiClient.getUserInfo("pcarolyn@vim.com")).thenReturn(SlackUserInfo(SlackUser("1234")))
        whenever(slackApiClient.getUserInfo("sadgirl@diane.com")).thenReturn(SlackUserInfo(SlackUser("5678")))

        val admins = listOf(
            Admin("princess-c", "pcarolyn@vim.com"),
            Admin("diane", "sadgirl@diane.com")
        )

        val currentWeek = Date.valueOf("2017-10-29")
        subject.notifyAdminsOfSelection(admins, emptyList(), currentWeek)

        verify(slackApiClient).openIm("1234")
        verify(slackApiClient).openIm("5678")
    }

    @Test
    fun `notifyAdminsOfSelection notifies admins of selected standuppers`() {
        whenever(slackApiClient.getUserInfo("pcarolyn@vim.com")).thenReturn(SlackUserInfo(SlackUser("1234")))
        whenever(slackApiClient.openIm("1234")).thenReturn(SlackChannelInfo(SlackChannel("channel-id-1")))

        whenever(slackApiClient.getUserInfo("sadgirl@diane.com")).thenReturn(SlackUserInfo(SlackUser("5678")))
        whenever(slackApiClient.openIm("5678")).thenReturn(SlackChannelInfo(SlackChannel("channel-id-2")))

        val admins = listOf(
            Admin("princess-c", "pcarolyn@vim.com"),
            Admin("diane", "sadgirl@diane.com")
        )
        val standuppers = listOf(
            Standupper(
                slackName = "peanutbutterman",
                email = "mister@pblivin.com",
                isForceOmitted = false,
                isForceSelected = false,
                selectionProbability = 0.0
            ), Standupper(
                slackName = "todd",
                email = "tchavez@whattimeisitrightnow.com",
                isForceOmitted = false,
                isForceSelected = false,
                selectionProbability = 0.0
            )
        )

        val currentWeek = Date.valueOf("2017-10-29")
        subject.notifyAdminsOfSelection(admins, standuppers, currentWeek)

        verify(slackApiClient).writeToChannel("channel-id-1", "peanutbutterman and todd have been selected to run standup for the week of October 29")
        verify(slackApiClient).writeToChannel("channel-id-2", "peanutbutterman and todd have been selected to run standup for the week of October 29")
    }
    // endregion
}