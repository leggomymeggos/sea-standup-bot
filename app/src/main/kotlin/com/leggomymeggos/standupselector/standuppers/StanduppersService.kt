package com.leggomymeggos.standupselector.standuppers

import com.leggomymeggos.standupselector.GoogleSheetsApiClient
import org.springframework.stereotype.Service

@Service
class StanduppersService(
    private val googleSheetsApiClient: GoogleSheetsApiClient,
    private val standupperRepository: StandupperRepository
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
}