package com.example.snake

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class StartScreen : AppCompatActivity() {

    private lateinit var startBtn: Button
    private lateinit var show_highscore: TextView
    private lateinit var background_music: MediaPlayer
    private lateinit var exit_btn:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_screen)

        startBtn = findViewById(R.id.start_btn)
        show_highscore = findViewById(R.id.highscore)
        exit_btn=findViewById(R.id.exit_btn)
        background_music = MediaPlayer.create(this, R.raw.backgroundmusic)

        // Retrieve high score from SharedPreferences
        val highScore = getSharedPreferences("SnakeGame", MODE_PRIVATE).getInt("highScore", 0)
        show_highscore.text = "Your HighScore: $highScore"
        background_music.start()


        // Set click listener for the start button
        startBtn.setOnClickListener {
            // Start the game when the start button is clicked
            startGame()
        }

        exit_btn.setOnClickListener {
            // Exit the game when the exit button is clicked
            finish()
        }



    }


    override fun onPause() {
        super.onPause()
        // Stop the background music when the activity is paused
        background_music.stop()
    }

    private fun startGame() {
        // Start MainActivity
        val intent = Intent(this@StartScreen, MainActivity::class.java)
        startActivity(intent)
    }
}
