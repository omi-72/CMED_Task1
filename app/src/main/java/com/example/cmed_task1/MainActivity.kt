package com.example.cmed_task1

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.cmed_task1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListener()

    }
    private fun initListener() {
        binding.buttonPanel.setOnClickListener {
            startStopService()

        }
    }

    private fun startStopService() {
        if (isMyServiceRunning(RunningService::class.java)){

            Toast.makeText(this
                ,"Service Stopped",
                Toast.LENGTH_LONG).show()

            stopService(Intent(this, RunningService::class.java))

        }else{
            Toast.makeText(this
                ,"Service Start",
                Toast.LENGTH_LONG).show()

            startService(Intent(this, RunningService::class.java))
        }
    }

    private fun isMyServiceRunning(clazz: Class<RunningService>): Boolean {
        val manager : ActivityManager = getSystemService(
            Context.ACTIVITY_SERVICE
        ) as ActivityManager

        for (service: ActivityManager.RunningServiceInfo in manager.getRunningServices(Integer.MAX_VALUE)){

            if (clazz.name.equals(service.service.className)){
                return true
            }
        }
        return false

    }
}