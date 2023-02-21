package com.example.lifecycle_management_and_intents

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SignedIn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.signed_in)

        //Get Text View
        var logText: TextView = findViewById(R.id.loggedInTextView)

        //get the intent
        val signIntent = intent

        var fName = signIntent.getStringExtra("FN_TEXT")
        var lName = signIntent.getStringExtra("LN_TEXT")

        logText.setText("${fName} ${lName} is logged in!")


    }
}