package vn.namnt.nabweather.data.remote

import android.content.Context
import android.net.ConnectivityManager
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.GsonBuilder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vn.namnt.nabweather.TestCoroutineRule
import vn.namnt.nabweather.common.TemperatureUnit
import vn.namnt.nabweather.data.WeatherInfoRemoteDatasource
import vn.namnt.nabweather.data.NoConnectivityException
import vn.namnt.nabweather.data.internal.remote.WeatherInfoRemoteDatasourceImpl
import vn.namnt.nabweather.data.internal.remote.interceptors.NoConnectionInterceptor
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

/**
 * @author namnt
 * @since 05/12/2022
 */
@OptIn(ExperimentalCoroutinesApi::class)
@Config(
    sdk = [31],
//    manifest = "src/main/AndroidManifest.xml"
)
@RunWith(RobolectricTestRunner::class)
class WeatherInfoRemoteDatasourceConnectivityTest {
    @get:Rule
    var testRule = TestCoroutineRule()

    private val coroutineDispatcher = UnconfinedTestDispatcher()

    private val gson = GsonBuilder().create()
    private val mockWebServer = MockWebServer()
    private lateinit var datasource: WeatherInfoRemoteDatasource

    @Before
    fun setup() {
        val client = OkHttpClient.Builder()
            .addNetworkInterceptor(NoConnectionInterceptor(RuntimeEnvironment.getApplication()))
            .readTimeout(2, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        datasource = WeatherInfoRemoteDatasourceImpl(retrofit, coroutineDispatcher)
    }

    @Test(expected = NoConnectivityException::class)
    fun noConnectivityTest() = runTest {
        mockWebServer.enqueue(MockResponse().setBody("{}"))

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val connectivityManager =
            appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val shadowConnectivityManager = Shadows.shadowOf(connectivityManager)

        shadowConnectivityManager.setDefaultNetworkActive(false)

        datasource.getWeatherInfo("any", 1, TemperatureUnit.DEFAULT)
    }

    // TODO: simulate no internet connection test
//    @Test(expected = NoInternetException::class)
//    fun noInternetTest() = runTest {
//        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
//        val connectivityManager =
//            appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val shadowConnectivityManager = Shadows.shadowOf(connectivityManager)
//
//
//
//        datasource.getWeatherInfo("any", 1, TemperatureUnit.DEFAULT)
//    }

    @Test(expected = SocketTimeoutException::class)
    fun timeoutTest() = runTest {
        datasource.getWeatherInfo("any", 1, TemperatureUnit.DEFAULT)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }
}
