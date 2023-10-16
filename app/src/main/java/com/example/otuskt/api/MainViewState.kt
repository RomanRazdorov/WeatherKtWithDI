package com.example.otuskt.api

sealed interface MainViewState

object Loading : MainViewState
data class Data(val weatherResponse: WeatherResponse) : MainViewState
object Error : MainViewState