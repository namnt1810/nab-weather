package vn.namnt.nabweather.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author namnt
 * @since 03/12/2022
 */
@Entity(
    tableName = "city_last_updated"
)
data class CityLastUpdatedTime(
    @PrimaryKey
    val city: String,
    val lastModified: Long = System.currentTimeMillis()
)