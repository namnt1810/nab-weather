package vn.namnt.nabweather.presentation.main

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
import vn.namnt.nabweather.domain.DomainErrorCodes
import vn.namnt.nabweather.domain.DomainException
import vn.namnt.nabweather.domain.Result
import vn.namnt.nabweather.presentation.internal.main.MainViewModel
import vn.namnt.nabweather.presentation.internal.main.WeatherInfoUiState
import vn.namnt.nabweather.presentation.internal.main.WeatherInfoUiState.*
import vn.namnt.nabweather.testcommon.MockData
import vn.namnt.nabweather.testcommon.TestCoroutineRule

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

    private lateinit var repository: vn.namnt.nabweather.domain.WeatherInfoRepository
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
        val job = launch(UnconfinedTestDispatcher(coroutineDispatcher.scheduler)) {
            viewModel.uiState.toList(uiStates)
        }

        repository.stub {
            onBlocking { getWeatherInfo(eq("any"), any(), any(), any()) }
                .doReturn(flowOf(Result.Success(emptyList())))
        }

        viewModel.getWeatherInfo("any")
        advanceUntilIdle()

        assertEquals(Loading::class, uiStates[1]::class)

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

        assertEquals(
            emptyList<vn.namnt.nabweather.entity.WeatherInfo>(),
            (viewModel.uiState.value as Success).list
        )
    }

    @Test
    fun `emit search query error when search with invalid value`() = runTest {
        viewModel.getWeatherInfo("vn")
        advanceUntilIdle()

        val last = viewModel.uiState.value

        assertEquals(Error.InvalidInput::class, last::class)
    }

    @Test
    fun `emit connection error after fetching`() = runTest {
        repository.stub {
            onBlocking { getWeatherInfo(any(), any(), any(), any()) }
                .doReturn(flowOf(Result.Error(DomainException(DomainErrorCodes.NO_INTERNET))))
        }

        viewModel.getWeatherInfo("any")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(Error.WrappedException::class, state::class)
        assertEquals(DomainException::class, (state as Error.WrappedException).exception::class)
        assertEquals(DomainErrorCodes.NO_INTERNET, (state.exception as DomainException).code)
    }

    @Test
    fun `emit 404 error`() = runTest {
        repository.stub {
            onBlocking { getWeatherInfo(any(), any(), any(), any()) }
                .doReturn(flowOf(Result.Error(DomainException(DomainErrorCodes.CITY_NOT_FOUND))))
        }

        viewModel.getWeatherInfo("notfound")
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(Error.WrappedException::class, state::class)
        assertEquals(DomainException::class, (state as Error.WrappedException).exception::class)
        assertEquals(DomainErrorCodes.CITY_NOT_FOUND, (state.exception as DomainException).code)
    }

    @After
    fun teardown() {

    }
}
