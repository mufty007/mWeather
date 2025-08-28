package com.example.mweather.presentation.ui.main

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.mweather.R
import com.example.mweather.data.api.Resource
import com.example.mweather.databinding.FragmentMainBinding
import com.example.mweather.presentation.utils.Extensions.hide
import com.example.mweather.presentation.utils.Extensions.show
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewModel()
        checkLocationPermissionAndFetch()
    }

    private fun setupUI() {
        binding.swipeRefresh.setOnRefreshListener {
            refreshWeatherData()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.weatherState.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        showLoading(true)
                        hideError()
                    }
                    is Resource.Success -> {
                        showLoading(false)
                        hideError()
                        updateUI(resource.data)
                    }
                    is Resource.Error -> {
                        showLoading(false)
                        showError(resource.message)
                    }
                }
            }
        }
    }

    private fun checkLocationPermissionAndFetch() {
        Dexter.withContext(requireContext())
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    viewModel.getCurrentLocationWeather()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    showError(getString(R.string.location_permission_required))
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    private fun refreshWeatherData() {
        viewModel.refreshWeatherData()
    }

    private fun updateUI(weatherData: com.example.mweather.data.models.CurrentWeatherResponse) {
        binding.apply {
            tvLocation.text = "${weatherData.location.name}, ${weatherData.location.country}"
            tvTemperature.text = "${weatherData.current.tempC.toInt()}°C"
            tvDescription.text = weatherData.current.condition.text
            tvFeelsLike.text = "Feels like ${weatherData.current.feelslikeC.toInt()}°C"
            
            tvHumidity.text = getString(R.string.humidity_format, weatherData.current.humidity)
            tvPressure.text = getString(R.string.pressure_format, weatherData.current.pressureMb.toInt())
            tvWindSpeed.text = getString(R.string.wind_speed_format, weatherData.current.windKph / 3.6) // Convert km/h to m/s
            tvVisibility.text = "${weatherData.current.visKm.toInt()} km"

            val iconUrl = "https:${weatherData.current.condition.icon}"
            Glide.with(this@MainFragment)
                .load(iconUrl)
                .into(ivWeatherIcon)
        }
    }

    private fun showLoading(show: Boolean) {
        binding.swipeRefresh.isRefreshing = show
        if (show) {
            binding.loadingLayout.show()
        } else {
            binding.loadingLayout.hide()
        }
    }

    private fun showError(message: String) {
        binding.tvError.text = message
        binding.errorLayout.show()
    }

    private fun hideError() {
        binding.errorLayout.hide()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}