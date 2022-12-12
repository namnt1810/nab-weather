package vn.namnt.nabweather.data.remote

import com.google.gson.GsonBuilder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vn.namnt.nabweather.TestCoroutineRule
import vn.namnt.nabweather.common.TemperatureUnit.*
import vn.namnt.nabweather.data.MockData
import vn.namnt.nabweather.data.WeatherInfoRemoteDatasource
import vn.namnt.nabweather.data.ApiErrorCodes
import vn.namnt.nabweather.data.internal.remote.interceptors.RequestInterceptor
import vn.namnt.nabweather.data.internal.remote.model.WeatherInfoResponse
import vn.namnt.nabweather.data.ApiException
import vn.namnt.nabweather.data.internal.remote.WeatherInfoRemoteDatasourceImpl
import vn.namnt.nabweather.data.internal.remote.toRemoteWeatherData

/**
 * @author namnt
 * @since 02/12/2022
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class WeatherInfoRemoteDatasourceTest {
    @get:Rule
    var testRule = TestCoroutineRule()

    private val coroutineDispatcher = UnconfinedTestDispatcher()

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

        datasource = WeatherInfoRemoteDatasourceImpl(retrofit, coroutineDispatcher)
    }

    @Test
    fun appIdIsCorrectTest() = runTest {
        mockWebServer.enqueue(MockResponse().setBody(MockData.remoteResponseJson))

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
        val expected = gson.fromJson(MockData.remoteResponseJson, WeatherInfoResponse::class.java).toRemoteWeatherData()

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
                .setResponseCode(ApiErrorCodes.API_KEY_ERROR)
                .setBody(
                    """
                {
                    "cod": "401"
                }
            """.trimIndent()
                )
        )

        try {
            datasource.getWeatherInfo("v", 7, DEFAULT)
        } catch (e: Exception) {
            assert(e is ApiException)
            assert((e as ApiException).code == ApiErrorCodes.API_KEY_ERROR)
        }
    }

    @Test
    fun invalidCityTest() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(ApiErrorCodes.NOT_FOUND)
                .setBody(
                    """
                {
                    "cod": "404"
                }
            """.trimIndent()
                )
        )

        try {
            datasource.getWeatherInfo("v", 7, DEFAULT)
        } catch (e: Exception) {
            assert(e is ApiException)
            assert((e as ApiException).code == ApiErrorCodes.NOT_FOUND)
        }
    }

    @Test
    fun emptyDataTest() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setBody(
                    """
                {
                    "city": {
                        "id": 1580578,
                        "name": "Ho Chi Minh City",
                        "coord": {
                            "lon": 106.6667,
                            "lat": 10.8333
                        },
                        "country": "VN",
                        "population": 0,
                        "timezone": 25200
                    },
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
                .setResponseCode(ApiErrorCodes.API_LIMIT_EXCEEDED)
                .setBody(
                    """
                {
                    "cod": "429"
                }
            """.trimIndent()
                )
        )

        try {
            datasource.getWeatherInfo("v", 7, DEFAULT)
        } catch (e: Exception) {
            assert(e is ApiException)
            assert((e as ApiException).code == ApiErrorCodes.API_LIMIT_EXCEEDED)
        }
    }

    @After
    fun teardown() = runTest {
        mockWebServer.shutdown()
    }
}
