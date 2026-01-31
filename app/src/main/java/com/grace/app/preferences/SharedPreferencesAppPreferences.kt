package com.grace.app.preferences

import android.content.SharedPreferences
import com.grace.app.constants.Constants
import javax.inject.Inject

class SharedPreferencesAppPreferences @Inject constructor(
    private val preferences: SharedPreferences
) : AppPreferences {
    override var isTncAccepted: Boolean
        get() = preferences.getBoolean(Constants.DISCLAIMER_TNC_KEY, false)
        set(value) {
            preferences.edit().putBoolean(Constants.DISCLAIMER_TNC_KEY, value).apply()
        }
}
