package com.leggomymeggos.standupselector.state

import org.springframework.data.repository.CrudRepository
import java.util.*

interface StateRepository : CrudRepository<StateEntity, UUID>