package com.example.otuskt

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.flowWithLifecycle
import com.example.otuskt.api.*
import com.example.otuskt.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import kotlin.Error



class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val editTextImageUrl = binding.urlField;


        binding.btnNext.setOnClickListener()
        {
            btnListener(editTextImageUrl);
        }

        lifecycle.coroutineScope.launch {

            viewModel.viewState
                .flowWithLifecycle(lifecycle)
                .collect { state ->
                    renderState(state)
                }
        }

        startKoin {
            androidContext(this@MainActivity)
            modules(appModule)
        }
        binding.swipeLayout.setOnRefreshListener {  viewModel.onRefresh() }
    }

    private fun renderState(state: MainViewState) {
        when (state)
        {
            is Data -> renderData(state.weatherResponse)
            Error -> renderError()
            Loading -> renderLoading()
        }
    }

    private fun renderData(weatherResponse: WeatherResponse) {
        binding.swipeLayout.isRefreshing = false
        binding.cityName.text = weatherResponse.name
        binding.tempTV.text = getString(R.string.temp, weatherResponse.weatherData.temp)
        binding.feelsLike.text = getString(R.string.temp, weatherResponse.weatherData.feelsLike)

        Picasso.get()
            .load(getString(R.string.icon_url, weatherResponse.weatherConditions[0].icon))
            .into(binding.weatherIV)
    }

    private fun renderLoading() {
        binding.swipeLayout.isRefreshing = false
    }

    private fun renderError() {
        binding.swipeLayout.isRefreshing = false
        Toast.makeText(this, getString(R.string.error_text), Toast.LENGTH_SHORT).show()
    }

    private fun btnListener(editTextImageUrl: EditText)
    {

        val imageUrl = editTextImageUrl.text.toString()
        if (imageUrl.isNotEmpty()) {
            Toast.makeText(this, "гуд", Toast.LENGTH_SHORT).show()

            val networkThread = Thread {
                try {
                    val bitmap = Picasso.get().load(imageUrl).get()

                    val diskThread = Thread {
                        saveImageToInternalStorage(bitmap, fileName = "image.jpg")
                    }
                    diskThread.start()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            networkThread.start()
        } else {
            Toast.makeText(this, "Введите URL изображения", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap, fileName: String) {
        val context = this

        try {
            // Открываем или создаем файл для сохранения изображения во внутренней памяти
            val fileOutputStream = context.openFileOutput(fileName, MODE_PRIVATE)

            // Сохраняем изображение в формате JPEG с качеством 100 (максимальное качество)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)

            // Закрываем поток после записи
            fileOutputStream.close()

            Log.i("SafeImage","Изображение успешно сохранено")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("SafeImage","Ошибка при сохранении изображения")
        }
    }


}