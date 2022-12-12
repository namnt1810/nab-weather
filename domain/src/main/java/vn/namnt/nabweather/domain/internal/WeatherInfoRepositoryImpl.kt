package vn.namnt.nabweather.domain.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import vn.namnt.nabweather.common.TemperatureUnit
import vn.namnt.nabweather.common.TemperatureUnit.*
import vn.namnt.nabweather.common.toDefaultTemperature
import vn.namnt.nabweather.common.toImperialTemperature
import vn.namnt.nabweather.common.toMetricTemperature
import vn.namnt.nabweather.data.*
import vn.namnt.nabweather.domain.DomainErrorCodes.API_KEY_ERROR
import vn.namnt.nabweather.domain.DomainErrorCodes.API_LIMIT_EXCEEDED
import vn.namnt.nabweather.domain.DomainErrorCodes.CITY_NOT_FOUND
import vn.namnt.nabweather.domain.DomainErrorCodes.NO_CONNECTIVITY
import vn.namnt.nabweather.domain.DomainErrorCodes.NO_INTERNET
import vn.namnt.nabweather.domain.DomainErrorCodes.SUCCESS
import vn.namnt.nabweather.domain.DomainErrorCodes.UNKNOWN
import vn.namnt.nabweather.domain.DomainException
import vn.namnt.nabweather.domain.Result
import vn.namnt.nabweather.domain.WeatherInfoRepository
import vn.namnt.nabweather.entity.WeatherInfo
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
                    fetchThenStoreRemoteData(city, temperatureUnit, requestTime)
                    cityInfo = localDatasource.getCityInfo(city)
                }

                if (cityInfo != null && !cityInfo.isActualRequestSuccess()) {
                    emit(Result.Error(DomainException(cityInfo.errorCode.toDomainErrorCode())))
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
                val domainException = when (e) {
                    is DomainException -> e
                    is NoInternetException -> DomainException(NO_INTERNET)
                    is NoConnectivityException -> DomainException(NO_CONNECTIVITY)
                    else -> DomainException(UNKNOWN, e.message, e)
                }

                emit(Result.Error(domainException))
            }
        }
    }

    private suspend fun fetchThenStoreRemoteData(
        city: String,
        temperatureUnit: TemperatureUnit,
        requestTime: Long
    ) {
        val response =
            remoteDatasource.getWeatherInfo(
                city,
                DEFAULT_TIME_COUNT,
                temperatureUnit
            )

        val cityInfoData = CityInfoData(
            city,
            response.city?.id,
            response.errorCode,
            requestTime
        )

        localDatasource.saveCityInfo(cityInfoData)

        if (response.errorCode != ApiErrorCodes.SUCCESS) {
            throw DomainException(response.errorCode.toDomainErrorCode())
        }

        val dataList = response.list!!.map {
            WeatherInfoData(
                response.city!!.id,
                response.city!!.name,
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

internal fun Int.toDomainErrorCode(): Int {
    return when (this) {
        ApiErrorCodes.SUCCESS -> SUCCESS
        ApiErrorCodes.NOT_FOUND -> CITY_NOT_FOUND
        ApiErrorCodes.API_KEY_ERROR -> API_KEY_ERROR
        ApiErrorCodes.API_LIMIT_EXCEEDED -> API_LIMIT_EXCEEDED
        else -> UNKNOWN
    }
}
