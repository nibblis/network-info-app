package app.test.networkapp.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Представляет организацию, полученную из сети или локальной базы данных.
 *
 * @property id Уникальный идентификатор организации.
 * @property name Название организации.
 * @property country Код страны (например, "USA"). Может быть null, если не указан.
 * @property created Дата создания записи об организации. Может быть null.
 */
@Entity(tableName = "organizations")
data class Organization(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "country")
    val country: String?,
    @ColumnInfo(name = "created")
    val created: Date?,
)
