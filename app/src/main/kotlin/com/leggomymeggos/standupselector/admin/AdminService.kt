package com.leggomymeggos.standupselector.admin

import com.leggomymeggos.standupselector.GoogleSheetsApiClient
import org.springframework.stereotype.Service

@Service
class AdminService(
    private val googleSheetsApiClient: GoogleSheetsApiClient,
    private val adminRepository: AdminRepository
) {
    fun populateAdmins() {
        val admins = googleSheetsApiClient.getAdmins().admins.map {
            AdminEntity(slackName = it.slackName, email = it.email)
        }
        adminRepository.saveAll(admins)
    }

    fun getAdmins(): List<Admin> {
        return adminRepository.findAll().map {
            Admin(slackName = it.slackName, email = it.email)
        }
    }
}