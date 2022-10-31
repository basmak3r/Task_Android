package com.example.batterydata

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi


class StartupOnBoot : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        Log.i("msg","msg")
        context.startForegroundService(Intent(context, BatteryReadService::class.java))


    }
}