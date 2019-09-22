package com.leggomymeggos.standupselector.admin

import com.leggomymeggos.standupselector.GoogleSheetsApiClient
import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class AdminServiceTest {
    private val googleSheetsApiClient = mock<GoogleSheetsApiClient>()
    private val adminRepository = mock<AdminRepository>()

    private val subject = AdminService(googleSheetsApiClient, adminRepository)

    @Test
    fun `populateAdmins fetches and saves admins`() {
        whenever(googleSheetsApiClient.getAdmins()).thenReturn(
            AdminResponse(
                listOf(
                    AdminValue(email = "e@mail.com", slackName = "slack-name")
                )
            )
        )

        subject.populateAdmins()

        verify(googleSheetsApiClient).getAdmins()

        val captor = argumentCaptor<List<AdminEntity>>()

        verify(adminRepository).saveAll(captor.capture())

        captor.firstValue.forEach {
            assertThat(it.email).isEqualTo("e@mail.com")
            assertThat(it.slackName).isEqualTo("slack-name")
        }
    }

    @Test
    fun `getAdmins gets admins`() {
        whenever(adminRepository.findAll()).thenReturn(listOf(
            AdminEntity(slackName = "slack-name", email = "e@mail.com"),
            AdminEntity(slackName = "other-name", email = "other@mail.com")
        ))

        val admins = subject.getAdmins()

        verify(adminRepository).findAll()
        assertThat(admins).containsExactly(
            Admin(slackName = "slack-name", email = "e@mail.com"),
            Admin(slackName = "other-name", email = "other@mail.com")
        )
    }
}