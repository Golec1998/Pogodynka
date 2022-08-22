package com.example.pogodynka

import android.graphics.drawable.AnimatedVectorDrawable
import android.media.Image
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import java.util.*

class MainViewFragment : Fragment() {

    private lateinit var appViewModel : AppViewModel
    private val pic = Picasso.get()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_view, container, false)

        appViewModel = ViewModelProvider(this).get(AppViewModel::class.java)
        updateWeather(appViewModel.city, view)

        val cityInput = view.findViewById<EditText>(R.id.cityInput)
        val updateButton = view.findViewById<Button>(R.id.updateButton)
        val changeViewButton = view.findViewById<Button>(R.id.switchToSenior)

        cityInput.setText(appViewModel.city)
        updateButton.setOnClickListener {
            updateWeather(cityInput.text.toString(), view)
        }

        changeViewButton.setOnClickListener { view.findNavController().navigate(R.id.action_mainViewFragment_to_seniorViewFragment) }

        return view
    }

    private fun updateWeather(c : String, view : View) {
        if(appViewModel.updateWeather(c)) Toast.makeText(context, "Nie znaleziono miasta", Toast.LENGTH_SHORT).show()

        // Temperatura i opis pogody
        view.findViewById<TextView>(R.id.temperature).setText(String.format("%.1f", appViewModel.forecast.main.temp) + "°C")
        view.findViewById<TextView>(R.id.weatherDescription).setText(appViewModel.forecast.weather[0].description.toString())

        // Ikona pogody
        pic.load("https://openweathermap.org/img/wn/${appViewModel.forecast.weather[0].icon}@4x.png").into(view.findViewById<ImageView>(R.id.weatherImage))

        // Wschód i zachód słońca
        val sr = Calendar.getInstance()
        sr.timeInMillis = appViewModel.forecast.sys.sunrise.toLong() * 1000
        val ss = Calendar.getInstance()
        ss.timeInMillis = appViewModel.forecast.sys.sunset.toLong() * 1000
        var sunrise = ""
        var sunset = ""
        if(sr.time.minutes.toString().length == 1)
            sunrise = sr.time.hours.toString() + ":0" + sr.time.minutes.toString()
        else
            sunrise = sr.time.hours.toString() + ":" + sr.time.minutes.toString()
        if(ss.time.minutes.toString().length == 1)
            sunset = ss.time.hours.toString() + ":0" + ss.time.minutes.toString()
        else
            sunset = ss.time.hours.toString() + ":" + ss.time.minutes.toString()
        view.findViewById<TextView>(R.id.sunrise).setText(sunrise)
        view.findViewById<TextView>(R.id.sunset).setText(sunset)

        // Temperatura max i min
        view.findViewById<TextView>(R.id.tempMin).setText(String.format("%.1f", appViewModel.forecast.main.temp_min) + "°C")
        view.findViewById<TextView>(R.id.tempMax).setText(String.format("%.1f", appViewModel.forecast.main.temp_max) + "°C")

        // Wiatr
        view.findViewById<TextView>(R.id.windSpeed).setText(appViewModel.forecast.wind.speed.toString() + " m/s")
        view.findViewById<TextView>(R.id.windDirection).setText(getDirection(appViewModel.forecast.wind.deg))

        // Wilgotność i ciśnienie
        view.findViewById<TextView>(R.id.humidity).setText(appViewModel.forecast.main.humidity.toString() + "%")
        view.findViewById<TextView>(R.id.pressure).setText(appViewModel.forecast.main.pressure.toString() + " hPa")

        // Zachmurzenie i widoczność
        view.findViewById<TextView>(R.id.visibility).setText(getVisibility(appViewModel.forecast.visibility))
        view.findViewById<TextView>(R.id.clouds).setText(appViewModel.forecast.clouds.all.toString() + "%")
    }

    private fun getDirection(deg : Int) : String {
        var dir = ""

        if(deg in 23..67)
            dir = "(NE)"
        else if(deg in 68..112)
            dir = "(E)"
        else if(deg in 113..157)
            dir = "(SE)"
        else if(deg in 158..202)
            dir = "(S)"
        else if(deg in 203..247)
            dir = "(SW)"
        else if(deg in 248..292)
            dir = "(W)"
        else if(deg in 293..337)
            dir = "(NW)"
        else
            dir = "(N)"

        return deg.toString() + "° " + dir
    }

    private fun getVisibility(vis : Int) : String {
        if(vis >= 1000)
            return (vis / 1000).toString() + "km"
        return vis.toString() + "m"
    }

}