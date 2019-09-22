package com.leggomymeggos.standupselector.standuppers

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.*

interface StandupperRepository : CrudRepository<StandupperEntity, UUID> {
    fun findBySlackName(slackName: String): StandupperEntity

    @Query("UPDATE StandupperEntity SET numberOfWeeksSelected = :numberOfWeeksSelected WHERE id = :id")
    fun updateNumberOfWeeksSelected(id: UUID, numberOfWeeksSelected: Int)
}