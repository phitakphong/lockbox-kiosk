package com.lockboxth.lockboxkiosk.helpers

import android.content.Context

/**
 * Created by layer on 8/2/2017.
 */
open class Prefs(context: Context) {
    val PREFS_FILENAME = "com.lockboxth.lockboxkiosk"
    val prefs = context.getSharedPreferences(PREFS_FILENAME, 0)
}