package com.leggomymeggos.standupselector.standuppers

import com.leggomymeggos.standupselector.GoogleSheetsApiClient
import com.leggomymeggos.standupselector.calculators.ProbabilityCalculator
import com.leggomymeggos.standupselector.calculators.TimeCalculator
import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.sql.Date
import java.util.*

class StanduppersServiceTest {
    private val googleSheetsApiClient = mock<GoogleSheetsApiClient>()
    private val standupperRepository = mock<StandupperRepository> {
        on { findBySlackName(any()) } doReturn StandupperEntity(
            slackName = "",
            email = "",
            lastConfirmedWeek = null,
            numberOfWeeksSelected = 0,
            forceSelection = false,
            omitSelection = false,
            googleId = 123
        )
    }
    private val timeCalculator = mock<TimeCalculator>()
    private val probabilityCalculator = mock<ProbabilityCalculator>()

    private val subject =
        StanduppersService(googleSheetsApiClient, standupperRepository, timeCalculator, probabilityCalculator)

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

    // region getStanduppers
    @Test
    fun `getStanduppers gets standuppers from repository`() {
        subject.getStanduppers()

        verify(standupperRepository).findAll()
    }

    @Test
    fun `getStanduppers requests number of weeks since last standup run`() {
        val firstStandupperWeek = Date.valueOf("2019-02-01")
        val secondStandupperWeek = Date.valueOf("2019-04-30")

        val standupper1 = StandupperEntity(
            slackName = "standupper-1",
            email = "email@example.com",
            lastConfirmedWeek = firstStandupperWeek,
            numberOfWeeksSelected = 0,
            forceSelection = false,
            omitSelection = false,
            googleId = 1
        )
        val standupper2 = StandupperEntity(
            slackName = "standupper-2",
            email = "email@example.com",
            lastConfirmedWeek = secondStandupperWeek,
            numberOfWeeksSelected = 0,
            forceSelection = false,
            omitSelection = false,
            googleId = 2
        )
        whenever(standupperRepository.findAll()).thenReturn(
            listOf(standupper1, standupper2)
        )

        subject.getStanduppers()

        verify(timeCalculator).numberOfWeeksSince(firstStandupperWeek)
        verify(timeCalculator).numberOfWeeksSince(secondStandupperWeek)
    }

    @Test
    fun `getStanduppers calculates probability for each standupper with the max numberOfWeeksSelected and max numberOfWeeksSinceSelection`() {
        val firstStandupperWeek = Date.valueOf("2019-02-01")
        val secondStandupperWeek = Date.valueOf("2019-04-30")
        val thirdStandupperWeek = Date.valueOf("2017-04-30")

        val standupper1 = StandupperEntity(
            slackName = "standupper-1",
            email = "email@example.com",
            lastConfirmedWeek = firstStandupperWeek,
            numberOfWeeksSelected = 1,
            forceSelection = false,
            omitSelection = false,
            googleId = 1
        )
        val standupper2 = StandupperEntity(
            slackName = "standupper-2",
            email = "email@example.com",
            lastConfirmedWeek = secondStandupperWeek,
            numberOfWeeksSelected = 5,
            forceSelection = false,
            omitSelection = false,
            googleId = 2
        )
        val standupper3 = StandupperEntity(
            slackName = "standupper-3",
            email = "email@example.com",
            lastConfirmedWeek = thirdStandupperWeek,
            numberOfWeeksSelected = 2,
            forceSelection = false,
            omitSelection = false,
            googleId = 3
        )

        whenever(timeCalculator.numberOfWeeksSince(firstStandupperWeek)).thenReturn(20)
        whenever(timeCalculator.numberOfWeeksSince(secondStandupperWeek)).thenReturn(18)
        whenever(timeCalculator.numberOfWeeksSince(thirdStandupperWeek)).thenReturn(19)

        whenever(standupperRepository.findAll()).thenReturn(listOf(standupper1, standupper2, standupper3))

        subject.getStanduppers()

        verify(probabilityCalculator).calculateStandupperProbability(
            numberOfWeeksStandupperSelected = 1,
            numberOfWeeksSinceLastStandupperConfirmation = 20,
            maxNumberOfWeeksSelected = 5,
            maxNumberOfWeeksSinceConfirmation = 20
        )
        verify(probabilityCalculator).calculateStandupperProbability(
            numberOfWeeksStandupperSelected = 5,
            numberOfWeeksSinceLastStandupperConfirmation = 18,
            maxNumberOfWeeksSelected = 5,
            maxNumberOfWeeksSinceConfirmation = 20
        )
        verify(probabilityCalculator).calculateStandupperProbability(
            numberOfWeeksStandupperSelected = 2,
            numberOfWeeksSinceLastStandupperConfirmation = 19,
            maxNumberOfWeeksSelected = 5,
            maxNumberOfWeeksSinceConfirmation = 20
        )
    }

    @Test
    fun `getStanduppers returns standuppers`() {
        val standupper1 = StandupperEntity(
            slackName = "standupper-1",
            email = "email@example.com",
            lastConfirmedWeek = null,
            numberOfWeeksSelected = 1,
            forceSelection = false,
            omitSelection = true,
            googleId = 1
        )
        val standupper2 = StandupperEntity(
            slackName = "standupper-2",
            email = "email@example.com",
            lastConfirmedWeek = null,
            numberOfWeeksSelected = 5,
            forceSelection = false,
            omitSelection = false,
            googleId = 2
        )

        whenever(timeCalculator.numberOfWeeksSince(anyOrNull())).thenReturn(2)
        whenever(probabilityCalculator.calculateStandupperProbability(any(), any(), any(), any())).thenReturn(20.0)

        whenever(standupperRepository.findAll()).thenReturn(listOf(standupper1, standupper2))

        val standuppers = subject.getStanduppers()

        assertThat(standuppers).containsAll(
            listOf(
                Standupper(
                    slackName = "standupper-1",
                    email = "email@example.com",
                    selectionProbability = 20.0,
                    isForceSelected = false,
                    isForceOmitted = true
                ),
                Standupper(
                    slackName = "standupper-2",
                    email = "email@example.com",
                    selectionProbability = 20.0,
                    isForceSelected = false,
                    isForceOmitted = false
                )
            )
        )
    }
    // endregion

    // region incrementSelection
    @Test
    fun `incrementSelection gets standupper`() {
        subject.incrementSelection(
            Standupper(
                slackName = "the-slack-name",
                email = "",
                isForceOmitted = false,
                isForceSelected = false,
                selectionProbability = 0.0
            )
        )

        verify(standupperRepository).findBySlackName("the-slack-name")
    }

    @Test
    fun `incrementSelection increments selection for standupper`() {
        val standupperID = UUID.randomUUID()
        val standupperEntity = StandupperEntity(
            id = standupperID,
            slackName = "",
            email = "",
            lastConfirmedWeek = null,
            numberOfWeeksSelected = 0,
            forceSelection = false,
            omitSelection = false,
            googleId = 123

        )
        val standupper = Standupper(
            slackName = "the-slack-name",
            email = "",
            isForceSelected = false,
            isForceOmitted = false,
            selectionProbability = 0.0
        )

        whenever(standupperRepository.findBySlackName(any())).thenReturn(standupperEntity.copy(numberOfWeeksSelected = 12))
        subject.incrementSelection(standupper)
        verify(standupperRepository).updateNumberOfWeeksSelected(standupperID, 13)

        whenever(standupperRepository.findBySlackName(any())).thenReturn(standupperEntity.copy(numberOfWeeksSelected = 1))
        subject.incrementSelection(standupper)
        verify(standupperRepository).updateNumberOfWeeksSelected(standupperID, 2)

        whenever(standupperRepository.findBySlackName(any())).thenReturn(standupperEntity.copy(numberOfWeeksSelected = 40))
        subject.incrementSelection(standupper)
        verify(standupperRepository).updateNumberOfWeeksSelected(standupperID, 41)
    }
    // endregion
}