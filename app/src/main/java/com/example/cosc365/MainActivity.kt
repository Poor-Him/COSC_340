package com.example.cosc365

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import org.json.JSONArray
//import java.io.BufferedReader
//import java.io.InputStreamReader

data class Set(
    val setId: String,
    val userId: String,
    val name: String,
    val url: String,
    val createdOn: String,
    val createdBy: String,
    val modifiedOn: String,
    val modifiedBy: String,
    val timeUntilNotification: Int,
    val items: MutableList<Item>
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
    private lateinit var continueButton: Button
    private lateinit var openSecondActivityButton: Button
    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var button4: Button

    //Private val that shares preference on if the user wants dark or light mode
    private val sharePref = "MyPrefsFile"

    //
    private var firstSet: Set? = null
    private var currentSet: Set? = null
    private var currentItemIndex = 0
    private var currentBackCard: Item? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        frontTextView = findViewById(R.id.front)
        backTextView = findViewById(R.id.back)
        continueButton = findViewById(R.id.continue_button)
        button1 = findViewById(R.id.button1)
        button2 = findViewById(R.id.button2)
        button3 = findViewById(R.id.button3)
        button4 = findViewById(R.id.button4)

        openSecondActivityButton = findViewById(R.id.open_second_activity_button)
        openSecondActivityButton.setOnClickListener {
            openSecondActivity()
        }

        // Read saved night mode state from SharedPreferences
        val sharedPref = getSharedPreferences(sharePref, Context.MODE_PRIVATE)
        val isDarkMode = sharedPref.getBoolean("dark_mode", false)

        // Apply saved night mode state to app's theme
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        // Read the data from JSON file in assets
        val jsonString = applicationContext.assets.open("data.json").bufferedReader().use {
            it.readText()
        }

        try {
            // Parse the JSON data
            val setResponseJsonArray = JSONArray(jsonString)
            val sets = mutableListOf<Set>()
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
                val items = mutableListOf<Item>()
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
                    items.add(item)
                }

                val set = Set(
                    setId,
                    userId,
                    name,
                    url,
                    createdOn,
                    createdBy,
                    modifiedOn,
                    modifiedBy,
                    timeUntilNotification,
                    items
                )
                sets.add(set)
            }

            // Update the UI with the first set's data
            if (sets.isNotEmpty()) {
                firstSet = sets[0]
                currentSet = firstSet
                updateFlashcardView(firstSet!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                this@MainActivity,
                "Failed to parse JSON data: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }

        continueButton.setOnClickListener {
            // Get the current front card
            val currentFrontCard = getCurrentItem(currentSet!!)

            // Show the backTextView with the current front card's key
            backTextView.text = currentFrontCard?.key ?: ""
            backTextView.visibility = View.VISIBLE

            // Hide the continueButton
            continueButton.visibility = View.GONE

            // Show the other buttons
            button1.visibility = View.VISIBLE
            button2.visibility = View.VISIBLE
            button3.visibility = View.VISIBLE
            button4.visibility = View.VISIBLE
        }

        button1.setOnClickListener {
            moveCurrentItem()
            val currentItem = getCurrentItem(currentSet!!)
            frontTextView.text = currentItem?.value ?: ""
            backTextView.visibility = View.GONE
            continueButton.visibility = View.VISIBLE
            button1.visibility = View.GONE
            button2.visibility = View.GONE
            button3.visibility = View.GONE
            button4.visibility = View.GONE
        }

        button2.setOnClickListener {
            moveCurrentItem()
            val currentItem = getCurrentItem(currentSet!!)
            frontTextView.text = currentItem?.value ?: ""
            backTextView.visibility = View.GONE
            continueButton.visibility = View.VISIBLE
            button1.visibility = View.GONE
            button2.visibility = View.GONE
            button3.visibility = View.GONE
            button4.visibility = View.GONE
        }

        button3.setOnClickListener {
            pushCurrentItem()
            moveCurrentItem()
            val currentItem = getCurrentItem(currentSet!!)
            frontTextView.text = currentItem?.value ?: ""
            backTextView.visibility = View.GONE
            continueButton.visibility = View.VISIBLE
            button1.visibility = View.GONE
            button2.visibility = View.GONE
            button3.visibility = View.GONE
            button4.visibility = View.GONE
        }

        button4.setOnClickListener {
            pushCurrentItem()
            moveCurrentItem()
            val currentItem = getCurrentItem(currentSet!!)
            frontTextView.text = currentItem?.value ?: ""
            backTextView.visibility = View.GONE
            continueButton.visibility = View.VISIBLE
            button1.visibility = View.GONE
            button2.visibility = View.GONE
            button3.visibility = View.GONE
            button4.visibility = View.GONE
        }
    }

    private fun getCurrentItem(set: Set): Item? {
        return set.items.getOrNull(currentItemIndex)
    }
    private fun moveCurrentItem() {
        currentItemIndex++
        if (currentItemIndex >= (currentSet?.items?.size ?: 0)) {
            currentSet = null
            currentItemIndex = 0
        }
    }

    private fun pushCurrentItem() {
        val currentItem = getCurrentItem(currentSet!!)
        currentItem?.let {
            currentSet?.items?.add(it)
        }
    }


    private fun updateFlashcardView(set: Set) {
        // Generate a random index different from the current front card index
        var randomIndex: Int
        do {
            randomIndex = set.items.indices.random()
        } while (randomIndex == currentItemIndex)

        // Get the current front card
        var currentFrontCard = getCurrentItem(currentSet!!)

        // Update the frontTextView with the new front card's value
        val newFrontCard = set.items[randomIndex]
        frontTextView.text = newFrontCard.value

        // Update the backTextView with the current back card's key
        backTextView.text = currentFrontCard?.key

        // Store the new front card and update the current back card
        currentFrontCard = newFrontCard
        currentBackCard = currentFrontCard

        // Update the current item index
        currentItemIndex = randomIndex

        // Hide the backTextView initially
        backTextView.visibility = View.INVISIBLE
    }



    // Function to go to the settings page
    private fun openSecondActivity() {
        // Handle the button click event here
        val intent = Intent(this, Settings::class.java)
        startActivity(intent)
    }
}


