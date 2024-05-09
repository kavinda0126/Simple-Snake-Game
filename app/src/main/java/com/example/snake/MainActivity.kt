package com.example.snake

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.snake.ViewModels.MainActivityData
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {


    private lateinit var board: RelativeLayout
    private lateinit var border: RelativeLayout
    private lateinit var lilu: LinearLayout
    private lateinit var upButton: Button
    private lateinit var downButton: Button
    private lateinit var leftButton: Button
    private lateinit var rightButton: Button
    private lateinit var playAgain: Button
    private lateinit var score: Button
    private lateinit var score2: TextView
    private lateinit var meat: ImageView
    private lateinit var snake: ImageView
    private var snakeSegments = mutableListOf<ImageView>()
    private val handler = Handler()
    private var delayMillis = 60L // Update snake position every 100 milliseconds
    private var currentDirection = "right" // Start moving right by default
    private var scorex = 0
    private lateinit var eatSound: MediaPlayer
    private lateinit var gameOverSound: MediaPlayer
    private lateinit var gameStartSound: MediaPlayer
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewModel:MainActivityData
    private lateinit var runnable  :Runnable;
    private lateinit var pauseendButton:Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        board = findViewById(R.id.board)
        border = findViewById(R.id.relativeLayout)
        lilu = findViewById(R.id.lilu)
        upButton = findViewById(R.id.up)
        downButton = findViewById(R.id.down)
        leftButton = findViewById(R.id.left)
        rightButton = findViewById(R.id.right)
        pauseendButton=findViewById(R.id.pauseBtn)

        playAgain = findViewById(R.id.playagain)
        score = findViewById(R.id.score)
        score2 = findViewById(R.id.score2)
        meat = ImageView(this)
        snake = ImageView(this)
        eatSound = MediaPlayer.create(this, R.raw.food)
        gameOverSound = MediaPlayer.create(this, R.raw.gameover2)
        gameStartSound = MediaPlayer.create(this, R.raw.gamestart)
        sharedPreferences = getSharedPreferences("SnakeGame", Context.MODE_PRIVATE)
        viewModel=ViewModelProvider(this)[MainActivityData::class.java]

        // Set click listener for the "Play Again" button
        playAgain.setOnClickListener {
            // Go back to StartScreen
            startActivity(Intent(this, StartScreen::class.java))
            finish()
        }

        viewModel.score.observe(this, { score ->
            scorex=score
            score2.text = "Score: $score"
        })




        handlePauseButtonClick()

        // Start the new game
        startNewGame()
    }


    override fun onPause() {
        super.onPause()
        // Pause the game loop when the activity is paused
        handler.removeCallbacks(runnable)
    }


    override fun onResume() {
        super.onResume()
        // Resume the game loop when the activity is resumed
        if (::runnable.isInitialized) {
            handler.postDelayed(runnable, delayMillis)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        // Clean up resources when the activity is destroyed
        handler.removeCallbacksAndMessages(null)
        eatSound.release()
        gameOverSound.release()
        gameStartSound.release()

    }





    // Start a new game
    private fun startNewGame() {

        board.removeAllViews()
        snakeSegments.clear()
        delayMillis = 60L
        currentDirection = "right"
        scorex = 0

        board.visibility = View.VISIBLE
        playAgain.visibility = View.INVISIBLE
        score.visibility = View.INVISIBLE
        score2.visibility = View.VISIBLE
        gameStartSound.start()

        snake.setImageResource(R.drawable.snake)
        snake.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        board.addView(snake)
        snakeSegments.add(snake)

        var snakeX = snake.x
        var snakeY = snake.y

        meat.setImageResource(R.drawable.meat)
        meat.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        board.addView(meat)

        val random = Random()
        val randomX = random.nextInt(801) - 400
        val randomY = random.nextInt(801) - 400
        meat.x = randomX.toFloat()
        meat.y = randomY.toFloat()

        runnable = object : Runnable {
            override fun run() {
                // Update the position of each snake segment
                for (i in snakeSegments.size - 1 downTo 1) {
                    snakeSegments[i].x = snakeSegments[i - 1].x
                    snakeSegments[i].y = snakeSegments[i - 1].y
                }

                // Update snake position based on current direction
                when (currentDirection) {
                    "up" -> snakeY -= 10
                    "down" -> snakeY += 10
                    "left" -> snakeX -= 10
                    "right" -> snakeX += 10
                }

                // Check boundaries and handle collisions
                val minX = -(board.width / 2 - snake.width / 2)
                val minY = -(board.height / 2 - snake.height / 2)
                val maxX = (board.width / 2 - snake.width / 2)
                val maxY = (board.height / 2 - snake.height / 2)

                if (snakeX < minX || snakeX > maxX || snakeY < minY || snakeY > maxY) {
                    handleGameOver()
                    return
                }

                snake.translationX = snakeX
                snake.translationY = snakeY

                // Check food collision
                checkFoodCollision()

                handler.postDelayed(this, delayMillis)
            }
        }

        handler.postDelayed(runnable, delayMillis)

        // Set button onClickListeners to update the currentDirection variable when pressed
        upButton.setOnClickListener { currentDirection = "up" }
        downButton.setOnClickListener { currentDirection = "down" }
        leftButton.setOnClickListener { currentDirection = "left" }
        rightButton.setOnClickListener { currentDirection = "right" }
    }


    private fun handlePauseButtonClick() {
        pauseendButton.setOnClickListener {
            // Pause the game
            handler.removeCallbacks(runnable)

            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Game Paused")
            alertDialogBuilder.setMessage("Do you want to resume or end the game?")

            alertDialogBuilder.setPositiveButton("Resume") { _, _ ->
                // Resume the game
                handler.postDelayed(runnable, delayMillis)
            }

            alertDialogBuilder.setNegativeButton("End Game") { _, _ ->
                // End the game
                handleGameOver()
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }

    private fun handleGameOver() {
        // Handle game over scenario
        gameOverSound.start()
        border.setBackgroundColor(resources.getColor(R.color.red))
        currentDirection = "pause"
        scorex= viewModel.score.value?.toInt() ?: 0

        // Check and update high score
        val highScore = sharedPreferences.getInt("highScore", 0)
        if (scorex > highScore) {
            sharedPreferences.edit().putInt("highScore", scorex).apply()
        }

        // Show a popup message
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Game Over")
        alertDialogBuilder.setMessage("Your score is $scorex.\n")

        alertDialogBuilder.setNegativeButton("Back to Menu") { dialogInterface: DialogInterface, _: Int ->
            // Go back to StartScreen
            startActivity(Intent(this, StartScreen::class.java))
            finish()
            dialogInterface.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun checkFoodCollision() {
        val distanceThreshold = 50
        val distance = sqrt((snake.x - meat.x).pow(2) + (snake.y - meat.y).pow(2))

        if (distance < distanceThreshold) {
            // Food collision detected
            val newSnake = ImageView(this)
            newSnake.setImageResource(R.drawable.snake)
            newSnake.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            board.addView(newSnake)
            snakeSegments.add(newSnake)

            val random = Random()
            val randomX = random.nextInt(801) - -100
            val randomY = random.nextInt(801) - -100
            meat.x = randomX.toFloat()
            meat.y = randomY.toFloat()

            delayMillis-- // Reduce delay value by 1
            eatSound.start()
            viewModel.increment();
            score2.text = "Your Score: ${viewModel.score.value}"
        }
    }
}
