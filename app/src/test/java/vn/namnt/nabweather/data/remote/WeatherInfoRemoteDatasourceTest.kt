package vn.namnt.nabweather.data.remote

import com.google.gson.GsonBuilder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vn.namnt.nabweather.common.TemperatureUnit.*
import vn.namnt.nabweather.data.remote.error.ApiErrorCodes
import vn.namnt.nabweather.data.remote.interceptors.RequestInterceptor
import vn.namnt.nabweather.data.remote.model.WeatherInfoResponse

/**
 * @author namnt
 * @since 02/12/2022
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class WeatherInfoRemoteDatasourceTest {
    private val testAppId = "123abc"
    private val gson = GsonBuilder().create()
    private lateinit var datasource: WeatherInfoRemoteDatasource
    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setup() {
        mockWebServer = MockWebServer()

        val client = OkHttpClient.Builder()
            .addInterceptor(RequestInterceptor(testAppId))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        datasource = WeatherInfoRemoteDatasourceImpl(retrofit)
    }

    @Test
    fun appIdIsCorrectTest() = runTest {
        mockWebServer.enqueue(MockResponse().setBody("{}"))

        datasource.getWeatherInfo("any", 1, DEFAULT)

        val request = mockWebServer.takeRequest()
        Assert.assertEquals(testAppId, request.requestUrl?.queryParameter("appid"))
    }

    @Test
    fun positiveResponseTest() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(MockData.remoteResponseJson)
        )

        val actual = datasource.getWeatherInfo("saigon", 7, METRIC)
        val expected = gson.fromJson(MockData.remoteResponseJson, WeatherInfoResponse::class.java)

        assert(actual.city.id == expected.city.id)

        val actualList = actual.list
        val expectedList = expected.list

        assert(actualList.size == expectedList.size)

        for (info in actualList) {
            assert(expectedList.contains(info))
        }
    }

    @Test
    fun apiKeyErrorTest() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setBody(
                    """
                {
                    "cod": "401"
                }
            """.trimIndent()
                )
        )

        val response = datasource.getWeatherInfo("v", 7, DEFAULT)

        Assert.assertEquals(ApiErrorCodes.API_KEY_ERROR, response.code)
    }

    @Test
    fun invalidCityTest() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setBody(
                    """
                {
                    "cod": "404"
                }
            """.trimIndent()
                )
        )

        val response = datasource.getWeatherInfo("v", 7, DEFAULT)

        Assert.assertEquals(ApiErrorCodes.NOT_FOUND, response.code)
    }

    @Test
    fun emptyDataTest() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setBody(
                    """
                {
                    "cod": "200",
                    "list": []
                }
            """.trimIndent()
                )
        )

        val response = datasource.getWeatherInfo("mytho", 7, DEFAULT)

        assert(response.list.isEmpty())
    }

    @Test
    fun apiCallExceededTest() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setBody(
                    """
                {
                    "cod": "429"
                }
            """.trimIndent()
                )
        )

        val response = datasource.getWeatherInfo("v", 7, DEFAULT)

        assert(response.code == ApiErrorCodes.API_LIMIT_EXCEEDED)
    }

    @After
    fun teardown() = runTest {
        mockWebServer.shutdown()
    }
}