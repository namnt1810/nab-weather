package vn.namnt.nabweather.data.internal.local

import androidx.annotation.VisibleForTesting
import vn.namnt.nabweather.data.CityInfoData
import vn.namnt.nabweather.data.WeatherInfoData
import vn.namnt.nabweather.data.WeatherInfoLocalDatasource
import vn.namnt.nabweather.data.internal.local.database.WeatherDatabase
import vn.namnt.nabweather.data.internal.local.database.entity.CityInfoDBO
import vn.namnt.nabweather.data.internal.local.database.entity.WeatherInfoDBO
import javax.inject.Inject

internal class WeatherInfoLocalDatasourceImpl @Inject constructor(
    private val database: WeatherDatabase
) : WeatherInfoLocalDatasource {
    override suspend fun saveWeatherInfo(vararg items: WeatherInfoData) {
        val dboList = items.map { it.toDbo() }
        database.cityWeatherDao().save(*dboList.toTypedArray())
    }

    override suspend fun getWeatherInfo(
        cityId: Int,
        fromDate: Long,
        daysCount: Int
    ): List<WeatherInfoData> {
        val dbo = database.cityWeatherDao().getWeatherInfo(cityId, fromDate, daysCount)
        return dbo.map { it.toData() }
    }

    override suspend fun saveCityInfo(data: CityInfoData) {
        database.cityWeatherDao().save(data.toDbo())
    }

    override suspend fun getCityInfo(city: String): CityInfoData? {
        return database.cityWeatherDao().getCityInfo(city)?.toData()
    }

    override suspend fun deleteObsoleteData(): Int {
        return database.cityWeatherDao().deleteObsoleteInfo()
    }
}

@VisibleForTesting
internal fun WeatherInfoData.toDbo(): WeatherInfoDBO = WeatherInfoDBO(
    cityId, city, date, tempInK, tempInC, tempInF, pressure, humidity, description
)

@VisibleForTesting
internal fun CityInfoData.toDbo(): CityInfoDBO = CityInfoDBO(
    city, actualId, errorCode, lastModified
)

@VisibleForTesting
internal fun WeatherInfoDBO.toData() = WeatherInfoData(
    cityId, city, date, tempInK, tempInC, tempInF, pressure, humidity, description
)

@VisibleForTesting
internal fun CityInfoDBO.toData() = CityInfoData(
    city, actualId, errorCode, lastModified
)
