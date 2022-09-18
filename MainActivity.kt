package com.example.stopwatchdemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.stopwatchdemo.databinding.ActivityMainBinding
import kotlinx.coroutines.NonCancellable.start
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private var isStarted = false
    private lateinit var serviceIntent: Intent
    private var time = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnStart.setOnClickListener {
                startOrStop()
            }

            btnReset.setOnClickListener {
                reset()
            }
        }

        serviceIntent = Intent(this, StopWatchService::class.java)
        registerReceiver(updatesTime, IntentFilter(StopWatchService.UPDATED_TIME))
    }
    private fun startOrStop(){
        if (isStarted){
            stop()
        }else{
            start()
        }
    }
    private fun start(){
        serviceIntent.putExtra(StopWatchService.CURRENT_TIME, time)
        startService(serviceIntent)
        binding.btnStart.text = "stop"
        isStarted = true
    }

    private fun stop(){
        stopService(serviceIntent)
        binding.btnStart.text = "start"
        isStarted = false
    }

    private fun reset(){
        stop()
        time = 0.0
        binding.tvTime.text = getFormattedTime(time)
    }

    // call the broadcast receiver... allows us to send or receive event

    private val updatesTime: BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            time = intent.getDoubleExtra(StopWatchService.CURRENT_TIME, 0.0)
            binding.tvTime.text = getFormattedTime(time)
        }

    }

    // Display the time in the format we wanted

    private fun getFormattedTime(time:Double):String{
        val timeInt = time.roundToInt()
        val hours = timeInt % 86400 / 3600
        val minutes = timeInt % 86400 % 3600 / 60
        val seconds = timeInt % 86400 % 3600 % 60

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

}