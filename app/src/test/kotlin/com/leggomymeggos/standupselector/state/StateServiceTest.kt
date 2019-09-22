package com.leggomymeggos.standupselector.state

import com.leggomymeggos.standupselector.GoogleSheetsApiClient
import com.leggomymeggos.standupselector.standuppers.Standupper
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.sql.Date
import java.util.*

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

    @Test
    fun `recordSelection updates most recent entity`() {
        val stateUuid = UUID.randomUUID()
        whenever(stateRepository.getLatestState()).thenReturn(
            StateEntity(
                id = stateUuid,
                weekOf = Date.valueOf("2018-10-02"),
                firstConfirmed = "",
                secondConfirmed = "",
                selected = "",
                rejected = "",
                issuanceId = 10,
                googleId = 123
            )
        )

        subject.recordSelection(Standupper(
            slackName = "WoodCharles CouldCharles",
            email = "",
            isForceOmitted = false,
            isForceSelected = false,
            selectionProbability = 0.0
        ))

        verify(stateRepository).getLatestState()
        verify(stateRepository).updateSelectedForEntity(stateUuid, "WoodCharles CouldCharles")
    }

    @Test
    fun `recordSelection adds selected standuppers`() {
        val stateUuid = UUID.randomUUID()
        whenever(stateRepository.getLatestState()).thenReturn(
            StateEntity(
                id = stateUuid,
                weekOf = Date.valueOf("2018-10-02"),
                firstConfirmed = "",
                secondConfirmed = "",
                selected = "Princess Carolyn",
                rejected = "",
                issuanceId = 10,
                googleId = 123
            )
        )

        subject.recordSelection(Standupper(
            slackName = "WoodCharles CouldCharles",
            email = "",
            isForceOmitted = false,
            isForceSelected = false,
            selectionProbability = 0.0
        ))

        verify(stateRepository).getLatestState()
        verify(stateRepository).updateSelectedForEntity(stateUuid, "Princess Carolyn,WoodCharles CouldCharles")
    }

    @Test
    fun `currentWeekDateString looks up the most recent state`() {
        val stateUuid = UUID.randomUUID()
        whenever(stateRepository.getLatestState()).thenReturn(
            StateEntity(
                id = stateUuid,
                weekOf = Date.valueOf("2018-10-02"),
                firstConfirmed = "",
                secondConfirmed = "",
                selected = "",
                rejected = "",
                issuanceId = 10,
                googleId = 123
            )
        )

        subject.currentWeek()

        verify(stateRepository).getLatestState()
    }

    @Test
    fun `currentWeekDateString returns week of date`() {
        val stateUuid = UUID.randomUUID()
        val entity = StateEntity(
            id = stateUuid,
            weekOf = Date.valueOf("2019-10-02"),
            firstConfirmed = "",
            secondConfirmed = "",
            selected = "",
            rejected = "",
            issuanceId = 10,
            googleId = 123
        )

        whenever(stateRepository.getLatestState()).thenReturn(entity)
        var formattedWeek = subject.currentWeek()
        assertThat(formattedWeek).isEqualTo(Date.valueOf("2019-10-02"))

        whenever(stateRepository.getLatestState()).thenReturn(entity.copy(weekOf = Date.valueOf("2019-10-11")))
        formattedWeek = subject.currentWeek()
        assertThat(formattedWeek).isEqualTo(Date.valueOf("2019-10-11"))
    }
}