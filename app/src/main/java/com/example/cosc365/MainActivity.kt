package com.example.cosc365

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL


data class Set(
    val setId: String,
    val userId: String,
    val name: String,
    val url: String,
    val createdOn: String,
    val createdBy: String,
    val modifiedOn: String,
    val modifiedBy: String,
    val timeUntilNotification: Int
)

data class Item(
    val itemId: String,
    val setId: String,
    val key: String,
    val value: String,
    val createdOn: String,
    val createdBy: String,
    val modifiedOn: String,
    val modifiedBy: String
)

class MainActivity : AppCompatActivity() {

    private lateinit var frontTextView: TextView
    private lateinit var backTextView: TextView

    //Private val that shares preference on if the user want dark or light mode
    private val sharePref = "MyPrefsFile"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        frontTextView = findViewById(R.id.front)
        backTextView = findViewById(R.id.back)

        // Read saved night mode state from SharedPreferences
        val sharedPref = getSharedPreferences(sharePref, Context.MODE_PRIVATE)
        val isDarkMode = sharedPref.getBoolean("dark_mode", false)

        // Apply saved night mode state to app's theme
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        // Get the authorized token from shared preferences
        val sharedToken = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val accessToken = sharedToken.getString("accessToken", null)

        // Fetch and parse the set using the authorized token
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val setUrl = "https://tweadapi20230306193844.azurewebsites.net/Set/sets"

                val setConnection = URL(setUrl).openConnection() as HttpURLConnection
                setConnection.requestMethod = "GET"
                setConnection.setRequestProperty("Authorization", "Bearer $accessToken")

                val setResponseCode = setConnection.responseCode
                val setInputStream = if (setResponseCode == HttpURLConnection.HTTP_OK) {
                    BufferedInputStream(setConnection.inputStream)
                } else {
                    BufferedInputStream(setConnection.errorStream)
                }
                val setResponseText = setInputStream.bufferedReader().use(BufferedReader::readText)

                // Parse the set response JSON
                val setResponseJsonArray = JSONArray(setResponseText)
                for (i in 0 until setResponseJsonArray.length()) {
                    val setJson = setResponseJsonArray.getJSONObject(i)
                    val setId = setJson.getString("setId")
                    val userId = setJson.getString("userId")
                    val name = setJson.getString("name")
                    val url = setJson.getString("url")
                    val createdOn = setJson.getString("createdOn")
                    val createdBy = setJson.getString("createdBy")
                    val modifiedOn = setJson.getString("modifiedOn")
                    val modifiedBy = setJson.getString("modifiedBy")
                    val timeUntilNotification = setJson.getInt("timeUntilNotification")

                    val itemsJsonArray = setJson.getJSONArray("items")
                    for (j in 0 until itemsJsonArray.length()) {
                        val itemJson = itemsJsonArray.getJSONObject(j)
                        val itemId = itemJson.getString("itemId")
                        val itemSetId = itemJson.getString("setId")
                        val key = itemJson.getString("key")
                        val value = itemJson.getString("value")
                        val itemCreatedOn = itemJson.getString("createdOn")
                        val itemCreatedBy = itemJson.getString("createdBy")
                        val itemModifiedOn = itemJson.getString("modifiedOn")
                        val itemModifiedBy = itemJson.getString("modifiedBy")

// Use the parsed set and item data as needed
// For example, you can create Set
                        // Create Set and Item objects or store the data in variables
                        val set = Set(
                            setId,
                            userId,
                            name,
                            url,
                            createdOn,
                            createdBy,
                            modifiedOn,
                            modifiedBy,
                            timeUntilNotification
                        )

                        val item = Item(
                            itemId,
                            itemSetId,
                            key,
                            value,
                            itemCreatedOn,
                            itemCreatedBy,
                            itemModifiedOn,
                            itemModifiedBy
                        )
                        updateFlashcardView(item)
                    }
                }

                // Close the set input stream and HTTP connection
                setInputStream.close()
                setConnection.disconnect()
            } catch (e: Exception) {
                // Handle the error while fetching or parsing the set
                e.printStackTrace()
            }
        }
    }
    private fun updateFlashcardView(item: Item) {
        frontTextView.text = item.key
        backTextView.text = item.value
    }

    // Function to go to the settings page
    fun openSecondActivity(view: View) {
        val intent = Intent(this, Settings::class.java)
        startActivity(intent)
    }
}
