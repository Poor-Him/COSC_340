package com.example.cosc365

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate

class Settings : AppCompatActivity() {

    //Private val that shares preference on if the user want dark or light mode
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

        // Find the logout button view
        val logoutButton: Button = findViewById(R.id.logoutButton)

        logoutButton.setOnClickListener {
            // Clear the access token from the shared preferences
            val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("accessToken", null)
            editor.apply()

            // Redirect to the login page
            val intent = Intent(this@Settings, LoginPage::class.java)
            startActivity(intent)
            finish() // Close the current activity
        }

    }

    //function that allows the button to go back to the main page
    fun goBackToMain(view: View) {
        finish()
    }

    //function to set light mode
    fun setLightMode(view: View) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        saveNightModeState(false)
    }

    //function to set Dark Mode
    fun setDarkMode(view: View) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        saveNightModeState(true)
    }

    //private function that saves the state of the theme
    private fun saveNightModeState(isDarkMode: Boolean) {
        val sharedPref = getSharedPreferences(sharePref, Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putBoolean("dark_mode", isDarkMode)
            apply()
        }
    }
}