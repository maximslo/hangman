package com.example.hangmanhw3

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var hangmanProgress: ImageView
    private lateinit var guessingWord: TextView
    private lateinit var newGameButton: Button
    private lateinit var currList: List<String>
    private lateinit var hintText: TextView
    private lateinit var hintButton: Button

    private var currPair: String = ""
    private var currWord: String = ""
    private var currHint: String = ""
    private var numGuesses = 0
    private var hintState = 0
    private val guessedLetters = mutableSetOf<Button>()
    private val allLetters = mutableSetOf<Button>()
    companion object {
        const val GUESSING_WORD_KEY = "GUESSING_WORD_KEY"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hangmanProgress = findViewById(R.id.hangmanPicture)
        guessingWord = findViewById(R.id.guessingWord)
        currPair = resources.getStringArray(R.array.wordBank).random()
        currList = currPair.split(",")
        currWord = currList[0]
        currHint = currList[1]
        hintState = 0
        newGameButton = findViewById(R.id.newGameButton)
        hintButton = findViewById(R.id.hintButton)
        hintText = findViewById(R.id.hintText)

        newGameButton.setOnClickListener(){
            newGame()
        }

        if (savedInstanceState != null) {
            currWord = savedInstanceState.getString("currWord", "")
            numGuesses = savedInstanceState.getInt("numGuesses", 0)
        } else {
            newGame()
        }
        getAllButtons()
    }
    private fun newGame() {
        hangmanProgress.setImageResource(R.drawable.state0)
        currPair = resources.getStringArray(R.array.wordBank).random()
        currList = currPair.split(",")
        currWord = currList[0]
        currHint = currList[1]
        hintState = 0
        numGuesses = 0
        hintText.text = "Hint:"
        newGameButton.isEnabled = true
        underscoreWord()
        resetButtons()
    }

}
