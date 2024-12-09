package com.pegahjadidi.happycycle.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.pegahjadidi.happycycle.R


class SignUp : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editTextName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        editTextName = findViewById(R.id.editTextText45)
        editTextEmail = findViewById(R.id.editTextText47)
        editTextPassword = findViewById(R.id.editTextText46)
        val continueBtn = findViewById<Button>(R.id.continueBtn)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val nextButton = findViewById<TextView>(R.id.textView2login)
        nextButton.setOnClickListener {
            // Navigate to OnboardScreen2 when the button is clicked
            val intent = Intent(this, Signin::class.java)
            startActivity(intent)
        }
        continueBtn.setOnClickListener {
            val userName = editTextName.text.toString().trim()
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (userName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                // Show a notification if any field is empty
                Toast.makeText(this, "Please enter all credentials", Toast.LENGTH_SHORT).show()
            } else {
                // Save user info and navigate to the Signin activity
                saveUserInfo(userName, email, password)
                startActivity(Intent(this, Signin::class.java))
            }
        }
    }

    private fun saveUserInfo(userName: String, email: String, password: String) {
        val editor = sharedPreferences.edit()
        editor.putString("Name", userName)  // Saving user name
        editor.putString("Email", email)
        editor.putString("Password", password)
        editor.apply()
    }
}
