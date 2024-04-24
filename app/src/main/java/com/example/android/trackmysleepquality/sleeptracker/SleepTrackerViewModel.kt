package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.*

class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        application: Application) : AndroidViewModel(application) {


    private var tonight = MutableLiveData<SleepNight?>()
    private val _navigateToSleepQuality=MutableLiveData<SleepNight>()
    val navigateToSleepQuality:LiveData<SleepNight>
    get() = _navigateToSleepQuality

    fun doneNavigation(){
        _navigateToSleepQuality.value=null
    }
    private val _showSnackbarEvent=MutableLiveData<Boolean>()
    val showSnackbarEvent:LiveData<Boolean>
    get() = _showSnackbarEvent

    fun doneSnackbar(){
        _showSnackbarEvent.value=false
    }


    private val nights = database.getAllNights()


    val nightsString = Transformations.map(nights) { nights ->
        formatNights(nights, application.resources)
    }

    val startButtonVisible=Transformations.map(tonight){
        null==it
    }
    val stopButtonVisible=Transformations.map(tonight){
        null!=it
    }
    val clearButtonVisible=Transformations.map(nights){
        it.isNotEmpty()
    }


    init {
        initializeTonight()
    }

    private fun initializeTonight() {
        viewModelScope.launch {
            tonight.value = getTonightFromDatabase()
        }
    }


    private suspend fun getTonightFromDatabase(): SleepNight? {
            var night = database.getTonight()
            if (night?.endTimeMilli != night?.startTimeMilli) {
                night = null
            }
            return night
    }


    private suspend fun clear() {
            database.clear() 
    }

    private suspend fun update(night: SleepNight) {
            database.update(night)
    }

    private suspend fun insert(night: SleepNight) {
            database.insert(night)
    }

    fun onStartTracking() {
        viewModelScope.launch {

            val newNight = SleepNight()

            insert(newNight)

            tonight.value = getTonightFromDatabase()
        }
    }

    fun onStopTracking() {
        viewModelScope.launch {

            val oldNight = tonight.value ?: return@launch
            oldNight.endTimeMilli = System.currentTimeMillis()

            update(oldNight)
            _navigateToSleepQuality.value=oldNight
        }
    }


    fun onClear() {
        viewModelScope.launch {
            clear()
            tonight.value = null
            _showSnackbarEvent.value=true
        }
    }


}

