package vn.namnt.nabweather.entity

import vn.namnt.nabweather.common.TemperatureUnit

/**
 * @author namnt
 * @since 01/12/2022
 */
data class WeatherInfo(
    val date: Long,
    val averageTemp: Float,
    val tempUnit: TemperatureUnit,
    val pressure: Int,
    val humidity: Int,
    val description: String = ""
)