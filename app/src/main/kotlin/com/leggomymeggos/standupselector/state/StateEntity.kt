package com.leggomymeggos.standupselector.state

import java.util.*
import java.sql.Date
import javax.persistence.*

@Entity
@Table(name = "state")
data class StateEntity(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(
        nullable = false,
        name = "week_of"
    )
    val weekOf: Date,

    @Column(
        nullable = false,
        name = "first_confirmed"
    )
    val firstConfirmed: String = "not_confirmed",

    @Column(
        nullable = false,
        name = "second_confirmed"
    )
    val secondConfirmed: String = "not_confirmed",

    @Column(
        nullable = false,
        name = "selected"
    )
    val selected: String, // slacknames: birdo, daisy, peach, rosalina, wendy, toadette

    @Column(
        nullable = false,
        name = "rejected"
    )
    val rejected: String, // slacknames: rosalina, peach, wendy, toadette

    @Column(
        nullable = false,
        name = "issuance_id"
    )
    val issuanceId: Int,

    @Column(
        name = "google_id",
        nullable = false
    )
    val googleId: Int
)