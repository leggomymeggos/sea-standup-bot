package com.leggomymeggos.standupselector.slack

import com.leggomymeggos.standupselector.admin.Admin
import com.leggomymeggos.standupselector.standuppers.Standupper
import org.springframework.stereotype.Service
import java.sql.Date

@Service
class MessagingService(
    private val slackApiClient: SlackApiClient
) {
    fun notifyStanduppersOfSelection(standuppers: List<Standupper>, weekOfDate: Date) {
        standuppers.forEach { standupper ->
            slackApiClient.getUserInfo(standupper.email).let { userInfo ->
                slackApiClient.openIm(userInfo.user.id).let { channelInfo ->
                    slackApiClient.writeToChannelWithSelectionAttachment(
                        channelId = channelInfo.channel.id,
                        message = "You have been selected to run standup for the week of ${dateString(weekOfDate)}"
                    )
                }
            }
        }
    }

    fun notifyAdminsOfSelection(
        admins: List<Admin>,
        standuppers: List<Standupper>,
        currentWeek: Date
    ) {
        val standupperNames = standuppers.joinToString(" and ") { it.slackName }
        admins.forEach { admin ->
            slackApiClient.getUserInfo(admin.email).let { adminInfo ->
                slackApiClient.openIm(adminInfo.user.id).let { channelInfo ->
                    slackApiClient.writeToChannel(
                        channelId = channelInfo.channel.id,
                        message = "$standupperNames have been selected to run standup for the week of ${dateString(currentWeek)}"
                    )
                }
            }
        }
    }

    private fun dateString(date: Date): String {
        val localDate = date.toLocalDate()
        val monthString = "${localDate.month.toString()[0]}${localDate.month.toString().toLowerCase().substring(1)}"
        return "$monthString ${localDate.dayOfMonth}"
    }
}