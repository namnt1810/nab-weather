package vn.namnt.nabweather.presentation.internal.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import vn.namnt.nabweather.common.TemperatureUnit
import vn.namnt.nabweather.domain.BuildConfig
import vn.namnt.nabweather.domain.Result
import vn.namnt.nabweather.domain.WeatherInfoRepository
import vn.namnt.nabweather.entity.WeatherInfo
import vn.namnt.nabweather.presentation.internal.main.WeatherInfoUiState.Error
import vn.namnt.nabweather.presentation.internal.main.WeatherInfoUiState.Initial
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

sealed class WeatherInfoUiState {
    object Initial : WeatherInfoUiState()
    class Loading : WeatherInfoUiState()
    data class Success(val list: List<WeatherInfo>) : WeatherInfoUiState()
    sealed class Error : WeatherInfoUiState() {
        class InvalidInput : Error()
        data class WrappedException(val exception: Throwable) : Error()
    }
}

class MainViewModel @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val repository: WeatherInfoRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<WeatherInfoUiState>(Initial)
    val uiState: StateFlow<WeatherInfoUiState> = _uiState

    fun getWeatherInfo(city: String) {
        viewModelScope.launch {
            if (!city.matches("^[a-zA-Z ]{3,}$".toRegex())) {
                _uiState.emit(Error.InvalidInput())
                return@launch
            }

            _uiState.emit(WeatherInfoUiState.Loading())

            repository.getWeatherInfo(
                city,
                LocalDateTime.now()
                    .with(LocalTime.MIDNIGHT)
                    .toInstant(ZoneId.systemDefault().rules.getOffset(Instant.now()))
                    .toEpochMilli(),
                TemperatureUnit.METRIC,
                Instant.now().toEpochMilli()
            ).flowOn(dispatcher)
                .catch {
                    if (BuildConfig.DEBUG) {
                        Log.e(this::class.simpleName, it.message, it)
                    }

                    _uiState.emit(Error.WrappedException(it))
                }.collect {
                    when (it) {
                        is Result.Success -> _uiState.emit(WeatherInfoUiState.Success(it.data))
                        is Result.Error -> {
                            if (BuildConfig.DEBUG) {
                                Log.e(this::class.simpleName, it.throwable.message, it.throwable)
                            }

                            _uiState.emit(Error.WrappedException(it.throwable))
                        }
                    }
                }
        }
    }
}
