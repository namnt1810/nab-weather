package vn.namnt.nabweather.data.internal.remote.model

import com.google.gson.annotations.SerializedName

/**
 * @author namnt
 * @since 05/12/2022
 */
data class WeatherInfoResponse(
    // Weather forecast specific
    @SerializedName("city")
    val city: CityInfo,

    @SerializedName("cnt")
    val count: Int,

    @SerializedName("list")
    val list: List<WeatherInfo>
) {
    data class CityInfo(
        @SerializedName("id")
        val id: Int,

        @SerializedName("name")
        val name: String
    )

    data class WeatherInfo(
        @SerializedName("dt")
        val date: Long,

        @SerializedName("temp")
        val temperature: TemperatureInfo,

        @SerializedName("pressure")
        val pressure: Int,

        @SerializedName("humidity")
        val humidity: Int,

        @SerializedName("weather")
        val detail: List<WeatherDetail>
    ) {
        data class TemperatureInfo(
            @SerializedName("day")
            val day: Float
        )

        data class WeatherDetail(
            @SerializedName("description")
            val description: String
        )
    }
}
