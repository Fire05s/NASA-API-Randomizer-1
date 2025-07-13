package com.example.nasaapiapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import com.example.nasaapiapp.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val random = Random

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButton(binding.randomizeButton)

        fetchAPODImage()
    }

    private fun fetchAPODImage() {
        val client = AsyncHttpClient()
        val params = RequestParams()
        val year = random.nextInt(2000, 2024)
        val month = random.nextInt(1, 12)
        val day = if (month == 4 || month == 6 || month == 9 || month == 11) {
            random.nextInt(1, 30)
        } else if (month == 2) {
            if (year % 4 == 0) {
                random.nextInt(1, 29)
            } else {
                random.nextInt(1, 28)
            }
        } else {
            random.nextInt(1, 31)
        }

        val yearString = year.toString()
        val monthString = month.toString()
        val dayString = day.toString()
        params["date"] = "$yearString-$monthString-$dayString"
        params["api_key"] = "API_KEY"

        client["https://api.nasa.gov/planetary/apod", params, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                Glide.with(this@MainActivity)
                    .load(json.jsonObject.getString("url"))
                    .fitCenter()
                    .into(binding.randomImage)
                binding.randomTitle.text = json.jsonObject.getString("title")
                binding.randomDate.text = json.jsonObject.getString("date")
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                throwable: Throwable?
            ) {
                Log.d("APOD Error", errorResponse)
            }
        }]
    }

    private fun setupButton(button: Button) {
        binding.randomizeButton.setOnClickListener {
            fetchAPODImage()
        }
    }


}