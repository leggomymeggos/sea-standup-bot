package com.leggomymeggos.standupselector.standuppers

import java.util.*
import javax.persistence.*

@Entity
@Table(name="standuppers")
data class StandupperEntity(
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
    val email: String,

    @Column(
        name = "last_confirmed_week",
        nullable = true
    )
    val lastConfirmedWeek: Date?,

    @Column(
        name = "number_of_weeks_selected",
        nullable = false
    )
    val numberOfWeeksSelected: Int,

    @Column(
        name = "force_selection",
        nullable = false
    )
    val forceSelection: Boolean,

    @Column(
        name = "omit_selection",
        nullable = false
    )
    val omitSelection: Boolean,

    @Column(
        name = "google_id",
        nullable = false
    )
    val googleId: Int
)