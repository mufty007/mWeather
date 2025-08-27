package com.example.mweather.presentation.ui.forecast

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mweather.data.models.ForecastDay
import com.example.mweather.databinding.ItemForecastBinding
import java.text.SimpleDateFormat
import java.util.*

class DailyForecastAdapter : ListAdapter<ForecastDay, DailyForecastAdapter.DailyForecastViewHolder>(DailyForecastDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyForecastViewHolder {
        val binding = ItemForecastBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DailyForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyForecastViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DailyForecastViewHolder(private val binding: ItemForecastBinding) : RecyclerView.ViewHolder(binding.root) {
        
        private val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())

        fun bind(item: ForecastDay) {
            val date = Date(item.dateEpoch * 1000)
            
            binding.apply {
                tvDate.text = if (isToday(date)) "Today" else dateFormat.format(date)
                tvTime.text = "" // No time for daily forecast
                tvDescription.text = item.day.condition.text
                tvTemp.text = "${item.day.maxTempC.toInt()}°/${item.day.minTempC.toInt()}°C"
                tvFeelsLike.text = "Avg ${item.day.avgTempC.toInt()}°C"
                tvHumidity.text = "💧 ${item.day.avgHumidity.toInt()}%"
                tvWind.text = "💨 ${String.format("%.1f", item.day.maxWindKph / 3.6)} m/s" // Convert to m/s

                val iconUrl = "https:${item.day.condition.icon}"
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

    class DailyForecastDiffCallback : DiffUtil.ItemCallback<ForecastDay>() {
        override fun areItemsTheSame(oldItem: ForecastDay, newItem: ForecastDay): Boolean {
            return oldItem.dateEpoch == newItem.dateEpoch
        }

        override fun areContentsTheSame(oldItem: ForecastDay, newItem: ForecastDay): Boolean {
            return oldItem == newItem
        }
    }
}