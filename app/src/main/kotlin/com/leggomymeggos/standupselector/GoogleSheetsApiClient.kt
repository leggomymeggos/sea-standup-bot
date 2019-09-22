package com.leggomymeggos.standupselector

import com.leggomymeggos.standupselector.admin.AdminResponse
import com.leggomymeggos.standupselector.standuppers.StandupperResponse
import com.leggomymeggos.standupselector.state.StateResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.net.URI

@Service
class GoogleSheetsApiClient(
    private val restTemplate: RestTemplate,
    @Value("\${google_sheets.url}") private val googleSheetsBaseUrl: String,
    @Value("\${google_sheets.token}") private val googleSheetsToken: String
) {
    fun getAdmins(): AdminResponse {
        val request = buildRequest("admin")

        return restTemplate.exchange(
            request,
            AdminResponse::class.java
        ).body as AdminResponse
    }

    fun getStanduppers(): StandupperResponse {
        val request = buildRequest("standuppers")

        return restTemplate.exchange(
            request,
            StandupperResponse::class.java
        ).body as StandupperResponse
    }

    fun getState(): StateResponse {
        val request = buildRequest("state")

        return restTemplate.exchange(
            request,
            StateResponse::class.java
        ).body as StateResponse
    }

    private fun buildRequest(sheetName: String) : RequestEntity<Void> {
        return RequestEntity.get(URI.create("$googleSheetsBaseUrl?sheet=$sheetName&token=$googleSheetsToken"))
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .build()
    }
}