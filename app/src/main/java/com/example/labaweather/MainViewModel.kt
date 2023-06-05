package com.example.labaweather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.labaweather.adapters.WeatherModel

class MainViewModel : ViewModel() {
    val liveDataCurrent = MutableLiveData<WeatherModel>()
    val liveDataList = MutableLiveData<List<WeatherModel>>()
}