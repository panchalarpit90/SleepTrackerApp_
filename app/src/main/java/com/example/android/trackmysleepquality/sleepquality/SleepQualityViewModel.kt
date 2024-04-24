package com.example.android.trackmysleepquality.sleepquality


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import kotlinx.coroutines.*

class SleepQualityViewModel(private val sleepNightKey:Long =0L,private val database: SleepDatabaseDao):ViewModel(){
    private val viewModelJob= Job()
    private val uiScope= CoroutineScope(Dispatchers.Main+viewModelJob)

    private val _navigateToSleepTracker= MutableLiveData<Boolean>()
    val navigateToSleepTracker:LiveData<Boolean>
    get() = _navigateToSleepTracker

    fun doneNavigation(){
        _navigateToSleepTracker.value=null
    }
    fun onSetSleepQuality(quality: Int) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                val tonight = database.get(sleepNightKey) ?: return@withContext
                tonight.sleepQuality = quality
                database.update(tonight)
            }
            _navigateToSleepTracker.value = true
        }
    }
}