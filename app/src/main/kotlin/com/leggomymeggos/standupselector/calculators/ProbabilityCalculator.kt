package com.leggomymeggos.standupselector.calculators

import org.springframework.stereotype.Component
import kotlin.math.floor

@Component
class ProbabilityCalculator {

    fun calculateStandupperProbability(
        numberOfWeeksStandupperSelected: Int,
        numberOfWeeksSinceLastStandupperConfirmation: Long,
        maxNumberOfWeeksSelected: Int,
        maxNumberOfWeeksSinceConfirmation: Long
    ): Double {
        val normalizedProximityScore = if (numberOfWeeksSinceLastStandupperConfirmation >= maxNumberOfWeeksSinceConfirmation) {
            100
        } else (100 * numberOfWeeksSinceLastStandupperConfirmation) / maxNumberOfWeeksSinceConfirmation

        val normalizedFrequencyScore = 100 - (if (numberOfWeeksStandupperSelected.toLong() >= maxNumberOfWeeksSelected) {
            100
        } else (100 * numberOfWeeksStandupperSelected) / maxNumberOfWeeksSelected)

        return floor((normalizedProximityScore * 0.60) + (normalizedFrequencyScore * 0.40))
    }
}