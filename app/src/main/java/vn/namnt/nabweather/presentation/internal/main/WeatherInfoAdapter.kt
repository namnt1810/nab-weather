package vn.namnt.nabweather.presentation.internal.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import vn.namnt.nabweather.R
import vn.namnt.nabweather.databinding.ItemWeatherInfoBinding
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * @author namnt
 * @since 08/12/2022
 */

private val dateFormat = DateTimeFormatter.ofPattern("EEE, dd MMM uuuu")

class WeatherInfoAdapter : RecyclerView.Adapter<WeatherInfoAdapter.WeatherInfoVH>() {
    private val infoList = mutableListOf<vn.namnt.nabweather.entity.WeatherInfo>()

    override fun getItemCount(): Int = infoList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherInfoVH {
        val binding =
            ItemWeatherInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeatherInfoVH(binding)
    }

    override fun onBindViewHolder(holder: WeatherInfoVH, position: Int) {
        holder.bindData(infoList[position])
    }

    fun setData(list: List<vn.namnt.nabweather.entity.WeatherInfo>) {
        infoList.clear()

        infoList.addAll(list)

        notifyItemRangeChanged(0, infoList.size)
    }

    class WeatherInfoVH(
        private val binding: ItemWeatherInfoBinding
    ) : ViewHolder(binding.root) {

        fun bindData(info: vn.namnt.nabweather.entity.WeatherInfo) {
            val context = binding.root.context

            binding.apply {
                weatherDate.text = context.getString(
                    R.string.weather_info_date,
                    dateFormat.format(
                        ZonedDateTime.ofInstant(
                            Instant.ofEpochMilli(info.date),
                            ZoneId.systemDefault()
                        )
                    )
                )

                weatherTemperature.text = context.getString(
                    R.string.weather_info_temperature,
                    info.averageTemp,
                    info.temperatureSymbol
                )

                weatherPressure.text =
                    context.getString(R.string.weather_info_pressure, info.pressure)

                weatherHumidity.text =
                    context.getString(R.string.weather_info_humidity, info.humidity)

                weatherDescription.text =
                    context.getString(R.string.weather_info_description, info.description)

                root.contentDescription =
                    "${weatherDate.text}. ${weatherTemperature.text}. ${weatherPressure.text}. ${weatherHumidity.text}. ${weatherDescription.text}."
            }
        }
    }
}

private val vn.namnt.nabweather.entity.WeatherInfo.temperatureSymbol: String
    get() = when (tempUnit) {
        vn.namnt.nabweather.common.TemperatureUnit.DEFAULT -> "K"
        vn.namnt.nabweather.common.TemperatureUnit.METRIC -> "C"
        vn.namnt.nabweather.common.TemperatureUnit.IMPERIAL -> "F"
    }
