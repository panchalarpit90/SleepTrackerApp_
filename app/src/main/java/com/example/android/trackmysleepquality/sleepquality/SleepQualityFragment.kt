package com.example.android.trackmysleepquality.sleepquality

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.databinding.FragmentSleepQualityBinding
import com.example.android.trackmysleepquality.sleeptracker.SleepTrackerFragmentDirections


class SleepQualityFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding: FragmentSleepQualityBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sleep_quality, container, false)

        val application = requireNotNull(this.activity).application
        val argument=SleepQualityFragmentArgs.fromBundle(arguments!!)
        val dataSource=SleepDatabase.getInstance(application).sleepDatabaseDao
        val viewModelFactory=SleepQualityViewModelFactory(argument.sleepNightKey,dataSource)
        val sleepQualityViewModel=ViewModelProvider(this,viewModelFactory).get(SleepQualityViewModel::class.java)
        binding.sleepQualityViewModel=sleepQualityViewModel
        sleepQualityViewModel.navigateToSleepTracker.observe(viewLifecycleOwner,  Observer {
            if (it == true) {
                this.findNavController().navigate(
                    SleepQualityFragmentDirections.actionSleepQualityFragmentToSleepTrackerFragment())
                sleepQualityViewModel.doneNavigation()
            }
        })
        return binding.root
    }
}
