package app.test.networkapp.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "networks")
data class Network(
    @PrimaryKey
    val inetnum: String,
    @ColumnInfo(name = "netname")
    val netname: String,
    @ColumnInfo(name = "country")
    val country: String?,
    @ColumnInfo(name = "organization_id")
    val organizationId: String
)