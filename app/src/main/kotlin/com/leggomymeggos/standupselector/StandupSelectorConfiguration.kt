package com.leggomymeggos.standupselector

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class StandupSelectorConfiguration {

    @Bean
    fun restTemplate() = RestTemplate()
}