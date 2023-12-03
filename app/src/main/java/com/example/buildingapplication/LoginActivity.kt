package com.example.buildingapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val editTextUsername = findViewById<EditText>(R.id.editTextUsername)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)

        buttonLogin.setOnClickListener {
            val enteredUsername = editTextUsername.text.toString()
            val enteredPassword = editTextPassword.text.toString()

            val validUsername = ""
            val validPassword = ""

            if (enteredUsername == validUsername && enteredPassword == validPassword) {
                // Authentication successful
                Toast.makeText(
                    this, "Authentication successful.",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("USERNAME_EXTRA", enteredUsername)
                startActivity(intent)
                finish()
            } else {
                // Authentication failed
                Toast.makeText(
                    this, "Authentication failed. Invalid username or password.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        buttonRegister.setOnClickListener {
            TODO("Implement")
        }
    }
}