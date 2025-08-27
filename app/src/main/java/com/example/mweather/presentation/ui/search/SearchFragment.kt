package com.example.mweather.presentation.ui.search

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mweather.R
import com.example.mweather.databinding.FragmentSearchBinding
import com.example.mweather.presentation.ui.main.MainViewModel
import com.example.mweather.presentation.ui.forecast.ForecastViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    init {
        android.util.Log.d("SearchFragment", "SearchFragment instance created")
    }

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val forecastViewModel: ForecastViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            android.util.Log.d("SearchFragment", "onCreate called")
            super.onCreate(savedInstanceState)
            
            // Test ViewModel initialization
            android.util.Log.d("SearchFragment", "Testing ViewModel initialization...")
            android.util.Log.d("SearchFragment", "SearchViewModel: ${viewModel::class.simpleName}")
            android.util.Log.d("SearchFragment", "MainViewModel: ${mainViewModel::class.simpleName}")
            android.util.Log.d("SearchFragment", "ForecastViewModel: ${forecastViewModel::class.simpleName}")
            android.util.Log.d("SearchFragment", "All ViewModels initialized successfully")
            
        } catch (e: Exception) {
            android.util.Log.e("SearchFragment", "Error in onCreate or ViewModel initialization", e)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        try {
            android.util.Log.d("SearchFragment", "onCreateView called")
            _binding = FragmentSearchBinding.inflate(inflater, container, false)
            android.util.Log.d("SearchFragment", "Binding inflated successfully")
            return binding.root
        } catch (e: Exception) {
            android.util.Log.e("SearchFragment", "Error in onCreateView", e)
            throw e
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        try {
            android.util.Log.d("SearchFragment", "onViewCreated called")
            super.onViewCreated(view, savedInstanceState)
            setupSearchInput()
            setupSearchButton()
            android.util.Log.d("SearchFragment", "SearchFragment setup completed")
        } catch (e: Exception) {
            android.util.Log.e("SearchFragment", "Error in onViewCreated", e)
        }
    }

    private fun setupSearchInput() {
        try {
            android.util.Log.d("SearchFragment", "Setting up search input")
            binding.etSearch.setOnEditorActionListener { _, actionId, event ->
                android.util.Log.d("SearchFragment", "Search input action triggered")
                if (actionId == EditorInfo.IME_ACTION_SEARCH || 
                    (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                    performSearch()
                    true
                } else {
                    false
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("SearchFragment", "Error setting up search input", e)
        }
    }

    private fun setupSearchButton() {
        try {
            android.util.Log.d("SearchFragment", "Setting up search button")
            binding.btnSearch.setOnClickListener {
                android.util.Log.d("SearchFragment", "Search button clicked")
                performSearch()
            }
        } catch (e: Exception) {
            android.util.Log.e("SearchFragment", "Error setting up search button", e)
        }
    }

    private fun performSearch() {
        try {
            // Null check for binding
            if (_binding == null) {
                return
            }
            
            val query = binding.etSearch.text?.toString()?.trim()
            
            // Hide any previous error
            binding.tvError.visibility = View.GONE
            
            if (query.isNullOrBlank()) {
                showError("Please enter a location name")
                return
            }
            
            if (query.length < 2) {
                showError("Please enter at least 2 characters")
                return
            }
            
            // Additional safety checks
            if (!isAdded || isDetached || activity == null) {
                return
            }
            
            // Log the search attempt
            android.util.Log.d("SearchFragment", "Attempting to search for: $query")
            
            // Update view models with additional safety
            try {
                mainViewModel.getWeatherByCity(query)
            } catch (e: Exception) {
                android.util.Log.e("SearchFragment", "Error updating main view model", e)
                showError("Failed to update weather data")
                return
            }
            
            try {
                forecastViewModel.getForecastByCity(query)
            } catch (e: Exception) {
                android.util.Log.e("SearchFragment", "Error updating forecast view model", e)
                // Don't return here - main weather might still work
            }
            
            // Clear search input
            try {
                binding.etSearch.text?.clear()
            } catch (e: Exception) {
                android.util.Log.e("SearchFragment", "Error clearing search text", e)
            }
            
            // Navigate back to home with additional safety checks
            try {
                if (isAdded && !isDetached && activity != null && _binding != null) {
                    findNavController().navigate(R.id.navigation_home)
                    android.util.Log.d("SearchFragment", "Navigation successful")
                }
            } catch (e: Exception) {
                android.util.Log.e("SearchFragment", "Navigation error", e)
                showError("Search completed but navigation failed")
            }
            
        } catch (e: Exception) {
            // Catch-all error handler
            android.util.Log.e("SearchFragment", "Unexpected error in performSearch", e)
            showError("An unexpected error occurred")
        }
    }
    
    private fun showError(message: String) {
        if (_binding != null) {
            binding.tvError.text = message
            binding.tvError.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}