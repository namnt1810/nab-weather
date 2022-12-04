package vn.namnt.nabweather.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import vn.namnt.nabweather.data.local.database.WeatherDatabase
import vn.namnt.nabweather.data.local.database.entity.CityLastUpdatedTimeDBO
import vn.namnt.nabweather.data.local.database.entity.WeatherInfoDBO
import javax.inject.Inject

class WeatherInfoLocalDatasourceImpl @Inject constructor(
    private val database: WeatherDatabase
) : WeatherInfoLocalDatasource {
    override suspend fun saveWeatherInfo(vararg items: WeatherInfoDBO) {
        withContext(Dispatchers.IO) {
            database.weatherDao().save(*items)
        }
    }

    override suspend fun getWeatherInfo(
        city: String,
        fromDate: Long,
        daysCount: Int
    ): List<WeatherInfoDBO> {
        return withContext(Dispatchers.IO) {
            database.weatherDao().getWeatherInfo(city, fromDate, daysCount)
        }
    }

    override suspend fun saveCityUpdateTime(city: String, lastUpdateTs: Long) {
        withContext(Dispatchers.IO) {
            database.cityUpdateTimeDao().save(CityLastUpdatedTimeDBO(city, lastUpdateTs))
        }
    }

    override suspend fun getCityLastUpdatedTime(city: String): Long {
        return withContext(Dispatchers.IO) {
            database.cityUpdateTimeDao().getCityLastUpdatedTime(city)
        }
    }

}