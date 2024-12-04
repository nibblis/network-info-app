package app.test.networkapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import app.test.networkapp.data.models.Network
import app.test.networkapp.data.models.Organization

@Database(
    entities = [Organization::class, Network::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun organizationDao(): OrganizationDao
    abstract fun networkDao(): NetworkDao
}