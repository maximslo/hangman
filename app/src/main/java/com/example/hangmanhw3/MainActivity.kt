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
    private fun getAllButtons() {
        val keyboardGroup: LinearLayout = findViewById(R.id.keyboard)

        for (i in 0 until keyboardGroup.childCount) {
            val child: View? = keyboardGroup.getChildAt(i)

            if (child is ViewGroup) {
                for (j in 0 until child.childCount) {
                    val innerChild: View? = child.getChildAt(j)

                    if (innerChild is Button) {
                        allLetters.add(innerChild)
                    }
                }
            }
        }
    }
    private fun disableAll() {
        val newGame: MaterialButton = findViewById(R.id.newGameButton)
        newGame.isEnabled = false
        val keyboardGroup: LinearLayout = findViewById(R.id.keyboard)
        allLetters.forEach { button ->
            button.isEnabled = false
            button.setBackgroundColor(Color.parseColor("#c6cfc8"))
        }
    }
    private fun youLost(view: View) {
        disableAll()
        val snackbar = Snackbar.make(view,
            "You lost :( Click to start a new game",
            Snackbar.LENGTH_INDEFINITE
        )
        snackbar.setAction("New Game") {
            newGame()
            snackbar.dismiss()
        }
        snackbar.show()
    }

    @SuppressLint("DiscouragedApi")
    private fun wrongLetter() {
        numGuesses += 1
        val curState = "state$numGuesses"
        val resourceId = resources.getIdentifier(curState, "drawable", packageName)
        hangmanProgress.setImageResource(resourceId)
        if(numGuesses > 6) {
            youLost(findViewById(android.R.id.content))
        }
    }
}
