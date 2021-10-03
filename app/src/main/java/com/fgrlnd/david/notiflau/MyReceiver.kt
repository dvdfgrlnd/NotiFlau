package com.fgrlnd.david.notiflau

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val packageName = intent.action
        if (packageName != null) {
            val launchIntent: Intent? =
                context.packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                // Update recent entry with current timestamp
                updateRecent(context, packageName)

                // Update notification
                sendNotification(context, queryInstalledApps(context))

                // Open app
                context.startActivity(launchIntent)
            }
        }
    }


    private fun updateRecent(context: Context, packageName: String) {
        val recent = loadRecent(context)

        // Filter out recent entry with same package name and then add a new entry
        val updatedRecent = recent.filter { it.packageName != packageName }.toMutableSet()
        updatedRecent.add(Recent(packageName, System.currentTimeMillis()))

        saveRecent(context, updatedRecent)
    }
}