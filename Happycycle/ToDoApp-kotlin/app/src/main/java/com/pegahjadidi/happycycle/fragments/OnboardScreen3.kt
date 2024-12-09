package com.pegahjadidi.happycycle.fragments

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.pegahjadidi.happycycle.R


class OnboardScreen3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboard_screen3)

        // Find the "Next" button by its ID
        val nextButton = findViewById<Button>(R.id.Next3)
        nextButton.setOnClickListener {
            // Navigate to OnboardScreen2 when the button is clicked
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }
}