package vn.namnt.nabweather.data.local

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * @author namnt
 * @since 02/12/2022
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class WeatherInfoLocalDatasourceTest {
    private lateinit var database: WeatherDatabase
    private lateinit var weatherDao: WeatherDao
    private lateinit var datasource: WeatherInfoLocalDatasource

    private val dbo = WeatherInfoDBO(
        "saigon",
        time,
        tempInC = 31f,
        pressure = 1001,
        humidity = 79,
        description = "light rain"
    )

    @Before
    fun setup() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(appContext, WeatherDatabase::class.java).build()

        datasource = WeatherInfoLocalDatasourceImpl(database)
    }

    @Test
    fun cityLastUpdatedTimeTest() = runTest {
        val time = System.currentTimeMillis()
        datasource.saveCityUpdateTime(CityLastUpdatedTime("saigon", time))

        assert(datasource.getCityLastUpdatedTime("saigon") == time)
    }

    @Test
    fun weatherInfoTest() = runTest {
        val time = System.currentTimeMillis()

        datasource.saveWeatherInfo(dbo)

        assert(datasource.getWeatherInfo("saigon", time, 1)[0] == dbo)
    }

    @After
    @Throws(IOException::class)
    fun teardown() {
        database.close()
    }
}