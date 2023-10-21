package com.ritorno.webverse.avatar

import android.content.Context

class CookieHelper(context: Context) {
    private val preference = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun getUpdateState() = preference.getBoolean(HAS_COOKIE, false)
    fun setUpdateState(state: Boolean){
        preference.edit().putBoolean(HAS_COOKIE, state).apply()
    }

    companion object {
        private const val HAS_COOKIE = "hasCookie"
        private const val PREFERENCE_NAME = "Ready Player Me"
    }
}
