package com.leggomymeggos.standupselector.admin

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "admin")
data class AdminEntity(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(
        name = "slack_name",
        nullable = false
    )
    val slackName: String,

    @Column(
        name = "email",
        nullable = false
    )
    val email: String
)