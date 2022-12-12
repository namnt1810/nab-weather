package vn.namnt.nabweather.data.local

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import vn.namnt.nabweather.common.TemperatureUnit
import vn.namnt.nabweather.data.WeatherInfoLocalDatasource
import vn.namnt.nabweather.data.internal.local.database.WeatherDatabase
import vn.namnt.nabweather.data.CityInfoData
import vn.namnt.nabweather.data.WeatherInfoData
import vn.namnt.nabweather.data.internal.local.WeatherInfoLocalDatasourceImpl
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

        val cityInfo = CityInfoData(
            "saigon",
            actualId = 1,
            200,
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

        val before = CityInfoData(
            "saigon",
            actualId = 1,
            200,
            lastModified = time
        )

        datasource.saveCityInfo(before)

        val timeAfter = time + 10 * 60 * 1000 // 10 minutes

        val after = CityInfoData(
            "saigon",
            actualId = 1,
            200,
            lastModified = timeAfter
        )

        datasource.saveCityInfo(after)

        Assert.assertEquals(after, datasource.getCityInfo("saigon"))
    }

    @Test
    fun weatherInfoTest() = runTest {
        val time = System.currentTimeMillis()

        val dbo = WeatherInfoData(
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

        val actual = datasource.getWeatherInfo(1, time, 1)[0]

        Assert.assertEquals(dbo, actual)
    }

    @Test
    fun replaceWeatherInfoTest() = runTest {
        val time = System.currentTimeMillis()

        val previous = WeatherInfoData(
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

        val after = WeatherInfoData(
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

        val actual = datasource.getWeatherInfo(1, time, 1)[0]

        Assert.assertNotEquals(previous, actual)
        Assert.assertEquals(after, actual)
    }

    @Test
    fun deleteObsoleteDataTest() = runTest {
        val now = System.currentTimeMillis()

        val oldCity = CityInfoData("saigon", 1, 200, now - 20 * 60 * 1000)
        datasource.saveCityInfo(oldCity)
        advanceUntilIdle()

        val oldCityWeather = WeatherInfoData(oldCity.actualId!!, "Ho Chi Minh", oldCity.lastModified, 0f, 0f, 0f, 0, 0)
        datasource.saveWeatherInfo(oldCityWeather)
        advanceUntilIdle()

        val newCity = CityInfoData("hanoi", 2, 200, now)
        datasource.saveCityInfo(newCity)
        advanceUntilIdle()

        val newCityWeather = WeatherInfoData(newCity.actualId!!, "Ha Noi", newCity.lastModified, 0f, 0f, 0f, 0, 0)
        datasource.saveWeatherInfo(newCityWeather)
        advanceUntilIdle()

        datasource.deleteObsoleteData()
        advanceUntilIdle()

        Assert.assertEquals(0, datasource.getWeatherInfo(1, oldCity.lastModified, 1).size)
        Assert.assertEquals(1, datasource.getWeatherInfo(2, newCity.lastModified, 1).size)
    }

    @After
    @Throws(IOException::class)
    fun teardown() {
        database.close()
    }
}
