package com.example.mweather.presentation.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mweather.R
import com.example.mweather.data.api.Resource
import com.example.mweather.domain.usecases.GetCurrentWeatherUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment() {

    @Inject
    lateinit var getCurrentWeatherUseCase: GetCurrentWeatherUseCase

    private lateinit var btnSearch: Button
    private lateinit var etSearch: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var tvError: TextView
    private lateinit var layoutResult: LinearLayout
    private lateinit var tvLocation: TextView
    private lateinit var tvTemperature: TextView
    private lateinit var tvCondition: TextView
    private lateinit var tvFeelsLike: TextView
    private lateinit var tvHumidity: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupSearch()
    }

    private fun initViews(view: View) {
        btnSearch = view.findViewById(R.id.btn_search)
        etSearch = view.findViewById(R.id.et_search)
        progressBar = view.findViewById(R.id.progress_bar)
        tvError = view.findViewById(R.id.tv_error)
        layoutResult = view.findViewById(R.id.layout_result)
        tvLocation = view.findViewById(R.id.tv_location)
        tvTemperature = view.findViewById(R.id.tv_temperature)
        tvCondition = view.findViewById(R.id.tv_condition)
        tvFeelsLike = view.findViewById(R.id.tv_feels_like)
        tvHumidity = view.findViewById(R.id.tv_humidity)
    }

    private fun setupSearch() {
        btnSearch.setOnClickListener {
            val query = etSearch.text?.toString()?.trim()
            
            if (query.isNullOrBlank()) {
                showError("Please enter a city name")
                return@setOnClickListener
            }
            
            if (query.length < 2) {
                showError("Please enter at least 2 characters")
                return@setOnClickListener
            }
            
            searchWeather(query)
        }
    }

    private fun searchWeather(query: String) {
        lifecycleScope.launch {
            try {
                showLoading(true)
                hideError()
                hideResult()
                
                getCurrentWeatherUseCase(query).collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            showLoading(true)
                        }
                        is Resource.Success -> {
                            showLoading(false)
                            showWeatherResult(resource.data)
                        }
                        is Resource.Error -> {
                            showLoading(false)
                            showError(resource.message)
                        }
                    }
                }
            } catch (e: Exception) {
                showLoading(false)
                showError("Search failed: ${e.message}")
            }
        }
    }

    private fun showWeatherResult(weather: com.example.mweather.data.models.CurrentWeatherResponse) {
        tvLocation.text = "${weather.location.name}, ${weather.location.country}"
        tvTemperature.text = "${weather.current.tempC.toInt()}°C"
        tvCondition.text = weather.current.condition.text
        tvFeelsLike.text = "Feels like ${weather.current.feelslikeC.toInt()}°C"
        tvHumidity.text = "Humidity ${weather.current.humidity}%"
        
        layoutResult.visibility = View.VISIBLE
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnSearch.isEnabled = !show
    }

    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = View.VISIBLE
    }

    private fun hideError() {
        tvError.visibility = View.GONE
    }

    private fun hideResult() {
        layoutResult.visibility = View.GONE
    }
}