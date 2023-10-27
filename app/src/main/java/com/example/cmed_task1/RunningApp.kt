package com.example.cmed_task1

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class RunningApp : Application() {

    private val CHANNEL_ID = "download_channel"
    private val NOTIFICATION_ID = 1
    private val FILE_URL = "https://video.blender.org/download/videos/3d95fb3d-c866-42c8-9db1-fe82f48ccb95-804.mp4"
    private val FILE_NAME = "samplefile.zip"

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "Download in Progress",
                "Running Notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Download in Progress")
                .setOngoing(true)
                .setProgress(100, 0, false)

            val notificationId = NOTIFICATION_ID
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notificationManager.notify(notificationId, notificationBuilder.build())

            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val url = URL(FILE_URL)
                    val connection = url.openConnection()
                    connection.connect()

                    val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), FILE_NAME)

                    val input = connection.getInputStream()
                    val output = FileOutputStream(file)
                    val data = ByteArray(4096)
                    var total: Long = 0
                    var count: Int
                    while (input.read(data).also { count = it } != -1) {
                        total += count
                        output.write(data, 0, count)

                        val progress = ((total.toFloat() / connection.contentLength) * 100).toInt()
                        notificationBuilder.setProgress(100, progress, false)
                        notificationManager.notify(notificationId, notificationBuilder.build())
                    }

                    output.flush()
                    output.close()
                    input.close()

                    notificationBuilder.setContentText("Download Complete")
                        .setProgress(0, 0, false)
                        .setOngoing(false)

                    notificationManager.notify(notificationId, notificationBuilder.build())

                    Handler(Looper.getMainLooper()).post {
                        // If the user navigates back to the app, dismiss the notification
                        notificationManager.cancel(notificationId)
                        // Update UI or handle download completion
                    }
                } catch (e: Exception) {
                    notificationBuilder.setContentText("Download Failed")
                        .setProgress(0, 0, false)
                        .setOngoing(false)

                    notificationManager.notify(notificationId, notificationBuilder.build())
                    e.printStackTrace()
                }
            }

        }
    }
}