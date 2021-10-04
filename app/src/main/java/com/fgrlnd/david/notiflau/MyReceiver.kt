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

                        // Close notification/status bar
                        context.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
                    }

                    // Reset recent app notification list
                    0
                }
            }
            sendNotification(context, queryInstalledApps(context), start)
        }
    }


}