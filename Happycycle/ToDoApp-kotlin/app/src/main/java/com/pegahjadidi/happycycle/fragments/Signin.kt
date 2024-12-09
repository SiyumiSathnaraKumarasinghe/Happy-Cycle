package com.pegahjadidi.happycycle.fragments

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.pegahjadidi.happycycle.MainActivity
import com.pegahjadidi.happycycle.R
import android.provider.Settings




class Signin : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)


        editTextEmail = findViewById(R.id.editTextText47)
        editTextPassword = findViewById(R.id.editTextText46)
        val loginBtn = findViewById<Button>(R.id.loginBtn)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        onLoginSuccess()


        loginBtn.setOnClickListener {
            val inputEmail = editTextEmail.text.toString().trim()
            val inputPassword = editTextPassword.text.toString().trim()

            if (inputEmail.isEmpty() || inputPassword.isEmpty()) {
                // Show a Toast message if any field is empty
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            } else {
                val savedEmail = sharedPreferences.getString("Email", "")
                val savedPassword = sharedPreferences.getString("Password", "")

                if (inputEmail == savedEmail && inputPassword == savedPassword) {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                    // Navigate to MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()  // Optional: close the Signin activity so the user can't go back to it
                } else {
                    Toast.makeText(this, "Invalid Email or Password", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    fun onLoginSuccess() {
        // Check for exact alarm permissions after successful login
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                // If running on Android 12 or higher, direct user to system settings
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            } else {
                // Permission is already granted; you can proceed with setting alarms
                scheduleAlarms()
            }
        } else {
            // For Android versions below 12, no additional permission is needed
            scheduleAlarms()
        }
    }

    private fun scheduleAlarms() {
        // Your logic to schedule alarms here
    }


}
