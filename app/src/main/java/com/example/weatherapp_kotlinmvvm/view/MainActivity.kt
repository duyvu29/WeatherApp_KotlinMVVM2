package com.example.weatherapp_kotlinmvvm.view

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.ViewModelProviders.*
import androidx.lifecycle.observe
import com.bumptech.glide.Glide
import com.example.weatherapp_kotlinmvvm.R
import com.example.weatherapp_kotlinmvvm.databinding.ActivityMainBinding
import com.example.weatherapp_kotlinmvvm.viewmodel.MainViewModel

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding


    private lateinit var viewmodel: MainViewModel
    private lateinit var GET: SharedPreferences
    private lateinit var SET: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GET = getSharedPreferences(packageName, MODE_PRIVATE)
        SET = GET.edit()

        viewmodel = ViewModelProvider(this).get(MainViewModel::class.java)
        var cName = GET.getString("Tên thanh phố", "hanoi")?.lowercase()
        binding.edtCityName.setText(cName)

        viewmodel.refreshData(cName!!)


        // Lấy dữ liệu live Data
        getLiveData()

        // sự kiện
        event ()
    }

    private fun event() {
        binding.swipeLayout.setOnRefreshListener {
            binding.layoutData.visibility = View.GONE
            binding.tvError.visibility = View.GONE
            binding.pbLoading.visibility = View.GONE

            var cityName = GET.getString("cityName", GET.getString("Tên thanh phố", "hanoi")?.lowercase())?.lowercase()
            binding.edtCityName.setText(cityName)
            viewmodel.refreshData(cityName!!)
            binding.swipeLayout.isRefreshing = false;
        }
        binding.imgSearchCity.setOnClickListener{
            val cityName = binding.edtCityName.text.toString()
            SET.putString("cityName", cityName);
            SET.apply()
            viewmodel.refreshData(cityName)
            getLiveData()
            Log.i(TAG, "onCreate: $cityName")
        }

    }

    private fun getLiveData() {
        viewmodel.weatherData.observe(this) { data ->
            data?.let {
                binding.layoutData.visibility = View.VISIBLE
                binding.tvCityCode.text = data.sys.country.toString()
                binding.tvCityName.text = data.name.toString()
                binding.tvClouds.text   = data.weather[0].description.toString()

                Glide.with(this)
                    .load("https://openweathermap.org/img/wn/" + data.weather.get(0).icon + "@2x.png")
                    .into(binding.imWeatherPictures)

                binding.tvDegree.text = data.main.temp.toString() + "°C"
                binding.tvHumidity.text = data.main.humidity.toString() + "%"
                binding.tvWindSpeed.text = data.wind.speed.toString()
                binding.tvLat.text = data.coord.lat.toString()
                binding.tvLon.text = data.coord.lon.toString()

            }
        }
        viewmodel.weatherError.observe(this) { error ->
            error?.let {
                if (error) {
                    binding.tvError.visibility = View.VISIBLE
                    binding.pbLoading.visibility = View.GONE
                    binding.layoutData.visibility = View.GONE
                } else {
                    binding.tvError.visibility = View.GONE
                }
            }
        }

        viewmodel.weatherLoading.observe(this) {loading ->
            loading?.let {
                if  (loading){
                    binding.pbLoading.visibility= View.VISIBLE
                    binding.tvError.visibility = View.GONE
                    binding.layoutData.visibility= View.GONE
                }else{
                    binding.pbLoading.visibility= View.GONE
                }
            }
        }
    }

}