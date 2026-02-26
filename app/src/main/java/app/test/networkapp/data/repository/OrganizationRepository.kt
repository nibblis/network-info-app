package app.test.networkapp.data.repository

import app.test.networkapp.data.local.NetworkDao
import app.test.networkapp.data.local.OrganizationDao
import app.test.networkapp.data.models.exception.OrganizationNotFoundException
import app.test.networkapp.data.models.Network
import app.test.networkapp.data.models.Organization
import app.test.networkapp.data.remote.RipeApiService
import app.test.networkapp.utils.extracOrganizations
import app.test.networkapp.utils.extractNetworks
import kotlinx.coroutines.flow.Flow

class OrganizationRepository(
    private val apiService: RipeApiService,
    private val organizationDao: OrganizationDao,
    private val networkDao: NetworkDao,
) {
    fun getOrganizations(): Flow<List<Organization>> {
        // Этот метод напрямую возвращает Flow из Room.
        // UI будет автоматически обновляться при любых изменениях в таблице organizations.
        return organizationDao.getAll()
    }

    suspend fun searchOrganizationByName(name: String): List<Organization> {

        val cachedOrgs = organizationDao.searchByName(name)
        if (cachedOrgs.isNotEmpty()) return cachedOrgs

        try {
            val networkResult = apiService.searchOrganizations(name)
            val organizations = extracOrganizations(networkResult)
            organizationDao.insertAll(organizations)
            return organizations
        } catch (e: Exception) {
            return emptyList()
        }
    }

    suspend fun getOrganizationById(id: String): Organization {

        val cachedOrg = organizationDao.getById(id)
        if (cachedOrg != null) return cachedOrg

        try {
            val networkResult = apiService.getOrganizationById(id)
            val organizations = extracOrganizations(networkResult)
            if (organizations.isEmpty()) {
                throw OrganizationNotFoundException()
            }
            organizationDao.insert(organizations[0])
            return organizations[0]
        } catch (e: Exception) {
            throw OrganizationNotFoundException()
        }
    }

    suspend fun getOrganizationNetworks(orgId: String): List<Network> {
        val cachedNetworks = networkDao.getNetworksByOrganization(orgId)
        if (cachedNetworks.isNotEmpty()) return cachedNetworks

        try {
            val networkResult = apiService.searchNetworksByOrganization(orgId = orgId)
            val networks = extractNetworks(networkResult)
            networkDao.insertAll(networks)
            return networks
        } catch (e: Exception) {
            return emptyList()
        }
    }
}