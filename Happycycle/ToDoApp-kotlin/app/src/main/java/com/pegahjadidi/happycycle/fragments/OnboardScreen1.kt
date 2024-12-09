package com.pegahjadidi.happycycle.fragments

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.pegahjadidi.happycycle.R


class OnboardScreen1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboard_screen1)

        // Find the "Next" button by its ID
        val nextButton = findViewById<Button>(R.id.Next1)
        nextButton.setOnClickListener {
            // Navigate to OnboardScreen2 when the button is clicked
            val intent = Intent(this, OnboardScreen2::class.java)
            startActivity(intent)
        }
    }
}
