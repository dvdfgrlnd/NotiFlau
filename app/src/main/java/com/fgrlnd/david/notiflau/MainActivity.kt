package com.fgrlnd.david.notiflau

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


class MainActivity : AppCompatActivity() {
    val CHANNEL_ID = "main_notification"
    val channel_name = "main_notification_name"
    val descriptionText = "description_text"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()

    }

    override fun onResume() {
        super.onResume()
        createGrid(queryInstalledApps())

        sendNotification(queryInstalledApps())
    }

    fun createGrid(apps: List<App>) {
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

    fun sendNotification(apps: List<App>) {
        val recent = loadRecent()
        val findRecent = { pn: String -> recent.find { it.packageName == pn } }

        // Sort apps by recent then alphabetical
        val sortedApps = apps.sortedWith { t1, t2 ->
            val r1 = findRecent(t1.packageName)
            val r2 = findRecent(t2.packageName)
            if (r1 != null && r2 != null) {
                // Multiply by -1 to reverse comparison s.t. timestamp is sorted in descending order
                (-1) * r1.used.compareTo(r2.used)
            } else if (r1 != null) {
                // t1 before t2
                -1
            } else if (r2 != null) {
                // t2 before t1
                1
            } else {
                t1.appName.compareTo(t2.appName)
            }
        }

        // Get the layouts to use in the custom notification
        val notificationLayout = RemoteViews(packageName, R.layout.notification_small)

        notificationLayout.addView(
            R.id.view_container,
            createNotificationButton("previous", "previous")
        )
        notificationLayout.addView(R.id.view_container, createNotificationButton("next", "next"))
        notificationLayout.addView(R.id.view_container, createNotificationButton("open", "open"))
        // Add to notification
        for (app in sortedApps) {
            val vb = createNotificationButton(app.packageName, app.appName)

            notificationLayout.addView(R.id.view_container, vb)
        }


        // Create an explicit intent for an Activity in your app
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)


        // Apply the layouts to the notification
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .setContentIntent(pendingIntent)


        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(1337, builder.build())
        }
    }

    fun loadRecent(): List<Recent> {
        val sharedPreferences = getSharedPreferences("recentprefs", MODE_PRIVATE)
        val recent = sharedPreferences.getStringSet("recent", null)
        if (recent != null) {
            return recent.map {
                val s = it.split("^")
                Recent(s[0], s[1].toLong())
            }
        }

        return emptyList()
    }

    private fun createNotificationButton(action: String, buttonText: String): RemoteViews {
        val intent2 = Intent(this, MyReceiver::class.java)
        intent2.action = action
        val pendingIntent2: PendingIntent =
            PendingIntent.getBroadcast(this, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT)

        val vb = RemoteViews(packageName, R.layout.button_view)
        vb.setOnClickPendingIntent(R.id.notif_button, pendingIntent2)
        vb.setTextViewText(R.id.notif_button, buttonText)
        return vb
    }

    fun queryInstalledApps(): List<App> {
        val installedApplications: List<ApplicationInfo> =
            packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val apps = installedApplications.filter {
            packageManager.getLaunchIntentForPackage(it.packageName) != null
        }
            .map {
                App(
                    it.loadLabel(packageManager).toString(),
                    it.packageName,
                    getIcon(it.packageName)
                )
            }

        return apps
    }

    private fun getIcon(packageName: String): Drawable {
        val icon: Drawable = try {
            packageManager.getApplicationIcon(packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            resources.getDrawable(R.drawable.square, null)
        }
        return icon
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, channel_name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    class switchButtonListener : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("Here", "I am here")
        }
    }

}