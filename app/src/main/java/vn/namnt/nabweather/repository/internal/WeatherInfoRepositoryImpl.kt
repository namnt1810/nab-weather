package vn.namnt.nabweather.repository.internal

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import vn.namnt.nabweather.BuildConfig
import vn.namnt.nabweather.common.TemperatureUnit
import vn.namnt.nabweather.common.TemperatureUnit.*
import vn.namnt.nabweather.data.*
import vn.namnt.nabweather.repository.Result
import vn.namnt.nabweather.entity.WeatherInfo
import vn.namnt.nabweather.repository.WeatherInfoRepository
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

                    val cityInfoData = CityInfoData(
                        city,
                        response.city.id,
                        ApiErrorCodes.SUCCESS,
                        requestTime
                    )

                    localDatasource.saveCityInfo(cityInfoData)

                    cityInfo = cityInfoData.copy()

                    val dataList = response.list.map {
                        WeatherInfoData(
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
                    localDatasource.saveWeatherInfo(*dataList.toTypedArray())
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
                if (BuildConfig.DEBUG) {
                    Log.e(this::class.simpleName, e.message, e)
                }

                if (e is ApiException) {
                    val cityInfoData = CityInfoData(
                        city,
                        null,
                        e.code,
                        requestTime
                    )

                    localDatasource.saveCityInfo(cityInfoData)
                }

                emit(Result.Error(e))
            }
        }
    }

    override suspend fun cleanupWeatherInfo(): Int {
        return localDatasource.deleteObsoleteData()
    }
}

internal fun CityInfoData?.needFetchRemoteInfo(timeToCompare: Long): Boolean {
    if (this == null) {
        return true
    }

    // If last request's time is is more than 10 minute, return true
    return timeToCompare - lastModified > 10 * 60 * 1000
}

internal fun CityInfoData.isActualRequestSuccess(): Boolean = errorCode == ApiErrorCodes.SUCCESS

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
