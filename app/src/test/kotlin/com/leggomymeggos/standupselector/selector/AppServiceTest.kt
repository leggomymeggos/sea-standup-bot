package com.leggomymeggos.standupselector.selector

import com.leggomymeggos.standupselector.admin.Admin
import com.leggomymeggos.standupselector.admin.AdminService
import com.leggomymeggos.standupselector.slack.MessagingService
import com.leggomymeggos.standupselector.standuppers.SelectionService
import com.leggomymeggos.standupselector.standuppers.Standupper
import com.leggomymeggos.standupselector.standuppers.StanduppersService
import com.leggomymeggos.standupselector.state.StateService
import com.nhaarman.mockito_kotlin.*
import org.junit.Test
import java.sql.Date

class AppServiceTest {

    private val standuppersService = mock<StanduppersService> {
        on { getStanduppers() } doReturn emptyList<Standupper>()
    }
    private val adminService = mock<AdminService>()
    private val stateService = mock<StateService>()
    private val selectionService = mock<SelectionService>()
    private val messagingService = mock<MessagingService>()

    private val subject = AppService(
        standuppersService,
        adminService,
        stateService,
        selectionService,
        messagingService
    )

    // region loadData
    @Test
    fun `loadData loads standuppers, admins, and state`() {
        subject.loadData()

        verify(standuppersService).populateStanduppers()
        verify(adminService).populateAdmins()
        verify(stateService).populateState()
    }
    // endregion

    // region pickWeeklyStanduppers
    @Test
    fun `pickWeeklyStanduppers gets standuppers`() {
        subject.pickWeeklyStanduppers()

        verify(standuppersService).getStanduppers()
    }

    @Test
    fun `pickWeeklyStanduppers selects standuppers`() {
        val standuppers = listOf(
            Standupper(
                slackName = "standupper-1",
                email = "stand@up.com",
                isForceSelected = false,
                isForceOmitted = false,
                selectionProbability = 0.0
            )
        )
        whenever(standuppersService.getStanduppers()).thenReturn(standuppers)

        subject.pickWeeklyStanduppers()

        verify(selectionService).selectStanduppersByProbability(standuppers)
    }

    @Test
    fun `pickWeeklyStanduppers notifies selected standuppers`() {
        val jonSnow = Standupper(
            slackName = "Jon Snow",
            email = "watcher@thewall.com",
            isForceSelected = false,
            isForceOmitted = false,
            selectionProbability = 0.0
        )
        val daenerys = Standupper(
            slackName = "Daenerys",
            email = "dracarys@flameon.com",
            isForceSelected = false,
            isForceOmitted = false,
            selectionProbability = 0.0
        )
        val standuppers = listOf(jonSnow, daenerys)
        whenever(selectionService.selectStanduppersByProbability(any())).thenReturn(standuppers)
        val currentWeek = Date.valueOf("2019-07-04")
        whenever(stateService.currentWeek()).thenReturn(currentWeek)

        subject.pickWeeklyStanduppers()

        verify(messagingService).notifyStanduppersOfSelection(listOf(jonSnow, daenerys), currentWeek)
    }

    @Test
    fun `pickWeeklyStanduppers notifies admins`() {
        val standuppers = listOf(
            Standupper(
                slackName = "Jon Snow",
                email = "watcher@thewall.com",
                isForceSelected = false,
                isForceOmitted = false,
                selectionProbability = 0.0
            ),
            Standupper(
                slackName = "Daenerys",
                email = "dracarys@flameon.com",
                isForceSelected = false,
                isForceOmitted = false,
                selectionProbability = 0.0
            )
        )
        whenever(selectionService.selectStanduppersByProbability(any())).thenReturn(standuppers)
        val admins = listOf(
            Admin(
                slackName = "Varys",
                email = "theSpider@spies.com"
            )
        )
        whenever(adminService.getAdmins()).thenReturn(admins)
        val currentWeek = Date.valueOf("2019-07-04")
        whenever(stateService.currentWeek()).thenReturn(currentWeek)

        subject.pickWeeklyStanduppers()

        verify(messagingService).notifyAdminsOfSelection(admins, standuppers, currentWeek)
    }

    @Test
    fun `pickWeeklyStanduppers increments selection of standuppers`() {
        val jonSnow = Standupper(
            slackName = "Jon Snow",
            email = "watcher@thewall.com",
            isForceSelected = false,
            isForceOmitted = false,
            selectionProbability = 0.0
        )
        val daenerys = Standupper(
            slackName = "Daenerys",
            email = "dracarys@flameon.com",
            isForceSelected = false,
            isForceOmitted = false,
            selectionProbability = 0.0
        )
        val standuppers = listOf(jonSnow, daenerys)
        whenever(selectionService.selectStanduppersByProbability(any())).thenReturn(standuppers)

        subject.pickWeeklyStanduppers()

        verify(standuppersService).incrementSelection(jonSnow)
        verify(standuppersService).incrementSelection(daenerys)
    }

    @Test
    fun `pickWeeklyStanduppers updates state`() {
        val jonSnow = Standupper(
            slackName = "Jon Snow",
            email = "watcher@thewall.com",
            isForceSelected = false,
            isForceOmitted = false,
            selectionProbability = 0.0
        )
        val daenerys = Standupper(
            slackName = "Daenerys",
            email = "dracarys@flameon.com",
            isForceSelected = false,
            isForceOmitted = false,
            selectionProbability = 0.0
        )
        val standuppers = listOf(jonSnow, daenerys)
        whenever(selectionService.selectStanduppersByProbability(any())).thenReturn(standuppers)

        subject.pickWeeklyStanduppers()

        verify(stateService).recordSelection(jonSnow)
        verify(stateService).recordSelection(daenerys)
    }

    @Test
    fun `pickWeeklyStanduppers notifies standuppers of selection`() {
        val jonSnow = Standupper(
            slackName = "Jon Snow",
            email = "watcher@thewall.com",
            isForceSelected = false,
            isForceOmitted = false,
            selectionProbability = 0.0
        )
        val daenerys = Standupper(
            slackName = "Daenerys",
            email = "dracarys@flameon.com",
            isForceSelected = false,
            isForceOmitted = false,
            selectionProbability = 0.0
        )
        val standuppers = listOf(jonSnow, daenerys)
        whenever(selectionService.selectStanduppersByProbability(any())).thenReturn(standuppers)

        val currentWeek = Date.valueOf("2019-01-20")
        whenever(stateService.currentWeek()).thenReturn(currentWeek)

        subject.pickWeeklyStanduppers()

        verify(stateService).currentWeek()
        verify(messagingService).notifyStanduppersOfSelection(standuppers, currentWeek)
    }
    // endregion
}