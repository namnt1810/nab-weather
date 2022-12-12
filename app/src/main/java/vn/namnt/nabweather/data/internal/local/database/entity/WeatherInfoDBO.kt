package vn.namnt.nabweather.data.internal.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import vn.namnt.nabweather.data.internal.local.database.entity.WeatherDatabaseTableName.WEATHER_INFO

/**
 * @author namnt
 * @since 02/12/2022
 */
@Entity(
    tableName = WEATHER_INFO,
    primaryKeys = ["city_id", "date"]
)
internal data class WeatherInfoDBO(
    @ColumnInfo(name = "city_id")
    val cityId: Int,
    val city: String,
    val date: Long,
    @ColumnInfo(name = "temperature_default")
    val tempInK: Float,
    @ColumnInfo(name = "temperature_metric")
    val tempInC: Float,
    @ColumnInfo(name = "temperature_imperial")
    val tempInF: Float,
    val pressure: Int,
    val humidity: Int,
    val description: String = ""
)