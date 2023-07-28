package com.example.weatherapp_kotlinmvvm.viewmodel

import android.nfc.Tag
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp_kotlinmvvm.model.WeatherModel
import com.example.weatherapp_kotlinmvvm.service.WeatherApiService
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

private const val TAG = "MainViewModel"
class MainViewModel : ViewModel() {
    private val weatherApiService = WeatherApiService()
    private val disposable = CompositeDisposable()

    val weatherData = MutableLiveData<WeatherModel>()
    val weatherError = MutableLiveData<Boolean>()
    val weatherLoading = MutableLiveData<Boolean>()
    fun refreshData(cityName: String){
        getDataFromApi(cityName)
    }

    private fun getDataFromApi(cityName : String ){
        weatherLoading.value = true
        disposable.add(
            weatherApiService.getDataService(cityName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<WeatherModel>(){
                    override fun onSuccess(t: WeatherModel) {
                        weatherData.value = t
                        weatherError.value = false
                        weatherLoading.value = false
                        Log.d(TAG,"onSuccess: Thành công")
                    }

                    override fun onError(e: Throwable) {
                        weatherError.value = true
                        weatherLoading.value = false
                        Log.e(TAG,"onError: " + e)
                    }

                })
        )
    }
}