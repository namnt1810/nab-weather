package vn.namnt.nabweather.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import vn.namnt.nabweather.data.local.database.entity.CityLastUpdatedTimeDBO
import vn.namnt.nabweather.data.local.database.entity.WeatherDatabaseTableName.CITY_LAST_UPDATED_TIME

@Dao
interface CityUpdateTimeDao {
    @Insert(onConflict = REPLACE)
    fun save(dbo: CityLastUpdatedTimeDBO)

    @Query(
        """
        SELECT last_modified FROM $CITY_LAST_UPDATED_TIME WHERE city = :city 
    """
    )
    fun getCityLastUpdatedTime(city: String): Long
}
