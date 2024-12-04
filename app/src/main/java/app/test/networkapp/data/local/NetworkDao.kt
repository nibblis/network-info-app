package app.test.networkapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.test.networkapp.data.models.Network

@Dao
interface NetworkDao {
    @Query("SELECT * FROM networks WHERE organization_id = :orgId")
    suspend fun getNetworksByOrganization(orgId: String): List<Network>

    @Query("SELECT * FROM networks WHERE inetnum LIKE '%' || :ipAddress || '%'")
    suspend fun searchByIp(ipAddress: String): List<Network>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(networks: List<Network>)
}