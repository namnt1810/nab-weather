package vn.namnt.nabweather.data

/**
 * @author namnt
 * @since 11/12/2022
 */
data class WeatherInfoData(
    val cityId: Int,
    val city: String,
    val date: Long,
    val tempInK: Float,
    val tempInC: Float,
    val tempInF: Float,
    val pressure: Int,
    val humidity: Int,
    val description: String = ""
)
