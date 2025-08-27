package com.example.mweather.presentation.ui.forecast

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mweather.data.models.ForecastHour
import com.example.mweather.databinding.ItemForecastBinding
import java.text.SimpleDateFormat
import java.util.*

class ForecastAdapter : ListAdapter<ForecastHour, ForecastAdapter.ForecastViewHolder>(ForecastDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val binding = ItemForecastBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ForecastViewHolder(private val binding: ItemForecastBinding) : RecyclerView.ViewHolder(binding.root) {
        
        private val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        fun bind(item: ForecastHour) {
            val date = Date(item.timeEpoch * 1000)
            
            binding.apply {
                tvDate.text = if (isToday(date)) "Today" else dateFormat.format(date)
                tvTime.text = timeFormat.format(date)
                tvDescription.text = item.condition.text
                tvTemp.text = "${item.tempC.toInt()}°C"
                tvFeelsLike.text = "Feels ${item.feelslikeC.toInt()}°C"
                tvHumidity.text = "💧 ${item.humidity}%"
                tvWind.text = "💨 ${String.format("%.1f", item.windKph / 3.6)} m/s" // Convert to m/s

                val iconUrl = "https:${item.condition.icon}"
                Glide.with(itemView.context)
                    .load(iconUrl)
                    .into(ivWeatherIcon)
            }
        }

        private fun isToday(date: Date): Boolean {
            val today = Calendar.getInstance()
            val itemDate = Calendar.getInstance().apply { time = date }
            return today.get(Calendar.YEAR) == itemDate.get(Calendar.YEAR) &&
                   today.get(Calendar.DAY_OF_YEAR) == itemDate.get(Calendar.DAY_OF_YEAR)
        }
    }

    class ForecastDiffCallback : DiffUtil.ItemCallback<ForecastHour>() {
        override fun areItemsTheSame(oldItem: ForecastHour, newItem: ForecastHour): Boolean {
            return oldItem.timeEpoch == newItem.timeEpoch
        }

        override fun areContentsTheSame(oldItem: ForecastHour, newItem: ForecastHour): Boolean {
            return oldItem == newItem
        }
    }
}