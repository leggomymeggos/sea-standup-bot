package com.leggomymeggos.standupselector.selector

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class SelectorControllerTest {

    private val selectorService = mock<AppService>()
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

    @Test
    fun `triggerSelection returns 200 response`() {
        mockMvc.perform(get("/trigger-selection"))
            .andExpect(status().isOk)
    }

    @Test
    fun `triggerSelection triggers standup selection`() {
        subject.triggerSelection()

        verify(selectorService).pickWeeklyStanduppers()
    }

    @Test
    fun `handleInteraction returns 200 response`() {
        mockMvc.perform(
            post("/handle-interaction")
                .content("{}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
    }
}