package com.leggomymeggos.standupselector.selector

import com.leggomymeggos.standupselector.admin.AdminService
import com.leggomymeggos.standupselector.standuppers.StanduppersService
import com.leggomymeggos.standupselector.state.StateService
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class StandupSelectorServiceTest {

    private val standuppersService = mock<StanduppersService>()
    private val adminService = mock<AdminService>()
    private val stateService = mock<StateService>()

    private val subject = StandupSelectorService(
        standuppersService,
        adminService,
        stateService
    )

    @Test
    fun `loadData loads standuppers, admins, and state`() {
        subject.loadData()

        verify(standuppersService).populateStanduppers()
        verify(adminService).populateAdmins()
        verify(stateService).populateState()
    }
}