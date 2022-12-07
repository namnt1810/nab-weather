package vn.namnt.nabweather.data.local

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import vn.namnt.nabweather.data.local.database.WeatherDatabase
import vn.namnt.nabweather.data.local.database.entity.CityInfoDBO
import vn.namnt.nabweather.data.local.database.entity.WeatherInfoDBO
import javax.inject.Inject

internal class WeatherInfoLocalDatasourceImpl @Inject constructor(
    private val database: WeatherDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : WeatherInfoLocalDatasource {
    override suspend fun saveWeatherInfo(vararg items: WeatherInfoDBO) {
        withContext(dispatcher) {
            database.weatherDao().save(*items)
        }
    }

    override suspend fun getWeatherInfo(
        cityId: Int,
        fromDate: Long,
        daysCount: Int
    ): List<WeatherInfoDBO> {
        return withContext(dispatcher) {
            database.weatherDao().getWeatherInfo(cityId, fromDate, daysCount)
        }
    }

    override suspend fun saveCityInfo(dbo: CityInfoDBO) {
        withContext(dispatcher) {
            database.cityUpdateTimeDao().save(dbo)
        }
    }

    override suspend fun getCityInfo(city: String): CityInfoDBO? {
        return withContext(dispatcher) {
            database.cityUpdateTimeDao().getCityInfo(city)
        }
    }

}