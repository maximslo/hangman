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
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("currWord", currWord)
        outState.putInt("numGuesses", numGuesses)
        outState.putString("GUESSING_WORD_KEY", guessingWord.text.toString())
        outState.putString("currHint", currHint)
        outState.putInt("hintState", hintState)
        allLetters.forEachIndexed { index, button ->
            outState.putBoolean("button$index", button.isEnabled)
        }
    }

    @SuppressLint("DiscouragedApi")
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currWord = savedInstanceState.getString("currWord", "")
        numGuesses = savedInstanceState.getInt("numGuesses", 0)
        guessingWord.text = savedInstanceState.getString(GUESSING_WORD_KEY, "")
        currHint = savedInstanceState.getString("currHint", "")
        hintState = savedInstanceState.getInt("hintState", 0)
        if(hintState == 0){
            hintText.text = "Hint:"
        }
        else if(hintState == 1 || hintState == 2){
            hintText.text = currHint
        }
        else {
            hintText.text = "vowel hint!"
        }
        val currentState = "state$numGuesses"
        hangmanProgress.setImageResource(resources.getIdentifier(currentState, "drawable", packageName))
        allLetters.forEachIndexed { index, button ->
            button.isEnabled = savedInstanceState.getBoolean("button$index")
            if (button.isEnabled) {
                button.setBackgroundColor(Color.parseColor("#42474f"))
            } else {
                button.setBackgroundColor(Color.parseColor("#c6cfc8"))
            }
        }
    }
    private fun underscoreWord() {
        val stringBuilder = StringBuilder()
        for (char in currWord) {
            stringBuilder.append("_ ")
        }
        guessingWord.text = stringBuilder.toString()
    }

    private fun resetButtons() {
        allLetters.forEach { button ->
            button.isEnabled = true
            button.setBackgroundColor(Color.parseColor("#42474f"))
        }
        guessedLetters.clear()
    }

    fun letterClick(view: View) {
        if (view is Button) {
            val button = view as Button
            guessedLetters.add(button)
            button.setBackgroundColor(Color.parseColor("#c6cfc8"))
            button.isEnabled = false
            val guess = view.text.toString().uppercase().first()
            if (currWord.contains(guess, ignoreCase = true)) {
                currWord.forEachIndexed { index, char ->
                    if (char.equals(guess, ignoreCase = true)) {
                        updateLetter(index, guess)
                    }
                }
            } else {
                wrongLetter()
            }
            checkWin()
        }
    }

    private fun updateLetter(index: Int, newChar: Char) {
        val currentText = StringBuilder(guessingWord.text.toString())
        currentText.setCharAt(index*2, newChar)
        guessingWord.text = currentText.toString()
    }

    private fun checkWin(){
        val trimmedWord = guessingWord.text.toString().replace(" ", "")
        if(trimmedWord.equals(currWord, ignoreCase = true)){
            disableAll()
            val snackbar = Snackbar.make(findViewById(android.R.id.content), "You Won! Click to start a new game :)", Snackbar.LENGTH_INDEFINITE)
            snackbar.setAction("New Game") {
                newGame()
                snackbar.dismiss()
            }
            snackbar.show()
        }
    }

    fun hintClick(view: View) {
        if (numGuesses == 6 && hintState >= 1) {
            val snackbar = Snackbar.make(view,
                "Hint not available",
                Snackbar.LENGTH_LONG)
            snackbar.show()
        }
        else{
            if(hintState == 0){
                hintText.append(currHint)
            }
            else if(hintState == 1)
                disableHalf()
            else if(hintState == 2){
                vowelHint()
            }
            hintState += 1
        }
    }

    private fun vowelHint() {
        wrongLetter() //costs the player a turn
        val vowels = setOf('A', 'E', 'I', 'O', 'U')
        for ((index, char) in currWord.withIndex()) {
            if (char.uppercaseChar() in vowels && guessingWord.text.toString()[index*2] == '_') {
                updateLetter(index, char.uppercaseChar())
            }
        }
        checkWin()
        hintText.text = "vowel hint!"
    }

    private fun disableHalf() {
        wrongLetter()
        val lettersNotInWord = notInWord()
        val lettersToDisable = randomSet(lettersNotInWord)

        allLetters.forEach { button ->
            val char = button.text.toString().uppercase().first()
            if (char in lettersToDisable) {
                button.isEnabled = false
                button.setBackgroundColor(Color.parseColor("#c6cfc8"))
            }
        }
    }

    private fun notInWord(): Set<Char> {
        val wordLetters = currWord.uppercase().toSet()
        return ('A'..'Z').toSet() - wordLetters
    }

    private fun randomSet(letters: Set<Char>): Set<Char> {
        val list = letters.toList()
        val halfSize = list.size / 2
        return list.shuffled().take(halfSize).toSet()
    }

}
