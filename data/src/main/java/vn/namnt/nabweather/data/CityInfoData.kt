package vn.namnt.nabweather.data

/**
 * @author namnt
 * @since 11/12/2022
 */
data class CityInfoData(
    val city: String,
    val actualId: Int? = null,
    val errorCode: Int,
    val lastModified: Long = System.currentTimeMillis()
)
