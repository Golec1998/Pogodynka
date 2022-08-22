package com.example.pogodynka

import android.content.Context
import android.content.res.Resources
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import java.net.URL

class AppViewModel : ViewModel() {
    var weatherJSON = ""
    var country = "pl"
    var city = "Pyskowice"
    lateinit var forecast : WeatherForecast

    fun updateWeather(c : String) : Boolean
    {
        weatherJSON = ""
        var result = true
        city = c
        runBlocking {
            val pending = viewModelScope.launch(Dispatchers.IO) {
                try {
                    weatherJSON = URL("https://api.openweathermap.org/data/2.5/weather?q=$city,$country&lang=$country&units=metric&appid=b31c2399a91bd7f2a5019bbe3fa1e049").readText()
                } catch (e : Exception) {}
            }
            pending.join()

            if (!(weatherJSON == "" || weatherJSON.contains("city not found", ignoreCase = true))) {
                result = false
                forecast = Gson().fromJson(weatherJSON, WeatherForecast::class.java)
            }
        }

        return result
    }

}