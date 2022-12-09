package com.example.newsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.work.*
import com.example.newsapp.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        myWorkManager()

        binding.loginBtn.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.skipBtn.setOnClickListener {
            startActivity(Intent(this, DashboardUserActivity::class.java))
        }
    }

//    private fun myWorkManager(){
//        val constraints = Constraints.Builder()
//            .setRequiresCharging(false)
//            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
//            .setRequiresCharging(false)
//            .setRequiresBatteryNotLow(true)
//            .build()
//
//        val myRequest = PeriodicWorkRequest.Builder(
//            MyWorker::class.java,
//            15,
//            TimeUnit.MINUTES
//        ).setConstraints(constraints)
//            .build()
//
//        WorkManager.getInstance(this)
//            .enqueueUniquePeriodicWork(
//                "my_id",
//                ExistingPeriodicWorkPolicy.KEEP,
//                myRequest
//            )
//    }

}