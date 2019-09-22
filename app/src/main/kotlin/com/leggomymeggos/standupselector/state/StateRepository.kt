package com.leggomymeggos.standupselector.state

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.*

interface StateRepository : CrudRepository<StateEntity, UUID> {

    @Query(value = "SELECT * FROM state ORDER BY week_of DESC LIMIT 1", nativeQuery = true)
    fun getLatestState(): StateEntity


    @Query("UPDATE StateEntity SET selected = :selected WHERE id = :id")
    fun updateSelectedForEntity(id: UUID, selected: String)
}