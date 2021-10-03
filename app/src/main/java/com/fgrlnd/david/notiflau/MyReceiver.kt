package com.fgrlnd.david.notiflau

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val action = intent.action
        if (action != null) {
            val start = when {
                action.startsWith("next") -> {
                    action.split(":")[1].toInt()
                }
                action.startsWith("previous") -> {
                    action.split(":")[1].toInt()
                }
                else -> {
                    // Launch app
                    val launchIntent: Intent? =
                        context.packageManager.getLaunchIntentForPackage(action)
                    if (launchIntent != null) {
                        // Update recent entry with current timestamp
                        updateRecent(context, action)

                        // Update notification
                        sendNotification(context, queryInstalledApps(context), 0)

                        // Open app
                        context.startActivity(launchIntent)
                    }

                    // Reset recent app notification list
                    0
                }
            }
            sendNotification(context, queryInstalledApps(context), start)
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