package vn.namnt.nabweather.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import vn.namnt.nabweather.data.local.database.entity.WeatherDatabaseTableName.CITY_LAST_UPDATED_TIME

/**
 * @author namnt
 * @since 03/12/2022
 */
@Entity(
    tableName = CITY_LAST_UPDATED_TIME
)
data class CityLastUpdatedTimeDBO(
    @PrimaryKey
    val city: String,
    @ColumnInfo(name = "last_modified")
    val lastModified: Long = System.currentTimeMillis()
)