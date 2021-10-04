package com.fgrlnd.david.notiflau

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

data class Recent(val packageName: String, val used: Long)

fun loadRecent(context: Context): List<Recent> {
    val sharedPreferences =
        context.getSharedPreferences("recentprefs", AppCompatActivity.MODE_PRIVATE)
    val recent = sharedPreferences.getStringSet("recent", null)
    if (recent != null) {
        return recent.map {
            val s = it.split("^")
            Recent(s[0], s[1].toLong())
        }
    }

    return emptyList()
}

fun saveRecent(context: Context, recent: Collection<Recent>) {
    val serialized = recent.map {
        "${it.packageName}^${it.used}"
    }.toSet()

    val sharedPreferences =
        context.applicationContext.getSharedPreferences(
            "recentprefs",
            AppCompatActivity.MODE_PRIVATE
        )
    val editor = sharedPreferences.edit()
    editor.putStringSet("recent", serialized)
    editor.commit()
}


fun updateRecentList(context: Context, recent: List<Recent>, apps: List<App>) {
    // Remove recent entries for uninstalled apps, and apps not used in the last week
    val now = System.currentTimeMillis()
    val filtered = recent.filter { rec ->
        apps.find { app -> app.packageName == rec.packageName } != null
                && (now - rec.used) < (7 * 24 * 60 * 60 * 1000)
    }

    saveRecent(context, filtered)
}

fun updateRecent(context: Context, packageName: String) {
    val recent = loadRecent(context)

    // Filter out recent entry with same package name and then add a new entry
    val updatedRecent = recent.filter { it.packageName != packageName }.toMutableSet()
    updatedRecent.add(Recent(packageName, System.currentTimeMillis()))

    saveRecent(context, updatedRecent)
}
