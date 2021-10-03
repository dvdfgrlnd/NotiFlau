package com.fgrlnd.david.notiflau

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel(this)

    }

    override fun onResume() {
        super.onResume()
        createGrid(queryInstalledApps(this))

        sendNotification(this, queryInstalledApps(this))
    }

    private fun createGrid(apps: List<App>) {
        val sortedApps = apps.sortedBy { it.appName.lowercase() }

        val grid: GridLayout = findViewById(R.id.grid_container)
        for (app in sortedApps) {
            val view = View.inflate(this, R.layout.button_view, null)
            val b = view.findViewById<Button>(R.id.notif_button)
            b.text = app.appName
            b.setOnClickListener {
                val launchIntent: Intent? =
                    packageManager.getLaunchIntentForPackage(app.packageName)
                if (launchIntent != null) {
                    startActivity(launchIntent)
                }
            }
            grid.addView(view)
        }
    }


}