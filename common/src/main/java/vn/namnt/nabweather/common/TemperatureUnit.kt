package vn.namnt.nabweather.common

/**
 * @author namnt
 * @since 02/12/2022
 */
enum class TemperatureUnit {
    DEFAULT,
    METRIC,
    IMPERIAL
}

internal fun kelvinToCelsius(tempInK: Float) = tempInK - 273.15f
internal fun celsiusToKelvin(tempInC: Float) = tempInC + 273.15f
internal fun celsiusToFahrenheit(tempInC: Float) = tempInC * 9 / 5 + 32
internal fun fahrenheitToCelsius(tempInF: Float) = (tempInF - 32) * 5 / 9

fun Float.toDefaultTemperature(fromUnit: TemperatureUnit): Float = when (fromUnit) {
    TemperatureUnit.DEFAULT -> this
    TemperatureUnit.METRIC -> celsiusToKelvin(this)
    TemperatureUnit.IMPERIAL -> celsiusToKelvin(fahrenheitToCelsius(this))
}

fun Float.toMetricTemperature(fromUnit: TemperatureUnit): Float = when (fromUnit) {
    TemperatureUnit.DEFAULT -> kelvinToCelsius(this)
    TemperatureUnit.METRIC -> this
    TemperatureUnit.IMPERIAL -> fahrenheitToCelsius(this)
}

fun Float.toImperialTemperature(fromUnit: TemperatureUnit): Float = when (fromUnit) {
    TemperatureUnit.DEFAULT -> celsiusToFahrenheit(kelvinToCelsius(this))
    TemperatureUnit.METRIC -> celsiusToFahrenheit(this)
    TemperatureUnit.IMPERIAL -> this
}
