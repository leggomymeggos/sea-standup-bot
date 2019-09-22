package com.leggomymeggos.standupselector

import com.leggomymeggos.standupselector.admin.AdminResponse
import com.leggomymeggos.standupselector.admin.AdminValue
import com.leggomymeggos.standupselector.standuppers.StandupperResponse
import com.leggomymeggos.standupselector.standuppers.StandupperValue
import com.leggomymeggos.standupselector.state.StateResponse
import com.leggomymeggos.standupselector.state.StateValue
import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import java.net.URI
import java.sql.Date

class GoogleSheetsApiClientTest {

    private val restTemplate = mock<RestTemplate>()

    private val subject = GoogleSheetsApiClient(restTemplate, "example.com", "google-auth-token")

    // region populateAdmins
    @Test
    fun `getAdmins gets the admin sheet`() {
        whenever(restTemplate.exchange(any<RequestEntity<Any>>(), any<Class<AdminResponse>>()))
            .thenReturn(ResponseEntity.ok(AdminResponse(emptyList())))

        subject.getAdmins()

        val captor = argumentCaptor<RequestEntity<Any>>()

        verify(restTemplate).exchange(
            captor.capture(),
            eq(AdminResponse::class.java)
        )

        val request = captor.firstValue

        assertThat(request.method).isEqualTo(HttpMethod.GET)
        assertThat(request.url).isEqualTo(URI.create("example.com?sheet=admin&token=google-auth-token"))
    }

    @Test
    fun `getAdmins returns admins sheet`() {
        val adminResponse = AdminResponse(listOf(AdminValue("slack-name", "email")))
        whenever(restTemplate.exchange(any<RequestEntity<Any>>(), any<Class<AdminResponse>>()))
            .thenReturn(ResponseEntity.ok(adminResponse))

        val result = subject.getAdmins()

        assertThat(result).isEqualTo(adminResponse)
    }
    // endregion

    // region getStanduppers
    @Test
    fun `getStanduppers gets the standuppers sheet`() {
        whenever(restTemplate.exchange(any<RequestEntity<Any>>(), any<Class<StandupperResponse>>()))
            .thenReturn(ResponseEntity.ok(StandupperResponse(emptyList())))

        subject.getStanduppers()

        val captor = argumentCaptor<RequestEntity<Any>>()

        verify(restTemplate).exchange(
            captor.capture(),
            eq(StandupperResponse::class.java)
        )

        val request = captor.firstValue

        assertThat(request.method).isEqualTo(HttpMethod.GET)
        assertThat(request.url).isEqualTo(URI.create("example.com?sheet=standuppers&token=google-auth-token"))
    }

    @Test
    fun `getStanduppers returns standuppers sheet`() {
        val standuppersResponse = StandupperResponse(
            listOf(
                StandupperValue(
                    email = "e@mail.com",
                    slackName = "slack-name",
                    lastStandupRun = Date.valueOf("2019-02-01"),
                    numTimesSelected = 10,
                    forceOmission = false,
                    forceSelection = true,
                    id = 1
                )
            )
        )
        whenever(restTemplate.exchange(any<RequestEntity<Any>>(), any<Class<StandupperResponse>>()))
            .thenReturn(ResponseEntity.ok(standuppersResponse))

        val result = subject.getStanduppers()

        assertThat(result).isEqualTo(standuppersResponse)
    }
    // endregion

    // region getState
    @Test
    fun `getState gets the state sheet`() {
        whenever(restTemplate.exchange(any<RequestEntity<Any>>(), any<Class<StateResponse>>()))
            .thenReturn(ResponseEntity.ok(StateResponse(emptyList())))

        subject.getState()

        val captor = argumentCaptor<RequestEntity<Any>>()

        verify(restTemplate).exchange(
            captor.capture(),
            eq(StateResponse::class.java)
        )

        val request = captor.firstValue

        assertThat(request.method).isEqualTo(HttpMethod.GET)
        assertThat(request.url).isEqualTo(URI.create("example.com?sheet=state&token=google-auth-token"))
    }

    @Test
    fun `getStates returns admins sheet`() {
        val stateResponse = StateResponse(listOf(StateValue(
            id = 1,
            weekOf = Date.valueOf("2019-09-01"),
            firstConfirmed = "great-standupper",
            secondConfirmed = "okay-standupper",
            selected = "first, second",
            rejected = "third, fourth",
            issuanceId = 10
        )))
        whenever(restTemplate.exchange(any<RequestEntity<Any>>(), any<Class<StateResponse>>()))
            .thenReturn(ResponseEntity.ok(stateResponse))

        val result = subject.getState()

        assertThat(result).isEqualTo(stateResponse)
    }
    // endregion
}