package vn.namnt.nabweather.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import vn.namnt.nabweather.data.local.database.entity.CityInfoDBO
import vn.namnt.nabweather.data.local.database.entity.WeatherDatabaseTableName.CITY_INFO

@Dao
interface CityInfoDao {
    @Insert(onConflict = REPLACE)
    fun save(dbo: CityInfoDBO)

    @Query(
        """
        SELECT * FROM $CITY_INFO WHERE city = :city 
    """
    )
    fun getCityInfo(city: String): CityInfoDBO
}
