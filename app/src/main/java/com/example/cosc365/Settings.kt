package com.example.cosc365

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate

class Settings : AppCompatActivity() {

    private val sharePref = "MyPrefsFile"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Read saved night mode state from SharedPreferences
        val sharedPref = getSharedPreferences(sharePref, Context.MODE_PRIVATE)
        val isDarkMode = sharedPref.getBoolean("dark_mode", false)

        // Apply saved night mode state to app's theme
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
    fun goBackToMain(view: View) {
        finish()
    }



    fun setLightMode(view: View) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        saveNightModeState(false)
    }

    fun setDarkMode(view: View) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        saveNightModeState(true)
    }

    private fun saveNightModeState(isDarkMode: Boolean) {
        val sharedPref = getSharedPreferences(sharePref, Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putBoolean("dark_mode", isDarkMode)
            apply()
        }
    }
}