package vn.namnt.nabweather.presentation.main

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.launch
import vn.namnt.nabweather.R
import vn.namnt.nabweather.databinding.ActivityMainBinding
import vn.namnt.nabweather.entity.WeatherInfo
import vn.namnt.nabweather.presentation.utils.ErrorResolver
import vn.namnt.nabweather.presentation.utils.ViewModelFactory
import javax.inject.Inject

/**
 * @author namnt
 * @since 01/12/2022
 */
class MainActivity: DaggerAppCompatActivity() {
    @Inject
    internal lateinit var errorResolver: ErrorResolver

    @Inject
    lateinit var vmFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, vmFactory)[MainViewModel::class.java]
    }

    private lateinit var binding: ActivityMainBinding

    private lateinit var adapter: WeatherInfoAdapter

    private var snackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setupView()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.uiState.collect {
                    handleUiState(it)
                }
            }
        }
    }

    private fun setupView() {
        adapter = WeatherInfoAdapter()

        binding.weatherInfoList.also {
            it.layoutManager = LinearLayoutManager(applicationContext)
            it.adapter = adapter
            it.addItemDecoration(DividerItemDecoration(applicationContext, LinearLayout.VERTICAL))
        }

        binding.progressBar.hide()

        binding.searchButton.setOnClickListener { onSearchClicked(it) }
    }

    private fun onSearchClicked(view: View) {
        view.isEnabled = false

        snackbar?.dismiss()

        adapter.setData(emptyList())

        binding.apply {
            progressBar.hide()
            progressContainer.isVisible = false
        }

        val searchQuery = binding.searchInput.text?.toString() ?: return

        viewModel.getWeatherInfo(searchQuery)
    }

    private fun handleUiState(state: WeatherInfoUiState) {
        when (state) {
            is WeatherInfoUiState.Initial -> {} // Do nothing
            is WeatherInfoUiState.Loading -> handleLoadingState()
            is WeatherInfoUiState.Success -> handleDataState(state.list)
            is WeatherInfoUiState.Error -> handleErrorState(state)
        }
    }

    private fun handleLoadingState() {
        binding.weatherInfoList.isEnabled = false
        binding.progressContainer.isVisible = true
        binding.progressBar.show()
    }

    private fun handleDataState(infoList: List<WeatherInfo>) {
        binding.searchButton.isEnabled = true
        binding.weatherInfoList.isEnabled = true
        binding.progressBar.hide()
        binding.progressContainer.isVisible = false

        adapter.setData(infoList)
    }

    private fun handleErrorState(error: WeatherInfoUiState.Error) {
        binding.searchButton.isEnabled = true
        binding.weatherInfoList.isEnabled = true
        binding.progressBar.hide()
        binding.progressContainer.isVisible = false

        val message = when (error) {
            is WeatherInfoUiState.Error.InvalidInput -> getString(R.string.error_invalid_search_query)
            is WeatherInfoUiState.Error.WrappedException -> errorResolver.localizedErrorMessage(error.exception)
        }
        snackbar = Snackbar.make(binding.coordinator, message, LENGTH_LONG).also {
            it.show()
        }

        lifecycleScope.launch {
            snackbar?.view?.requestFocus()
        }
    }
}
