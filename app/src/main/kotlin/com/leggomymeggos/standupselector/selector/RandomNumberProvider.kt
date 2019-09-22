package com.leggomymeggos.standupselector.selector

import org.springframework.stereotype.Component

@Component
class RandomNumberProvider {
    fun get() = Math.random()
}