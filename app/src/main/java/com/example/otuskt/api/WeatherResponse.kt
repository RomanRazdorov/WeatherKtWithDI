package com.example.otuskt.api

import com.google.gson.annotations.SerializedName

data class WeatherResponse (
    val name: String,
    @SerializedName("weather")
    val weatherConditions: List<WeatherConditions>,
    @SerializedName("main")
    val weatherData: WeatherData
)

data class WeatherData(
    val temp: Double,
    @SerializedName("feels_like")
    val feelsLike: Double
)

data class WeatherConditions(
    val icon: String
)