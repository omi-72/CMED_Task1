package com.example.cmed_task1

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

class RunningService : Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action){
            Actions.START.toString() -> start()
            Actions.STOP.toString() -> stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }
    private fun start(){
        val notification = NotificationCompat.Builder(this, "running_channel")
            .setSmallIcon(R.drawable.ic_launcher_forground)
            .setContentTitle("Download in Progress")
            .setContentText("Elapsed time: 00:50")
            .setOngoing(true)
            .setProgress(100, 0, false)
            .build()
        startForeground(1, notification)

    }

    enum class Actions {
        START, STOP
    }
}