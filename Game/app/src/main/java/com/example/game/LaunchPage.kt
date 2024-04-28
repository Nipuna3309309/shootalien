package com.example.game

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class LaunchPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch_page)

        // Delay for 5 seconds
        Handler().postDelayed({
            // Start the main activity after the delay
            startActivity(Intent(this, MainActivity::class.java))
            finish()  // Finish the launch activity so it's not in the back stack
        }, 5000)  // 5000 milliseconds = 5 seconds
    }
}
