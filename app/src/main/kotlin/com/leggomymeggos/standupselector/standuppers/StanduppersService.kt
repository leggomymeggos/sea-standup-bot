package com.leggomymeggos.standupselector.standuppers

import com.leggomymeggos.standupselector.GoogleSheetsApiClient
import com.leggomymeggos.standupselector.calculators.ProbabilityCalculator
import com.leggomymeggos.standupselector.calculators.TimeCalculator
import org.springframework.stereotype.Service

@Service
class StanduppersService(
    private val googleSheetsApiClient: GoogleSheetsApiClient,
    private val standupperRepository: StandupperRepository,
    private val timeCalculator: TimeCalculator,
    private val probabilityCalculator: ProbabilityCalculator
) {
    fun populateStanduppers() {
        val standuppers = googleSheetsApiClient.getStanduppers().standuppers.map {
            StandupperEntity(
                email = it.email,
                slackName = it.slackName,
                lastConfirmedWeek = it.lastStandupRun,
                numberOfWeeksSelected = it.numTimesSelected,
                forceSelection = it.forceSelection,
                omitSelection = it.forceOmission,
                googleId = it.id
            )
        }

        standupperRepository.saveAll(standuppers)
    }

    fun getStanduppers(): List<Standupper> {
        val standupperEntities = standupperRepository.findAll()
        val standupperWeeksSinceSelection = standupperEntities.map {
            timeCalculator.numberOfWeeksSince(it.lastConfirmedWeek)
        }
        val maxWeeksSinceSelection = standupperWeeksSinceSelection.max() ?: 0
        val maxNumberOfWeeksSelected = standupperEntities.map { it.numberOfWeeksSelected }.max() ?: 0
        val standupperProbabilities = standupperEntities.mapIndexed { index, standupperEntity ->
            probabilityCalculator.calculateStandupperProbability(
                numberOfWeeksStandupperSelected = standupperEntity.numberOfWeeksSelected,
                numberOfWeeksSinceLastStandupperConfirmation = standupperWeeksSinceSelection[index],
                maxNumberOfWeeksSelected = maxNumberOfWeeksSelected,
                maxNumberOfWeeksSinceConfirmation = maxWeeksSinceSelection
            )
        }

        return standupperEntities.mapIndexed { index, standupper ->
            Standupper(
                email = standupper.email,
                slackName = standupper.slackName,
                isForceOmitted = standupper.omitSelection,
                isForceSelected = standupper.forceSelection,
                selectionProbability = standupperProbabilities[index]
            )
        }
    }

    fun incrementSelection(standupper: Standupper) {
        standupperRepository.findBySlackName(standupper.slackName).let {
            standupperRepository.updateNumberOfWeeksSelected(it.id, it.numberOfWeeksSelected + 1)
        }
    }
}