package com.leggomymeggos.standupselector.state

import com.leggomymeggos.standupselector.GoogleSheetsApiClient
import org.springframework.stereotype.Service

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
}