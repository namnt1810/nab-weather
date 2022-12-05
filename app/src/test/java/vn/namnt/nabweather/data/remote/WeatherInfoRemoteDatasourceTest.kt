package vn.namnt.nabweather.data.remote

import com.google.gson.GsonBuilder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import vn.namnt.nabweather.common.TemperatureUnit.*

/**
 * @author namnt
 * @since 02/12/2022
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class WeatherInfoRemoteDatasourceTest {

    private lateinit var datasource: WeatherInfoRemoteDatasource
    private val gson by lazy {
        GsonBuilder().create()
    }

    @Before
    fun setup() {
    }

    @Test
    fun positiveResponseTest() = runTest {
        val actual = datasource.getWeatherInfo("saigon", 7, METRIC)
        val expected = gson.fromJson(MockData.remoteResponseJson, WeatherInfoResponse::class.java)

        assert(actual.city.id == expected.city.id)

        val actualList = actual.list
        val expectedList = expected.list

        assert(actualList.size() == expectedList.size())

        for (info in actualList) {
            assert(expectedList.contains(info))
        }
    }

    @Test
    fun apiKeyErrorTest() = runTest {
        val response = datasource.getWeatherInfo("v", 7, DEFAULT)

        assert(response.code == ApiErrorCodes.API_KEY_ERROR)
    }

    @Test
    fun invalidCityTest() = runTest {
        val response = datasource.getWeatherInfo("v", 7, DEFAULT)

        assert(response.code == ApiErrorCodes.NOT_FOUND)
    }

    @Test
    fun emptyDataTest() = runTest {
        val response = datasource.getWeatherInfo("mytho", 7, DEFAULT)

        assert(response.city == "mytho")
        assert(response.list.size() == 0)
    }

    @Test
    fun noNetworkErrorTest() = runTest {
        Assert.assertThrows(NoConectivityException::class) {
            datasource.getWeatherInfo(
                "saigon",
                7,
                DEFAULT
            )
        }
    }

    @Test
    fun noInternetAccessTest() = runTest {
        Assert.assertThrows(NoInternetException::class) {
            datasource.getWeatherInfo(
                "saigon",
                7,
                DEFAULT
            )
        }
    }

    @Test
    fun apiCallExceededTest() = runTest {
        val response = datasource.getWeatherInfo("v", 7, DEFAULT)

        assert(response.code == ApiErrorCodes.API_LIMIT_EXCEEDED)
    }

    @After
    fun teardown() = runTest {

    }
}