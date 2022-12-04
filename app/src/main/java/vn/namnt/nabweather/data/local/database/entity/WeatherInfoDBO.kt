package vn.namnt.nabweather.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import vn.namnt.nabweather.data.local.database.entity.WeatherDatabaseTableName.WEATHER_INFO

/**
 * @author namnt
 * @since 02/12/2022
 */
@Entity(
    tableName = WEATHER_INFO,
    primaryKeys = ["city", "date"]
)
data class WeatherInfoDBO(
    val city: String,
    val date: Long,
    @ColumnInfo(name = "temperature_default")
    val tempInK: Float? = null,
    @ColumnInfo(name = "temperature_metric")
    val tempInC: Float? = null,
    @ColumnInfo(name = "temperature_imperial")
    val tempInF: Float? = null,
    val pressure: Int,
    val humidity: Int,
    val description: String = ""
)