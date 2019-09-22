package com.leggomymeggos.standupselector.selector

import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SelectorController(
    private val selectorService: AppService
) {

    @GetMapping("/populate")
    fun populateDatabase() {
        selectorService.loadData()
    }

    @GetMapping("/trigger-selection")
    fun triggerSelection(): ResponseEntity<String> {
        selectorService.pickWeeklyStanduppers()
        return ResponseEntity.ok("""{"message":"picked the standuppers!!"}""")
    }

    @PostMapping("/handle-interaction")
    fun handleInteraction(request: RequestEntity<String>): ResponseEntity<String> {
        return ResponseEntity.ok("""{"message":"made it to the server!!"}""")
    }
}