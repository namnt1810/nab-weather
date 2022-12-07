package vn.namnt.nabweather.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import vn.namnt.nabweather.data.local.database.entity.WeatherDatabaseTableName.CITY_INFO

/**
 * @author namnt
 * @since 03/12/2022
 */
@Entity(
    tableName = CITY_INFO
)
data class CityInfoDBO(
    @PrimaryKey
    val city: String,
    @ColumnInfo(name = "actual_city_id")
    val actualId: Int? = null,
    @ColumnInfo(name = "last_modified")
    val lastModified: Long = System.currentTimeMillis()
)