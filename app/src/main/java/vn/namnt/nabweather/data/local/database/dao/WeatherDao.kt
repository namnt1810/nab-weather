package vn.namnt.nabweather.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import vn.namnt.nabweather.data.local.database.entity.WeatherDatabaseTableName.WEATHER_INFO
import vn.namnt.nabweather.data.local.database.entity.WeatherInfoDBO

@Dao
interface WeatherDao {
    @Insert(onConflict = REPLACE)
    fun save(vararg items: WeatherInfoDBO)

    @Query(
        """
        SELECT * FROM $WEATHER_INFO WHERE city_id = :cityId AND date >= :fromDate ORDER BY date LIMIT :daysCount
    """
    )
    fun getWeatherInfo(cityId: Int, fromDate: Long, daysCount: Int): List<WeatherInfoDBO>
}
