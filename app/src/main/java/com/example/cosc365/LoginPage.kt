package com.example.cosc365

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedReader
//import java.io.IOException
//import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

class LoginPage : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    private var accessToken: String? = null

    //Private val that shares preference on if the user want dark or light mode
    private val sharePref = "MyPrefsFile"
    override fun onCreate(savedInstanceState: Bundle?) {

        // Read saved night mode state from SharedPreferences
        val sharedPref = getSharedPreferences(sharePref, Context.MODE_PRIVATE)
        val isDarkMode = sharedPref.getBoolean("dark_mode", false)

        // Apply saved night mode state to app's theme
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        // Find views
        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)

        // Set click listener for login button
        loginButton.setOnClickListener {
            val email = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Launch a coroutine to perform the network operation in the background
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val url = URL("https://tweadapi20230306193844.azurewebsites.net/Auth/user")

                    // Create an HTTP connection object
                    val connection = url.openConnection() as HttpURLConnection

                    // Set the HTTP request method (GET, POST, PUT, DELETE, etc.)
                    connection.requestMethod = "POST"
                    connection.doOutput = true
                    connection.doInput = true

                    // Set the request body with the email and password as JSON
                    val requestBody = JSONObject().apply {
                        put("email", email)
                        put("password", password)
                    }

                    // Set the request headers
                    connection.setRequestProperty("Content-Type", "application/json")
                    connection.setRequestProperty("Accept", "application/json")

                    // Write the request body to the output stream
                    val outputStream: OutputStream = connection.outputStream
                    val requestBodyBytes = requestBody.toString().toByteArray(Charsets.UTF_8)
                    outputStream.write(requestBodyBytes)
                    outputStream.flush()
                    outputStream.close()

                    // Read the response from the input stream
                    val responseCode = connection.responseCode
                    val inputStream = if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedInputStream(connection.inputStream)
                    } else {
                        BufferedInputStream(connection.errorStream)
                    }
                    val responseText = inputStream.bufferedReader().use(BufferedReader::readText)

                    // Parse the response JSON and extract the access token
                    val responseJson = JSONObject(responseText)
                    val accessToken = responseJson.getString("token")

                    val sharedToken = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    sharedToken.edit().putString("accessToken", accessToken).apply()

                    // Login successful, start main activity
                    withContext(Dispatchers.Main) {
                        val intent = Intent(this@LoginPage, MainActivity::class.java)
                        startActivity(intent)
                    }

                    // Close the input stream and HTTP connection
                    inputStream.close()
                    connection.disconnect()
                } catch (e: Exception) {
                    // Login failed, show error message
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginPage, "Invalid email or password", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    //button to go to settings
    fun settingsButtonClicked(view: View) {
        val intent = Intent(this, Settings::class.java)
        startActivity(intent)
    }
}