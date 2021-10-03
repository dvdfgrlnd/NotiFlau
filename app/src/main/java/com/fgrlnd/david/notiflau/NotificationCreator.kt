package com.fgrlnd.david.notiflau

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

private const val channelId = "main_notification"
private const val channelName = "main_notification_name"
private const val descriptionText = "description_text"

fun sendNotification(context: Context, apps: List<App>) {
    val recent = loadRecent(context)
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

    updateRecentList(context, recent, apps)

    // Get the layouts to use in the custom notification
    val notificationLayout = RemoteViews(context.packageName, R.layout.notification_small)

    notificationLayout.addView(
        R.id.view_container,
        createNotificationButton(context, "previous", "previous")
    )
    notificationLayout.addView(
        R.id.view_container,
        createNotificationButton(context, "next", "next")
    )
    notificationLayout.addView(
        R.id.view_container,
        createNotificationButton(context, "open", "open")
    )
    // Add to notification
    for (app in sortedApps) {
        val vb = createNotificationButton(context, app.packageName, app.appName)

        notificationLayout.addView(R.id.view_container, vb)
    }


    // Create an explicit intent for an Activity in your app
    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)


    // Apply the layouts to the notification
    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setStyle(NotificationCompat.DecoratedCustomViewStyle())
        .setCustomContentView(notificationLayout)
        .setContentIntent(pendingIntent)


    with(NotificationManagerCompat.from(context)) {
        // notificationId is a unique int for each notification that you must define
        notify(1337, builder.build())
    }
}

private fun createNotificationButton(
    context: Context,
    action: String,
    buttonText: String
): RemoteViews {
    val intent2 = Intent(context, MyReceiver::class.java)
    intent2.action = action
    val pendingIntent2: PendingIntent =
        PendingIntent.getBroadcast(context, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT)

    val vb = RemoteViews(context.packageName, R.layout.button_view)
    vb.setOnClickPendingIntent(R.id.notif_button, pendingIntent2)
    vb.setTextViewText(R.id.notif_button, buttonText)
    return vb
}

fun createNotificationChannel(context: Context) {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val importance = NotificationManager.IMPORTANCE_MIN
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
