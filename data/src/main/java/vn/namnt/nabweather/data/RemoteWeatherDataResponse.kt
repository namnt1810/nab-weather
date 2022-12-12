package vn.namnt.nabweather.data

/**
 * @author namnt
 * @since 11/12/2022
 */
data class RemoteWeatherDataResponse(
    val errorCode: Int,

    // Weather forecast specific
    val city: CityInfo?,

    val list: List<WeatherInfo>?
) {
    data class CityInfo(
        val id: Int,
        val name: String
    )

    data class WeatherInfo(
        val date: Long,

        val temperature: TemperatureInfo,

        val pressure: Int,
        val humidity: Int,

        val detail: List<WeatherDetail>
    ) {
        data class TemperatureInfo(
            val day: Float
        )

        data class WeatherDetail(
            val description: String
        )
    }
}