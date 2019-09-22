package com.leggomymeggos.standupselector.state

import com.leggomymeggos.standupselector.GoogleSheetsApiClient
import com.leggomymeggos.standupselector.standuppers.Standupper
import org.springframework.stereotype.Service
import java.sql.Date

@Service
class StateService(
    val googleSheetsApiClient: GoogleSheetsApiClient,
    val stateRepository: StateRepository
) {
    fun populateState() {
        val stateEntities = googleSheetsApiClient.getState().stateValues.map {
            StateEntity(
                weekOf = it.weekOf,
                firstConfirmed = it.firstConfirmed,
                secondConfirmed = it.secondConfirmed,
                selected = it.selected,
                rejected = it.rejected,
                issuanceId = it.issuanceId,
                googleId = it.id
            )
        }

        stateRepository.saveAll(stateEntities)
    }

    fun recordSelection(standupper: Standupper) {
        stateRepository.getLatestState().let { entity ->
            val selected = entity.selected.split(",").filterNot { it.isEmpty() }.toMutableList()
            selected.add(standupper.slackName)
            stateRepository.updateSelectedForEntity(entity.id, selected.joinToString(","))
        }
    }

    fun currentWeek(): Date {
        return stateRepository.getLatestState().weekOf
    }
}