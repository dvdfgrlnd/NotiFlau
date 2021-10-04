package com.fgrlnd.david.notiflau

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import kotlin.math.floor
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel(this)

    }

    override fun onResume() {
        super.onResume()

        val grid: GridLayout = findViewById(R.id.grid_container)
        grid.post {
            createGrid(queryInstalledApps(this))
        }

        val apps = queryInstalledApps(this);
        sendNotification(this, apps, 0)
    }

    private fun createGrid(apps: List<App>) {
        val sortedApps = apps.sortedBy { it.appName.lowercase() }

        val grid: GridLayout = findViewById(R.id.grid_container)
        val d = floor(0.8 * (grid.width / 9).toDouble()).toInt()
        for (app in sortedApps) {
            val view = View.inflate(this, R.layout.button_view, null)
            val b = view.findViewById<ImageView>(R.id.notif_button)
            b.setImageBitmap(app.icon.toBitmap(d, d))
            b.setOnClickListener {
                val launchIntent: Intent? =
                    packageManager.getLaunchIntentForPackage(app.packageName)
                if (launchIntent != null) {
                    // Update recent entry with current timestamp
                    updateRecent(this, app.packageName)

                    // Update notification
                    sendNotification(this, apps, 0)

                    startActivity(launchIntent)
                }
            }
            grid.addView(view)
        }
    }


}