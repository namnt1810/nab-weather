package vn.namnt.nabweather.ui.main

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import vn.namnt.nabweather.common.TemperatureUnit
import vn.namnt.nabweather.entity.WeatherInfo
import vn.namnt.nabweather.repository.Result
import vn.namnt.nabweather.repository.WeatherInfoRepository
import vn.namnt.nabweather.ui.main.WeatherInfoUiState.Error
import vn.namnt.nabweather.ui.main.WeatherInfoUiState.Initial
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.TemporalField
import java.util.*

sealed class WeatherInfoUiState {
    object Initial : WeatherInfoUiState()
    object Loading : WeatherInfoUiState()
    data class Success(val list: List<WeatherInfo>) : WeatherInfoUiState()
    data class Error(val exception: Throwable) : WeatherInfoUiState()
}

class MainViewModel constructor(
    private val dispatcher: CoroutineDispatcher,
    private val repository: WeatherInfoRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<WeatherInfoUiState>(Initial)
    val uiState: StateFlow<WeatherInfoUiState> = _uiState

    fun getWeatherInfo(city: String) {
        viewModelScope.launch {
            if (!city.matches("^[a-zA-Z]{2,}$".toRegex())) {
                _uiState.emit(Error(IllegalArgumentException("Search query must be greater than 3 characters and contain only alphabet characters")))
                return@launch
            }

            _uiState.emit(WeatherInfoUiState.Loading)

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
                    _uiState.emit(Error(it))
                }.collect {
                    when (it) {
                        is Result.Success -> _uiState.emit(WeatherInfoUiState.Success(it.data))
                        is Result.Error -> _uiState.emit(Error(it.throwable))
                    }
                }

        }
    }
}
