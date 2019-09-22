package com.leggomymeggos.standupselector.calculators

import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.util.*

@Component
class TimeCalculator {

    fun numberOfWeeksSince(date: Date?): Long {
        if (date == null) {
            return Long.MAX_VALUE
        }

        val millisSinceLastConfirmation = Instant.now().toEpochMilli() - date.toInstant().toEpochMilli()
        val daysPassed = Duration.ofMillis(millisSinceLastConfirmation).toDays()

        return daysPassed / 7
    }
}