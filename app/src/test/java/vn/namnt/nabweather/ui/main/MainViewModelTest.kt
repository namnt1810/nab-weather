package vn.namnt.nabweather.ui.main

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*
import vn.namnt.nabweather.TestCoroutineRule
import vn.namnt.nabweather.data.remote.MockData
import vn.namnt.nabweather.data.remote.error.NoInternetException
import vn.namnt.nabweather.entity.WeatherInfo
import vn.namnt.nabweather.repository.Result
import vn.namnt.nabweather.repository.WeatherInfoRepository
import vn.namnt.nabweather.repository.exception.DomainException
import vn.namnt.nabweather.ui.main.WeatherInfoUiState.*

/**
 * @author namnt
 * @since 07/12/2022
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    var testRule = TestCoroutineRule()

    private val coroutineDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: WeatherInfoRepository
    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        repository = mock()
        viewModel = MainViewModel(coroutineDispatcher, repository)
    }

    @Test
    fun `emit initial state on first creation`() = runTest {
        assertEquals(Initial, viewModel.uiState.value)
    }

    @Test
    fun `emit loading state while fetching data`() = runTest {
        val uiStates = mutableListOf<WeatherInfoUiState>()
        val job = launch(UnconfinedTestDispatcher(coroutineDispatcher.scheduler)) { viewModel.uiState.toList(uiStates) }

        repository.stub {
            onBlocking { getWeatherInfo(eq("any"), any(), any(), any()) }
                .doReturn(flowOf(Result.Success(emptyList())))
        }

        viewModel.getWeatherInfo("any")
        advanceUntilIdle()

        assertEquals(Loading, uiStates[1])

        job.cancel()
    }

    @Test
    fun `emit data state after fetch success with non-empty data`() = runTest {
        repository.stub {
            onBlocking { getWeatherInfo(eq("saigon"), any(), any(), any()) }
                .doReturn(flowOf(Result.Success(MockData.repositoryWeatherInfo)))
        }

        viewModel.getWeatherInfo("saigon")
        advanceUntilIdle()

        assertEquals(MockData.repositoryWeatherInfo, (viewModel.uiState.value as Success).list)
    }

    @Test
    fun `emit data state after fetch success with empty data`() = runTest {
        repository.stub {
            onBlocking { getWeatherInfo(any(), any(), any(), any()) }
                .doReturn(flowOf(Result.Success(emptyList())))
        }

        viewModel.getWeatherInfo("empty")
        advanceUntilIdle()

        assertEquals(emptyList<WeatherInfo>(), (viewModel.uiState.value as Success).list)
    }

    @Test
    fun `emit search query error when search with invalid value`() = runTest {
//        val uiStates = mutableListOf<WeatherInfoUiState>()
//        val job = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.toList(uiStates) }

        viewModel.getWeatherInfo("s")
        advanceUntilIdle()

        val last = viewModel.uiState.value

        assertEquals(Error::class, last::class)
        assertEquals(IllegalArgumentException::class, (last as Error).exception::class)

//        job.cancel()
    }

    @Test
    fun `emit connection error after fetching`() = runTest {
        repository.stub {
            onBlocking { getWeatherInfo(any(), any(), any(), any()) }
                .doReturn(flowOf(Result.Error(NoInternetException())))
        }

        viewModel.getWeatherInfo("any")
        advanceUntilIdle()

        assertEquals(Error::class, viewModel.uiState.value::class)
        assertEquals(NoInternetException::class, (viewModel.uiState.value as Error).exception::class)
    }

    @Test
    fun `emit 404 error`() = runTest {
        repository.stub {
            onBlocking { getWeatherInfo(any(), any(), any(), any()) }
                .doReturn(flowOf(Result.Error(DomainException("404"))))
        }

        viewModel.getWeatherInfo("notfound")
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(Error::class, state::class)
        assertEquals(DomainException::class, (state as Error).exception::class)
        assertEquals("404", (state.exception as DomainException).code)
    }

    @After
    fun teardown() {

    }
}