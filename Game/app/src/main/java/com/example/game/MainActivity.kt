
package com.example.game

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.example.game.GameActivity
import com.example.game.R

class MainActivity : AppCompatActivity() {

    private var isMute = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.play).setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }
        findViewById<View>(R.id.instructionsBtn).setOnClickListener {
            startActivity(Intent(this, Instructions::class.java))
        }
        val highScoreTxt = findViewById<TextView>(R.id.highScoreTxt)

        val prefs = getSharedPreferences("game", Context.MODE_PRIVATE)
        highScoreTxt.text = "HighScore: ${prefs.getInt("highscore", 0)}"

        isMute = prefs.getBoolean("isMute", false)

        val volumeCtrl = findViewById<ImageView>(R.id.volumeCtrl)

        if (isMute) {
            volumeCtrl.setImageResource(R.drawable.baseline_volume_off_24)
        } else {
            volumeCtrl.setImageResource(R.drawable.baseline_volume_up_24)
        }

        volumeCtrl.setOnClickListener {
            isMute = !isMute
            if (isMute) {
                volumeCtrl.setImageResource(R.drawable.baseline_volume_off_24)
                val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true)
            } else {
                volumeCtrl.setImageResource(R.drawable.baseline_volume_up_24)
                val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false)
            }

            val editor = prefs.edit()
            editor.putBoolean("isMute", isMute)
            editor.apply()
        }
    }
}
