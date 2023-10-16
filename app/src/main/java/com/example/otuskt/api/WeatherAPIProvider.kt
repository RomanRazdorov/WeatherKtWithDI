package com.example.otuskt.api

import androidx.viewbinding.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
private const val API_KEY = "fe0c9af753d835967eaa9b8e7e1022b1"

object WeatherAPIProvider {

    private var retrofit: Retrofit = createRetrofit()

    private fun createRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    private fun createClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor {chain ->
                val url = chain.request().url
                    .newBuilder()
                    .addQueryParameter("appid", API_KEY)
                    .build()

                val request = chain.request().newBuilder()
                    .url(url)
                    .build()

                chain.proceed(request)
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            })
            .build()

    fun getWeatherApi(): WeatherAPI = retrofit.create(WeatherAPI::class.java)
}