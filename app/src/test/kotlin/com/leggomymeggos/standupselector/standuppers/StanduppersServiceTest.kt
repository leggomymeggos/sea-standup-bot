package com.leggomymeggos.standupselector.standuppers

import com.leggomymeggos.standupselector.GoogleSheetsApiClient
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.sql.Date

class StanduppersServiceTest {
    private val googleSheetsApiClient = mock<GoogleSheetsApiClient>()
    private val standupperRepository = mock<StandupperRepository>()

    private val subject = StanduppersService(googleSheetsApiClient, standupperRepository)

    @Test
    fun `populateStanduppers fetches and saves standuppers`() {
        whenever(googleSheetsApiClient.getStanduppers()).thenReturn(
            StandupperResponse(
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
        )

        subject.populateStanduppers()

        verify(googleSheetsApiClient).getStanduppers()

        val captor = argumentCaptor<List<StandupperEntity>>()

        verify(standupperRepository).saveAll(captor.capture())

        captor.firstValue.forEach {
            assertThat(it.email).isEqualTo("e@mail.com")
            assertThat(it.slackName).isEqualTo("slack-name")
            assertThat(it.lastConfirmedWeek).isEqualTo(Date.valueOf("2019-02-01"))
            assertThat(it.numberOfWeeksSelected).isEqualTo(10)
            assertThat(it.forceSelection).isTrue()
            assertThat(it.omitSelection).isFalse()
            assertThat(it.googleId).isEqualTo(1)
        }
    }
}