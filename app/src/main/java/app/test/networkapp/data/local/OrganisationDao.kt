package app.test.networkapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.test.networkapp.data.models.Organization

@Dao
interface OrganizationDao {
    @Query("SELECT * FROM organizations WHERE name LIKE '%' || :name || '%'")
    suspend fun searchByName(name: String): List<Organization>

    @Query("SELECT * FROM organizations WHERE id = :orgId")
    suspend fun getById(orgId: String): Organization?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(organizations: List<Organization>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(organization: Organization)
}