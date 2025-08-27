package com.example.mweather.data.api

sealed class ApiResponse<T> {
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Error<T>(val message: String, val throwable: Throwable? = null) : ApiResponse<T>()
    class Loading<T> : ApiResponse<T>()
}

sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val message: String) : Resource<T>()
    class Loading<T> : Resource<T>()
}