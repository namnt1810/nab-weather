package vn.namnt.nabweather.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import vn.namnt.nabweather.data.local.database.dao.CityInfoDao
import vn.namnt.nabweather.data.local.database.dao.WeatherDao
import vn.namnt.nabweather.data.local.database.entity.CityInfoDBO
import vn.namnt.nabweather.data.local.database.entity.WeatherInfoDBO

@Database(
    entities = [WeatherInfoDBO::class, CityInfoDBO::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    abstract fun cityUpdateTimeDao(): CityInfoDao
}
