package com.leggomymeggos.standupselector.calculators

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test


class ProbabilityCalculatorTest {
    private val subject = ProbabilityCalculator()

    @Test
    fun `calculateStandupperProbability returns lower probability for recent confirmed standuppers`() {
        // proximityScore = (100 * 0)/10 -> 0 * .6 -> 0
        // frequencyScore = 100 - (200/3) -> 33.33 * .4 -> 13
        assertThat(
            subject.calculateStandupperProbability(
                numberOfWeeksSinceLastStandupperConfirmation = 0,
                numberOfWeeksStandupperSelected = 2,
                maxNumberOfWeeksSinceConfirmation = 10,
                maxNumberOfWeeksSelected = 3
            )
        ).isEqualTo(13.0)

        // proximityScore = (100 * 2)/10 -> 20 * .6 -> 12
        // frequencyScore = 100 - (200/3) -> 33.33 * .4 -> 13
        assertThat(
            subject.calculateStandupperProbability(
                numberOfWeeksSinceLastStandupperConfirmation = 2,
                numberOfWeeksStandupperSelected = 2,
                maxNumberOfWeeksSinceConfirmation = 10,
                maxNumberOfWeeksSelected = 3
            )
        ).isEqualTo(25.0)

        // proximity starts at 100 because the numberOfWeeksSinceConfirmation == maxWeeksSinceConfirmation
        // proximityScore = 100 * .6 -> 60
        // frequencyScore = 100 - (200/3) -> 33.33 * .4 -> 13
        assertThat(
            subject.calculateStandupperProbability(
                numberOfWeeksSinceLastStandupperConfirmation = 10,
                numberOfWeeksStandupperSelected = 2,
                maxNumberOfWeeksSinceConfirmation = 10,
                maxNumberOfWeeksSelected = 3
            )
        ).isEqualTo(73.0)

        // proximity starts at 100 because the numberOfWeeksSinceConfirmation > maxWeeksSinceConfirmation
        // proximityScore = 100 * .6 -> 60
        // frequencyScore = 100 - (200/3) -> 33.33 * .4 -> 13
        assertThat(
            subject.calculateStandupperProbability(
                numberOfWeeksSinceLastStandupperConfirmation = 20,
                numberOfWeeksStandupperSelected = 2,
                maxNumberOfWeeksSinceConfirmation = 10,
                maxNumberOfWeeksSelected = 3
            )
        ).isEqualTo(73.0)
    }

    @Test
    fun `calculateStandupperProbability returns lower probability for more selected standuppers`() {
        // proximityScore = (100 * 0)/10 -> 0 * .6 -> 0
        // frequencyScore = 100 - (0/3) -> 100 * .4 -> 40
        assertThat(
            subject.calculateStandupperProbability(
                numberOfWeeksSinceLastStandupperConfirmation = 0,
                numberOfWeeksStandupperSelected = 0,
                maxNumberOfWeeksSinceConfirmation = 10,
                maxNumberOfWeeksSelected = 3
            )
        ).isEqualTo(40.0)

        // proximityScore = (100 * 0)/10 -> 0 * .6 -> 0
        // frequencyScore = 100 - (200/3) -> 33.33 * .4 -> 13
        assertThat(
            subject.calculateStandupperProbability(
                numberOfWeeksSinceLastStandupperConfirmation = 0,
                numberOfWeeksStandupperSelected = 2,
                maxNumberOfWeeksSinceConfirmation = 10,
                maxNumberOfWeeksSelected = 3
            )
        ).isEqualTo(13.0)

        // proximityScore = (100 * 0)/10 -> 0 * .6 -> 0
        // frequencyScore = 100 - 100 -> 0 * .4 -> 0
        assertThat(
            subject.calculateStandupperProbability(
                numberOfWeeksSinceLastStandupperConfirmation = 0,
                numberOfWeeksStandupperSelected = 3,
                maxNumberOfWeeksSinceConfirmation = 10,
                maxNumberOfWeeksSelected = 3
            )
        ).isEqualTo(0.0)
    }
}