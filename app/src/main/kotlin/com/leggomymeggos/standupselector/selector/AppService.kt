package com.leggomymeggos.standupselector.selector

import com.leggomymeggos.standupselector.admin.AdminService
import com.leggomymeggos.standupselector.slack.MessagingService
import com.leggomymeggos.standupselector.standuppers.SelectionService
import com.leggomymeggos.standupselector.standuppers.StanduppersService
import com.leggomymeggos.standupselector.state.StateService
import org.springframework.stereotype.Service

@Service
class AppService(
    private val standuppersService: StanduppersService,
    private val adminService: AdminService,
    private val stateService: StateService,
    private val selectionService: SelectionService,
    private val messagingService: MessagingService
) {
    fun loadData() {
        standuppersService.populateStanduppers()
        adminService.populateAdmins()
        stateService.populateState()
    }

    fun pickWeeklyStanduppers() {
        val selectedStanduppers = standuppersService.getStanduppers().let {
            selectionService.selectStanduppersByProbability(it)
        }

        selectedStanduppers.forEach {
            stateService.recordSelection(it)
            standuppersService.incrementSelection(it)
        }

        val currentWeek = stateService.currentWeek()
        messagingService.notifyStanduppersOfSelection(selectedStanduppers, currentWeek)

        messagingService.notifyAdminsOfSelection(
            admins = adminService.getAdmins(),
            standuppers = selectedStanduppers,
            currentWeek = currentWeek
        )
    }
}