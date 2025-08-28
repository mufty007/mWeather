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
import com.example.mweather.databinding.FragmentForecastBinding
import com.example.mweather.presentation.utils.Extensions.hide
import com.example.mweather.presentation.utils.Extensions.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForecastFragment : Fragment() {

    private var _binding: FragmentForecastBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ForecastViewModel by activityViewModels()
    private lateinit var dailyForecastAdapter: DailyForecastAdapter

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
        observeViewModel()
    }

    private fun setupRecyclerView() {
        dailyForecastAdapter = DailyForecastAdapter()
        binding.rvForecast.apply {
            adapter = dailyForecastAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        
        // Setup SwipeRefreshLayout
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshForecastData()
        }
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
                        
                        // Submit the forecast days directly to the adapter
                        dailyForecastAdapter.submitList(resource.data.forecast.forecastDay)
                        android.util.Log.d("ForecastFragment", "Loaded ${resource.data.forecast.forecastDay.size} forecast days")
                    }
                    is Resource.Error -> {
                        showLoading(false)
                        showError(resource.message)
                        android.util.Log.e("ForecastFragment", "Error loading forecast: ${resource.message}")
                    }
                }
            }
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