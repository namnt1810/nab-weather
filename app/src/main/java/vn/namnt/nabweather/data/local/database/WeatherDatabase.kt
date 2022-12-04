package vn.namnt.nabweather.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import vn.namnt.nabweather.data.local.database.dao.CityUpdateTimeDao
import vn.namnt.nabweather.data.local.database.dao.WeatherDao
import vn.namnt.nabweather.data.local.database.entity.CityLastUpdatedTimeDBO
import vn.namnt.nabweather.data.local.database.entity.WeatherInfoDBO

@Database(
    entities = [WeatherInfoDBO::class, CityLastUpdatedTimeDBO::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    abstract fun cityUpdateTimeDao(): CityUpdateTimeDao
}
