package com.fgrlnd.david.notiflau

import android.graphics.drawable.Drawable

data class App(val appName: String, val packageName: String, val icon: Drawable)

data class Recent(val packageName: String, val used: Long)
