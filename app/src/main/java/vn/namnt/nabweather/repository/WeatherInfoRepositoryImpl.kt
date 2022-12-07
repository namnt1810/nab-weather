package vn.namnt.nabweather.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import vn.namnt.nabweather.common.TemperatureUnit
import vn.namnt.nabweather.common.TemperatureUnit.*
import vn.namnt.nabweather.data.local.WeatherInfoLocalDatasource
import vn.namnt.nabweather.data.local.database.entity.CityInfoDBO
import vn.namnt.nabweather.data.local.database.entity.WeatherInfoDBO
import vn.namnt.nabweather.data.remote.WeatherInfoRemoteDatasource
import vn.namnt.nabweather.data.remote.model.isSuccess
import vn.namnt.nabweather.entity.WeatherInfo
import vn.namnt.nabweather.repository.exception.DomainException

internal const val DEFAULT_TIME_COUNT = 7

internal class WeatherInfoRepositoryImpl constructor(
    private val localDatasource: WeatherInfoLocalDatasource,
    private val remoteDatasource: WeatherInfoRemoteDatasource
) : WeatherInfoRepository {
    override suspend fun getWeatherInfo(
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
                        if (response.isSuccess) response.city.id else null,
                        response.code,
                        requestTime
                    )

                    localDatasource.saveCityInfo(cityInfoDBO)

                    cityInfo = cityInfoDBO.copy()

                    if (response.isSuccess) {
                        val dboItems = response.list.map {
                            WeatherInfoDBO(
                                response.city.id,
                                response.city.name,
                                it.date,
                                it.temperature.day.toDefaultTemperature(temperatureUnit),
                                it.temperature.day.toMetricTemperature(temperatureUnit),
                                it.temperature.day.toImperialTemperature(temperatureUnit),
                                it.pressure,
                                it.humidity,
                                it.detail.firstOrNull()?.description ?: ""
                            )
                        }
                        localDatasource.saveWeatherInfo(*dboItems.toTypedArray())
                    } else {
                        throw DomainException(response.code)
                    }
                }

                if (cityInfo != null && !cityInfo.isActualRequestSuccess()) {
                    emit(Result.Error(DomainException(cityInfo.errorCode)))
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
                emit(Result.Error(e))
            }
        }
    }
}

internal fun CityInfoDBO?.needFetchRemoteInfo(timeToCompare: Long): Boolean {
    if (this == null) {
        return true
    }

    // If last request's time is is more than 10 minute, return true
    return timeToCompare - lastModified > 10 * 60 * 1000
}

internal fun CityInfoDBO.isActualRequestSuccess(): Boolean = errorCode == "200"

internal fun kelvinToCelcius(tempInK: Float) = tempInK - 273.15f
internal fun celciusToKelvin(tempInC: Float) = tempInC + 273.15f
internal fun celciusToFahrenheit(tempInC: Float) = tempInC * 9 / 5 + 32
internal fun fahrenheitToCelcius(tempInF: Float) = (tempInF - 32) * 5 / 9

internal fun Float.toDefaultTemperature(fromUnit: TemperatureUnit): Float = when (fromUnit) {
    DEFAULT -> this
    METRIC -> celciusToKelvin(this)
    IMPERIAL -> celciusToKelvin(fahrenheitToCelcius(this))
}

internal fun Float.toMetricTemperature(fromUnit: TemperatureUnit): Float = when (fromUnit) {
    DEFAULT -> kelvinToCelcius(this)
    METRIC -> this
    IMPERIAL -> fahrenheitToCelcius(this)
}

internal fun Float.toImperialTemperature(fromUnit: TemperatureUnit): Float = when (fromUnit) {
    DEFAULT -> celciusToFahrenheit(kelvinToCelcius(this))
    METRIC -> celciusToFahrenheit(this)
    IMPERIAL -> this
}
