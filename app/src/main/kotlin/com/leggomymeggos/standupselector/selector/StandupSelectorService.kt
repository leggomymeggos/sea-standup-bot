package com.leggomymeggos.standupselector.selector

import com.leggomymeggos.standupselector.admin.AdminService
import com.leggomymeggos.standupselector.standuppers.StanduppersService
import com.leggomymeggos.standupselector.state.StateService
import org.springframework.stereotype.Service

@Service
class StandupSelectorService(
    private val standuppersService: StanduppersService,
    private val adminService: AdminService,
    private val stateService: StateService
) {
    fun loadData() {
        standuppersService.populateStanduppers()
        adminService.populateAdmins()
        stateService.populateState()
    }
}