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
import vn.namnt.nabweather.data.local.database.WeatherDatabase
import vn.namnt.nabweather.data.local.database.entity.WeatherInfoDBO
import java.io.IOException

/**
 * @author namnt
 * @since 02/12/2022
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class WeatherInfoLocalDatasourceTest {
    private lateinit var database: WeatherDatabase
    private lateinit var datasource: WeatherInfoLocalDatasource

    @Before
    fun setup() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(appContext, WeatherDatabase::class.java).build()

        datasource = WeatherInfoLocalDatasourceImpl(database)
    }

    @Test
    fun cityLastUpdatedTimeTest() = runTest {
        val time = System.currentTimeMillis()
        datasource.saveCityUpdateTime("saigon", time)

        assert(datasource.getCityLastUpdatedTime("saigon") == time)
    }

    @Test
    fun replaceCityUpdatedTimeTest() = runTest {
        val time = System.currentTimeMillis()

        datasource.saveCityUpdateTime("saigon", time)

        val timeAfter = time + 10 * 60 * 1000 // 10 minutes

        datasource.saveCityUpdateTime("saigon", timeAfter)

        assert(datasource.getCityLastUpdatedTime("saigon") != time)
        assert(datasource.getCityLastUpdatedTime("saigon") == timeAfter)
    }

    @Test
    fun weatherInfoTest() = runTest {
        val time = System.currentTimeMillis()

        val dbo = WeatherInfoDBO(
            "saigon",
            time,
            tempInC = 31f,
            pressure = 1001,
            humidity = 79,
            description = "light rain"
        )

        datasource.saveWeatherInfo(dbo)

        assert(datasource.getWeatherInfo("saigon", time, 1)[0] == dbo)
    }

    @Test
    fun replaceWeatherInfoTest() = runTest {
        val time = System.currentTimeMillis()

        val previous = WeatherInfoDBO(
            "saigon",
            time,
            tempInC = 31f,
            pressure = 1001,
            humidity = 79,
            description = "light rain"
        )

        datasource.saveWeatherInfo(previous)

        val after = WeatherInfoDBO(
            "saigon",
            time,
            tempInC = 33f,
            pressure = 1011,
            humidity = 60,
            description = "sunny"
        )

        datasource.saveWeatherInfo(after)

        assert(datasource.getWeatherInfo("saigon", time, 1)[0] != previous)
        assert(datasource.getWeatherInfo("saigon", time, 1)[0] == after)
    }

    @After
    @Throws(IOException::class)
    fun teardown() {
        database.close()
    }
}