package com.example.mweather.presentation.utils

object Constants {
    const val CACHE_EXPIRY_TIME = 30 * 60 * 1000L // 30 minutes
    const val SEARCH_DELAY = 500L // 500ms debounce for search
    
    object WeatherIcons {
        const val BASE_URL = "https://openweathermap.org/img/wn/"
        const val ICON_SIZE_2X = "@2x.png"
        const val ICON_SIZE_4X = "@4x.png"
        
        fun getIconUrl(iconCode: String, size: String = ICON_SIZE_2X): String {
            return "$BASE_URL$iconCode$size"
        }
    }
    
    object Permissions {
        const val LOCATION_PERMISSION_CODE = 1001
        val LOCATION_PERMISSIONS = arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
}