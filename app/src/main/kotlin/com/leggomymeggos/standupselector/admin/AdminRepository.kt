package com.leggomymeggos.standupselector.admin

import org.springframework.data.repository.CrudRepository
import java.util.*

interface AdminRepository: CrudRepository<AdminEntity, UUID> {

}