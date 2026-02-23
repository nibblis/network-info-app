package app.test.networkapp.data.repository

import app.test.networkapp.data.local.NetworkDao
import app.test.networkapp.data.models.exception.NetworkNotFoundException
import app.test.networkapp.data.models.Network
import app.test.networkapp.data.remote.RipeApiService
import app.test.networkapp.utils.extractNetworks

class NetworkRepository(
    private val apiService: RipeApiService,
    private val networkDao: NetworkDao
) {
    suspend fun searchByIp(ipAddress: String): List<Network> {

        val cachedNetworks = networkDao.searchByIp(ipAddress)
        if (cachedNetworks.isNotEmpty()) return cachedNetworks

        val networkResult = apiService.searchNetworkByIp(ipAddress)
        val networks = extractNetworks(networkResult)

        networkDao.insertAll(networks)

        return networks
    }

    suspend fun getNetworkByInetnum(inetnum: String): Network {
        val cachedNetwork = networkDao.searchByIp(inetnum).firstOrNull()
        return cachedNetwork ?: throw NetworkNotFoundException()
    }

    /**
     * Clears all network data from the local cache (database).
     */
    suspend fun clearCache() {
        networkDao.clearAll()
    }
}
