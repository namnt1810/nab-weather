package vn.namnt.nabweather.data.local

import vn.namnt.nabweather.data.local.database.WeatherDatabase
import vn.namnt.nabweather.data.local.database.entity.CityInfoDBO
import vn.namnt.nabweather.data.local.database.entity.WeatherInfoDBO
import javax.inject.Inject

internal class WeatherInfoLocalDatasourceImpl @Inject constructor(
    private val database: WeatherDatabase
) : WeatherInfoLocalDatasource {
    override suspend fun saveWeatherInfo(vararg items: WeatherInfoDBO) {
        database.cityWeatherDao().save(*items)
    }

    override suspend fun getWeatherInfo(
        cityId: Int,
        fromDate: Long,
        daysCount: Int
    ): List<WeatherInfoDBO> {
        return database.cityWeatherDao().getWeatherInfo(cityId, fromDate, daysCount)
    }

    override suspend fun saveCityInfo(dbo: CityInfoDBO) {
        database.cityWeatherDao().save(dbo)
    }

    override suspend fun getCityInfo(city: String): CityInfoDBO? {
        return database.cityWeatherDao().getCityInfo(city)
    }

    override suspend fun deleteObsoleteData(): Int {
        return database.cityWeatherDao().deleteObsoleteInfo()
    }
}
