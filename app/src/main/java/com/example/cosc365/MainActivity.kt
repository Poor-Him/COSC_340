package com.example.cosc365

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {

    private val PREFS_NAME = "MyPrefsFile"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Read saved night mode state from SharedPreferences
        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isDarkMode = sharedPref.getBoolean("dark_mode", false)

        // Apply saved night mode state to app's theme
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
    fun openSecondActivity(view: View) {
        val intent = Intent(this, Settings::class.java)
        startActivity(intent)
    }
}

