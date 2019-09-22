package com.leggomymeggos.standupselector.state

import com.leggomymeggos.standupselector.GoogleSheetsApiClient
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.sql.Date

class StateServiceTest {
    private val googleSheetsApiClient = mock<GoogleSheetsApiClient>()
    private val stateRepository = mock<StateRepository>()

    private val subject = StateService(googleSheetsApiClient, stateRepository)

    @Test
    fun `populateState fetches and saves state`() {
        whenever(googleSheetsApiClient.getState()).thenReturn(
            StateResponse(
                listOf(
                    StateValue(
                        id = 1,
                        weekOf = Date.valueOf("2019-09-01"),
                        firstConfirmed = "great-standupper",
                        secondConfirmed = "okay-standupper",
                        selected = "first, second",
                        rejected = "third, fourth",
                        issuanceId = 10
                    )
                )
            )
        )

        subject.populateState()

        verify(googleSheetsApiClient).getState()

        val captor = argumentCaptor<List<StateEntity>>()

        verify(stateRepository).saveAll(captor.capture())

        captor.firstValue.forEach {
            assertThat(it.weekOf).isEqualTo(Date.valueOf("2019-09-01"))
            assertThat(it.firstConfirmed).isEqualTo("great-standupper")
            assertThat(it.secondConfirmed).isEqualTo("okay-standupper")
            assertThat(it.selected).isEqualTo("first, second")
            assertThat(it.rejected).isEqualTo("third, fourth")
            assertThat(it.googleId).isEqualTo(1)
            assertThat(it.issuanceId).isEqualTo(10)
        }
    }
}