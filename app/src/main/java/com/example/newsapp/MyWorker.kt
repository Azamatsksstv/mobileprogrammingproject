package com.example.newsapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*

class MyWorker(context: Context, workerParameters: WorkerParameters):
    Worker(context,workerParameters){

    companion object{
        const val CHANNEL_ID = "channel_id"
        const val NOTIFICATION_ID = 1
    }

    override fun doWork(): Result {
        Log.d("success:", "doWork: Success function called")
        showNotification()
        return Result.success()
    }



    private fun showNotification() {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,0,intent,0
        )

        val notification = NotificationCompat.Builder(
            applicationContext,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("New Category")
            .setContentText("You can see new books")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                CHANNEL_ID,
                "channel name",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "channel description"
            }

            val notificationManager = applicationContext.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            notificationManager.createNotificationChannel(channel)
//            channel.lightColor = Color.RED
        }
        with(NotificationManagerCompat.from(applicationContext)){
            notify(NOTIFICATION_ID, notification.build())
        }
    }
}