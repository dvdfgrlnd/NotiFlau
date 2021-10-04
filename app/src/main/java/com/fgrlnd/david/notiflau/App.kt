package com.fgrlnd.david.notiflau

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

data class App(val appName: String, val packageName: String, val icon: Drawable?)

fun queryInstalledApps(context: Context): List<App> {
    val installedApplications: List<ApplicationInfo> =
        context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
    val apps = installedApplications.filter {
        context.packageManager.getLaunchIntentForPackage(it.packageName) != null
    }
        .map {
            App(
                it.loadLabel(context.packageManager).toString(),
                it.packageName,
                null
            )
        }

    return apps
}

private fun getIcon(context: Context, packageName: String): Drawable {
    val icon: Drawable = try {
        context.packageManager.getApplicationIcon(packageName)
    } catch (e: PackageManager.NameNotFoundException) {
        context.resources.getDrawable(R.drawable.square, null)
    }
    return icon
}

