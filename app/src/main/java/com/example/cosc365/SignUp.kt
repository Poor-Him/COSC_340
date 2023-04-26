package com.example.cosc365

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class SignUpActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Find views
        emailEditText = findViewById(R.id.emailEditText)
        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        signUpButton = findViewById(R.id.signUpButton)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        // Set click listener for sign up button
        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Validate inputs
            if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length < 6){
                Toast.makeText(this@SignUpActivity, "Password is less than 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Launch a coroutine to perform the network operation in the background
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val url = URL("https://tweadapi20230306193844.azurewebsites.net/Auth/register")

                    // Create an HTTP connection object
                    val connection = url.openConnection() as HttpURLConnection

                    // Set the HTTP request method (GET, POST, PUT, DELETE, etc.)
                    connection.requestMethod = "POST"

                    // Set the request body with the email, username, and password as JSON
                    val requestBody = JSONObject().apply {
                        put("email", email)
                        put("userName", username)
                        put("password", password)
                    }

                    // Set the request headers
                    connection.setRequestProperty("Content-Type", "application/json")
                    connection.setRequestProperty("Accept", "*/*")
                    connection.setRequestProperty("Content-Length", requestBody.toString().length.toString())
                    connection.doOutput = true
                    connection.outputStream.use { outputStream ->
                        outputStream.write(requestBody.toString().toByteArray(Charsets.UTF_8))
                    }

                    // Send the HTTP request and read the response
                    val responseCode = connection.responseCode
                    val inputStream = if (responseCode == HttpURLConnection.HTTP_OK) {
                        connection.inputStream
                    } else {
                        connection.errorStream
                    }
                    val responseText = inputStream.bufferedReader().use { it.readText() }

                    // Parse the response JSON and check for success message
                    val responseJson = JSONObject(responseText)
                    val message = responseJson.getString("message")
                    val success = message == "User created successfully"

                    // Display success or error message
                    withContext(Dispatchers.Main) {
                        if (success) {
                            Toast.makeText(this@SignUpActivity, "Account Successfully Created", Toast.LENGTH_SHORT).show()
                            //Goes back to login page
                            finish()
                        } else {
                            var errorMessage = "Failed to create account. "
                            errorMessage += if (message.contains("email")) {
                                "Email is already taken."
                            } else if (message.contains("username")) {
                                "Username is already taken."
                            } else {
                                "Please check your input."
                            }
                            Toast.makeText(this@SignUpActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }

                    // Close the HTTP connection
                    connection.disconnect()
                } catch (e: Exception) {
                    // Display error message
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@SignUpActivity, "Failed to create account. Please check your input.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
