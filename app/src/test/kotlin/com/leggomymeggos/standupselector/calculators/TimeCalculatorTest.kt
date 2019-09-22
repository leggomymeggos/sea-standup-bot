package com.leggomymeggos.standupselector.calculators

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.Duration
import java.time.Instant
import java.util.*

class TimeCalculatorTest {

    private val subject = TimeCalculator()

    private val twentyWeeksAgo = Date.from(Instant.now().minus(Duration.ofDays(141)))
    private val threeWeeksAgo = Date.from(Instant.now().minus(Duration.ofDays(21)))
    private val twoWeeksAgo = Date.from(Instant.now().minus(Duration.ofDays(20)))
    private val thisWeek = Date()

    @Test
    fun `numberOfWeeksSince returns the number of weeks since the last standup run`() {
        assertThat(subject.numberOfWeeksSince(threeWeeksAgo)).isEqualTo(3)
        assertThat(subject.numberOfWeeksSince(twoWeeksAgo)).isEqualTo(2)
        assertThat(subject.numberOfWeeksSince(twentyWeeksAgo)).isEqualTo(20)
        assertThat(subject.numberOfWeeksSince(thisWeek)).isEqualTo(0)
    }

    @Test
    fun `numberOfWeeksSince returns max long when lastConfirmedWeek is null`() {
        assertThat(subject.numberOfWeeksSince(null)).isEqualTo(Long.MAX_VALUE)
    }
}