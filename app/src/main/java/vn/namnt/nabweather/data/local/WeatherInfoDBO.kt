package vn.namnt.nabweather.data.local

import androidx.room.Entity

/**
 * @author namnt
 * @since 02/12/2022
 */
@Entity(
    tableName = "weather",
    primaryKeys = ["city", "date"]
)
data class WeatherInfoDBO(
    val city: String,
    val date: Long,
    val tempInK: Float? = null,
    val tempInC: Float? = null,
    val tempInF: Float? = null,
    val pressure: Int,
    val humidity: Int,
    val description: String = ""
)