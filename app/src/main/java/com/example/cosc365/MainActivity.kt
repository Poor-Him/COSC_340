package com.example.cosc365

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    fun openSecondActivity(view: View) {
        val intent = Intent(this, Settings::class.java)
        startActivity(intent)
    }
}

