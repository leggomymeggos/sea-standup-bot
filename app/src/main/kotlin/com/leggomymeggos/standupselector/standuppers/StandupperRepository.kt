package com.leggomymeggos.standupselector.standuppers

import org.springframework.data.repository.CrudRepository
import java.util.*

interface StandupperRepository : CrudRepository<StandupperEntity, UUID>