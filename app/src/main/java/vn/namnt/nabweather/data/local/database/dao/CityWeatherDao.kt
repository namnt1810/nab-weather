package vn.namnt.nabweather.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import vn.namnt.nabweather.data.local.database.entity.CityInfoDBO
import vn.namnt.nabweather.data.local.database.entity.WeatherDatabaseTableName
import vn.namnt.nabweather.data.local.database.entity.WeatherInfoDBO

/**
 * @author namnt
 * @since 10/12/2022
 */
@Dao
interface CityWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(dbo: CityInfoDBO)

    @Query(
        """
        SELECT * FROM ${WeatherDatabaseTableName.CITY_INFO} WHERE city = :city 
    """
    )
    suspend fun getCityInfo(city: String): CityInfoDBO?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(vararg items: WeatherInfoDBO)

    @Query(
        """
        SELECT * FROM ${WeatherDatabaseTableName.WEATHER_INFO} WHERE city_id = :cityId AND date >= :fromDate ORDER BY date LIMIT :daysCount
    """
    )
    suspend fun getWeatherInfo(cityId: Int, fromDate: Long, daysCount: Int): List<WeatherInfoDBO>

    @Query(
        """
            DELETE FROM ${WeatherDatabaseTableName.WEATHER_INFO}
            WHERE ROWID IN (
                SELECT w.ROWID
                FROM ${WeatherDatabaseTableName.WEATHER_INFO} w
                    INNER JOIN ${WeatherDatabaseTableName.CITY_INFO} c
                    ON (w.city_id = c.actual_city_id)
                WHERE strftime('%s', 'now') * 1000 - c.last_modified >= 10 * 60 * 1000
            );
        """
    )
    suspend fun deleteObsoleteInfo(): Int
}
