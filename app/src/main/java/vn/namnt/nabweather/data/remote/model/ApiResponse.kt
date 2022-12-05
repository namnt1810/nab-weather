package vn.namnt.nabweather.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * @author namnt
 * @since 05/12/2022
 */
data class WeatherInfoResponse(
    // Common field
    @SerializedName("cod")
    val code: String,

    // Weather forecast specific
    val city: CityInfo,

    @SerializedName("cnt")
    val count: Int,

    val list: List<WeatherInfo>
) {
    data class CityInfo(
        val id: Int,
        val name: String
    )

    data class WeatherInfo(
        @SerializedName("dt")
        val date: Long,

        @SerializedName("temp.day")
        val temperature: Float = 0f,

        val pressure: Int,
        val humidity: Int,

        @SerializedName("weather.description")
        val description: String = ""
    )
}