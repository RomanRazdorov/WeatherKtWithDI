package com.example.otuskt.api
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { WeatherAPIProvider.getWeatherApi() }
    viewModel { MainViewModel(get()) }
}