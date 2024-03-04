package com.example.hangmanhw3

import android.graphics.Color
import android.util.Log
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var gameProgressImage: ImageView
    private lateinit var wordDisplay: TextView
    private lateinit var startNewGameButton: Button
    private lateinit var wordAndHint: List<String>
    private lateinit var hintDisplayText: TextView
    private lateinit var showHintButton: Button

    private var selectedWordAndHint: String = ""
    private var selectedWord: String = ""
    private var hintForWord: String = ""
    private var incorrectGuesses = 0
    private var hintUsageStatus = 0
    private val buttonsForGuessedLetters = mutableSetOf<Button>()
    private val allKeyboardButtons = mutableSetOf<Button>()

    companion object {
        const val KEY_FOR_CURRENT_WORD = "KEY_FOR_CURRENT_WORD"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameProgressImage = findViewById(R.id.hangmanPicture)
        wordDisplay = findViewById(R.id.guessingWord)
        selectedWordAndHint = resources.getStringArray(R.array.wordBank).random()
        wordAndHint = selectedWordAndHint.split(",")
        selectedWord = wordAndHint[0]
        hintForWord = wordAndHint[1]
        hintUsageStatus = 0
        startNewGameButton = findViewById(R.id.newGameButton)
        showHintButton = findViewById(R.id.hintButton)
        hintDisplayText = findViewById(R.id.hintText)

        startNewGameButton.setOnClickListener {
            startNewGame()
        }

        savedInstanceState?.let {
            selectedWord = it.getString("selectedWord", "")
            incorrectGuesses = it.getInt("incorrectGuesses", 0)
        } ?: startNewGame()

        initializeAllKeyboardButtons()
    }

    private fun startNewGame() {
        gameProgressImage.setImageResource(R.drawable.state0)
        selectedWordAndHint = resources.getStringArray(R.array.wordBank).random()
        wordAndHint = selectedWordAndHint.split(",")
        selectedWord = wordAndHint[0]
        hintForWord = wordAndHint[1]
        hintUsageStatus = 0
        incorrectGuesses = 0
        hintDisplayText.text = "Hint:"
        startNewGameButton.isEnabled = true
        displayWordWithUnderscores()
        reinitializeButtons()
        Log.d("startNewGame", "success")
    }

    private fun initializeAllKeyboardButtons() {
        val keyboardLayout: LinearLayout = findViewById(R.id.keyboard)

        for (i in 0 until keyboardLayout.childCount) {
            val row = keyboardLayout.getChildAt(i) as? ViewGroup
            row?.let {
                for (j in 0 until it.childCount) {
                    (it.getChildAt(j) as? Button)?.let { button ->
                        allKeyboardButtons.add(button)
                    }
                }
            }
        }
    }

    private fun disableAllKeyboardButtons() {
        startNewGameButton.isEnabled = false
        allKeyboardButtons.forEach { button ->
            button.isEnabled = false
            button.setBackgroundColor(Color.parseColor("#8b0000"))
        }
    }

    private fun showLoseMessage(view: View) {

        disableAllKeyboardButtons()
        Snackbar.make(view, "You lost. Start a new game?", Snackbar.LENGTH_INDEFINITE).apply {
            setAction("New Game") {
                startNewGame()
                dismiss()
            }
            show()
        }
    }

    private fun processIncorrectGuess() {
        incorrectGuesses++
        val drawableState = "state$incorrectGuesses"
        resources.getIdentifier(drawableState, "drawable", packageName).let {
            gameProgressImage.setImageResource(it)
        }
        if (incorrectGuesses > 6) {
            showLoseMessage(findViewById(android.R.id.content))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("selectedWord", selectedWord)
        outState.putInt("incorrectGuesses", incorrectGuesses)
        outState.putString(KEY_FOR_CURRENT_WORD, wordDisplay.text.toString())
        outState.putString("hintForWord", hintForWord)
        outState.putInt("hintUsageStatus", hintUsageStatus)
        allKeyboardButtons.forEachIndexed { index, button ->
            outState.putBoolean("buttonState$index", button.isEnabled)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        selectedWord = savedInstanceState.getString("selectedWord", "")
        incorrectGuesses = savedInstanceState.getInt("incorrectGuesses", 0)
        wordDisplay.text = savedInstanceState.getString(KEY_FOR_CURRENT_WORD, "")
        hintForWord = savedInstanceState.getString("hintForWord", "")
        hintUsageStatus = savedInstanceState.getInt("hintUsageStatus", 0)
        updateHintDisplay()
        updateHangmanImage()
        updateButtonStates(savedInstanceState)
    }

    private fun displayWordWithUnderscores() {
        wordDisplay.text = selectedWord.map { "_" }.joinToString(" ")
    }

    private fun reinitializeButtons() {
        allKeyboardButtons.forEach { button ->
            button.isEnabled = true
            button.setBackgroundColor(Color.parseColor("#c30010"))
        }
        buttonsForGuessedLetters.clear()
    }

    fun onLetterButtonClick(view: View) {
        (view as? Button)?.let { button ->
            buttonsForGuessedLetters.add(button)
            button.setBackgroundColor(Color.parseColor("#8b0000"))
            button.isEnabled = false
            val guessedLetter = button.text.toString().first().uppercaseChar()
            if (selectedWord.contains(guessedLetter, ignoreCase = true)) {
                selectedWord.forEachIndexed { index, char ->
                    if (char.equals(guessedLetter, ignoreCase = true)) {
                        revealLetterInWord(index, guessedLetter)
                    }
                }
            } else {
                processIncorrectGuess()
            }
            checkWin()
        }
    }

    private fun revealLetterInWord(index: Int, letter: Char) {
        val currentDisplay = StringBuilder(wordDisplay.text.toString())
        currentDisplay[index * 2] = letter
        wordDisplay.text = currentDisplay.toString()
    }

    private fun checkWin() {
        val trimmedWord = wordDisplay.text.toString().replace(" ", "")
        if (trimmedWord.equals(selectedWord, ignoreCase = true)) {
            disableAllKeyboardButtons()
            val snackbar = Snackbar.make(findViewById(android.R.id.content), "Nice win! Start a new game?", Snackbar.LENGTH_INDEFINITE)
            snackbar.setAction("New Game") {
                startNewGame()
                snackbar.dismiss()
            }
            snackbar.show()
        }
    }

    fun onHintButtonClick(view: View) {
        if (incorrectGuesses == 6 && hintUsageStatus >= 1) {
            Snackbar.make(view, "No hints available", Snackbar.LENGTH_LONG).show()
        } else {
            when (hintUsageStatus) {
                0 -> hintDisplayText.append(hintForWord)
                1 -> disableHalfOfTheLetters()
                2 -> provideVowelHint()
            }
            hintUsageStatus++
        }
    }

    private fun provideVowelHint() {
        processIncorrectGuess()
        val vowels = listOf('A', 'E', 'I', 'O', 'U')
        selectedWord.withIndex().forEach { (index, char) ->
            if (char.uppercaseChar() in vowels && wordDisplay.text[index * 2] == '_') {
                revealLetterInWord(index, char.uppercaseChar())
            }
        }
        checkWin()
        hintDisplayText.text = "Vowel hint provided!"
    }

    private fun disableHalfOfTheLetters() {
        processIncorrectGuess()
        val lettersNotInSelectedWord = ('A'..'Z').toSet() - selectedWord.uppercase().toSet()
        val lettersToDisable = lettersNotInSelectedWord.shuffled().take(lettersNotInSelectedWord.size / 2).toSet()

        allKeyboardButtons.forEach { button ->
            if (button.text.toString().uppercase().first() in lettersToDisable) {
                button.isEnabled = false
                button.setBackgroundColor(Color.parseColor("#8b0000"))
            }
        }
    }

    private fun updateHintDisplay() {
        hintDisplayText.text = when (hintUsageStatus) {
            0 -> "Hint:"
            1, 2 -> hintForWord
            else -> "Vowel hint provided!"
        }
    }

    private fun updateHangmanImage() {
        val currentStateDrawable = "state$incorrectGuesses"
        gameProgressImage.setImageResource(resources.getIdentifier(currentStateDrawable, "drawable", packageName))
    }

    private fun updateButtonStates(savedInstanceState: Bundle) {
        allKeyboardButtons.forEachIndexed { index, button ->
            button.isEnabled = savedInstanceState.getBoolean("buttonState$index", true)
            button.setBackgroundColor(if (button.isEnabled) Color.parseColor("#c30010") else Color.parseColor("#8b0000"))
        }
        Log.v("updateButtonStates", "success")
    }
}
