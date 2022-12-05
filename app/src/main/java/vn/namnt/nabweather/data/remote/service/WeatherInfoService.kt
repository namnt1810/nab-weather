package vn.namnt.nabweather.data.remote.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import vn.namnt.nabweather.data.remote.model.WeatherInfoResponse

/**
 * @author namnt
 * @since 05/12/2022
 */
internal interface WeatherInfoService {
    @GET("/daily")
    fun getWeatherForecast(
        @Query("q")
        city: String,
        @Query("cnt")
        days: Int,
        @Query("units")
        units: String = "default"
    ): Call<WeatherInfoResponse>
}