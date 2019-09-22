package com.leggomymeggos.standupselector.selector

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class RandomNumberProviderTest {
    val subject = RandomNumberProvider()

    @Test
    fun `get provides a number greater than 0`() {
        for (i in 0..1000) {
            assertThat(subject.get()).isGreaterThan(0.0)
        }
    }

    @Test
    fun `get provides a number lesser than 1`() {
        for (i in 0..1000) {
            assertThat(subject.get()).isLessThan(1.0)
        }
    }
}