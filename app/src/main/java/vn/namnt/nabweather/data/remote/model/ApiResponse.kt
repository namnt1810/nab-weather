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

        @SerializedName("temp")
        val temperature: TemperatureInfo,

        val pressure: Int,
        val humidity: Int,

        @SerializedName("weather")
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

val WeatherInfoResponse.isSuccess: Boolean
    get() = this.code == "200"
