package com.robertohuertas.endless

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class StartReceiver : BroadcastReceiver() {
    private val PREFS_KEY_intent = "myPreferncesintent"
    private val PASSWORD_KEY_intent = "myPreferncesintent"
    var telApp: Intent? =null

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED && getServiceState(context)
            == ServiceState.STARTED) {
            val intentxx = Intent(context, MainActivity2::class.java)
            intentxx.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intentxx)
        }
    }
}