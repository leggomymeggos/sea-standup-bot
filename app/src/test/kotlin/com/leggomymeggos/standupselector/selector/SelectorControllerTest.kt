package com.leggomymeggos.standupselector.selector

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class SelectorControllerTest {

    private val selectorService = mock<StandupSelectorService>()
    private val subject = SelectorController(selectorService)

    private val mockMvc = MockMvcBuilders.standaloneSetup(subject).build()
    
    @Test
    fun `populate returns 200 response`() {
        mockMvc.perform(get("/populate"))
            .andExpect(status().isOk)
    }
    
    @Test
    fun `populate tells selector service to populate the database`() {
        subject.populateDatabase()

        verify(selectorService).loadData()
    }
}