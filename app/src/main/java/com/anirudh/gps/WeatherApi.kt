package com.anirudh.gps

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class WeatherResponse(
    val main: Main
)

data class Main(
    val temp: Double
)

interface WeatherApi {
    @GET("weather")
    fun getWeatherData(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String
    ): Call<WeatherResponse>
}

fun fetchWeatherData(latitude: Double, longitude: Double, apiKey: String) {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val weatherApi = retrofit.create(WeatherApi::class.java)
    val call = weatherApi.getWeatherData(latitude, longitude, apiKey)
    call.enqueue(object : Callback<WeatherResponse> {
        override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
            if (response.isSuccessful) {
                val weatherResponse = response.body()
                val temperatureInKelvin = weatherResponse?.main?.temp ?: 0.0

                // Convert temperature from Kelvin to Celsius (or Fahrenheit if needed)
                val temperatureInCelsius = temperatureInKelvin - 273.15
                val temperatureInFarenheit = temperatureInCelsius*9/5+32
            } else {
                // Handle unsuccessful response
            }
        }

        override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
            // Handle failure (e.g., network error)
        }
    })
}
