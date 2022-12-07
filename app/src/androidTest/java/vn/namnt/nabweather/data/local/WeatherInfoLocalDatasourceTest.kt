package vn.namnt.nabweather.data.local

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import vn.namnt.nabweather.common.TemperatureUnit
import vn.namnt.nabweather.data.local.database.WeatherDatabase
import vn.namnt.nabweather.data.local.database.entity.CityInfoDBO
import vn.namnt.nabweather.data.local.database.entity.WeatherInfoDBO
import vn.namnt.nabweather.repository.toDefaultTemperature
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
    fun getSetCityInfoTest() = runTest {
        val time = System.currentTimeMillis()

        val cityInfo = CityInfoDBO(
            "saigon",
            actualId = 1,
            lastModified = time
        )

        datasource.saveCityInfo(cityInfo)

        Assert.assertEquals(cityInfo, datasource.getCityInfo("saigon"))
    }

    @Test
    fun nonExistCityInfoTest() = runTest {
        Assert.assertNull(datasource.getCityInfo("saigon"))
    }

    @Test
    fun replaceCityInfoTest() = runTest {
        val time = System.currentTimeMillis()

        val before = CityInfoDBO(
            "saigon",
            actualId = 1,
            lastModified = time
        )

        datasource.saveCityInfo(before)

        val timeAfter = time + 10 * 60 * 1000 // 10 minutes

        val after = CityInfoDBO(
            "saigon",
            actualId = 1,
            lastModified = timeAfter
        )

        datasource.saveCityInfo(after)

        Assert.assertEquals(after, datasource.getCityInfo("saigon"))
    }

    @Test
    fun weatherInfoTest() = runTest {
        val time = System.currentTimeMillis()

        val dbo = WeatherInfoDBO(
            1,
            "saigon",
            time,
            tempInK = 31f.toDefaultTemperature(TemperatureUnit.METRIC),
            tempInC = 31f,
            tempInF = 31f.toDefaultTemperature(TemperatureUnit.METRIC),
            pressure = 1001,
            humidity = 79,
            description = "light rain"
        )

        datasource.saveWeatherInfo(dbo)

        assert(datasource.getWeatherInfo(1, time, 1)[0] == dbo)
    }

    @Test
    fun replaceWeatherInfoTest() = runTest {
        val time = System.currentTimeMillis()

        val previous = WeatherInfoDBO(
            1,
            "saigon",
            time,
            tempInK = 31f.toDefaultTemperature(TemperatureUnit.METRIC),
            tempInC = 31f,
            tempInF = 31f.toDefaultTemperature(TemperatureUnit.METRIC),
            pressure = 1001,
            humidity = 79,
            description = "light rain"
        )

        datasource.saveWeatherInfo(previous)

        val after = WeatherInfoDBO(
            1,
            "saigon",
            time,
            tempInK = 33f.toDefaultTemperature(TemperatureUnit.METRIC),
            tempInC = 33f,
            tempInF = 33f.toDefaultTemperature(TemperatureUnit.METRIC),
            pressure = 1011,
            humidity = 60,
            description = "sunny"
        )

        datasource.saveWeatherInfo(after)

        assert(datasource.getWeatherInfo(1, time, 1)[0] != previous)
        assert(datasource.getWeatherInfo(1, time, 1)[0] == after)
    }

    @After
    @Throws(IOException::class)
    fun teardown() {
        database.close()
    }
}