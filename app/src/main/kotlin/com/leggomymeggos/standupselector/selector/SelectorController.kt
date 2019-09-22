package com.leggomymeggos.standupselector.selector

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SelectorController(
    private val selectorService: StandupSelectorService
) {

    @GetMapping("/populate")
    fun populateDatabase() {
        selectorService.loadData()
    }
}