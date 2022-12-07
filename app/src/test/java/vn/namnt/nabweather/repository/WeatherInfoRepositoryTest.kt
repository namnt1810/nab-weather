package vn.namnt.nabweather.repository

import com.google.gson.GsonBuilder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*
import vn.namnt.nabweather.common.TemperatureUnit.*
import vn.namnt.nabweather.data.local.WeatherInfoLocalDatasource
import vn.namnt.nabweather.data.local.database.entity.CityInfoDBO
import vn.namnt.nabweather.data.local.database.entity.WeatherInfoDBO
import vn.namnt.nabweather.data.remote.MockData
import vn.namnt.nabweather.data.remote.WeatherInfoRemoteDatasource
import vn.namnt.nabweather.data.remote.error.NoConnectivityException
import vn.namnt.nabweather.data.remote.error.NoInternetException
import vn.namnt.nabweather.data.remote.model.WeatherInfoResponse
import vn.namnt.nabweather.entity.WeatherInfo
import java.util.*

/**
 * @author namnt
 * @since 05/12/2022
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class WeatherInfoRepositoryTest {
    private val gson = GsonBuilder().create()
    private lateinit var calendar: Calendar

    lateinit var localDatasource: WeatherInfoLocalDatasource
    lateinit var remoteDatasource: WeatherInfoRemoteDatasource

    private lateinit var repository: WeatherInfoRepository

    @Before
    fun setup() {
        calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
    }

    @Test
    fun `fetch remote then store in local`() = runTest {
        localDatasource = mock()
        remoteDatasource = mock()
        repository = WeatherInfoRepositoryImpl(localDatasource, remoteDatasource)

        val remoteExpected =
            gson.fromJson(MockData.remoteResponseJson, WeatherInfoResponse::class.java)
        val localExpected = remoteExpected.list.map {
            WeatherInfoDBO(
                remoteExpected.city.id,
                remoteExpected.city.name,
                it.date,
                it.temperature.day.toDefaultTemperature(METRIC),
                it.temperature.day.toMetricTemperature(METRIC),
                it.temperature.day.toImperialTemperature(METRIC),
                it.pressure,
                it.humidity,
                it.detail.firstOrNull()?.description ?: ""
            )
        }
        val repoExpected = remoteExpected.list.map {
            WeatherInfo(
                it.date,
                it.temperature.day,
                METRIC,
                it.pressure,
                it.humidity,
                it.detail.firstOrNull()?.description ?: ""
            )
        }

        localDatasource.stub {
            onBlocking { getCityInfo("saigon") }.doReturn(null)
        }
        localDatasource.stub {
            onBlocking { getWeatherInfo(1580578, calendar.timeInMillis, 7) }.doReturn(localExpected)
        }
        remoteDatasource.stub {
            onBlocking { getWeatherInfo("saigon", 7, METRIC) }.doReturn(remoteExpected)
        }

        val requestTime = System.currentTimeMillis()

        val actual = repository.getWeatherInfo(
            "saigon",
            calendar.timeInMillis,
            METRIC,
            requestTime
        ).last()

        verify(localDatasource).getCityInfo("saigon")
        verify(localDatasource).saveCityInfo(CityInfoDBO("saigon", 1580578, "200", requestTime))
        verify(localDatasource).saveWeatherInfo(*localExpected.toTypedArray())
        verify(localDatasource).getWeatherInfo(1580578, calendar.timeInMillis, 7)

        assertEquals(repoExpected, actual.data)
    }

    @Test
    fun `get from local`() = runTest {
        localDatasource = mock()
        remoteDatasource = mock()
        repository = WeatherInfoRepositoryImpl(localDatasource, remoteDatasource)

        val remoteExpected =
            gson.fromJson(MockData.remoteResponseJson, WeatherInfoResponse::class.java)
        val repoExpected = remoteExpected.list.map {
            WeatherInfo(
                it.date,
                it.temperature.day,
                METRIC,
                it.pressure,
                it.humidity,
                it.detail.firstOrNull()?.description ?: ""
            )
        }

        val lastModified = System.currentTimeMillis() - 5 * 60 * 1000
        localDatasource.stub {
            onBlocking {
                getCityInfo("saigon")
            }.doReturn(CityInfoDBO("saigon", 1580578, "200", lastModified))
        }
        localDatasource.stub {
            onBlocking {
                getWeatherInfo(1580578, calendar.timeInMillis, 7)
            }.doReturn(MockData.localWeatherInfo)
        }

        val actual = repository.getWeatherInfo(
            "saigon",
            calendar.timeInMillis,
            METRIC,
            System.currentTimeMillis()
        ).last()

        verify(localDatasource).getCityInfo("saigon")
        verify(localDatasource).getWeatherInfo(1580578, calendar.timeInMillis, 7)
        assertEquals(repoExpected, actual.data)
    }

    @Test
    fun `no connectivity error`() = runTest {
        localDatasource = mock()
        remoteDatasource = mock()

        localDatasource.stub {
            onBlocking { getCityInfo("saigon") }.doReturn(null)
        }
        remoteDatasource.stub {
            onBlocking { getWeatherInfo("saigon", 7, DEFAULT) }.thenAnswer { throw NoConnectivityException() }
        }

        repository = WeatherInfoRepositoryImpl(localDatasource, remoteDatasource)

        val result = repository.getWeatherInfo("saigon", calendar.timeInMillis, DEFAULT, System.currentTimeMillis()).last()

        assertEquals(Result.Error::class, result::class)
        assertEquals(NoConnectivityException::class, (result as Result.Error).throwable::class)
    }

    @Test
    fun `no internet error`() = runTest {
        localDatasource = mock()
        remoteDatasource = mock()

        localDatasource.stub {
            onBlocking { getCityInfo("saigon") }.doReturn(null)
        }
        remoteDatasource.stub {
            onBlocking { getWeatherInfo("saigon", 7, DEFAULT) }.thenAnswer { throw NoInternetException() }
        }

        repository = WeatherInfoRepositoryImpl(localDatasource, remoteDatasource)

        val result = repository.getWeatherInfo("saigon", calendar.timeInMillis, DEFAULT, System.currentTimeMillis()).last()

        assertEquals(Result.Error::class, result::class)
        assertEquals(NoInternetException::class, (result as Result.Error).throwable::class)
    }

    @After
    fun teardown() {

    }
}