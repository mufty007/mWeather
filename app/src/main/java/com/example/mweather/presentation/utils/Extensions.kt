package com.example.mweather.presentation.utils

import android.view.View

object Extensions {
    fun View.show() {
        visibility = View.VISIBLE
    }
    
    fun View.hide() {
        visibility = View.GONE
    }
    
    fun View.invisible() {
        visibility = View.INVISIBLE
    }
}