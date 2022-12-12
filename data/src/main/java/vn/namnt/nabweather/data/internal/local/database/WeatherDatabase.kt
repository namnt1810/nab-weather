package vn.namnt.nabweather.data.internal.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import vn.namnt.nabweather.data.internal.local.database.dao.CityWeatherDao
import vn.namnt.nabweather.data.internal.local.database.entity.CityInfoDBO
import vn.namnt.nabweather.data.internal.local.database.entity.WeatherInfoDBO

@Database(
    entities = [WeatherInfoDBO::class, CityInfoDBO::class],
    version = 1,
    exportSchema = false
)
internal abstract class WeatherDatabase : RoomDatabase() {
    abstract fun cityWeatherDao(): CityWeatherDao
}
