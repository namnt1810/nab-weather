package vn.namnt.nabweather.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import vn.namnt.nabweather.common.TemperatureUnit
import vn.namnt.nabweather.common.TemperatureUnit.*
import vn.namnt.nabweather.data.local.WeatherInfoLocalDatasource
import vn.namnt.nabweather.data.local.database.entity.CityInfoDBO
import vn.namnt.nabweather.data.local.database.entity.WeatherInfoDBO
import vn.namnt.nabweather.data.remote.WeatherInfoRemoteDatasource
import vn.namnt.nabweather.data.remote.error.ApiErrorCodes
import vn.namnt.nabweather.entity.WeatherInfo
import vn.namnt.nabweather.repository.exception.ApiException
import javax.inject.Inject

internal const val DEFAULT_TIME_COUNT = 7

internal class WeatherInfoRepositoryImpl @Inject constructor(
    private val localDatasource: WeatherInfoLocalDatasource,
    private val remoteDatasource: WeatherInfoRemoteDatasource
) : WeatherInfoRepository {
    override fun getWeatherInfo(
        city: String,
        fromDate: Long,
        temperatureUnit: TemperatureUnit,
        requestTime: Long
    ): Flow<Result<List<WeatherInfo>>> {
        return flow {
            try {
                var cityInfo = localDatasource.getCityInfo(city)

                if (cityInfo.needFetchRemoteInfo(requestTime)) {
                    val response =
                        remoteDatasource.getWeatherInfo(city, DEFAULT_TIME_COUNT, temperatureUnit)

                    val cityInfoDBO = CityInfoDBO(
                        city,
                        response.city.id,
                        ApiErrorCodes.SUCCESS,
                        requestTime
                    )

                    localDatasource.saveCityInfo(cityInfoDBO)

                    cityInfo = cityInfoDBO.copy()

                    val dboItems = response.list.map {
                        WeatherInfoDBO(
                            response.city.id,
                            response.city.name,
                            it.date * 1000,
                            it.temperature.day.toDefaultTemperature(temperatureUnit),
                            it.temperature.day.toMetricTemperature(temperatureUnit),
                            it.temperature.day.toImperialTemperature(temperatureUnit),
                            it.pressure,
                            it.humidity,
                            it.detail.firstOrNull()?.description ?: ""
                        )
                    }
                    localDatasource.saveWeatherInfo(*dboItems.toTypedArray())
                }

                if (cityInfo != null && !cityInfo.isActualRequestSuccess()) {
                    emit(Result.Error(ApiException(cityInfo.errorCode)))
                } else {
                    val weatherDboList = localDatasource.getWeatherInfo(
                        cityInfo!!.actualId!!,
                        fromDate,
                        DEFAULT_TIME_COUNT
                    )
                    val infoList = weatherDboList.map {
                        WeatherInfo(
                            it.date,
                            when (temperatureUnit) {
                                DEFAULT -> it.tempInK
                                METRIC -> it.tempInC
                                IMPERIAL -> it.tempInF
                            },
                            temperatureUnit,
                            it.pressure,
                            it.humidity,
                            it.description
                        )
                    }
                    emit(Result.Success(infoList))
                }
            } catch (e: Exception) {
                if (e is ApiException) {
                    val cityInfoDBO = CityInfoDBO(
                        city,
                        null,
                        e.code,
                        requestTime
                    )

                    localDatasource.saveCityInfo(cityInfoDBO)
                }

                emit(Result.Error(e))
            }
        }
    }

    override suspend fun cleanupWeatherInfo(): Int {
        return localDatasource.deleteObsoleteData()
    }
}

internal fun CityInfoDBO?.needFetchRemoteInfo(timeToCompare: Long): Boolean {
    if (this == null) {
        return true
    }

    // If last request's time is is more than 10 minute, return true
    return timeToCompare - lastModified > 10 * 60 * 1000
}

internal fun CityInfoDBO.isActualRequestSuccess(): Boolean = errorCode == ApiErrorCodes.SUCCESS

internal fun kelvinToCelsius(tempInK: Float) = tempInK - 273.15f
internal fun celsiusToKelvin(tempInC: Float) = tempInC + 273.15f
internal fun celsiusToFahrenheit(tempInC: Float) = tempInC * 9 / 5 + 32
internal fun fahrenheitToCelsius(tempInF: Float) = (tempInF - 32) * 5 / 9

internal fun Float.toDefaultTemperature(fromUnit: TemperatureUnit): Float = when (fromUnit) {
    DEFAULT -> this
    METRIC -> celsiusToKelvin(this)
    IMPERIAL -> celsiusToKelvin(fahrenheitToCelsius(this))
}

internal fun Float.toMetricTemperature(fromUnit: TemperatureUnit): Float = when (fromUnit) {
    DEFAULT -> kelvinToCelsius(this)
    METRIC -> this
    IMPERIAL -> fahrenheitToCelsius(this)
}

internal fun Float.toImperialTemperature(fromUnit: TemperatureUnit): Float = when (fromUnit) {
    DEFAULT -> celsiusToFahrenheit(kelvinToCelsius(this))
    METRIC -> celsiusToFahrenheit(this)
    IMPERIAL -> this
}
