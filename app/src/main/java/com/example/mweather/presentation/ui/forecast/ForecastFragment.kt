package com.example.mweather.presentation.ui.forecast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mweather.data.api.Resource
import com.example.mweather.data.models.ForecastDay
import com.example.mweather.databinding.FragmentForecastBinding
import com.example.mweather.presentation.utils.Extensions.hide
import com.example.mweather.presentation.utils.Extensions.show
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ForecastFragment : Fragment() {

    private var _binding: FragmentForecastBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ForecastViewModel by activityViewModels()
    private lateinit var forecastAdapter: ForecastAdapter
    
    private var forecastDays: List<ForecastDay> = emptyList()
    private var selectedDayIndex = 0
    
    private val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
    private val todayFormat = SimpleDateFormat("MMM d", Locale.getDefault())
    private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForecastBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupTabs()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        forecastAdapter = ForecastAdapter()
        binding.rvForecast.apply {
            adapter = forecastAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
    
    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    selectedDayIndex = it.position
                    android.util.Log.d("ForecastTab", "Tab selected: position ${it.position}, text: ${it.text}")
                    android.util.Log.d("ForecastTab", "Forecast days size: ${forecastDays.size}")
                    android.util.Log.d("ForecastTab", "Selected index: $selectedDayIndex")
                    if (selectedDayIndex < forecastDays.size) {
                        android.util.Log.d("ForecastTab", "Selected day date: ${forecastDays[selectedDayIndex].date}")
                    }
                    updateForecastForSelectedDay()
                }
            }
            
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.forecastState.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        showLoading(true)
                        hideError()
                    }
                    is Resource.Success -> {
                        showLoading(false)
                        hideError()
                        forecastDays = resource.data.forecast.forecastDay
                        setupDayTabs()
                        updateForecastForSelectedDay()
                    }
                    is Resource.Error -> {
                        showLoading(false)
                        showError(resource.message)
                    }
                }
            }
        }
    }
    
    private fun setupDayTabs() {
        binding.tabLayout.removeAllTabs()
        
        // Get today's date string for comparison
        val todayString = apiDateFormat.format(Calendar.getInstance().time)
        android.util.Log.d("ForecastTab", "Today's date string: $todayString")
        android.util.Log.d("ForecastTab", "Total forecast days received: ${forecastDays.size}")
        
        forecastDays.forEachIndexed { index, day ->
            val date = Date(day.dateEpoch * 1000)
            
            // Compare with today's date string
            val isToday = day.date == todayString
            
            val tabText = if (isToday) {
                "Today"
            } else {
                // Format the day name from the epoch timestamp
                dayFormat.format(date)
            }
            
            val tab = binding.tabLayout.newTab().setText(tabText)
            binding.tabLayout.addTab(tab)
            
            // Debug logging to check the data
            android.util.Log.d("ForecastTab", "Tab $index: $tabText, Date: ${day.date}, DateEpoch: ${day.dateEpoch}, IsToday: $isToday")
            android.util.Log.d("ForecastTab", "Hours in this day: ${day.hour.size}")
        }
        
        // Find the index of "today" and select it
        selectedDayIndex = findTodayIndex()
        android.util.Log.d("ForecastTab", "Found today at index: $selectedDayIndex")
        if (binding.tabLayout.tabCount > selectedDayIndex && selectedDayIndex >= 0) {
            binding.tabLayout.selectTab(binding.tabLayout.getTabAt(selectedDayIndex))
        }
    }
    
    private fun findTodayIndex(): Int {
        val todayString = apiDateFormat.format(Calendar.getInstance().time)
        
        forecastDays.forEachIndexed { index, day ->
            if (day.date == todayString) {
                android.util.Log.d("ForecastTab", "Found today at index $index for date ${day.date}")
                return index
            }
        }
        android.util.Log.d("ForecastTab", "Today not found in forecast data, defaulting to index 0")
        return 0 // Default to first day if today is not found
    }
    
    private fun updateForecastForSelectedDay() {
        android.util.Log.d("ForecastTab", "updateForecastForSelectedDay called")
        android.util.Log.d("ForecastTab", "selectedDayIndex: $selectedDayIndex, forecastDays.size: ${forecastDays.size}")
        
        if (selectedDayIndex >= 0 && selectedDayIndex < forecastDays.size) {
            val selectedDay = forecastDays[selectedDayIndex]
            android.util.Log.d("ForecastTab", "Updating forecast for day index: $selectedDayIndex")
            android.util.Log.d("ForecastTab", "Selected day date: ${selectedDay.date}")
            android.util.Log.d("ForecastTab", "Selected day hours count: ${selectedDay.hour.size}")
            
            if (selectedDay.hour.isNotEmpty()) {
                android.util.Log.d("ForecastTab", "First hour data: ${selectedDay.hour[0].time}")
            }
            
            forecastAdapter.submitList(selectedDay.hour)
        } else {
            android.util.Log.e("ForecastTab", "Invalid selectedDayIndex: $selectedDayIndex for forecastDays size: ${forecastDays.size}")
        }
    }
    
    private fun isToday(date: Date): Boolean {
        val today = Calendar.getInstance()
        val itemDate = Calendar.getInstance().apply { time = date }
        val isToday = today.get(Calendar.YEAR) == itemDate.get(Calendar.YEAR) &&
               today.get(Calendar.DAY_OF_YEAR) == itemDate.get(Calendar.DAY_OF_YEAR)
        
        android.util.Log.d("ForecastTab", "Checking if date is today: ${itemDate.time}, isToday: $isToday")
        return isToday
    }
    
    private fun isTodayByDateString(dateString: String): Boolean {
        try {
            val today = Calendar.getInstance()
            val todayString = apiDateFormat.format(today.time)
            
            android.util.Log.d("ForecastTab", "Comparing dates - Today: $todayString, API Date: $dateString")
            return dateString == todayString
        } catch (e: Exception) {
            android.util.Log.e("ForecastTab", "Error comparing dates", e)
            return false
        }
    }

    private fun showLoading(show: Boolean) {
        if (show) {
            binding.progressBar.show()
        } else {
            binding.progressBar.hide()
        }
    }

    private fun showError(message: String) {
        binding.tvError.text = message
        binding.tvError.show()
    }

    private fun hideError() {
        binding.tvError.hide()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}