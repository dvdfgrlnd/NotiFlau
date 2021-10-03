package com.fgrlnd.david.notiflau

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val packageName = intent.action
        if (packageName != null) {
            val launchIntent: Intent? =
                context.packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                updateRecent(context, packageName)
                context.startActivity(launchIntent)
            }
        }
    }


    fun updateRecent(context: Context, packageName: String) {
        val sharedPreferences =
            context.applicationContext.getSharedPreferences(
                "recentprefs",
                AppCompatActivity.MODE_PRIVATE
            )
        val recent = sharedPreferences.getStringSet("recent", null)
        val r: Set<Recent> = recent?.map {
            val s = it.split("^")
            Recent(s[0], s[1].toLong())
        }?.toSet()
            ?: mutableSetOf()

        val r2 = r.filter { it.packageName != packageName }.toMutableSet()
        r2.add(Recent(packageName, System.currentTimeMillis()))
        val r3 = r2.map {
            "${it.packageName}^${it.used}"
        }.toSet()
        val editor = sharedPreferences.edit()
        editor.putStringSet("recent", r3)
        editor.commit()
    }
}